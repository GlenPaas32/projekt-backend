package ee.gpaas.projekt.repository;

import ee.gpaas.projekt.entity.Discipline;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface DisciplineRepository extends JpaRepository<Discipline, Long> {
    Optional<Discipline> findByNameIgnoreCase(String name);
}
