package com.nukkitx.api.block;

import com.google.common.base.Preconditions;
import com.nukkitx.api.metadata.Dyed;
import com.nukkitx.api.metadata.Metadata;
import com.nukkitx.api.metadata.Wood;
import com.nukkitx.api.metadata.block.*;
import com.nukkitx.api.metadata.blockentity.*;
import com.nukkitx.api.metadata.data.LogDirectional;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.Builder;
import lombok.NonNull;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class BlockTypes {
    private static final TIntObjectMap<BlockType> BY_ID = new TIntObjectHashMap<>(192);
    private static final Map<String, BlockType> BY_NAME = new HashMap<>();

    public static final BlockType AIR = IntBlock.builder().name("air").id(0).maxStackSize(0).transparent(true).solid(true).floodable(true).build();
    public static final BlockType STONE = IntBlock.builder().name("stone").id(1).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(1.5f).metadataClass(Stone.class).build();
    public static final BlockType GRASS_BLOCK = IntBlock.builder().name("grass").id(2).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(0.6f).build();
    public static final BlockType DIRT = IntBlock.builder().name("dirt").id(3).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(0.5f).metadataClass(Dirt.class).build();
    public static final BlockType COBBLESTONE = IntBlock.builder().name("cobblestone").id(4).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(2f).build();
    public static final BlockType WOOD_PLANKS = IntBlock.builder().name("planks").id(5).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(2f).metadataClass(Wood.class).flammable(true).build();
    public static final BlockType SAPLING = IntBlock.builder().name("sapling").id(6).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(0f).floodable(true).metadataClass(Sapling.class).build();
    public static final BlockType BEDROCK = IntBlock.builder().name("bedrock").id(7).maxStackSize(64).solid(true).filterLight(15).hardness(-1f).build();
    public static final BlockType WATER = IntBlock.builder().name("flowing_water").id(8).maxStackSize(0).transparent(true).solid(true).filterLight(2).hardness(100f).metadataClass(Liquid.class).build();
    public static final BlockType STATIONARY_WATER = IntBlock.builder().name("water").id(9).maxStackSize(0).transparent(true).solid(true).filterLight(2).hardness(100f).metadataClass(Liquid.class).build();
    public static final BlockType LAVA = IntBlock.builder().name("flowing_lava").id(10).maxStackSize(0).transparent(true).solid(true).emitLight(15).hardness(100f).metadataClass(Liquid.class).build();
    public static final BlockType STATIONARY_LAVA = IntBlock.builder().name("lava").id(11).maxStackSize(0).transparent(true).solid(true).emitLight(15).hardness(100f).metadataClass(Liquid.class).build();
    public static final BlockType SAND = IntBlock.builder().name("sand").id(12).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(0.5f).metadataClass(Sand.class).build();
    public static final BlockType GRAVEL = IntBlock.builder().name("gravel").id(13).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(0.6f).build();
    public static final BlockType GOLD_ORE = IntBlock.builder().name("gold_ore").id(14).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(3f).build();
    public static final BlockType IRON_ORE = IntBlock.builder().name("iron_ore").id(15).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(3f).build();
    public static final BlockType COAL_ORE = IntBlock.builder().name("coal_ore").id(16).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(3f).build();
    public static final BlockType WOOD = IntBlock.builder().name("log").id(17).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(2f).metadataClass(Log.class).flammable(true).build();
    public static final BlockType LEAVES = IntBlock.builder().name("leaves").id(18).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(0.2f).metadataClass(Leaves.class).build();
    public static final BlockType SPONGE = IntBlock.builder().name("sponge").id(19).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(0.6f).metadataClass(Sponge.class).build();
    public static final BlockType GLASS = IntBlock.builder().name("glass").id(20).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(0.3f).build();
    public static final BlockType LAPIS_LAZULI_ORE = IntBlock.builder().name("lapis_ore").id(21).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(3f).build();
    public static final BlockType LAPIS_LAZULI_BLOCK = IntBlock.builder().name("lapis_block").id(22).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(3f).build();
    public static final BlockType DISPENSER = IntBlock.builder().name("dispenser").id(23).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(3.5f).metadataClass(DirectionalPowerable.class).blockEntityClass(DispenserBlockEntity.class).build();
    public static final BlockType SANDSTONE = IntBlock.builder().name("sandstone").id(24).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(0.8f).metadataClass(Sandstone.class).build();
    public static final BlockType NOTE_BLOCK = IntBlock.builder().name("note_block").id(25).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(0.8f).metadataClass(Directional.class).blockEntityClass(MusicBlockEntity.class).build();
    public static final BlockType BED = IntBlock.builder().name("bed").id(26).maxStackSize(1).diggable(true).transparent(true).solid(true).hardness(0.2f).floodable(true).metadataClass(Bed.class).blockEntityClass(BedBlockEntity.class).build();
    public static final BlockType POWERED_RAIL = IntBlock.builder().name("golden_rail").id(27).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(0.7f).metadataClass(ActivableRail.class).floodable(true).build();
    public static final BlockType DETECTOR_RAIL = IntBlock.builder().name("detector_rail").id(28).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(0.7f).metadataClass(ActivableRail.class).floodable(true).build();
    public static final BlockType STICKY_PISTON = IntBlock.builder().name("sticky_piston").id(29).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(0.7f).metadataClass(Piston.class).blockEntityClass(PistonBlockEntity.class).build();
    public static final BlockType COBWEB = IntBlock.builder().name("web").id(30).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(4f).floodable(true).build();
    public static final BlockType TALL_GRASS = IntBlock.builder().name("tallgrass").id(31).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(0f).floodable(true).metadataClass(TallGrass.class).build();
    public static final BlockType DEAD_BUSH = IntBlock.builder().name("deadbush").id(32).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(0f).floodable(true).build();
    public static final BlockType PISTON = IntBlock.builder().name("piston").id(33).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(0.5f).metadataClass(Piston.class).build();
    public static final BlockType PISTON_HEAD = IntBlock.builder().name("pistonarmcollision").id(34).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(0.5f).metadataClass(PistonHead.class).blockEntityClass(PistonBlockEntity.class).build();
    public static final BlockType WOOL = IntBlock.builder().name("wool").id(35).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(0.8f).metadataClass(Dyed.class).flammable(true).build();
    public static final BlockType ELEMENT_0 = IntBlock.builder().name("element_0").id(36).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(1.5f).build();
    public static final BlockType DANDELION = IntBlock.builder().name("yellow_flower").id(37).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(0f).floodable(true).build();
    public static final BlockType FLOWER = IntBlock.builder().name("red_flower").id(38).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(0f).floodable(true).metadataClass(Flower.class).build();
    public static final BlockType BROWN_MUSHROOM = IntBlock.builder().name("brown_mushroom").id(39).maxStackSize(64).diggable(true).solid(true).emitLight(1).filterLight(15).hardness(0f).build();
    public static final BlockType RED_MUSHROOM = IntBlock.builder().name("red_mushroom").id(40).maxStackSize(64).diggable(true).solid(true).emitLight(1).filterLight(15).hardness(0f).floodable(true).build();
    public static final BlockType GOLD_BLOCK = IntBlock.builder().name("gold_block").id(41).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(3f).build();
    public static final BlockType IRON_BLOCK = IntBlock.builder().name("iron_block").id(42).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(5f).build();
    public static final BlockType DOUBLE_STONE_SLAB = IntBlock.builder().name("double_stone_slab").id(43).maxStackSize(64).diggable(true).transparent(true).solid(true).metadataClass(StoneSlab.class).hardness(2f).build();
    public static final BlockType STONE_SLAB = IntBlock.builder().name("stone_slab").id(44).maxStackSize(64).diggable(true).transparent(true).solid(true).metadataClass(StoneSlab.class).hardness(2f).metadataClass(StoneSlab.class).build();
    public static final BlockType BRICKS = IntBlock.builder().name("brick_block").id(45).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(2f).build();
    public static final BlockType TNT = IntBlock.builder().name("tnt").id(46).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(0f).build();
    public static final BlockType BOOKSHELF = IntBlock.builder().name("bookshelf").id(47).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(1.5f).build();
    public static final BlockType MOSS_STONE = IntBlock.builder().name("mossy_cobblestone").id(48).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(2f).build();
    public static final BlockType OBSIDIAN = IntBlock.builder().name("obsidian").id(49).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(50f).build();
    public static final BlockType TORCH = IntBlock.builder().name("torch").id(50).maxStackSize(64).diggable(true).transparent(true).solid(true).metadataClass(Directional.class).emitLight(14).metadataClass(Directional.class).hardness(0f).floodable(true).build();
    public static final BlockType FIRE = IntBlock.builder().name("fire").id(51).maxStackSize(0).diggable(true).transparent(true).solid(true).emitLight(15).hardness(0f).floodable(true).build();
    public static final BlockType MONSTER_SPAWNER = IntBlock.builder().name("mob_spawner").id(52).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(5f).blockEntityClass(MobSpawnerBlockEntity.class).build();
    public static final BlockType OAK_WOOD_STAIRS = IntBlock.builder().name("oak_stairs").id(53).maxStackSize(64).diggable(true).transparent(true).solid(true).filterLight(15).hardness(2f).flammable(true).metadataClass(Stairs.class).build();
    public static final BlockType CHEST = IntBlock.builder().name("chest").id(54).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(2.5f).metadataClass(SimpleDirectional.class).blockEntityClass(ChestBlockEntity.class).build();
    public static final BlockType REDSTONE_WIRE = IntBlock.builder().name("redstone_wire").id(55).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(0f).build();
    public static final BlockType DIAMOND_ORE = IntBlock.builder().name("diamond_ore").id(56).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(3f).build();
    public static final BlockType DIAMOND_BLOCK = IntBlock.builder().name("diamond_block").id(57).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(5f).build();
    public static final BlockType CRAFTING_TABLE = IntBlock.builder().name("crafting_table").id(58).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(2.5f).build();
    public static final BlockType CROPS = IntBlock.builder().name("wheat").id(59).maxStackSize(0).diggable(true).solid(true).filterLight(15).hardness(0f).metadataClass(Crops.class).floodable(true).build();
    public static final BlockType FARMLAND = IntBlock.builder().name("farmland").id(60).maxStackSize(64).diggable(true).transparent(true).solid(true).filterLight(15).hardness(0.6f).metadataClass(Farmland.class).build();
    public static final BlockType FURNACE = IntBlock.builder().name("furnace").id(61).maxStackSize(64).diggable(true).transparent(true).solid(true).emitLight(13).hardness(3.5f).metadataClass(SimpleDirectional.class).blockEntityClass(FurnaceBlockEntity.class).build();
    public static final BlockType BURNING_FURNACE = IntBlock.builder().name("lit_furnace").id(62).maxStackSize(64).diggable(true).transparent(true).solid(true).emitLight(13).metadataClass(SimpleDirectional.class).hardness(3.5f).blockEntityClass(FurnaceBlockEntity.class).build();
    public static final BlockType SIGN = IntBlock.builder().name("standing_sign").id(63).maxStackSize(16).diggable(true).transparent(true).solid(true).hardness(1f).metadataClass(CardinalDirectional.class).blockEntityClass(SignBlockEntity.class).build();
    public static final BlockType WOODEN_DOOR = IntBlock.builder().name("wooden_door").id(64).maxStackSize(64).diggable(true).transparent(true).metadataClass(Door.class).solid(true).hardness(3f).flammable(true).build();
    public static final BlockType LADDER = IntBlock.builder().name("ladder").id(65).maxStackSize(64).diggable(true).transparent(true).solid(true).metadataClass(SimpleDirectional.class).hardness(0.4f).build();
    public static final BlockType RAIL = IntBlock.builder().name("rail").id(66).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(0.7f).metadataClass(Rail.class).floodable(true).build();
    public static final BlockType COBBLESTONE_STAIRS = IntBlock.builder().name("stone_stairs").id(67).maxStackSize(64).diggable(true).transparent(true).solid(true).metadataClass(Stairs.class).hardness(2f).filterLight(15).build();
    public static final BlockType WALL_SIGN = IntBlock.builder().name("wall_sign").id(68).maxStackSize(16).diggable(true).transparent(true).solid(true).hardness(1f).metadataClass(SimpleDirectional.class).blockEntityClass(SignBlockEntity.class).build();
    public static final BlockType LEVER = IntBlock.builder().name("lever").id(69).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(0.5f).metadataClass(Lever.class).floodable(true).build();
    public static final BlockType STONE_PRESSURE_PLATE = IntBlock.builder().name("stone_pressure_plate").id(70).maxStackSize(64).diggable(true).transparent(true).solid(true).metadataClass(PressurePlate.class).hardness(0.5f).build();
    public static final BlockType IRON_DOOR = IntBlock.builder().name("iron_door").id(71).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(5f).metadataClass(Door.class).build();
    public static final BlockType WOODEN_PRESSURE_PLATE = IntBlock.builder().name("wooden_pressure_plate").id(72).maxStackSize(64).diggable(true).transparent(true).solid(true).metadataClass(PressurePlate.class).hardness(0.5f).build();
    public static final BlockType REDSTONE_ORE = IntBlock.builder().name("redstone_ore").id(73).maxStackSize(64).diggable(true).transparent(true).solid(true).emitLight(9).hardness(3f).build();
    public static final BlockType GLOWING_REDSTONE_ORE = IntBlock.builder().name("lit_redstone_ore").id(74).maxStackSize(64).diggable(true).transparent(true).solid(true).emitLight(9).hardness(3f).build();
    public static final BlockType REDSTONE_TORCH = IntBlock.builder().name("unlit_redstone_torch").id(75).maxStackSize(64).diggable(true).transparent(true).metadataClass(Directional.class).solid(true).emitLight(7).metadataClass(Directional.class).hardness(0f).floodable(true).build();
    public static final BlockType REDSTONE_TORCH_ACTIVE = IntBlock.builder().name("lit_redstone_torch").id(76).maxStackSize(64).diggable(true).transparent(true).metadataClass(Directional.class).solid(true).emitLight(7).metadataClass(Directional.class).hardness(0f).floodable(true).build();
    public static final BlockType STONE_BUTTON = IntBlock.builder().name("stone_button").id(77).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(0.5f).floodable(true).metadataClass(DirectionalPowerable.class).build();
    public static final BlockType TOP_SNOW = IntBlock.builder().name("snow_layer").id(78).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(0.1f).metadataClass(TopSnow.class).floodable(true).build();
    public static final BlockType ICE = IntBlock.builder().name("ice").id(79).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(0.5f).build();
    public static final BlockType SNOW = IntBlock.builder().name("snow").id(80).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(0.2f).build();
    public static final BlockType CACTUS = IntBlock.builder().name("cactus").id(81).maxStackSize(64).diggable(true).transparent(true).metadataClass(Cactus.class).solid(true).hardness(0.4f).build();
    public static final BlockType CLAY = IntBlock.builder().name("clay").id(82).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(0.6f).build();
    public static final BlockType SUGAR_CANE = IntBlock.builder().name("reeds").id(83).maxStackSize(64).diggable(true).transparent(true).solid(true).metadataClass(SugarCane.class).hardness(0f).floodable(true).build();
    public static final BlockType JUKEBOX = IntBlock.builder().name("jukebox").id(84).maxStackSize(64).diggable(true).solid(true).filterLight(15).metadataClass(Jukebox.class).hardness(2f).blockEntityClass(JukeboxBlockEntity.class).build();
    public static final BlockType FENCE = IntBlock.builder().name("fence").id(85).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(2f).build();
    public static final BlockType PUMPKIN = IntBlock.builder().name("pumpkin").id(86).maxStackSize(64).diggable(true).transparent(true).metadataClass(Pumpkin.class).solid(true).filterLight(15).hardness(1f).build();
    public static final BlockType NETHERRACK = IntBlock.builder().name("netherrack").id(87).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(0.4f).build();
    public static final BlockType SOUL_SAND = IntBlock.builder().name("soul_sand").id(88).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(0.5f).build();
    public static final BlockType GLOWSTONE = IntBlock.builder().name("glowstone").id(89).maxStackSize(64).diggable(true).transparent(true).solid(true).emitLight(15).hardness(0.3f).build();
    public static final BlockType PORTAL = IntBlock.builder().name("portal").id(90).maxStackSize(0).solid(true).filterLight(15).hardness(-1f).metadataClass(NetherPortal.class).build();
    public static final BlockType JACK_OLANTERN = IntBlock.builder().name("lit_pumpkin").id(91).maxStackSize(64).diggable(true).transparent(true).metadataClass(Pumpkin.class).solid(true).emitLight(15).filterLight(15).hardness(1f).build();
    public static final BlockType CAKE = IntBlock.builder().name("cake").id(92).maxStackSize(1).diggable(true).transparent(true).solid(true).hardness(0.5f).metadataClass(Cake.class).build();
    public static final BlockType REDSTONE_REPEATER = IntBlock.builder().name("unpowered_repeater").id(93).maxStackSize(64).diggable(true).transparent(true).solid(true).metadataClass(RedstoneRepeater.class).hardness(0f).build();
    public static final BlockType REDSTONE_REPEATER_ACTIVE = IntBlock.builder().name("powered_repeater").id(94).maxStackSize(64).diggable(true).transparent(true).solid(true).metadataClass(RedstoneRepeater.class).hardness(0f).build();
    public static final BlockType INVISIBLE_BEDROCK = IntBlock.builder().name("invisiblebedrock").id(95).maxStackSize(64).transparent(true).solid(true).hardness(-1f).build();
    public static final BlockType TRAPDOOR = IntBlock.builder().name("trapdoor").id(96).maxStackSize(64).diggable(true).transparent(true).metadataClass(Trapdoor.class).solid(true).hardness(3f).build();
    public static final BlockType MONSTER_EGG = IntBlock.builder().name("monster_egg").id(97).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(0.75f).metadataClass(MonsterEgg.class).build();
    public static final BlockType STONE_BRICK = IntBlock.builder().name("stonebrick").id(98).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(1.5f).metadataClass(StoneBrick.class).build();
    public static final BlockType BROWN_MUSHROOM_BLOCK = IntBlock.builder().name("brown_mushroom_block").id(99).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(0.2f).build();
    public static final BlockType RED_MUSHROOM_BLOCK = IntBlock.builder().name("red_mushroom_block").id(100).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(0.2f).build();
    public static final BlockType IRON_BARS = IntBlock.builder().name("iron_bars").id(101).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(5f).build();
    public static final BlockType GLASS_PANE = IntBlock.builder().name("glass_pane").id(102).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(0.3f).build();
    public static final BlockType MELON = IntBlock.builder().name("melon_block").id(103).maxStackSize(64).diggable(true).transparent(true).solid(true).filterLight(15).hardness(1f).build();
    public static final BlockType PUMPKIN_STEM = IntBlock.builder().name("pumpkin_stem").id(104).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(0f).metadataClass(Crops.class).floodable(true).build();
    public static final BlockType MELON_STEM = IntBlock.builder().name("melon_stem").id(105).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(0f).metadataClass(Crops.class).floodable(true).build();
    public static final BlockType VINES = IntBlock.builder().name("vine").id(106).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(0.2f).metadataClass(SimpleDirectional.class).floodable(true).build();
    public static final BlockType FENCE_GATE = IntBlock.builder().name("fence_gate").id(107).maxStackSize(64).diggable(true).transparent(true).solid(true).metadataClass(FenceGate.class).hardness(2f).build();
    public static final BlockType BRICK_STAIRS = IntBlock.builder().name("brick_stairs").id(108).maxStackSize(64).diggable(true).transparent(true).solid(true).metadataClass(Stairs.class).filterLight(15).hardness(2f).build();
    public static final BlockType STONE_BRICK_STAIRS = IntBlock.builder().name("stone_brick_stairs").id(109).maxStackSize(64).diggable(true).transparent(true).metadataClass(Stairs.class).solid(true).hardness(1.5f).filterLight(15).build();
    public static final BlockType MYCELIUM = IntBlock.builder().name("mycelium").id(110).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(0.6f).build();
    public static final BlockType LILY_PAD = IntBlock.builder().name("waterlily").id(111).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(0f).floodable(true).build();
    public static final BlockType NETHER_BRICK = IntBlock.builder().name("nether_brick").id(112).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(2f).build();
    public static final BlockType NETHER_BRICK_FENCE = IntBlock.builder().name("nether_brick_fence").id(113).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(2f).build();
    public static final BlockType NETHER_BRICK_STAIRS = IntBlock.builder().name("nether_brick_stairs").id(114).maxStackSize(64).diggable(true).transparent(true).metadataClass(Stairs.class).solid(true).filterLight(15).hardness(2f).build();
    public static final BlockType NETHER_WART = IntBlock.builder().name("nether_wart").id(115).maxStackSize(64).diggable(true).transparent(true).metadataClass(NetherWart.class).solid(true).hardness(0f).floodable(true).build();
    public static final BlockType ENCHANTMENT_TABLE = IntBlock.builder().name("enchanting_table").id(116).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(5f).build();
    public static final BlockType BREWING_STAND = IntBlock.builder().name("brewing_stand").id(117).maxStackSize(64).diggable(true).transparent(true).metadataClass(BrewingStand.class).solid(true).emitLight(1).hardness(0.5f).blockEntityClass(BrewingStandBlockEntity.class).build();
    public static final BlockType CAULDRON = IntBlock.builder().name("cauldron").id(118).maxStackSize(64).diggable(true).transparent(true).solid(true).metadataClass(Cauldron.class).hardness(2f).blockEntityClass(CauldronBlockEntity.class).build();
    public static final BlockType END_PORTAL = IntBlock.builder().name("end_portal").id(119).maxStackSize(64).transparent(true).solid(true).emitLight(15).hardness(-1f).blockEntityClass(EndPortalBlockEntity.class).build();
    public static final BlockType END_PORTAL_FRAME = IntBlock.builder().name("end_portal_frame").id(120).maxStackSize(64).transparent(true).solid(true).metadataClass(EndPortalFrame.class).emitLight(1).hardness(-1f).build();
    public static final BlockType END_STONE = IntBlock.builder().name("end_stone").id(121).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(3f).build();
    public static final BlockType DRAGON_EGG = IntBlock.builder().name("dragon_egg").id(122).maxStackSize(64).transparent(true).solid(true).emitLight(1).hardness(3f).build();
    public static final BlockType REDSTONE_LAMP = IntBlock.builder().name("redstone_lamp").id(123).maxStackSize(64).diggable(true).transparent(true).solid(true).emitLight(15).hardness(0.3f).build();
    public static final BlockType REDSTONE_LAMP_ACTIVE = IntBlock.builder().name("lit_redstone_lamp").id(124).maxStackSize(64).diggable(true).transparent(true).solid(true).emitLight(15).hardness(0.3f).build();
    public static final BlockType DROPPER = IntBlock.builder().name("dropper").id(125).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(3.5f).metadataClass(DirectionalPowerable.class).blockEntityClass(DropperBlockEntity.class).build();
    public static final BlockType ACTIVATOR_RAIL = IntBlock.builder().name("activator_rail").id(126).maxStackSize(64).diggable(true).transparent(true).solid(true).metadataClass(ActivableRail.class).hardness(0.7f).floodable(true).build();
    public static final BlockType COCOA = IntBlock.builder().name("cocoa").id(127).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(0.2f).metadataClass(Cocoa.class).build();
    public static final BlockType SANDSTONE_STAIRS = IntBlock.builder().name("sandstone_stairs").id(128).maxStackSize(64).diggable(true).transparent(true).solid(true).metadataClass(Stairs.class).filterLight(15).hardness(0.8f).build();
    public static final BlockType EMERALD_ORE = IntBlock.builder().name("emerald_ore").id(129).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(3f).build();
    public static final BlockType ENDER_CHEST = IntBlock.builder().name("ender_chest").id(130).maxStackSize(64).diggable(true).transparent(true).solid(true).metadataClass(SimpleDirectional.class).hardness(22.5f).blockEntityClass(EnderChestBlockEntity.class).build();
    public static final BlockType TRIPWIRE_HOOK = IntBlock.builder().name("tripwire_hook").id(131).maxStackSize(64).diggable(true).transparent(true).solid(true).metadataClass(TripwireHook.class).hardness(0f).floodable(true).build();
    public static final BlockType TRIPWIRE = IntBlock.builder().name("tripwire").id(132).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(0f).floodable(true).build();
    public static final BlockType EMERALD_BLOCK = IntBlock.builder().name("emerald_block").id(133).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(5f).build();
    public static final BlockType SPRUCE_WOOD_STAIRS = IntBlock.builder().name("spruce_stairs").id(134).maxStackSize(64).diggable(true).transparent(true).solid(true).metadataClass(Stairs.class).filterLight(15).hardness(2f).flammable(true).build();
    public static final BlockType BIRCH_WOOD_STAIRS = IntBlock.builder().name("birch_stairs").id(135).maxStackSize(64).diggable(true).transparent(true).solid(true).metadataClass(Stairs.class).filterLight(15).hardness(2f).flammable(true).build();
    public static final BlockType JUNGLE_WOOD_STAIRS = IntBlock.builder().name("jungle_stairs").id(136).maxStackSize(64).diggable(true).transparent(true).solid(true).metadataClass(Stairs.class).filterLight(15).hardness(2f).flammable(true).build();
    public static final BlockType COMMAND_BLOCK = IntBlock.builder().name("command_block").id(137).maxStackSize(64).solid(true).filterLight(15).hardness(-1f).blockEntityClass(CommandBlockEntity.class).build();
    public static final BlockType BEACON = IntBlock.builder().name("beacon").id(138).maxStackSize(64).diggable(true).solid(true).emitLight(15).hardness(3f).blockEntityClass(BeaconBlockEntity.class).build();
    public static final BlockType COBBLESTONE_WALL = IntBlock.builder().name("cobblestone_wall").id(139).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(2f).build();
    public static final BlockType FLOWER_POT = IntBlock.builder().name("flower_pot").id(140).maxStackSize(64).diggable(true).transparent(true).solid(true).floodable(true).hardness(0f).metadataClass(FlowerPotBlockEntity.class).build();
    public static final BlockType CARROTS = IntBlock.builder().name("carrots").id(141).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(0f).metadataClass(Crops.class).floodable(true).build();
    public static final BlockType POTATO = IntBlock.builder().name("potatoes").id(142).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(0f).metadataClass(Crops.class).floodable(true).build();
    public static final BlockType WOODEN_BUTTON = IntBlock.builder().name("wooden_button").id(143).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(0.5f).floodable(true).metadataClass(DirectionalPowerable.class).build();
    public static final BlockType MOB_HEAD = IntBlock.builder().name("skull").id(144).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(1f).floodable(true).blockEntityClass(SkullBlockEntity.class).build();
    public static final BlockType ANVIL = IntBlock.builder().name("anvil").id(145).maxStackSize(64).diggable(true).transparent(true).solid(true).metadataClass(AnvilBlock.class).hardness(5f).build();
    public static final BlockType TRAPPED_CHEST = IntBlock.builder().name("trapped_chest").id(146).maxStackSize(64).diggable(true).transparent(true).solid(true).metadataClass(SimpleDirectional.class).hardness(2.5f).blockEntityClass(ChestBlockEntity.class).build();
    public static final BlockType WEIGHTED_PRESSURE_PLATE_LIGHT = IntBlock.builder().name("light_weighted_pressure_plate").id(147).maxStackSize(64).diggable(true).metadataClass(WeightedPressurePlate.class).transparent(true).solid(true).hardness(0.5f).build();
    public static final BlockType WEIGHTED_PRESSURE_PLATE_HEAVY = IntBlock.builder().name("heavy_weighted_pressure_plate").id(148).maxStackSize(64).diggable(true).metadataClass(WeightedPressurePlate.class).transparent(true).solid(true).hardness(0.5f).build();
    public static final BlockType REDSTONE_COMPARATOR_UNPOWERED = IntBlock.builder().name("unpowered_comparator").id(149).maxStackSize(64).diggable(true).transparent(true).solid(true).metadataClass(RedstoneComparator.class).hardness(0f).blockEntityClass(ComparatorBlockEntity.class).build();
    public static final BlockType REDSTONE_COMPARATOR_POWERED = IntBlock.builder().name("powered_comparator").id(150).maxStackSize(64).diggable(true).transparent(true).solid(true).metadataClass(RedstoneComparator.class).hardness(0f).blockEntityClass(ComparatorBlockEntity.class).build();
    public static final BlockType DAYLIGHT_SENSOR = IntBlock.builder().name("daylight_detector").id(151).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(0.2f).metadataClass(DaylightSensor.class).blockEntityClass(DaylightDetectorBlockEntity.class).build();
    public static final BlockType REDSTONE_BLOCK = IntBlock.builder().name("redstone_block").id(152).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(5f).build();
    public static final BlockType NETHER_QUARTZ_ORE = IntBlock.builder().name("quartz_ore").id(153).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(3f).build();
    public static final BlockType HOPPER = IntBlock.builder().name("hopper").id(154).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(3f).metadataClass(DirectionalPowerable.class).blockEntityClass(HopperBlockEntity.class).build();
    public static final BlockType QUARTZ_BLOCK = IntBlock.builder().name("quartz_block").id(155).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(0.8f).build();
    public static final BlockType QUARTZ_STAIRS = IntBlock.builder().name("quartz_stairs").id(156).maxStackSize(64).diggable(true).transparent(true).solid(true).metadataClass(Stairs.class).filterLight(15).hardness(0.8f).build();
    public static final BlockType WOODEN_DOUBLE_SLAB = IntBlock.builder().name("double_wooden_slab").id(157).maxStackSize(64).diggable(true).transparent(true).metadataClass(WoodSlab.class).solid(true).flammable(true).hardness(2f).build();
    public static final BlockType WOODEN_SLAB = IntBlock.builder().name("wooden_slab").id(158).maxStackSize(64).diggable(true).transparent(true).solid(true).metadataClass(WoodSlab.class).flammable(true).hardness(2f).build();
    public static final BlockType COLORED_TERRACOTTA = IntBlock.builder().name("stained_hardened_clay").id(159).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(1.25f).metadataClass(Dyed.class).build();
    public static final BlockType STAINED_GLASS_PANE = IntBlock.builder().name("stained_glass_pane").id(160).maxStackSize(64).diggable(true).transparent(true).solid(true).metadataClass(Dyed.class).hardness(0.3f).build();
    public static final BlockType LEAVES2 = IntBlock.builder().name("leaves2").id(161).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(0.2f).metadataClass(Leaves2.class).build();
    public static final BlockType WOOD2 = IntBlock.builder().name("log2").id(162).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(2f).metadataClass(Log.class).flammable(true).build();
    public static final BlockType ACACIA_WOOD_STAIRS = IntBlock.builder().name("acacia_stairs").id(163).maxStackSize(64).diggable(true).transparent(true).solid(true).metadataClass(Stairs.class).filterLight(15).flammable(true).hardness(2f).build();
    public static final BlockType DARK_OAK_WOOD_STAIRS = IntBlock.builder().name("dark_oak_stairs").id(164).maxStackSize(64).diggable(true).transparent(true).solid(true).metadataClass(Stairs.class).filterLight(15).flammable(true).hardness(2f).build();
    public static final BlockType SLIME_BLOCK = IntBlock.builder().name("slime").id(165).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(0f).build();
    public static final BlockType GLOW_STICK = IntBlock.builder().name("glow_stick").id(166).maxStackSize(64).diggable(true).transparent(true).solid(true).emitLight(14).hardness(0f).floodable(true).build();
    public static final BlockType IRON_TRAPDOOR = IntBlock.builder().name("iron_trapdoor").id(167).maxStackSize(64).diggable(true).transparent(true).solid(true).metadataClass(Trapdoor.class).hardness(5f).build();
    public static final BlockType PRISMARINE = IntBlock.builder().name("prismarine").id(168).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(1.5f).build();
    public static final BlockType SEA_LANTERN = IntBlock.builder().name("sealantern").id(169).maxStackSize(64).diggable(true).transparent(true).solid(true).emitLight(15).hardness(0.3f).build();
    public static final BlockType HAY_BALE = IntBlock.builder().name("hay_block").id(170).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(0.5f).metadataClass(Directional.class).build();
    public static final BlockType CARPET = IntBlock.builder().name("carpet").id(171).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(0.1f).metadataClass(Dyed.class).floodable(true).build();
    public static final BlockType TERRACOTTA = IntBlock.builder().name("hardened_clay").id(172).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(1.25f).build();
    public static final BlockType COAL_BLOCK = IntBlock.builder().name("coal_block").id(173).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(5f).build();
    public static final BlockType PACKED_ICE = IntBlock.builder().name("packed_ice").id(174).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(0.5f).build();
    public static final BlockType SUNFLOWER = IntBlock.builder().name("double_plant").id(175).maxStackSize(64).diggable(true).transparent(true).solid(true).metadataClass(Flower.class).hardness(0f).floodable(true).build();
    public static final BlockType STANDING_BANNER = IntBlock.builder().name("standing_banner").id(176).maxStackSize(16).diggable(true).transparent(true).solid(true).metadataClass(CardinalDirectional.class).hardness(1f).blockEntityClass(BannerBlockEntity.class).build();
    public static final BlockType WALL_BANNER = IntBlock.builder().name("wall_banner").id(177).maxStackSize(16).diggable(true).transparent(true).solid(true).hardness(1f).metadataClass(SimpleDirectional.class).blockEntityClass(BannerBlockEntity.class).build();
    public static final BlockType INVERTED_DAYLIGHT_SENSOR = IntBlock.builder().name("daylight_detector_inverted").id(178).maxStackSize(64).diggable(true).transparent(true).solid(true).metadataClass(DaylightSensor.class).hardness(0.2f).blockEntityClass(DaylightDetectorBlockEntity.class).build();
    public static final BlockType RED_SANDSTONE = IntBlock.builder().name("red_sandstone").id(179).maxStackSize(64).diggable(true).solid(true).filterLight(15).metadataClass(Sandstone.class).hardness(0.8f).build();
    public static final BlockType RED_SANDSTONE_STAIRS = IntBlock.builder().name("red_sandstone_stairs").id(180).maxStackSize(64).diggable(true).transparent(true).metadataClass(Stairs.class).solid(true).filterLight(15).hardness(0.8f).build();
    public static final BlockType DOUBLE_RED_SANDSTONE_SLAB = IntBlock.builder().name("double_stone_slab2").id(181).maxStackSize(64).diggable(true).transparent(true).metadataClass(StoneSlab2.class).solid(true).hardness(2f).build();
    public static final BlockType RED_SANDSTONE_SLAB = IntBlock.builder().name("stone_slab2").id(182).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(2f).metadataClass(StoneSlab2.class).build();
    public static final BlockType SPRUCE_FENCE_GATE = IntBlock.builder().name("spruce_fence_gate").id(183).maxStackSize(64).diggable(true).transparent(true).solid(true).metadataClass(FenceGate.class).hardness(2f).build();
    public static final BlockType BIRCH_FENCE_GATE = IntBlock.builder().name("birch_fence_gate").id(184).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(2f).metadataClass(FenceGate.class).build();
    public static final BlockType JUNGLE_FENCE_GATE = IntBlock.builder().name("jungle_fence_gate").id(185).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(2f).metadataClass(FenceGate.class).build();
    public static final BlockType DARK_OAK_FENCE_GATE = IntBlock.builder().name("dark_oak_fence_gate").id(186).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(2f).metadataClass(FenceGate.class).build();
    public static final BlockType ACACIA_FENCE_GATE = IntBlock.builder().name("acacia_fence_gate").id(187).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(2f).metadataClass(FenceGate.class).build();
    public static final BlockType REPEATING_COMMAND_BLOCK = IntBlock.builder().name("repeating_command_block").id(188).maxStackSize(64).solid(true).filterLight(15).hardness(-1f).blockEntityClass(CommandBlockEntity.class).build();
    public static final BlockType CHAIN_COMMAND_BLOCK = IntBlock.builder().name("chain_command_block").id(189).maxStackSize(64).solid(true).filterLight(15).hardness(-1f).blockEntityClass(CommandBlockEntity.class).build();
    public static final BlockType HARD_GLASS_PANE = IntBlock.builder().name("hard_glass_pane").id(190).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(1.8f).build();
    public static final BlockType HARD_STAINED_GLASS_PANE = IntBlock.builder().name("hard_stained_glass_pane").id(191).maxStackSize(64).diggable(true).transparent(true).metadataClass(Dyed.class).solid(true).hardness(1.8f).build();
    public static final BlockType CHEMICAL_HEAT = IntBlock.builder().name("chemical_heat").id(192).maxStackSize(64).diggable(false).transparent(true).build();
    public static final BlockType SPRUCE_DOOR = IntBlock.builder().name("spruce_door").id(193).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(3f).metadataClass(Door.class).build();
    public static final BlockType BIRCH_DOOR = IntBlock.builder().name("birch_door").id(194).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(3f).metadataClass(Door.class).build();
    public static final BlockType JUNGLE_DOOR = IntBlock.builder().name("jungle_door").id(195).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(3f).metadataClass(Door.class).build();
    public static final BlockType ACACIA_DOOR = IntBlock.builder().name("acacia_door").id(196).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(3f).metadataClass(Door.class).build();
    public static final BlockType DARK_OAK_DOOR = IntBlock.builder().name("dark_oak_door").id(197).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(3f).metadataClass(Door.class).build();
    public static final BlockType GRASS_PATH = IntBlock.builder().name("grass_path").id(198).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(0.65f).build();
    public static final BlockType ITEM_FRAME = IntBlock.builder().name("frame").id(199).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(0f).floodable(true).metadataClass(SimpleDirectional.class).blockEntityClass(ItemFrameBlockEntity.class).build();
    public static final BlockType CHORUS_FLOWER = IntBlock.builder().name("chorus_flower").id(200).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(0.4f).metadataClass(ChorusFlower.class).build();
    public static final BlockType PURPUR_BLOCK = IntBlock.builder().name("purpur_block").id(201).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(1.5f).build();
    public static final BlockType COLORED_TORCH_RG = IntBlock.builder().name("colored_torch_rg").id(202).maxStackSize(64).diggable(true).solid(true).transparent(true).metadataClass(Directional.class).build();
    public static final BlockType PURPUR_STAIRS = IntBlock.builder().name("purpur_stairs").id(203).maxStackSize(64).diggable(true).transparent(true).solid(true).metadataClass(Stairs.class).hardness(1.5f).build();
    public static final BlockType COLORED_TORCH_BP = IntBlock.builder().name("colored_torch_bp").id(204).maxStackSize(64).diggable(true).solid(true).transparent(true).metadataClass(Directional.class).build();
    public static final BlockType SHULKER_BOX_UNDYED = IntBlock.builder().name("undyed_shulker_box").id(205).maxStackSize(1).diggable(true).transparent(true).solid(true).hardness(2f).blockEntityClass(ShulkerBoxBlockEntity.class).build();
    public static final BlockType END_STONE_BRICKS = IntBlock.builder().name("end_bricks").id(206).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(2f).build();
    public static final BlockType FROSTED_ICE = IntBlock.builder().name("frosted_ice").id(207).maxStackSize(64).diggable(true).transparent(true).solid(true).filterLight(2).hardness(0.5f).build();
    public static final BlockType END_ROD = IntBlock.builder().name("end_rod").id(208).maxStackSize(64).diggable(true).transparent(true).solid(true).emitLight(14).hardness(0f).floodable(true).metadataClass(Directional.class).build();
    public static final BlockType END_GATEWAY = IntBlock.builder().name("end_gateway").id(209).maxStackSize(64).solid(true).emitLight(15).filterLight(15).hardness(-1f).blockEntityClass(EndGatewayBlockEntity.class).build();
    public static final BlockType ALLOW = IntBlock.builder().name("allow").id(210).maxStackSize(64).solid(true).filterLight(15).hardness(-1f).build();
    public static final BlockType DENY = IntBlock.builder().name("deny").id(211).maxStackSize(64).solid(true).filterLight(15).hardness(-1f).build();
    public static final BlockType BORDER_BLOCK = IntBlock.builder().name("border_block").id(212).maxStackSize(64).transparent(true).solid(true).hardness(-1f).build();
    public static final BlockType MAGMA_BLOCK = IntBlock.builder().name("magma").id(213).maxStackSize(64).diggable(true).solid(true).emitLight(3).filterLight(15).hardness(0.5f).build();
    public static final BlockType NETHER_WART_BLOCK = IntBlock.builder().name("nether_wart_block").id(214).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(1f).build();
    public static final BlockType RED_NETHER_BRICK = IntBlock.builder().name("red_nether_brick").id(215).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(2f).build();
    public static final BlockType BONE_BLOCK = IntBlock.builder().name("bone_block").id(216).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(2f).build();
    public static final BlockType SHULKER_BOX = IntBlock.builder().name("shulker_box").id(218).maxStackSize(1).diggable(true).transparent(true).solid(true).hardness(2f).blockEntityClass(ShulkerBoxBlockEntity.class).metadataClass(Dyed.class).build();
    public static final BlockType PURPLE_GLAZED_TERRACOTTA = IntBlock.builder().name("purple_glazed_terracotta").id(219).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(1.4f).build();
    public static final BlockType WHITE_GLAZED_TERRACOTTA = IntBlock.builder().name("white_glazed_terracotta").id(220).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(1.4f).build();
    public static final BlockType ORANGE_GLAZED_TERRACOTTA = IntBlock.builder().name("orange_glazed_terracotta").id(221).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(1.4f).build();
    public static final BlockType MAGENTA_GLAZED_TERRACOTTA = IntBlock.builder().name("magenta_glazed_terracotta").id(222).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(1.4f).build();
    public static final BlockType LIGHT_BLUE_GLAZED_TERRACOTTA = IntBlock.builder().name("light_blue_glazed_terracotta").id(223).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(1.4f).build();
    public static final BlockType YELLOW_GLAZED_TERRACOTTA = IntBlock.builder().name("yellow_glazed_terracotta").id(224).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(1.4f).build();
    public static final BlockType LIME_GLAZED_TERRACOTTA = IntBlock.builder().name("lime_glazed_terracotta").id(225).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(1.4f).build();
    public static final BlockType PINK_GLAZED_TERRACOTTA = IntBlock.builder().name("pink_glazed_terracotta").id(226).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(1.4f).build();
    public static final BlockType GRAY_GLAZED_TERRACOTTA = IntBlock.builder().name("gray_glazed_terracotta").id(227).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(1.4f).build();
    public static final BlockType LIGHT_GRAY_GLAZED_TERRACOTTA = IntBlock.builder().name("silver_glazed_terracotta").id(228).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(1.4f).build();
    public static final BlockType CYAN_GLAZED_TERRACOTTA = IntBlock.builder().name("cyan_glazed_terracotta").id(229).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(1.4f).build();
    public static final BlockType CHALKBOARD = IntBlock.builder().name("chalkboard").id(230).maxStackSize(16).transparent(true).solid(true).hardness(-1f).blockEntityClass(ChalkboardBlockEntity.class).build();
    public static final BlockType BLUE_GLAZED_TERRACOTTA = IntBlock.builder().name("blue_glazed_terracotta").id(231).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(1.4f).build();
    public static final BlockType BROWN_GLAZED_TERRACOTTA = IntBlock.builder().name("brown_glazed_terracotta").id(232).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(1.4f).build();
    public static final BlockType GREEN_GLAZED_TERRACOTTA = IntBlock.builder().name("green_glazed_terracotta").id(233).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(1.4f).build();
    public static final BlockType RED_GLAZED_TERRACOTTA = IntBlock.builder().name("red_glazed_terracotta").id(234).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(1.4f).build();
    public static final BlockType BLACK_GLAZED_TERRACOTTA = IntBlock.builder().name("black_glazed_terracotta").id(235).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(1.4f).build();
    public static final BlockType CONCRETE = IntBlock.builder().name("conrete").id(236).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(1.8f).build();
    public static final BlockType CONCRETE_POWDER = IntBlock.builder().name("concretepowder").id(237).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(0.5f).build();
    public static final BlockType CHEMISTRY_TABLE = IntBlock.builder().name("chemistry_table").id(238).maxStackSize(64).diggable(true).solid(true).hardness(0.5f).blockEntityClass(ChemistryTableBlockEntity.class).build();
    public static final BlockType UNDERWATER_TORCH = IntBlock.builder().name("underwater_torch").id(239).maxStackSize(64).diggable(true).solid(true).transparent(true).metadataClass(Directional.class).build();
    public static final BlockType CHORUS_PLANT = IntBlock.builder().name("chorus_plant").id(240).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(0.4f).metadataClass(Directional.class).build();
    public static final BlockType STAINED_GLASS = IntBlock.builder().name("stained_glass").id(241).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(0.3f).metadataClass(Dyed.class).build();
    public static final BlockType CAMERA = IntBlock.builder().name("camera").id(242).maxStackSize(64).solid(true).filterLight(15).hardness(-1f).build();
    public static final BlockType PODZOL = IntBlock.builder().name("podzol").id(243).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(0.5f).build();
    public static final BlockType BEETROOT = IntBlock.builder().name("beetroot").id(244).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(0f).metadataClass(Crops.class).build();
    public static final BlockType STONECUTTER = IntBlock.builder().name("stonecutter").id(245).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(3.5f).build();
    public static final BlockType GLOWING_OBSIDIAN = IntBlock.builder().name("glowingobsidian").id(246).maxStackSize(64).diggable(true).solid(true).emitLight(12).filterLight(15).hardness(50f).build();
    public static final BlockType NETHER_REACTOR_CORE = IntBlock.builder().name("netherreactor").id(247).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(3f).blockEntityClass(NetherReactorBlockEntity.class).build();
    public static final BlockType UPDATE_GAME_BLOCK = IntBlock.builder().name("info_update").id(248).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(0f).build();
    public static final BlockType UPDATE_GAME_BLOCK2 = IntBlock.builder().name("info_update2").id(249).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(0f).build();
    public static final BlockType MOVED_BY_PISTON = IntBlock.builder().name("movingblock").id(250).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(0f).blockEntityClass(MovingBlockEntity.class).build();
    public static final BlockType OBSERVER = IntBlock.builder().name("observer").id(251).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(3f).metadataClass(DirectionalPowerable.class).build();
    public static final BlockType STRUCTURE_BLOCK = IntBlock.builder().name("structure_block").id(252).maxStackSize(64).solid(true).filterLight(15).hardness(-1f).blockEntityClass(StructureBlockEntity.class).build();
    public static final BlockType HARD_GLASS = IntBlock.builder().name("hard_glass").id(253).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(1.8f).build();
    public static final BlockType HARD_STAINED_GLASS = IntBlock.builder().name("hard_stained_glass").id(254).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(1.8f).metadataClass(Dyed.class).build();
    public static final BlockType RESERVED6 = IntBlock.builder().name("reserved6").id(255).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(-1f).build();
    public static final BlockType PRISMARINE_STAIRS = IntBlock.builder().name("prismarine_stairs").id(257).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(1.5f).metadataClass(Stairs.class).build();
    public static final BlockType DARK_PRISMARINE_STAIRS = IntBlock.builder().name("dark_prismarine_stairs").id(258).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(1.5f).metadataClass(Stairs.class).build();
    public static final BlockType PRISMARINE_BRICKS_STAIRS = IntBlock.builder().name("prismarine_bricks_stairs").id(259).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(1.5f).metadataClass(Stairs.class).build();
    public static final BlockType STRIPPED_SPRUCE_LOG = IntBlock.builder().name("stripped_spruce_log").id(260).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(2f).metadataClass(LogDirectional.class).build();
    public static final BlockType STRIPPED_BIRCH_LOG = IntBlock.builder().name("stripped_birch_log").id(261).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(2f).metadataClass(LogDirectional.class).build();
    public static final BlockType STRIPPED_JUNGLE_LOG = IntBlock.builder().name("stripped_jungle_log").id(262).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(2f).metadataClass(LogDirectional.class).build();
    public static final BlockType STRIPPED_ACACIA_LOG = IntBlock.builder().name("stripped_acacia_log").id(263).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(2f).metadataClass(LogDirectional.class).build();
    public static final BlockType STRIPPED_DARK_OAK_LOG = IntBlock.builder().name("stripped_dark_oak_log").id(264).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(2f).metadataClass(LogDirectional.class).build();
    public static final BlockType STRIPPED_OAK_LOG = IntBlock.builder().name("stripped_oak_log").id(265).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(2f).metadataClass(LogDirectional.class).build();
    public static final BlockType BLUE_ICE = IntBlock.builder().name("blue_ice").id(266).maxStackSize(64).diggable(true).transparent(true).solid(true).filterLight(2).hardness(0.5f).build();

    static { // Lazy
        for (int i = 1; i <= 118; i++) {
            IntBlock.builder().name("element_" + i).id(266 + i).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(0.5f).build();
        }
    }

    public static final BlockType SEA_GRASS = IntBlock.builder().name("seagrass").id(385).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(0f).build();
    public static final BlockType CORAL = IntBlock.builder().name("coral").id(386).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(1.5f).build();
    public static final BlockType CORAL_BLOCK = IntBlock.builder().name("coral_block").id(387).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(1.5f).build();
    public static final BlockType CORAL_FAN = IntBlock.builder().name("coral_fan").id(388).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(1.5f).build();
    public static final BlockType CORAL_FAN_DEAD = IntBlock.builder().name("coral_fan_dead").id(389).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(1.5f).build();
    public static final BlockType CORAL_FAN_HANG = IntBlock.builder().name("coral_fan_hang").id(390).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(1.5f).build();
    public static final BlockType CORAL_FAN_HANG_2 = IntBlock.builder().name("coral_fan_hang2").id(391).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(1.5f).build();
    public static final BlockType CORAL_FAN_HANG_3 = IntBlock.builder().name("coral_fan_hang3").id(392).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(1.5f).build();
    public static final BlockType KELP = IntBlock.builder().name("kelp").id(393).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(0f).build();
    public static final BlockType DRIED_KELP_BLOCK = IntBlock.builder().name("dried_kelp_block").id(394).maxStackSize(64).diggable(true).solid(true).filterLight(15).hardness(1.5f).build();
    public static final BlockType ACACIA_BUTTON = IntBlock.builder().name("acacia_button").id(395).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(0.5f).floodable(true).metadataClass(DirectionalPowerable.class).build();
    public static final BlockType BIRCH_BUTTON = IntBlock.builder().name("acacia_button").id(396).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(0.5f).floodable(true).metadataClass(DirectionalPowerable.class).build();
    public static final BlockType DARK_OAK_BUTTON = IntBlock.builder().name("acacia_button").id(397).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(0.5f).floodable(true).metadataClass(DirectionalPowerable.class).build();
    public static final BlockType JUNGLE_BUTTON = IntBlock.builder().name("acacia_button").id(398).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(0.5f).floodable(true).metadataClass(DirectionalPowerable.class).build();
    public static final BlockType SPRUCE_BUTTON = IntBlock.builder().name("acacia_button").id(399).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(0.5f).floodable(true).metadataClass(DirectionalPowerable.class).build();
    public static final BlockType ACACIA_TRAPDOOR = IntBlock.builder().name("acacia_trapdoor").id(400).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(0.5f).floodable(true).metadataClass(Trapdoor.class).build();
    public static final BlockType BIRCH_TRAPDOOR = IntBlock.builder().name("acacia_trapdoor").id(401).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(3f).floodable(true).metadataClass(Trapdoor.class).build();
    public static final BlockType DARK_OAK_TRAPDOOR = IntBlock.builder().name("acacia_trapdoor").id(402).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(3f).floodable(true).metadataClass(Trapdoor.class).build();
    public static final BlockType JUNGLE_TRAPDOOR = IntBlock.builder().name("acacia_trapdoor").id(403).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(3f).floodable(true).metadataClass(Trapdoor.class).build();
    public static final BlockType SPRUCE_TRAPDOOR = IntBlock.builder().name("acacia_trapdoor").id(404).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(3f).floodable(true).metadataClass(Trapdoor.class).build();
    public static final BlockType ACACIA_PRESSURE_PLATE = IntBlock.builder().name("acacia_pressure_plate").id(405).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(0.5f).floodable(true).metadataClass(PressurePlate.class).build();
    public static final BlockType BIRCH_PRESSURE_PLATE = IntBlock.builder().name("acacia_pressure_plate").id(406).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(0.5f).floodable(true).metadataClass(PressurePlate.class).build();
    public static final BlockType DARK_OAK_PRESSURE_PLATE = IntBlock.builder().name("acacia_pressure_plate").id(407).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(0.5f).floodable(true).metadataClass(PressurePlate.class).build();
    public static final BlockType JUNGLE_PRESSURE_PLATE = IntBlock.builder().name("acacia_pressure_plate").id(408).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(0.5f).floodable(true).metadataClass(PressurePlate.class).build();
    public static final BlockType SPRUCE_PRESSURE_PLATE = IntBlock.builder().name("acacia_pressure_plate").id(409).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(0.5f).floodable(true).metadataClass(PressurePlate.class).build();
    public static final BlockType CARVED_PUMPKIN = IntBlock.builder().name("carved_pumpkin").id(410).maxStackSize(64).diggable(true).transparent(true).solid(true).filterLight(15).hardness(1f).metadataClass(SimpleDirectional.class).build();
    public static final BlockType SEA_PICKLE = IntBlock.builder().name("sea_pickle").id(411).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(0f).build();
    public static final BlockType CONDUIT = IntBlock.builder().name("conduit").id(412).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(3f).build();

    public static final BlockType TURTLE_EGG = IntBlock.builder().name("turtle_egg").id(414).maxStackSize(64).diggable(true).transparent(true).solid(true).hardness(0f).build();
    public static final BlockType BUBBLE_COLUMN = IntBlock.builder().name("bubble_column").id(415).maxStackSize(64).diggable(false).transparent(true).solid(false).hardness(0f).build();

    public static BlockType byName(@Nonnull String name) {
        Preconditions.checkNotNull(name, "name");
        BlockType type = BY_NAME.get(name);
        if (type == null) {
            throw new IllegalArgumentException("Name " + name + " is not valid.");
        }
        return type;
    }

    public static BlockType byId(int id) {
        BlockType type = BY_ID.get(id);
        if (type == null) {
            throw new IllegalArgumentException("ID " + (id < 0 ? 255 - id : id) + " is not valid.");
        }
        return type;
    }

    @Builder
    private static class IntBlock implements BlockType {
        private final int id;
        @NonNull
        private final String name;
        private final int maxStackSize;
        private final boolean diggable;
        private final boolean transparent;
        private final boolean flammable;
        private final boolean floodable;
        private final boolean solid;
        private final int emitLight;
        private final int filterLight;
        private final float hardness;
        private final int burnChance;
        private final int burnability;
        private final float resistance;
        private final Class<? extends Metadata> metadataClass;
        private final Class<? extends BlockEntity> blockEntityClass;

        public IntBlock(int id, String name, int maxStackSize, boolean diggable, boolean transparent, boolean flammable,
                        boolean floodable, boolean solid, int emitLight, int filterLight, float hardness, int burnChance,
                        int burnability, float resistance, Class<? extends Metadata> aClass, Class<? extends BlockEntity> blockEntityClass) {
            if (id > 255) {
                id = 255 - id;
            }
            this.id = id;
            this.name = name;
            this.maxStackSize = maxStackSize < 1 ? 64 : maxStackSize;
            this.diggable = diggable;
            this.transparent = transparent;
            this.flammable = flammable;
            this.floodable = floodable;
            this.solid = solid;
            this.emitLight = emitLight;
            this.filterLight = filterLight;
            this.hardness = hardness;
            this.burnChance = burnChance;
            this.burnability = burnability;
            this.resistance = resistance;
            this.metadataClass = aClass;
            this.blockEntityClass = blockEntityClass;

            BY_ID.put(id, this);
            BY_NAME.put(name, this);
        }

        @Override
        public int getId() {
            return id;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Class<? extends Metadata> getMetadataClass() {
            return metadataClass;
        }

        @Override
        public int getMaximumStackSize() {
            return maxStackSize;
        }

        @Override
        public boolean isDiggable() {
            return diggable;
        }

        @Override
        public boolean isTransparent() {
            return transparent;
        }

        @Override
        public boolean isFlammable() {
            return flammable;
        }

        @Override
        public int emitsLight() {
            return emitLight;
        }

        @Override
        public int filtersLight() {
            return filterLight;
        }

        @Override
        public float hardness() {
            return hardness;
        }

        @Override
        public boolean isFloodable() {
            return floodable;
        }

        @Override
        public boolean isSolid() {
            return solid;
        }

        @Override
        public int burnChance() {
            return burnChance;
        }

        @Override
        public int burnability() {
            return burnability;
        }

        @Override
        public float resistance() {
            return resistance;
        }

        @Override
        public String toString() {
            return name;
        }

        @Override
        public int hashCode() {
            return id;
        }
    }
}
