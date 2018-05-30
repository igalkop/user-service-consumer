package com.example.userserviceconsumer.pact;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactProviderRuleMk2;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.RequestResponsePact;
import com.example.userserviceconsumer.domain.User;
import com.example.userserviceconsumer.utils.CommunicationUtils;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.util.HashMap;
import java.util.Map;

import static com.example.userserviceconsumer.pact.PactConstants.REGEX_NON_EMPTY_STRING;
import static com.example.userserviceconsumer.pact.PactConstants.USER_SERVICE_GET_USER_CONSUMER_HTTP;
import static com.example.userserviceconsumer.pact.PactConstants.USER_SERVICE_PROVIDER_HTTP;
import static org.assertj.core.api.Java6Assertions.assertThat;

public class TestUserServiceGet {


    @Rule
    public PactProviderRuleMk2 mockProvider
            = new PactProviderRuleMk2(USER_SERVICE_PROVIDER_HTTP, "localhost", 9898, this);


    @Pact(provider = USER_SERVICE_PROVIDER_HTTP, consumer = USER_SERVICE_GET_USER_CONSUMER_HTTP)
    public RequestResponsePact createPact(PactDslWithProvider builder) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");


        PactDslJsonBody body = new PactDslJsonBody()
                .integerType("id", 1111)
                .stringMatcher("firstName",REGEX_NON_EMPTY_STRING,  "first")
                .stringMatcher("lastName", REGEX_NON_EMPTY_STRING, "last");

        return builder
                .given("user 1111 already exists")
                .uponReceiving("get user request")
                .path("/user/1111")
                .method("GET")
                .willRespondWith()
                .status(200)
                .headers(headers)
                .body(body)
                .given("user 9999 does NOT  exists")
                .uponReceiving("get user request")
                .path("/user/9999")
                .method("GET")
                .willRespondWith()
                .status(404)
                .headers(headers)
//                .body("")
                .toPact();

    }

    @Test
    @PactVerification(USER_SERVICE_PROVIDER_HTTP)
    public void testCreateUser() {


        User user = new User(1111, "first", "last");
        CommunicationUtils communicationUtils = new CommunicationUtils(mockProvider.getUrl() + "/user/" );
        ResponseEntity<User> responoseUser1234 = communicationUtils.getUser("1111");
        assertThat(responoseUser1234.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responoseUser1234.getBody()).isEqualTo(user);


        String userNotExistsId = "9999";
        try {
            communicationUtils.getUser(userNotExistsId);
        } catch (HttpClientErrorException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }

    }
}
