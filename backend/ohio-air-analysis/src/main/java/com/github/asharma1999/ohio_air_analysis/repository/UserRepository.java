package com.github.asharma1999.ohio_air_analysis.repository;

import com.github.asharma1999.ohio_air_analysis.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
