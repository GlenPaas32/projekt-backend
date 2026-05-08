package ee.gpaas.projekt.service;

import ee.gpaas.projekt.entity.Discipline;
import ee.gpaas.projekt.entity.Result;
import ee.gpaas.projekt.repository.DisciplineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class PointsCalculatorService {

    @Autowired
    private DisciplineRepository disciplineRepository;

    public int calculatePoints(Result result) {
        String disciplineName = result.getDiscipline();
        Discipline discipline = disciplineRepository.findByNameIgnoreCase(disciplineName)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Spordiala ei leitud: " + disciplineName
                ));

        return (int) (result.getResultValue() * discipline.getPointsFactor());
    }
}