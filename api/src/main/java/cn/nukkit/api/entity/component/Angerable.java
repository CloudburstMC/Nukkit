package cn.nukkit.api.entity.component;

public interface Angerable extends EntityComponent {

    /**
     * Checks if this entity is angry
     *
     * @return Anger true if angry
     */
    boolean isAngry();

    /**
     * Sets the anger of this entity.
     * <p>
     * An angry entity can not be fed or tamed, and will actively look for
     * targets to attack.
     *
     * @param angry true if angry
     */
    void setAngry(boolean angry);
}
