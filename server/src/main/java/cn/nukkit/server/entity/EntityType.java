/*
 * GNU GENERAL PUBLIC LICENSE
 * Copyright (C) 2018 NukkitX Project
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * verion 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * Contact info: info@nukkitx.com
 */

package cn.nukkit.server.entity;

public enum EntityType {
    CHICKEN(10, 0.7f, 0.4f),
    COW(11, 1.4f, 0.9f),
    PIG(12, 0.9f),
    SHEEP(13, 1.3f, 0.9f),
    WOLF(14, 0.85f, 0.6f),
    VILLAGER(15, 1.8f, 0.6f, 0.6f, 1.62f),
    MOOSHROOM(16, 1.4f, 0.9f),
    SQUID(17, 0.8f),
    RABBIT(18, 0.5f, 0.4f),
    BAT(19, 0.9f, 0.5f),
    IRON_GOLEM(20, 2.7f, 1.4f),
    SNOW_GOLEM(21, 1.9f, 0.7f),
    OCELOT(22, 0.35f, 0.3f),
    HORSE(23, 1.6f, 1.3965f),
    DONKEY(24, 1.6f, 1.3965f),
    MULE(25, 1.6f, 1.3965f),
    SKELETON_HORSE(26, 1.6f, 1.3965f),
    ZOMBIE_HORSE(27, 1.6f, 1.3965f),
    POLAR_BEAR(28, 1.4f, 1.3f),
    LLAMA(29, 1.87f, 0.9f),
    PARROT(30, 0.9f, 0.5f),
    DOLPHIN(31, 0f), //TODO
    ZOMBIE(32, 1.8f, 0.6f, 0.6f, 1.62f),
    CREEPER(33, 1.7f, 0.6f, 0.6f, 1.62f),
    SKELETON(34, 1.8f, 0.6f, 0.6f, 1.62f),
    SPIDER(35, 0.9f, 1.4f, 1.4f, 1f),
    ZOMBIE_PIGMAN(36, 1.8f, 0.6f, 0.6f, 1.62f),
    SLIME(37, 0.51f),
    ENDERMAN(38, 2.9f, 0.6f),
    SILVERFISH(39, 0.3f, 0.4f),
    CAVE_SPIDER(40, 0.5f, 0.7f),
    GHAST(41, 4.0f),
    MAGMA_CUBE(42, 0.51f),
    BLAZE(43, 1.8f, 0.6f),
    ZOMBIE_VILLAGER(44, 1.8f, 0.6f, 0.6f, 1.62f),
    WITCH(45, 1.8f, 0.6f, 0.6f, 1.62f),
    STRAY(46, 1.8f, 0.6f, 0.6f, 1.62f),
    HUSK(47, 1.8f, 0.6f, 0.6f, 1.62f),
    WITHER_SKELETON(48, 2.4f, 0.7f),
    GUARDIAN(49, 0.85f),
    ELDER_GUARDIAN(50, 1.9975f),
    NPC(51, 1.8f, 0.6f, 0.6f, 1.62f),
    WITHER(52, 3.5f, 0.9f),
    ENDER_DRAGON(53, 4f, 13f),
    SHULKER(54, 1f, 1f),
    ENDERMITE(55, 0.3f, 0.4f),
    AGENT(56, 0f),
    VINDICATOR(57, 1.8f, 0.6f, 0.6f, 1.62f),


    ARMOR_STAND(61, 0f),
    TRIPOD_CAMERA(62, 0f),
    PLAYER(63, 1.8f, 0.6f, 0.6f, 1.62f),
    ITEM(64, 0.25f, 0.25f),
    PRIMED_TNT(65, 0.98f, 0.98f),
    FALLING_BLOCK(66, 0.98f, 0.98f),
    MOVING_BLOCK(67, 0f),
    EXPERIENCE_BOTTLE(68, 0.25f, 0.25f),
    EXPERIENCE_ORB(69, 0f),
    EYE_OF_ENDER(70, 0f),
    ENDER_CRYSTAL(71, 0f),
    FIREWORKS_ROCKET(72, 0f),
    TRIDENT(73, 0f),

    SHULKER_BULLET(76, 0f),
    FISHING_HOOK(77, 0f),
    CHALKBOARD(78, 0f),
    DRAGON_FIREBALL(79, 0f),
    ARROW(80, 0.25f, 0.25f),
    SNOWBALL(81, 0f),
    EGG(82, 0f),
    PAINTING(83, 0f),
    MINECART(84, 0f),
    LARGE_FIREBALL(85, 0f),
    SPLASH_POTION(86, 0f),
    ENDER_PEARL(87, 0f),
    LEASH_FENCE_KNOT(88, 0f),
    WITHER_SKULL(89, 0f),
    BOAT(90, 0.7f, 1.6f, 1.6f, 0.35f),
    WITHER_SKULL_DANGEROUS(91, 0f),
    LIGHTNING_BOLT(93, 0f),
    SMALL_FIREBALL(94, 0f),
    AREA_EFFECT_CLOUD(95, 0f),
    HOPPER_MINECART(96, 0f),
    TNT_MINECART(97, 0f),
    CHEST_MINECART(98, 0f),

    COMMAND_BLOCK_MINECART(100, 0f),
    LINGERING_POTION(101, 0f),
    LLAMA_SPIT(102, 0f),
    EVOCATION_FANG(103, 0f),
    EVOCATION_ILLAGER(104, 0f),
    VEX(105, 0f),
    ICE_BOMB(106, 0f),
    BALLOON(107, 0f), //TODO
    PUFFER_FISH(108, 0.7f, 0.7f),
    SALMON(109, 0.5f, 0.7f),
    TROPICAL_FISH(111, 0.6f, 0.6f),
    FISH(112, 0.25f, 0.5f);


    private final int type;
    private final float height;
    private final float width;
    private final float length;
    private final float offset;

    EntityType(int type, float height) {
        this(type, height, 0f);
    }

    EntityType(int type, float height, float width) {
        this(type, height, width, width);
    }

    EntityType(int type, float height, float width, float length) {
        this(type, height, width, length, 0f);
    }

    EntityType(int type, float height, float width, float length, float offset) {
        this.type = type;
        this.height = height;
        this.width = width;
        this.length = length;
        this.offset = offset + 0.00001f;
    }

    public int getType() {
        return type;
    }

    public float getHeight() {
        return height;
    }

    public float getWidth() {
        return width;
    }

    public float getLength() {
        return length;
    }

    public float getOffset() {
        return offset;
    }
}