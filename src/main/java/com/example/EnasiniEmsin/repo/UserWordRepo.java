package com.example.EnasiniEmsin.repo;

import com.example.EnasiniEmsin.entity.UserWord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserWordRepo extends JpaRepository<UserWord, Long> {


}
