package com.developer.mobileappws.services;

import com.developer.mobileappws.dto.AddressDto;
import com.developer.mobileappws.entity.PasswordResetTokenEntity;
import com.developer.mobileappws.entity.UserEntity;
import com.developer.mobileappws.exceptions.UserServiceException;
import com.developer.mobileappws.models.response.ErrorMessages;
import com.developer.mobileappws.repository.PasswordResetTokenRepository;
import com.developer.mobileappws.repository.UserRepository;
import com.developer.mobileappws.services.interfaces.UserService;
import com.developer.mobileappws.dto.UserDto;
import com.developer.mobileappws.utility.Utils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    UserRepository userRepository;

    @Autowired
    Utils utils;

    @Autowired
    PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

//    private void sendEmail(UserEntity userEntity) throws MessagingException, UnsupportedEncodingException {
//        String link = "http://localhost:8080/verification-service/email-verification.html?token="+userEntity.getEmailVerificationToken();
//        // SimpleMailMessage msg = new SimpleMailMessage();
//        MimeMessage msg = javaMailSender.createMimeMessage();
//        // msg.setTo(userEntity.getEmail());
//        msg.setRecipient(MimeMessage.RecipientType.TO,new InternetAddress(userEntity.getEmail()));
//        msg.setFrom(new InternetAddress("hcms.server@gmail.com","HCMS Server"));
//        msg.setSubject("Complete your registration!");
//        msg.setText("Hello There, \n Please verify your email to login. \n " + link);
//        javaMailSender.send(msg);
//        System.out.println("Mail sent successfully");
//    }

    @Override
    public UserDto createUser(UserDto user) {

        // Check if user already exists or not...
        if (userRepository.findUserByEmail(user.getEmail()) != null) throw
                new RuntimeException("Record Already Exists!");

        // Updating the addresses...(Address Id)
        for (int i = 0; i < user.getAddresses().size(); i++) {
            AddressDto address = user.getAddresses().get(i);
            address.setUserDetails(user);
            address.setAddressId(utils.generateAddressId(30));
            user.getAddresses().set(i,address);
        }

        // Creating an entity data from incoming DTO...
        ModelMapper modelMapper = new ModelMapper();
        UserEntity userEntity = modelMapper.map(user,UserEntity.class);

        // Create / Encode userid and password and save it into entity model...
        String publicUserId = utils.generateUserId(30);
        userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userEntity.setUserId(publicUserId);
        userEntity.setEmailVerificationToken(utils.generateEmailVerificationToken(publicUserId));
        userEntity.setEmailVerificationStatus(Boolean.FALSE);

        // Save the Entity to the MySql Database using repository...
        UserEntity storedUserDetails = userRepository.save(userEntity);

        // send email for verification...
//        try {
//            sendEmail(storedUserDetails);
//        } catch (Exception e) {
//            throw new UserServiceException("Error Mail can't be sent");
//        }

        // now return a dto which we will get from the saved entity...
        return modelMapper.map(storedUserDetails,UserDto.class);
    }

    @Override
    public UserDto getUser(String email) {
        UserEntity userEntity = userRepository.findUserByEmail(email);

        if (userEntity == null) throw new UsernameNotFoundException(email);

        UserDto returnValue = new UserDto();
        BeanUtils.copyProperties(userEntity,returnValue);
        return returnValue;
    }

    @Override
    public UserDto getUserByUserId(String userId) {
        UserEntity userEntity = userRepository.findUserByUserId(userId);
        if (userEntity == null) throw new UsernameNotFoundException(userId);
        return new ModelMapper().map(userEntity,UserDto.class);
    }

    @Override
    public UserDto updateUser(String userId, UserDto user) {
        UserDto returnValue = new UserDto();

        UserEntity userEntity = userRepository.findUserByUserId(userId);
        if (userEntity == null)
            throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

        userEntity.setFirstName(user.getFirstName());
        userEntity.setLastName(user.getLastName());

        UserEntity updateUser = userRepository.save(userEntity);
        BeanUtils.copyProperties(updateUser,returnValue);

        return returnValue;
    }

    @Override
    public void deleteUser(String userId) {
        UserEntity userEntity = userRepository.findUserByUserId(userId);
        if (userEntity == null)
            throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
        userRepository.delete(userEntity);
    }

    @Override
    public List<UserDto> getUsers(int page, int limit) {
        List<UserDto> returnValue = new ArrayList<>();

        if (page > 0) page-=1;

        Pageable pageable = PageRequest.of(page,limit);
        Page<UserEntity> usersPage = userRepository.findAll(pageable);

        List<UserEntity> userEntities = usersPage.getContent();

        for (UserEntity userEntity : userEntities) {
            UserDto userDto = new UserDto();
            BeanUtils.copyProperties(userEntity,userDto);
            returnValue.add(userDto);
        }

        return returnValue;
    }

    @Override
    public boolean verifyEmailToken(String token) {
        boolean returnValue = false;
        UserEntity userEntity = userRepository.findUserByEmailVerificationToken(token);
        if (userEntity != null) {
            boolean hasTokenExpired = Utils.hasTokenExpired(token);
            if (!hasTokenExpired) {
                userEntity.setEmailVerificationToken(null);
                userEntity.setEmailVerificationStatus(Boolean.TRUE);
                userRepository.save(userEntity);
                returnValue = true;
            }
        }
        return returnValue;
    }

    @Override
    public boolean requestPasswordReset(String email) {
        boolean returnValue =false;

        UserEntity userEntity = userRepository.findUserByEmail(email);
        if (userEntity == null) return returnValue;

        String token = new Utils().generatePasswordResetToken(userEntity.getUserId());

        PasswordResetTokenEntity passwordTokenEntity = new PasswordResetTokenEntity();
        passwordTokenEntity.setToken(token);
        passwordTokenEntity.setUserDetails(userEntity);
        passwordResetTokenRepository.save(passwordTokenEntity);
        // send email for link...
        return returnValue;
    }

    @Override
    public boolean resetPassword(String token, String password) {
        boolean returnValue = false;

        if (Utils.hasTokenExpired(token)) return false;

        PasswordResetTokenEntity passwordResetTokenEntity = passwordResetTokenRepository.findByToken(token);

        if (passwordResetTokenEntity == null) return false;

        String encodedPassword = bCryptPasswordEncoder.encode(password);

        UserEntity userEntity = passwordResetTokenEntity.getUserDetails();
        userEntity.setEncryptedPassword(encodedPassword);
        UserEntity savedUserEntity = userRepository.save(userEntity);

        if (savedUserEntity != null && savedUserEntity.getEncryptedPassword().equalsIgnoreCase(encodedPassword)) {
            returnValue = true;
        }
        passwordResetTokenRepository.delete(passwordResetTokenEntity);
        return returnValue;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findUserByEmail(email);
        if (userEntity == null) throw new UsernameNotFoundException(email);

        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_"+userEntity.getRole()));

        return new User(userEntity.getEmail(),
                userEntity.getEncryptedPassword(),
                userEntity.getEmailVerificationStatus(),true,true,true,
                authorities);
        //return new User(userEntity.getEmail(),userEntity.getEncryptedPassword(),new ArrayList<>());
    }
}
