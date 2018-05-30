package com.example.userserviceconsumer.utils;

import com.example.userserviceconsumer.domain.User;
import com.example.userserviceconsumer.domain.UserCreatedResponse;
import com.example.userserviceconsumer.domain.Users;
import lombok.Data;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;

@Data
public class CommunicationUtils {


    private RestTemplate restTemplate = new RestTemplate();
    private String url;

    public CommunicationUtils(String url) {
        this.url = url;
    }

    public ResponseEntity<UserCreatedResponse> createUser(User user) {
        return restTemplate.postForEntity(url, user, UserCreatedResponse.class);
    }

    public ResponseEntity<User> getUser(String userId) {
        return restTemplate.getForEntity(url + "/" + userId, User.class);
    }

    public ResponseEntity<Users> getAllUsers() {

        ResponseEntity<Users> getResponse =
                restTemplate.exchange(url,
                        HttpMethod.GET, null, new ParameterizedTypeReference<Users>() {});
        return getResponse;
    }

    public ResponseEntity<HttpStatus> deleteUser(String userId) {
        ResponseEntity<HttpStatus> deleteResponse = restTemplate.exchange(url + "/" + userId, HttpMethod.DELETE, null, HttpStatus.class);
        return deleteResponse;
    }
}
