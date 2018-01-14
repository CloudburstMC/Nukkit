package cn.nukkit.api.entity.component;

public interface Sittable extends EntityComponent {

    /**
     * Checks if this animal is sitting
     *
     * @return true if sitting
     */
    boolean isSitting();

    /**
     * Sets if this animal is sitting. Will remove any path that the animal
     * was following beforehand.
     *
     * @param sitting true if sitting
     */
    void setSitting(boolean sitting);
}
