package net.engineeringdigest.journalApp.controller;

import java.util.*;
import net.engineeringdigest.journalApp.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private UserController userController;
    private User user;


//    @GetMapping("/all-users")
//    public ResponseEntity<?> getAllUsers(){
//        List<User> allUsers = userController.getAll();
//        if(allUsers!=null && !allUsers.isEmpty()){
//            return new ResponseEntity<>(allUsers, HttpStatus.OK);
//        }
//        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//    }

}
