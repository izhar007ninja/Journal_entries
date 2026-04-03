package net.engineeringdigest.journalApp.service;
import java.util.*;

import lombok.extern.slf4j.Slf4j;
import net.engineeringdigest.journalApp.entity.User;
import net.engineeringdigest.journalApp.repository.UserRepository;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserService {
    @Autowired
 private UserRepository userRepository;
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();



    public List<User> getAll(){
        return userRepository.findAll();
    }
    public boolean createUser(User user,String password){
       try{
           user.setPassword(passwordEncoder.encode(password));
           user.setRoles(Arrays.asList("USER"));
           userRepository.save(user);
          
           return true;
       }
       catch (Exception e){
           System.out.println(e);

       return false;
       }


    }
    public Optional<?> findById(ObjectId id){
        return userRepository.findById(id);
    }
    public User findByUserName(String username){
       return userRepository.findByUserName(username);
    }

    public void saveUser(User user){

        userRepository.save(user); // No role reset, no password encoding
    }

    public void deleteById(ObjectId id){
        userRepository.deleteById(id);
    }




}
