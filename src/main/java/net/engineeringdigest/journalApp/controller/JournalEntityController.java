package net.engineeringdigest.journalApp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.engineeringdigest.journalApp.entity.JournalEntity;
import net.engineeringdigest.journalApp.entity.User;
import net.engineeringdigest.journalApp.service.JournalEntityService;
import net.engineeringdigest.journalApp.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/journal")
@Tag(name = "Journal APIs")

public class JournalEntityController {


    @Autowired
    private JournalEntityService journalEntityService;
    @Autowired
    private UserService userService;



    @GetMapping
    @Operation(summary = "get all general entries")
    public ResponseEntity<?> getAll(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUserName(authentication.getName());

        List<JournalEntity> list = user.getUserEntries();
        if(list!=null && !list.isEmpty()){
            return new ResponseEntity<>(list,HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    @PostMapping
    public ResponseEntity<JournalEntity> createEntry(@RequestBody JournalEntity myEntity){
       try {
           Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
           User currUser = userService.findByUserName(authentication.getName());
           journalEntityService.saveAll(myEntity,authentication.getName());
           return new ResponseEntity<>(myEntity,HttpStatus.CREATED);
       }
       catch (Exception e){
           return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
       }


    }

    @GetMapping("id/{myId}") 
    public ResponseEntity<?> getEntityById(@PathVariable String myId){
        ObjectId objectId = new ObjectId(myId);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currUser = userService.findByUserName(authentication.getName());

        List<JournalEntity> collected = currUser.getUserEntries().stream().filter(x->x.getId().equals(objectId)).collect(Collectors.toList());
        if(!collected.isEmpty()){
            Optional<JournalEntity> fetched = journalEntityService.findById(objectId);
            if(fetched.isPresent()){
                return new ResponseEntity<>(fetched.get(),HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/id/{myId}")
    public ResponseEntity<?> deleteEntityById(@PathVariable ObjectId myId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        journalEntityService.deleteById(myId,authentication.getName());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/id/{myId}")
    public ResponseEntity<?> updateEntityById(@PathVariable ObjectId myId,@RequestBody JournalEntity myEntity){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currUser = userService.findByUserName(authentication.getName());
        List<JournalEntity> collected = currUser.getUserEntries().stream().filter(x->x.getId().equals(myId)).collect(Collectors.toList());


        if(!collected.isEmpty()){
            Optional<JournalEntity> old = journalEntityService.findById(myId);
            if(old.isPresent()){
                JournalEntity oldEntity = old.get();
                oldEntity.setTitle(myEntity.getTitle()!=null && !myEntity.getTitle().equals("") ? myEntity.getTitle() : oldEntity.getTitle());
                oldEntity.setContent(myEntity.getContent()!=null && !myEntity.getContent().equals("") ? myEntity.getContent(): oldEntity.getContent());
            journalEntityService.saveAll(oldEntity);
            return new ResponseEntity<>(oldEntity,HttpStatus.OK);
            }
        }


        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    }

//    @GetMapping
//    public ResponseEntity<?> authenticationCheck(){
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        return new ResponseEntity<>("authenticated"+authentication.getName(),HttpStatus.OK);
//    }





}
