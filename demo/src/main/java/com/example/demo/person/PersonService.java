package com.example.demo.person;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonService {

    private final PersonRepository repo;

    public PersonService(PersonRepository repo) {
        this.repo = repo;
    }

    public List<Person> getAll() {
        return repo.findAll();
    }

    public Person getById(Long id) {
        return repo.findById(id).orElseThrow();
    }

    public Person create(Person person) {
        return repo.save(person);
    }

    public Person update(Long id, Person updated) {
        Person p = getById(id);
        p.setName(updated.getName());
        p.setAge(updated.getAge());
        return repo.save(p);
    }

    public Person patch(Long id, Person partial) {
        Person p = getById(id);
        if (partial.getName() != null) p.setName(partial.getName());
        if (partial.getAge() != 0) p.setAge(partial.getAge());
        return repo.save(p);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    public List<Person> search(String name) {
        return repo.findByNameContainingIgnoreCase(name);
    }
}
