package ee.gpaas.projekt.controller;

import ee.gpaas.projekt.entity.Discipline;
import ee.gpaas.projekt.repository.DisciplineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/disciplines")
public class DisciplineController {

    @Autowired
    private DisciplineRepository disciplineRepository;

    @GetMapping
    public List<Discipline> getAll() {
        return disciplineRepository.findAll();
    }

    @PostMapping
    public Discipline create(@RequestBody Discipline discipline) {
        return disciplineRepository.save(discipline);
    }
}