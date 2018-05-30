package com.example.userserviceconsumer;

import com.example.userserviceconsumer.domain.User;
import com.example.userserviceconsumer.domain.Users;
import com.example.userserviceconsumer.utils.CommunicationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;


@Component
public class CommandLineAppStartupRunner implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(CommandLineAppStartupRunner.class);
    @Override
    public void run(String...args) throws Exception {
        logger.info("Application started with command-line arguments: {} .", Arrays.toString(args));

        CommunicationUtils communicationUtils = new CommunicationUtils("http://localhost:1234/user/");
        System.out.println("creating user 123");
        try {
            communicationUtils.createUser(new User(123, "aaa", "bbb"));
        } catch (Exception e) {
            System.out.println("failed to create user 123");
        }
        System.out.println("creating user 456");
        try {
            communicationUtils.createUser(new User(456, "aaa", "bbb"));
        } catch (Exception e) {
            System.out.println("failed to create user 456");
        }


        System.out.println("creating user 789");
        try {
            communicationUtils.createUser(new User(789, "aaa", "bbb"));
        } catch (Exception e) {
            System.out.println("failed to create user 789");
        }

        System.out.println("get user 123:");
        ResponseEntity<User> user123 = communicationUtils.getUser("123");
        System.out.println("user123 = " + user123);

        System.out.println("all users:");
        ResponseEntity<Users> allUsersResponse = communicationUtils.getAllUsers();
        allUsersResponse.getBody().getUserList().forEach(user -> System.out.println("user = " + user));


    }
}
