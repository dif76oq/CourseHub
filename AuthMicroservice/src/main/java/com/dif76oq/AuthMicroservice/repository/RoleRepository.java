package com.dif76oq.AuthMicroservice.repository;

import com.dif76oq.AuthMicroservice.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role findByName(String user);
}
