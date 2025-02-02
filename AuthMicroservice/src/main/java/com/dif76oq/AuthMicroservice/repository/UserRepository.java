package com.dif76oq.AuthMicroservice.repository;

import com.dif76oq.AuthMicroservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findById(int id);
    Optional<User> findByUsername(String name);

    Optional<User> findByEmail(String name);
}
