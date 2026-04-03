package net.engineeringdigest.journalApp.service;

import net.engineeringdigest.journalApp.api.response.WeatherResponse;
import net.engineeringdigest.journalApp.cache.AppCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherService {

    @Value("${weather.api.key}")
    private String apiKey;
  //  private static final String API = "https://api.openweathermap.org/data/2.5/weather?q=CITY&appid=API_KEY&units=metric";
    @Autowired
    private AppCache appCache;
    @Autowired
    private RedisService redisService;


    @Autowired
    private RestTemplate restTemplate;

    public WeatherResponse getWeather(String city){
        WeatherResponse response = redisService.get(city,WeatherResponse.class);
        if (response != null) {
            return response;
        }
        else {

            String finalAPi = appCache.APP_CACHE.get(AppCache.Keys.weather_api.toString()).replace(AppCache.Keys.CITY.toString(),city).replace(AppCache.Keys.API_KEY.toString(),apiKey);
            ResponseEntity<WeatherResponse> weatherResponse = restTemplate.exchange(finalAPi, HttpMethod.GET,null, WeatherResponse.class);

            redisService.set(city,weatherResponse.getBody(),300L);
            return weatherResponse.getBody();

        }


    }


}
