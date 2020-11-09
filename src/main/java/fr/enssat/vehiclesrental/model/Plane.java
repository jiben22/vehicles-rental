package fr.enssat.vehiclesrental.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Set;

/**
 * Represent a plane hold in the database.
 */
@Entity
@Table(name = "Plane")
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@PrimaryKeyJoinColumn(name = "id")
public class Plane extends Vehicule implements Serializable {
    /**
     * Number of hours when the plane has been used
     */
    @Column(nullable = false)
    @NonNull
    private int nbHours;

    /**
     * Number of engines used by the plane
     */
    @Column(nullable = false)
    @NonNull
    private int nbEngines;

    /**
     * Constructor of an a plane
     */
    @Builder
    private Plane(long id, String brand,String model, int maximumSpeed, float rentPricePerDay, int nbSeats, State state, Set<Booking> bookings, int nbHours, int nbEngines) {
        super(id, brand, model, maximumSpeed, rentPricePerDay, nbSeats, state, bookings);
        this.nbHours = nbHours;
        this.nbEngines = nbEngines;
    }
}