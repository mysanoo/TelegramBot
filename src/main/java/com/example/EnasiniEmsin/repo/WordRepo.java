package com.example.EnasiniEmsin.repo;

import com.example.EnasiniEmsin.entity.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WordRepo extends JpaRepository<Word, Integer> {
}
