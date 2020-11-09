package fr.enssat.vehiclesrental.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Set;

/**
 * Represent a car hold in the database.
 */
@Entity
@Table(name = "Motorbike")
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@PrimaryKeyJoinColumn(name = "id")
public class Motorbike extends Vehicule implements Serializable {
    /**
     * Number of kilometer that a motorbike has been used
     */
    @Column(nullable = false)
    @NonNull
    private int km;

    /**
     * Power of the motorbike
     */
    @Column(nullable = false)
    @NonNull
    private int horsePower;

    /**
     * Constructor of a motorbike
     */
    @Builder
    private Motorbike(long id, String brand, String model, int maximumSpeed, float rentPricePerDay, int nbSeats, State state, int km, int horsePower, Set<Booking> bookings) {
        super(id, brand, model, maximumSpeed, rentPricePerDay, nbSeats, state, bookings);
        this.km = km;
        this.horsePower = horsePower;
    }
}