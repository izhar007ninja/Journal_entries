package net.engineeringdigest.journalApp.controller;

import java.util.*;

import io.swagger.v3.oas.annotations.tags.Tag;
import net.engineeringdigest.journalApp.api.response.WeatherResponse;
import net.engineeringdigest.journalApp.entity.User;
import net.engineeringdigest.journalApp.service.UserService;
import net.engineeringdigest.journalApp.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Tag(name = "User APIs")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private WeatherService weatherService;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


//    @GetMapping
//    public List<User> getAll(){
//        return userService.getAll();
//
//    }


    @PutMapping
    public ResponseEntity<?> updateUser(@RequestBody User user){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currUser = userService.findByUserName(authentication.getName());
        if(currUser != null){
            currUser.setUserName(user.getUserName());


            userService.createUser(currUser, user.getPassword());
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping
    public ResponseEntity<?> greeting(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        WeatherResponse weatherResponse = weatherService.getWeather("Mumbai");
        String message = "";
        if(weatherResponse!=null){
            message+= " weather feels like " + weatherResponse.getMain().getFeelsLike();
        }

        return new ResponseEntity<>("Hii"+authentication.getName()+ message,HttpStatus.OK);


    }





}
