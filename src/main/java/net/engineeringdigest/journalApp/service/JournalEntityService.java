package net.engineeringdigest.journalApp.service;
import java.time.LocalDateTime;
import java.util.*;

import net.engineeringdigest.journalApp.entity.JournalEntity;
import net.engineeringdigest.journalApp.entity.User;
import net.engineeringdigest.journalApp.repository.JournalEntryRepository;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class JournalEntityService {

    private static final Logger log = LoggerFactory.getLogger(JournalEntityService.class);
    @Autowired
    private JournalEntryRepository journalEntryRepository;
    @Autowired
    private UserService userService;


    public List<JournalEntity> getAll(){
        return journalEntryRepository.findAll();
    }

    @Transactional
    public void saveAll(JournalEntity journalEntity,String userName){
        User user = userService.findByUserName(userName);
        journalEntity.setDate(LocalDateTime.now());
        JournalEntity je = journalEntryRepository.save(journalEntity);
        user.getUserEntries().add(je);
        userService.saveUser(user);
    }
    public void saveAll(JournalEntity journalEntity){
        journalEntryRepository.save(journalEntity);
    }

    public Optional<JournalEntity> findById(ObjectId id){
        return journalEntryRepository.findById(id);
    }
    @Transactional
    public void deleteById(ObjectId id, String userName){
       try {
           User user = userService.findByUserName(userName);
           boolean removed = user.getUserEntries().removeIf(x->x.getId().equals(id));
           if(removed){
               userService.saveUser(user);
               journalEntryRepository.deleteById(id);
           }

       }
       catch (Exception e){
           System.out.println(e);
       }
    }

}
