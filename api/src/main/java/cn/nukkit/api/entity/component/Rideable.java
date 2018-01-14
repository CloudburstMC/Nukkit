package cn.nukkit.api.entity.component;

import cn.nukkit.api.entity.Entity;

import javax.annotation.Nonnull;
import java.util.List;

public interface Rideable {
    /**
     * Gets a list of passengers of this vehicle.
     * <p>
     * The returned list will not be directly linked to the entity's current
     * passengers, and no guarantees are made as to its mutability.
     *
     * @return list of entities corresponding to current passengers.
     */
    List<Entity> getPassengers();

    /**
     * Add a passenger to the vehicle.
     *
     * @param passenger The passenger to add
     * @return false if it could not be done for whatever reason
     */
    boolean addPassenger(@Nonnull Entity passenger);

    /**
     * Remove a passenger from the vehicle.
     *
     * @param passenger The passenger to remove
     * @return false if it could not be done for whatever reason
     */
    boolean removePassenger(@Nonnull Entity passenger);

    /**
     * Check if a vehicle has passengers.
     *
     * @return True if the vehicle has no passengers.
     */
    boolean isEmpty();

    /**
     * Eject any passenger.
     *
     * @return True if there was a passenger.
     */
    boolean eject();
}
