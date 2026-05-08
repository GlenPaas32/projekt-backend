package ee.gpaas.projekt.controller;

import ee.gpaas.projekt.dto.AthleteSummary;
import ee.gpaas.projekt.entity.Athlete;
import ee.gpaas.projekt.entity.Result;
import ee.gpaas.projekt.service.AthleteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/athletes")
public class AthleteController {

    @Autowired
    private AthleteService athleteService;
    @GetMapping
    public ResponseEntity<Page<AthleteSummary>> getAthletes(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String country,
            @RequestParam(required = false, defaultValue = "desc") String sortDirection) {
        Page<AthleteSummary> athletes = athleteService.getAthletes(page, size, country, sortDirection);
        return ResponseEntity.ok(athletes);
    }

    // Loo uus sportlane
    @PostMapping
    public ResponseEntity<Athlete> createAthlete(@RequestBody Athlete athlete) {
        Athlete saved = athleteService.createAthlete(athlete);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // Lisa sportlasele tulemus
    @PostMapping("/{id}/results")
    public ResponseEntity<Result> addResult(@PathVariable("id") Long athleteId,
                                            @RequestBody Result result) {
        Result savedResult = athleteService.addResult(athleteId, result);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedResult);
    }

    // GET sportlase info koos tulemuste listiga
    @GetMapping("/{id}")
    public ResponseEntity<Athlete> getAthlete(@PathVariable Long id) {
        Athlete athlete = athleteService.getAthleteById(id);
        return ResponseEntity.ok(athlete);
    }

    // GET kogupunktid
    @GetMapping("/{id}/points")
    public ResponseEntity<Map<String,Object>> getTotalPoints(@PathVariable Long id) {
        int total = athleteService.getTotalPoints(id);
        Athlete athlete = athleteService.getAthleteById(id);

        Map<String,Object> response = new HashMap<>();
        response.put("athleteId", athlete.getId());
        response.put("name", athlete.getName());
        response.put("totalPoints", total);

        return ResponseEntity.ok(response);
    }
}
