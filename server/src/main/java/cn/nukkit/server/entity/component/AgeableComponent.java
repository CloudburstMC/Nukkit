package cn.nukkit.server.entity.component;

import cn.nukkit.api.entity.component.Ageable;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nonnegative;
@RequiredArgsConstructor
public class AgeableComponent implements Ageable {
    private final int babyAge;
    private final int adultAge;
    private boolean stale;
    private boolean breedable;
    private boolean ageLock;
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
    public boolean getAgeLock() {
        return ageLock;
    }

    @Override
    public void setAgeLock(boolean lock) {
        this.ageLock = lock;
    }

    @Override
    public void setBaby() {
        stale = true;
        age = babyAge;
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

    @Override
    public boolean canBreed() {
        return breedable;
    }

    @Override
    public void setBreed(boolean breed) {
        stale = true;
        this.breedable = breed;
    }
}
