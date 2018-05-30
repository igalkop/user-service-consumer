package com.example.userserviceconsumer.pact;

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
import static com.example.userserviceconsumer.pact.PactConstants.USER_SERVICE_DELETE_USER_CONSUMER_HTTP;
import static com.example.userserviceconsumer.pact.PactConstants.USER_SERVICE_PROVIDER_HTTP;
import static org.assertj.core.api.Java6Assertions.assertThat;

public class TestUserServiceDelete {


    @Rule
    public PactProviderRuleMk2 mockProvider
            = new PactProviderRuleMk2(USER_SERVICE_PROVIDER_HTTP, "localhost", 9898, this);

    @Pact(provider = USER_SERVICE_PROVIDER_HTTP, consumer = USER_SERVICE_DELETE_USER_CONSUMER_HTTP)
    public RequestResponsePact createPact(PactDslWithProvider builder) {

        return builder
                .given("user 1111 already exists")
                .uponReceiving("create user request")
                .path("/user/1111")
                .method("DELETE")
                .willRespondWith()
                .status(HttpStatus.NO_CONTENT.value())
                .given("user 9999 does NOT  exists")
                .uponReceiving("create user request")
                .path("/user/9999")
                .method("DELETE")
                .willRespondWith()
                .status(HttpStatus.NO_CONTENT.value())
                .toPact();

    }

    @Test
    @PactVerification(USER_SERVICE_PROVIDER_HTTP)
    public void testDeleteUser() {
        System.out.println("mockProvider.getUrl() = " + mockProvider.getUrl());

        CommunicationUtils communicationUtils = new CommunicationUtils(mockProvider.getUrl() + "/user");
        String userIdToDelete1 = "1111";
        ResponseEntity<HttpStatus> httpStatusResponseEntity = communicationUtils.deleteUser(userIdToDelete1);
        assertThat(httpStatusResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);


        String userIdToDelete2 = "9999";
        ResponseEntity<HttpStatus> httpStatusResponseEntity2 = communicationUtils.deleteUser(userIdToDelete2);
        assertThat(httpStatusResponseEntity2.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}
