package com.example.userserviceconsumer.pact.user;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactProviderRuleMk2;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.RequestResponsePact;
import com.example.userserviceconsumer.domain.User;
import com.example.userserviceconsumer.domain.UserCreatedResponse;
import com.example.userserviceconsumer.utils.CommunicationUtils;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import java.util.HashMap;
import java.util.Map;

import static com.example.userserviceconsumer.pact.PactConstants.USER_SERVICE_CREATE_USER_CONSUMER_HTTP;
import static com.example.userserviceconsumer.pact.PactConstants.USER_SERVICE_PROVIDER_HTTP;
import static org.assertj.core.api.Java6Assertions.assertThat;

public class TestUserServiceCreate {


    @Rule
    public PactProviderRuleMk2 mockProvider
            = new PactProviderRuleMk2(USER_SERVICE_PROVIDER_HTTP, "localhost", 9898, this);

    String keyOfNewUser = "352c7587-4b29-43ba-955a-99a72afefb21";//UUID.randomUUID();

    @Pact(provider = USER_SERVICE_PROVIDER_HTTP, consumer = USER_SERVICE_CREATE_USER_CONSUMER_HTTP)
    public RequestResponsePact createPact(PactDslWithProvider builder) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        String userBody = "{\"id\": 1234, \"firstName\": \"first\", \"lastName\": \"last\"}";
        String userBody2 = "{\"id\": 9876, \"firstName\": \"first\", \"lastName\": \"last\"}";

        DslPart responseCreateUser = new PactDslJsonBody()
                .uuid("newUserKey", keyOfNewUser).asBody();

        return builder
                .given("")
                .uponReceiving("create user request")
                .path("/user")
                .headers(headers)
                .body(userBody)
                .method("POST")
                .willRespondWith()
                .status(HttpStatus.CREATED.value())
                .body(responseCreateUser)
                .given("user 9876 already exists")
                .uponReceiving("create user request when user already exists")
                .path("/user")
                .headers(headers)
                .body(userBody2)
                .method("POST")
                .willRespondWith()
                .status(HttpStatus.BAD_REQUEST.value())
                .toPact();

    }

    @Test
    @PactVerification(USER_SERVICE_PROVIDER_HTTP)
    public void testCreateUser() {
        System.out.println("mockProvider.getUrl() = " + mockProvider.getUrl());

        CommunicationUtils communicationUtils = new CommunicationUtils(mockProvider.getUrl() + "/user");
        User user = new User(1234, "first", "last");
        ResponseEntity<UserCreatedResponse> userCreatedResponseResponseEntity = communicationUtils.createUser(user);
        assertThat(userCreatedResponseResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(userCreatedResponseResponseEntity.getBody().getNewUserKey()).isEqualTo(keyOfNewUser);

        try {
            User userExisting = new User(9876, "first", "last");
            communicationUtils.createUser(userExisting);
        } catch (HttpClientErrorException ex) {
            System.out.println("error create user 9876");
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }
}
