package com.workintech.s17d2.rest;

import com.workintech.s17d2.model.*;
import com.workintech.s17d2.tax.Taxable;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/developers")
public class DeveloperController {

    public Map<Integer, Developer> developers;

    private final Taxable taxable;

    @Autowired
    public DeveloperController(Taxable taxable) {
        this.taxable = taxable;
    }

    @PostConstruct
    public void init() {
        developers = new HashMap<>();
    }

    @GetMapping
    public List<Developer> getAll() {
        return new ArrayList<>(developers.values());
    }

    @GetMapping("/{id}")
    public Developer getById(@PathVariable int id) {
        return developers.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Developer save(@RequestBody Developer developer) {
        Developer created = createDeveloper(developer);
        developers.put(created.getId(), created);
        return created;
    }

    @PutMapping("/{id}")
    public Developer update(@PathVariable int id, @RequestBody Developer developer) {
        developer.setId(id);
        Developer updated = createDeveloper(developer);
        developers.put(id, updated);
        return updated;
    }

    @DeleteMapping("/{id}")
    public Developer delete(@PathVariable int id) {
        return developers.remove(id);
    }

    private Developer createDeveloper(Developer developer) {
        double salary = developer.getSalary();
        Experience experience = developer.getExperience() == null ? Experience.JUNIOR : developer.getExperience();
        switch (experience) {
            case MID:
                return new MidDeveloper(developer.getId(), developer.getName(),
                        salary - salary * taxable.getMiddleTaxRate() / 100);
            case SENIOR:
                return new SeniorDeveloper(developer.getId(), developer.getName(),
                        salary - salary * taxable.getUpperTaxRate() / 100);
            case JUNIOR:
            default:
                return new JuniorDeveloper(developer.getId(), developer.getName(),
                        salary - salary * taxable.getSimpleTaxRate() / 100);
        }
    }
}
