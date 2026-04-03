package net.engineeringdigest.journalApp.scheduler;

import net.engineeringdigest.journalApp.entity.JournalEntity;
import net.engineeringdigest.journalApp.entity.User;
import net.engineeringdigest.journalApp.enums.Sentiments;
import net.engineeringdigest.journalApp.service.EmailService;
import net.engineeringdigest.journalApp.service.UserRespositoryImpl;
import org.jetbrains.annotations.Async;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class UserScheduler {

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRespositoryImpl userRespository;

    @Scheduled(cron = "0 9 * * SUN")
    public void fetchUsersAndSendSaEmail(){
     List<User> users = userRespository.getUserForSA();

     for(User user:users){
         List<JournalEntity> je = user.getUserEntries();
         List<Sentiments> filteredJournalEntity = je.stream().filter(x->x.getDate()
                 .isAfter(LocalDateTime.now().minus(7, ChronoUnit.DAYS))).map(x->x.getSentiments()).collect(Collectors.toList());
     Map<Sentiments,Integer> mp = new HashMap<>();

     for (int i = 0;i<filteredJournalEntity.size();i++){
         if (!mp.containsKey(filteredJournalEntity.get(i))){
             mp.put(filteredJournalEntity.get(i),1);
         }
         else{
             mp.put(filteredJournalEntity.get(i),mp.get(filteredJournalEntity.get(i))+1);
         }
     }

     int max = 0;
     Sentiments maxSentiment = null;

     for (Map.Entry<Sentiments,Integer> mp1 : mp.entrySet()){
         if (mp1.getValue()>max){
             max = mp1.getValue();
             maxSentiment = mp1.getKey();
         }
     }

if (maxSentiment!=null){
    emailService.mailSender(user.getEmail(),"something",maxSentiment.toString());
}

     }


    }

}
