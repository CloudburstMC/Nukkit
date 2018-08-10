package com.nukkitx.api.metadata.block;

/**
 * @author CreeperFace
 */
public class NetherWart extends Growable {

    public NetherWart(int age) {
        super(age);
    }

    @Override
    public boolean isFullyGrown() {
        return this.age >= 3;
    }
}
