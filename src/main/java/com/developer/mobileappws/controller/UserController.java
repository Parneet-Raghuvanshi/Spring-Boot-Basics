package com.developer.mobileappws.controller;

import com.developer.mobileappws.dto.AddressDto;
import com.developer.mobileappws.exceptions.UserServiceException;
import com.developer.mobileappws.models.request.PasswordResetModel;
import com.developer.mobileappws.models.request.PasswordResetRequestModel;
import com.developer.mobileappws.models.response.AddressRest;
import com.developer.mobileappws.models.response.ErrorMessages;
import com.developer.mobileappws.models.response.OperationStatusModel;
import com.developer.mobileappws.services.interfaces.AddressService;
import com.developer.mobileappws.services.interfaces.UserService;
import com.developer.mobileappws.dto.UserDto;
import com.developer.mobileappws.models.request.UserDetailRequestModel;
import com.developer.mobileappws.models.response.UserRest;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

// for specific route for entire rest controller...
//@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/users") //http://localhost:8080/users
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    AddressService addressService;

    // To Produce Data in both types...
    @GetMapping(path="/{id}",
            produces = { MediaType.APPLICATION_XML_VALUE,MediaType.APPLICATION_JSON_VALUE })
    public UserRest getUser(@PathVariable String id) {
        // Get a DTO for user , which will get from Service.GetUserByUserId()
        UserDto userDto = userService.getUserByUserId(id);
        return new ModelMapper().map(userDto, UserRest.class);
    }

    @GetMapping(
            produces = { MediaType.APPLICATION_XML_VALUE,MediaType.APPLICATION_JSON_VALUE })
    public List<UserRest> getUser(
            @RequestParam(value = "page",defaultValue = "0") int page,
            @RequestParam(value = "limit",defaultValue = "20") int limit) {
        // Response to send back to the client...
        List<UserRest> userResponse = new ArrayList<>();

        List<UserDto> users = userService.getUsers(page,limit);

        for (UserDto userDto : users) {
            UserRest userRest = new UserRest();
            BeanUtils.copyProperties(userDto,userRest);
            userResponse.add(userRest);
        }

        return userResponse;
    }

    @PostMapping(
            consumes = { MediaType.APPLICATION_XML_VALUE,MediaType.APPLICATION_JSON_VALUE },
            produces = { MediaType.APPLICATION_XML_VALUE,MediaType.APPLICATION_JSON_VALUE })
    public UserRest createUser(@RequestBody UserDetailRequestModel userDetailRequestModel)
            throws UserServiceException {
        // Response to send back to the client...
        if (userDetailRequestModel.getFirstName().isEmpty())
            throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());

        // Method Two for Model Mapper...
        UserDto userDto =  new ModelMapper().map(userDetailRequestModel,UserDto.class);

        // Now get a DTO for created user , which will get from Service.CreateUser()
        UserDto createdUser = userService.createUser(userDto);

        return new ModelMapper().map(createdUser,UserRest.class);
    }

    @PutMapping(path="/{id}",
            consumes = { MediaType.APPLICATION_XML_VALUE,MediaType.APPLICATION_JSON_VALUE },
            produces = { MediaType.APPLICATION_XML_VALUE,MediaType.APPLICATION_JSON_VALUE })
    public UserRest updateUser(@PathVariable String id
            ,@RequestBody UserDetailRequestModel userDetailRequestModel) {
        // Response to send back to the client...
        UserRest userResponse = new UserRest();

        if (userDetailRequestModel.getFirstName().isEmpty())
            throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());

        // Create a DTO from incoming payload...
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userDetailRequestModel,userDto);

        // Now get a DTO for created user , which will get from Service.CreateUser()
        UserDto updatedUser = userService.updateUser(id,userDto);
        BeanUtils.copyProperties(updatedUser,userResponse);

        return userResponse;
    }

    @DeleteMapping(path="/{id}",
            consumes = { MediaType.APPLICATION_XML_VALUE,MediaType.APPLICATION_JSON_VALUE },
            produces = { MediaType.APPLICATION_XML_VALUE,MediaType.APPLICATION_JSON_VALUE })
    public OperationStatusModel deleteUser(@PathVariable String id) {
        OperationStatusModel returnValue = new OperationStatusModel();
        returnValue.setOperationName("USER_DELETE");

        userService.deleteUser(id);

        returnValue.setOperationResult("SUCCESS");
        return returnValue;
    }

    // --> /mobile-app-ws/users/{userId}/addresses
    @GetMapping(path="/{id}/addresses",
            produces = { MediaType.APPLICATION_XML_VALUE,MediaType.APPLICATION_JSON_VALUE })
    public List<AddressRest> getUserAddresses(@PathVariable String id) {
        List<AddressRest> returnValue = new ArrayList<>();
        List<AddressDto> address = addressService.getAddresses(id);

        if (address != null && !address.isEmpty()) {
            Type listType = new TypeToken<List<AddressRest>>() {}.getType();
            returnValue = new ModelMapper().map(address, listType);
        }

        return returnValue;
    }

    // --> /mobile-app-ws/users/{userId}/addresses/{addressId}
    @GetMapping(path="/{userId}/addresses/{addressId}",
            produces = { MediaType.APPLICATION_XML_VALUE,MediaType.APPLICATION_JSON_VALUE })
    public AddressRest getUserAddress(@PathVariable String addressId) {
        AddressDto address = addressService.getAddress(addressId);
        return new ModelMapper().map(address, AddressRest.class);
    }

    // for specific route or function...
    //@CrossOrigin(origins = "*")
    @GetMapping(path = "/email-verification",
            produces = { MediaType.APPLICATION_XML_VALUE,MediaType.APPLICATION_JSON_VALUE })
    public OperationStatusModel verifyEmailToken(@RequestParam(value = "token") String token) {
        OperationStatusModel returnValue = new OperationStatusModel();
        returnValue.setOperationName("VERIFY_EMAIL");

        boolean isVerified = userService.verifyEmailToken(token);
        if (isVerified) returnValue.setOperationResult("SUCCESS");
        else returnValue.setOperationResult("FAIL");
        return returnValue;
    }

    @PostMapping(path = "/password-reset-request",
            consumes = { MediaType.APPLICATION_XML_VALUE,MediaType.APPLICATION_JSON_VALUE },
            produces = { MediaType.APPLICATION_XML_VALUE,MediaType.APPLICATION_JSON_VALUE })
    public OperationStatusModel requestPasswordReset(@RequestBody PasswordResetRequestModel passwordResetRequestModel) {
        OperationStatusModel returnValue = new OperationStatusModel();

        boolean operationResult = userService.requestPasswordReset(passwordResetRequestModel.getEmail());

        returnValue.setOperationName("RESET_PASSWORD_REQUEST");
        returnValue.setOperationResult("FAIL");
        if (operationResult) returnValue.setOperationResult("SUCCESS");
        return returnValue;
    }

    @PostMapping(path = "/password-reset",
            consumes = { MediaType.APPLICATION_XML_VALUE,MediaType.APPLICATION_JSON_VALUE },
            produces = { MediaType.APPLICATION_XML_VALUE,MediaType.APPLICATION_JSON_VALUE })
    public OperationStatusModel resetPassword(@RequestBody PasswordResetModel passwordResetModel) {
        OperationStatusModel returnValue = new OperationStatusModel();

        boolean operationResult = userService.resetPassword(passwordResetModel.getToken(),passwordResetModel.getPassword());

        returnValue.setOperationName("PASSWORD_RESET");
        returnValue.setOperationResult("FAIL");
        if (operationResult) returnValue.setOperationResult("SUCCESS");
        return returnValue;
    }
}
