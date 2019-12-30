package cn.nukkit.block;

import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityMusic;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.math.BlockFace;
import cn.nukkit.network.protocol.BlockEventPacket;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.player.Player;
import cn.nukkit.utils.Identifier;

import java.util.IdentityHashMap;
import java.util.Map;

import static cn.nukkit.block.BlockIds.*;

/**
 * Created by Snake1999 on 2016/1/17.
 * Package cn.nukkit.block in project nukkit.
 */
public class BlockNoteblock extends BlockSolid {

    private static final Map<Identifier, Instrument> INSTRUMENTS = new IdentityHashMap<>();
    ;

    static {
        INSTRUMENTS.put(GOLD_BLOCK, Instrument.GLOCKENSPIEL);
        INSTRUMENTS.put(CLAY, Instrument.FLUTE);
        INSTRUMENTS.put(PACKED_ICE, Instrument.CHIME);
        INSTRUMENTS.put(WOOL, Instrument.GUITAR);
        INSTRUMENTS.put(BONE_BLOCK, Instrument.XYLOPHONE);
        INSTRUMENTS.put(IRON_BLOCK, Instrument.VIBRAPHONE);
        INSTRUMENTS.put(SOUL_SAND, Instrument.COW_BELL);
        INSTRUMENTS.put(PUMPKIN, Instrument.DIDGERIDOO);
        INSTRUMENTS.put(EMERALD_BLOCK, Instrument.SQUARE_WAVE);
        INSTRUMENTS.put(HAY_BLOCK, Instrument.BANJO);
        INSTRUMENTS.put(GLOWSTONE, Instrument.ELECTRIC_PIANO);
        INSTRUMENTS.put(LOG, Instrument.BASS);
        INSTRUMENTS.put(LOG2, Instrument.BASS);
        INSTRUMENTS.put(PLANKS, Instrument.BASS);
        INSTRUMENTS.put(DOUBLE_WOODEN_SLAB, Instrument.BASS);
        INSTRUMENTS.put(WOODEN_SLAB, Instrument.BASS);
        INSTRUMENTS.put(OAK_STAIRS, Instrument.BASS);
        INSTRUMENTS.put(SPRUCE_STAIRS, Instrument.BASS);
        INSTRUMENTS.put(BIRCH_STAIRS, Instrument.BASS);
        INSTRUMENTS.put(JUNGLE_STAIRS, Instrument.BASS);
        INSTRUMENTS.put(ACACIA_STAIRS, Instrument.BASS);
        INSTRUMENTS.put(DARK_OAK_STAIRS, Instrument.BASS);
        INSTRUMENTS.put(FENCE, Instrument.BASS);
        INSTRUMENTS.put(SPRUCE_FENCE_GATE, Instrument.BASS);
        INSTRUMENTS.put(BIRCH_FENCE_GATE, Instrument.BASS);
        INSTRUMENTS.put(JUNGLE_FENCE_GATE, Instrument.BASS);
        INSTRUMENTS.put(DARK_OAK_FENCE_GATE, Instrument.BASS);
        INSTRUMENTS.put(ACACIA_FENCE_GATE, Instrument.BASS);
        INSTRUMENTS.put(WOODEN_DOOR, Instrument.BASS);
        INSTRUMENTS.put(SPRUCE_DOOR, Instrument.BASS);
        INSTRUMENTS.put(BIRCH_DOOR, Instrument.BASS);
        INSTRUMENTS.put(JUNGLE_DOOR, Instrument.BASS);
        INSTRUMENTS.put(ACACIA_DOOR, Instrument.BASS);
        INSTRUMENTS.put(DARK_OAK_DOOR, Instrument.BASS);
        INSTRUMENTS.put(WOODEN_PRESSURE_PLATE, Instrument.BASS);
        INSTRUMENTS.put(TRAPDOOR, Instrument.BASS);
        INSTRUMENTS.put(STANDING_SIGN, Instrument.BASS);
        INSTRUMENTS.put(WALL_SIGN, Instrument.BASS);
        INSTRUMENTS.put(NOTEBLOCK, Instrument.BASS);
        INSTRUMENTS.put(BOOKSHELF, Instrument.BASS);
        INSTRUMENTS.put(CHEST, Instrument.BASS);
        INSTRUMENTS.put(TRAPPED_CHEST, Instrument.BASS);
        INSTRUMENTS.put(CRAFTING_TABLE, Instrument.BASS);
        INSTRUMENTS.put(JUKEBOX, Instrument.BASS);
        INSTRUMENTS.put(BROWN_MUSHROOM_BLOCK, Instrument.BASS);
        INSTRUMENTS.put(RED_MUSHROOM_BLOCK, Instrument.BASS);
        INSTRUMENTS.put(DAYLIGHT_DETECTOR, Instrument.BASS);
        INSTRUMENTS.put(DAYLIGHT_DETECTOR_INVERTED, Instrument.BASS);
        INSTRUMENTS.put(STANDING_BANNER, Instrument.BASS);
        INSTRUMENTS.put(WALL_BANNER, Instrument.BASS);
        INSTRUMENTS.put(SAND, Instrument.DRUM);
        INSTRUMENTS.put(GRAVEL, Instrument.DRUM);
        INSTRUMENTS.put(CONCRETE_POWDER, Instrument.DRUM);
        INSTRUMENTS.put(GLASS, Instrument.STICKS);
        INSTRUMENTS.put(GLASS_PANE, Instrument.STICKS);
        INSTRUMENTS.put(STAINED_GLASS_PANE, Instrument.STICKS);
        INSTRUMENTS.put(STAINED_GLASS, Instrument.STICKS);
        INSTRUMENTS.put(BEACON, Instrument.STICKS);
        INSTRUMENTS.put(SEA_LANTERN, Instrument.STICKS);
        INSTRUMENTS.put(STONE, Instrument.BASS_DRUM);
        INSTRUMENTS.put(SANDSTONE, Instrument.BASS_DRUM);
        INSTRUMENTS.put(RED_SANDSTONE, Instrument.BASS_DRUM);
        INSTRUMENTS.put(COBBLESTONE, Instrument.BASS_DRUM);
        INSTRUMENTS.put(MOSSY_COBBLESTONE, Instrument.BASS_DRUM);
        INSTRUMENTS.put(BRICK_BLOCK, Instrument.BASS_DRUM);
        INSTRUMENTS.put(STONEBRICK, Instrument.BASS_DRUM);
        INSTRUMENTS.put(NETHER_BRICK, Instrument.BASS_DRUM);
        INSTRUMENTS.put(RED_NETHER_BRICK, Instrument.BASS_DRUM);
        INSTRUMENTS.put(QUARTZ_BLOCK, Instrument.BASS_DRUM);
        INSTRUMENTS.put(DOUBLE_STONE_SLAB, Instrument.BASS_DRUM);
        INSTRUMENTS.put(STONE_SLAB, Instrument.BASS_DRUM);
        INSTRUMENTS.put(DOUBLE_STONE_SLAB2, Instrument.BASS_DRUM);
        INSTRUMENTS.put(STONE_SLAB2, Instrument.BASS_DRUM);
        INSTRUMENTS.put(STONE_STAIRS, Instrument.BASS_DRUM);
        INSTRUMENTS.put(BRICK_STAIRS, Instrument.BASS_DRUM);
        INSTRUMENTS.put(STONE_BRICK_STAIRS, Instrument.BASS_DRUM);
        INSTRUMENTS.put(NETHER_BRICK_STAIRS, Instrument.BASS_DRUM);
        INSTRUMENTS.put(SANDSTONE_STAIRS, Instrument.BASS_DRUM);
        INSTRUMENTS.put(QUARTZ_STAIRS, Instrument.BASS_DRUM);
        INSTRUMENTS.put(RED_SANDSTONE_STAIRS, Instrument.BASS_DRUM);
        INSTRUMENTS.put(PURPUR_STAIRS, Instrument.BASS_DRUM);
        INSTRUMENTS.put(COBBLESTONE_WALL, Instrument.BASS_DRUM);
        INSTRUMENTS.put(NETHER_BRICK_FENCE, Instrument.BASS_DRUM);
        INSTRUMENTS.put(BEDROCK, Instrument.BASS_DRUM);
        INSTRUMENTS.put(GOLD_ORE, Instrument.BASS_DRUM);
        INSTRUMENTS.put(IRON_ORE, Instrument.BASS_DRUM);
        INSTRUMENTS.put(COAL_ORE, Instrument.BASS_DRUM);
        INSTRUMENTS.put(LAPIS_ORE, Instrument.BASS_DRUM);
        INSTRUMENTS.put(DIAMOND_ORE, Instrument.BASS_DRUM);
        INSTRUMENTS.put(REDSTONE_ORE, Instrument.BASS_DRUM);
        INSTRUMENTS.put(LIT_REDSTONE_ORE, Instrument.BASS_DRUM);
        INSTRUMENTS.put(EMERALD_ORE, Instrument.BASS_DRUM);
        INSTRUMENTS.put(DROPPER, Instrument.BASS_DRUM);
        INSTRUMENTS.put(DISPENSER, Instrument.BASS_DRUM);
        INSTRUMENTS.put(FURNACE, Instrument.BASS_DRUM);
        INSTRUMENTS.put(LIT_FURNACE, Instrument.BASS_DRUM);
        INSTRUMENTS.put(OBSIDIAN, Instrument.BASS_DRUM);
        INSTRUMENTS.put(GLOWING_OBSIDIAN, Instrument.BASS_DRUM);
        INSTRUMENTS.put(MOB_SPAWNER, Instrument.BASS_DRUM);
        INSTRUMENTS.put(STONE_PRESSURE_PLATE, Instrument.BASS_DRUM);
        INSTRUMENTS.put(NETHERRACK, Instrument.BASS_DRUM);
        INSTRUMENTS.put(QUARTZ_ORE, Instrument.BASS_DRUM);
        INSTRUMENTS.put(ENCHANTING_TABLE, Instrument.BASS_DRUM);
        INSTRUMENTS.put(END_PORTAL_FRAME, Instrument.BASS_DRUM);
        INSTRUMENTS.put(END_STONE, Instrument.BASS_DRUM);
        INSTRUMENTS.put(END_BRICKS, Instrument.BASS_DRUM);
        INSTRUMENTS.put(ENDER_CHEST, Instrument.BASS_DRUM);
        INSTRUMENTS.put(STAINED_HARDENED_CLAY, Instrument.BASS_DRUM);
        INSTRUMENTS.put(HARDENED_CLAY, Instrument.BASS_DRUM);
        INSTRUMENTS.put(PRISMARINE, Instrument.BASS_DRUM);
        INSTRUMENTS.put(COAL_BLOCK, Instrument.BASS_DRUM);
        INSTRUMENTS.put(PURPUR_BLOCK, Instrument.BASS_DRUM);
        INSTRUMENTS.put(MAGMA, Instrument.BASS_DRUM);
        INSTRUMENTS.put(CONCRETE, Instrument.BASS_DRUM);
        INSTRUMENTS.put(STONECUTTER, Instrument.BASS_DRUM);
        INSTRUMENTS.put(OBSERVER, Instrument.BASS_DRUM);
    }

    public BlockNoteblock(Identifier id) {
        super(id);
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public double getHardness() {
        return 0.8D;
    }

    @Override
    public double getResistance() {
        return 4D;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        this.getLevel().setBlock(block, this, true);
        this.createBlockEntity();
        return true;
    }

    public int getStrength() {
        BlockEntityMusic blockEntity = this.getBlockEntity();
        return blockEntity != null ? blockEntity.getPitch() : 0;
    }

    public void increaseStrength() {
        BlockEntityMusic blockEntity = this.getBlockEntity();
        if (blockEntity != null) {
            blockEntity.changePitch();
        }
    }

    public Instrument getInstrument() {
        return INSTRUMENTS.getOrDefault(this.down().getId(), Instrument.PIANO);
    }

    public void emitSound() {
        if (this.up().getId() != AIR) return;

        Instrument instrument = this.getInstrument();

        this.level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_NOTE, instrument.ordinal() << 8 | this.getStrength());

        BlockEventPacket pk = new BlockEventPacket();
        pk.x = this.getFloorX();
        pk.y = this.getFloorY();
        pk.z = this.getFloorZ();
        pk.case1 = instrument.ordinal();
        pk.case2 = this.getStrength();
        this.getLevel().addChunkPacket(this.getFloorX() >> 4, this.getFloorZ() >> 4, pk);
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        this.increaseStrength();
        this.emitSound();
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_REDSTONE) {
            BlockEntityMusic blockEntity = this.getBlockEntity();
            if (blockEntity != null) {
                if (this.getLevel().isBlockPowered(this)) {
                    if (!blockEntity.isPowered()) {
                        this.emitSound();
                    }
                    blockEntity.setPowered(true);
                } else {
                    blockEntity.setPowered(false);
                }
            }
        }
        return super.onUpdate(type);
    }

    private BlockEntityMusic getBlockEntity() {
        BlockEntity blockEntity = this.getLevel().getBlockEntity(this);
        if (blockEntity instanceof BlockEntityMusic) {
            return (BlockEntityMusic) blockEntity;
        }
        return null;
    }

    private BlockEntityMusic createBlockEntity() {
        return new BlockEntityMusic(this.getLevel().getChunk(this.getFloorX() >> 4, this.getFloorZ() >> 4),
                                        BlockEntity.getDefaultCompound(this, BlockEntity.MUSIC));
    }

    public enum Instrument {
        PIANO(Sound.NOTE_HARP),
        BASS_DRUM(Sound.NOTE_BD),
        DRUM(Sound.NOTE_SNARE),
        STICKS(Sound.NOTE_HAT),
        BASS(Sound.NOTE_BASS),
        GLOCKENSPIEL(Sound.NOTE_BELL),
        FLUTE(Sound.NOTE_FLUTE),
        CHIME(Sound.NOTE_CHIME),
        GUITAR(Sound.NOTE_GUITAR),
        XYLOPHONE(Sound.NOTE_XYLOPHONE),
        VIBRAPHONE(Sound.NOTE_IRON_XYLOPHONE),
        COW_BELL(Sound.NOTE_COW_BELL),
        DIDGERIDOO(Sound.NOTE_DIDGERIDOO),
        SQUARE_WAVE(Sound.NOTE_BIT),
        BANJO(Sound.NOTE_BANJO),
        ELECTRIC_PIANO(Sound.NOTE_PLING);

        private final Sound sound;

        Instrument(Sound sound) {
            this.sound = sound;
        }

        public Sound getSound() {
            return sound;
        }
    }
}
