package com.github.asharma1999.ohio_air_analysis.service;

import com.github.asharma1999.ohio_air_analysis.model.User;
import com.github.asharma1999.ohio_air_analysis.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }
}
