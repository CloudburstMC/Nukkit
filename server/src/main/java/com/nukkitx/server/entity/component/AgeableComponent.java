package com.nukkitx.server.entity.component;

import com.nukkitx.api.entity.component.Ageable;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nonnegative;
@RequiredArgsConstructor
public class AgeableComponent implements Ageable {
    private final int adultAge;
    private boolean stale;
    private int age;

    @Override
    @Nonnegative
    public int getAge() {
        return age;
    }

    @Override
    public void setAge(@Nonnegative int age) {
        stale = true;
        this.age = age;
    }

    @Override
    public boolean isBaby() {
        return age < adultAge;
    }

    @Override
    public void setAdult() {
        stale = true;
        age = adultAge;
    }

    @Override
    public boolean isAdult() {
        return age >= adultAge;
    }
}
