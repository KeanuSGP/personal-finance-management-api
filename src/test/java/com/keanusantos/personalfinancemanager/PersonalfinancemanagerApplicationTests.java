package com.keanusantos.personalfinancemanager;

import com.keanusantos.personalfinancemanager.domain.user.User;
import com.keanusantos.personalfinancemanager.domain.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

@SpringBootTest
@Profile("test")
class PersonalfinancemanagerApplicationTests {

    @Autowired
    private UserRepository userRepository;

    public void run(String... args) {

        User user1 = new User(null, "Keanu", "keanu@teste.com", "123456");

        userRepository.save(user1);
    }

}
