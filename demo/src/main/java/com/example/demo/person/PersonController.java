package com.example.demo.person;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/persons")
public class PersonController {

    private final PersonService service;

    public PersonController(PersonService service) {
        this.service = service;
    }

    // GET alle
    @GetMapping
    public List<Person> getAll() {
        return service.getAll();
    }

    // GET by ID
    @GetMapping("/{id}")
    public Person getById(@PathVariable Long id) {
        return service.getById(id);
    }

    // Suche: /api/persons/search?name=abc
    @GetMapping("/search")
    public List<Person> search(@RequestParam String name) {
        return service.search(name);
    }

    // POST
    @PostMapping
    public Person create(@RequestBody Person person) {
        return service.create(person);
    }

    // PUT (komplett ersetzen)
    @PutMapping("/{id}")
    public Person update(@PathVariable Long id,
                         @RequestBody Person person) {
        return service.update(id, person);
    }

    // PATCH (teilweise)
    @PatchMapping("/{id}")
    public Person patch(@PathVariable Long id,
                        @RequestBody Person person) {
        return service.patch(id, person);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
