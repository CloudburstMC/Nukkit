package cn.nukkit.api.entity.item;

import cn.nukkit.api.entity.Entity;

/**
 * @author CreeperFace
 */
public interface ExperienceOrb extends Entity {

    /**
     * Gets how much experience is contained within this orb
     *
     * @return Amount of experience
     */
    int getExperience();

    /**
     * Sets how much experience is contained within this orb
     *
     * @param value Amount of experience
     */
    void setExperience(int value);
}
