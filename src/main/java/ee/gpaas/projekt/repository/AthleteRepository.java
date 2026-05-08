package ee.gpaas.projekt.repository;

import ee.gpaas.projekt.dto.AthleteSummary;
import ee.gpaas.projekt.entity.Athlete;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AthleteRepository extends JpaRepository<Athlete, Long> {

    @Query(
            value = """
                    SELECT new ee.gpaas.projekt.dto.AthleteSummary(
                        a.id,
                        a.name,
                        a.country,
                        COALESCE(SUM(r.points), 0)
                    )
                    FROM Athlete a
                    LEFT JOIN a.results r
                    WHERE (:country IS NULL OR LOWER(a.country) = LOWER(:country))
                    GROUP BY a.id, a.name, a.country
                    ORDER BY COALESCE(SUM(r.points), 0) ASC, a.id ASC
                    """,
            countQuery = """
                    SELECT COUNT(a)
                    FROM Athlete a
                    WHERE (:country IS NULL OR LOWER(a.country) = LOWER(:country))
                    """
    )
    Page<AthleteSummary> findSummariesOrderByTotalPointsAsc(@Param("country") String country, Pageable pageable);

    @Query(
            value = """
                    SELECT new ee.gpaas.projekt.dto.AthleteSummary(
                        a.id,
                        a.name,
                        a.country,
                        COALESCE(SUM(r.points), 0)
                    )
                    FROM Athlete a
                    LEFT JOIN a.results r
                    WHERE (:country IS NULL OR LOWER(a.country) = LOWER(:country))
                    GROUP BY a.id, a.name, a.country
                    ORDER BY COALESCE(SUM(r.points), 0) DESC, a.id ASC
                    """,
            countQuery = """
                    SELECT COUNT(a)
                    FROM Athlete a
                    WHERE (:country IS NULL OR LOWER(a.country) = LOWER(:country))
                    """
    )
    Page<AthleteSummary> findSummariesOrderByTotalPointsDesc(@Param("country") String country, Pageable pageable);
}
