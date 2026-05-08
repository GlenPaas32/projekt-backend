package ee.gpaas.projekt.dto;

import lombok.Getter;

@Getter
public class AthleteSummary {

    private final Long id;
    private final String name;
    private final String country;
    private final Long totalPoints;

    public AthleteSummary(Long id, String name, String country, Long totalPoints) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.totalPoints = totalPoints;
    }
}
