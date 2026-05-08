package ee.gpaas.projekt.repository;

import ee.gpaas.projekt.entity.Result;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResultRepository extends JpaRepository<Result, Long> {

    List<Result> findByAthleteId(Long athleteId);
}
