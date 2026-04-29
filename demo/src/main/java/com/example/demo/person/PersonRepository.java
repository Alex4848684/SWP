package com.example.demo.person;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PersonRepository extends JpaRepository<Person, Long> {

    // einfache Suche nach Namen
    List<Person> findByNameContainingIgnoreCase(String name);
}
