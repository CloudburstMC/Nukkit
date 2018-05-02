package com.nukkitx.api.entity.component;

import javax.annotation.Nonnegative;

public interface Ageable extends EntityComponent {

    /**
     * Gets the age of this animal.
     *
     * @return Age
     */
    @Nonnegative
    int getAge();

    /**
     * Sets the age of this animal.
     *
     * @param age New age
     */
    void setAge(@Nonnegative int age);

    /**
     * Returns whether the animal is a baby
     *
     * @return baby
     */
    boolean isBaby();

    /**
     * Sets the age of the animal to an adult
     */
    void setAdult();

    /**
     * Returns true if the animal is an adult.
     *
     * @return return true if the animal is an adult
     */
    boolean isAdult();
}
