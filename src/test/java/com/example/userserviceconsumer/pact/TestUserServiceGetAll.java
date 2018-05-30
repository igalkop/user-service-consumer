package com.example.userserviceconsumer.pact;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactProviderRuleMk2;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.RequestResponsePact;
import com.example.userserviceconsumer.domain.User;
import com.example.userserviceconsumer.domain.Users;
import com.example.userserviceconsumer.utils.CommunicationUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.userserviceconsumer.pact.PactConstants.REGEX_NON_EMPTY_STRING;
import static com.example.userserviceconsumer.pact.PactConstants.USER_SERVICE_GET_ALL_USERS_CONSUMER_HTTP;
import static com.example.userserviceconsumer.pact.PactConstants.USER_SERVICE_PROVIDER_HTTP;
import static org.assertj.core.api.Java6Assertions.assertThat;

public class TestUserServiceGetAll {


    @Rule
    public PactProviderRuleMk2 mockProvider
            = new PactProviderRuleMk2(USER_SERVICE_PROVIDER_HTTP, "localhost", 9898, this);

    User user1 = new User(1111, "first", "last");

    @Pact(provider = USER_SERVICE_PROVIDER_HTTP, consumer = USER_SERVICE_GET_ALL_USERS_CONSUMER_HTTP)
    public RequestResponsePact createPact(PactDslWithProvider builder) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        DslPart body = new PactDslJsonBody()
                .minArrayLike("userList", 1)
                .integerType("id", 1111)
                .stringMatcher("firstName", REGEX_NON_EMPTY_STRING,  "first")
                .stringMatcher("lastName", REGEX_NON_EMPTY_STRING, "last")
                .closeObject()
                .closeArray();

        return builder
                .given("user 1111 already exists")
                .uponReceiving("get user request")
                .path("/user/")
                .method("GET")
                .willRespondWith()
                .status(200)
                .headers(headers)
                .body(body)
                .toPact();

    }

    @Test
    @PactVerification(USER_SERVICE_PROVIDER_HTTP)
    @SneakyThrows
    public void testGetAllUsers() {


        CommunicationUtils communicationUtils = new CommunicationUtils(mockProvider.getUrl() + "/user/" );
        ResponseEntity<Users> allUsersResponse = communicationUtils.getAllUsers();
        assertThat(allUsersResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(allUsersResponse.getBody().getUserList().get(0)).isEqualTo(user1);

    }
}
