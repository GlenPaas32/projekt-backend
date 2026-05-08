package ee.gpaas.projekt.entity;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
public class Discipline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private double pointsFactor; // Punktide arvutuse koefitsient

    // Getterid ja setterid
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPointsFactor() { return pointsFactor; }
    public void setPointsFactor(double pointsFactor) { this.pointsFactor = pointsFactor; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Discipline)) return false;
        Discipline that = (Discipline) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
