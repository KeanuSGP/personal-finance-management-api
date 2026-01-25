package com.keanusantos.personalfinancemanager.config;

import com.keanusantos.personalfinancemanager.domain.user.User;
import com.keanusantos.personalfinancemanager.domain.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Arrays;

@Configuration
@Profile("test")
public class TestConfig implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;


    @Override
    public void run(String... args) throws Exception {
        User user1 = new User(null, "Keanu", "keanu@test.com", "123456");
        User user2 = new User(null, "Santos", "Santos@test.com", "123456");

        userRepository.saveAll(Arrays.asList(user1, user2));
    }
}
