package com.nukkitx.api.entity.misc;

import com.nukkitx.api.entity.Entity;

public interface Painting extends Entity {

    enum Type {
        ALBAN(1, 1, "Alban"),
        AZTEC(1, 1, "Aztec"),
        AZTEC2(1, 1, "Aztec2"),
        BOMB(1, 1, "Bomb"),
        KEBAB(1, 1, "Kebab"),
        PLANT(1, 1, "Plant"),
        WASTELAND(1, 1, "Wasteland"),
        GRAHAM(1, 2, "Graham"),
        WANDERER(1, 2, "Wanderer"),
        COURBET(2, 1, "Courbet"),
        CREEBET(2, 1, "Creebet"),
        POOL(2, 1, "Pool"),
        SEA(2, 1, "Sea"),
        SUNSET(2, 1, "Sunset"),
        BUST(2, 2, "Bust"),
        EARTH(2, 2, "Earth"),
        FIRE(2, 2, "Fire"),
        MATCH(2, 2, "Match"),
        SKULL_AND_ROSES(2, 2, "SkullAndRoses"),
        STAGE(2, 2, "Stage"),
        VOID(2, 2, "Void"),
        WATER(2, 2, "Water"),
        WIND(2, 2, "Wind"),
        WITHER(2, 2, "Wither"),
        FIGHTERS(4, 2, "Fighters"),
        DONKEY_KONG(4, 3, "DonkeyKong"),
        SKELETON(4, 3, "Skeleton"),
        BURNING_SKULL(4, 4, "BurningSkull"),
        PIG_SCENE(4, 4, "Pigscene"),
        POINTER(4, 4, "Pointer");

        private byte width;
        private byte height;
        private String name;

        Type(int width, int height, String name) {
            this.width = (byte) width;
            this.height = (byte) height;
            this.name = name;
        }

        public byte getHeight() {
            return height;
        }

        public byte getWidth() {
            return width;
        }

        public String getName() {
            return name;
        }
    }
}
