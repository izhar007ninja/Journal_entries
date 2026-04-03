package net.engineeringdigest.journalApp.controller;


import io.jsonwebtoken.Jwt;
import lombok.extern.slf4j.Slf4j;
import net.engineeringdigest.journalApp.entity.User;
import net.engineeringdigest.journalApp.repository.UserRepository;
import net.engineeringdigest.journalApp.service.UserDetailsServiceImpl;
import net.engineeringdigest.journalApp.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth/google")
@Slf4j
public class GoogleAuthController {
    @Value("${client.id}")
    private String clientId;

    @Value("${client.secret}")
    private String clientSecret;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;


    @GetMapping("/callback")
    public ResponseEntity<?> handleGoogleCallback(@RequestParam String code){

        try{
            String tokenEndPoint = "https://oauth2.googleapis.com/token";
            MultiValueMap<String,String> params = new LinkedMultiValueMap<>();

            params.add("code",code);
            params.add("client_id",clientId);
            params.add("client_secret",clientSecret);
            params.add("redirect_uri","https://developers.google.com/oauthplayground");
            params.add("grant_type","authorization_code");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            HttpEntity<MultiValueMap<String,String>> request = new HttpEntity<>(params,headers);

            ResponseEntity<Map> responseToken = restTemplate.postForEntity(tokenEndPoint,request,Map.class);

            String idToken = (String) responseToken.getBody().get("id_token");

            String userInfoUrl = "https://oauth2.googleapis.com/tokeninfo?id_token="+idToken;

            ResponseEntity<Map> userInfoResponse = restTemplate.getForEntity(userInfoUrl,Map.class);

            if(userInfoResponse.getStatusCode()== HttpStatus.OK) {
                Map<String, Object> userInfo = userInfoResponse.getBody();
                String email = (String) userInfo.get("email");
                UserDetails userDetails = null;
                try {
                    userDetails = userDetailsService.loadUserByUsername(email);
                } catch (Exception e) {
                    User user = new User();
                    user.setEmail(email);
                    user.setUserName(email);
                    user.setPassword(passwordEncoder.encode("hello"));
                    user.setRoles(Arrays.asList("USER"));
                    userRepository.save(user);

                }
                String token = jwtUtil.generateToken(email);
                return new ResponseEntity<>(token, HttpStatus.OK);

            }

            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);

        }
        catch (Exception e){
            log.error("fishy in google auth ");
            return new ResponseEntity<>("bad "+e.getMessage(),HttpStatus.BAD_REQUEST);
        }

    }

}