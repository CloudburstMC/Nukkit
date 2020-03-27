package cn.nukkit.entity.misc;

import cn.nukkit.entity.Entity;
import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

import javax.annotation.Nonnegative;

public interface ExperienceOrb extends Entity {

    /**
     * Split sizes used for dropping experience orbs.
     */
    ImmutableList<Integer> ORB_SPLIT_SIZES = ImmutableList.<Integer>builder()
            .add(2477, 1237, 617, 307, 149, 73, 37, 17, 7, 3, 1)
            .build(); //This is indexed biggest to smallest so that we can return as soon as we found the biggest value.

    /**
     * Returns the largest size of normal XP orb that will be spawned for the specified amount of XP. Used to split XP
     * up into multiple orbs when an amount of XP is dropped.
     */
    static int getMaxOrbSize(int amount) {
        for (int split : ORB_SPLIT_SIZES) {
            if (amount >= split) {
                return split;
            }
        }

        return 1;
    }

    /**
     * Splits the specified amount of XP into an array of acceptable XP orb sizes.
     */
    static IntList splitIntoOrbSizes(int amount) {
        IntList result = new IntArrayList();

        while (amount > 0) {
            int size = getMaxOrbSize(amount);
            result.add(size);
            amount -= size;
        }

        return result;
    }

    @Nonnegative
    int getExperience();

    void setExperience(@Nonnegative int experience);

    @Nonnegative
    int getPickupDelay();

    void setPickupDelay(@Nonnegative int pickupDelay);
}
