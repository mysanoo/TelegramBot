package com.example.EnasiniEmsin.repo;

import com.example.EnasiniEmsin.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
}
