package net.engineeringdigest.journalApp.cache;


import net.engineeringdigest.journalApp.entity.ConfigJournalApp;
import net.engineeringdigest.journalApp.repository.ConfigJournalAppRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;


@Component
public class AppCache {

    public enum Keys{
        weather_api,
        CITY,
        API_KEY

    }

    public Map<String,String> APP_CACHE;
    @Autowired
    private ConfigJournalAppRepository configJournalAppRepository;


    @PostConstruct
    public void init(){
        APP_CACHE = new HashMap<>();

        List<ConfigJournalApp> all = configJournalAppRepository.findAll();

        for (ConfigJournalApp configJournalApp:all){
            APP_CACHE.put(configJournalApp.getKey(), configJournalApp.getValue());
        }

    }


}
