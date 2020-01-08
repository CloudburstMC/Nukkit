package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityMusic;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.math.BlockFace;
import cn.nukkit.network.protocol.BlockEventPacket;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.BlockColor;

/**
 * Created by Snake1999 on 2016/1/17.
 * Package cn.nukkit.block in project nukkit.
 */
public class BlockNoteblock extends BlockSolid {

    public BlockNoteblock() {

    }

    @Override
    public String getName() {
        return "Note Block";
    }

    @Override
    public int getId() {
        return NOTEBLOCK;
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
        return this.createBlockEntity() != null;
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
        switch (this.down().getId()) {
            case GOLD_BLOCK:
                return Instrument.GLOCKENSPIEL;
            case CLAY_BLOCK:
                return Instrument.FLUTE;
            case PACKED_ICE:
                return Instrument.CHIME;
            case WOOL:
                return Instrument.GUITAR;
            case BONE_BLOCK:
                return Instrument.XYLOPHONE;
            case IRON_BLOCK:
                return Instrument.VIBRAPHONE;
            case SOUL_SAND:
                return Instrument.COW_BELL;
            case PUMPKIN:
                return Instrument.DIDGERIDOO;
            case EMERALD_BLOCK:
                return Instrument.SQUARE_WAVE;
            case HAY_BALE:
                return Instrument.BANJO;
            case GLOWSTONE_BLOCK:
                return Instrument.ELECTRIC_PIANO;
            case LOG:
            case LOG2:
            case PLANKS:
            case DOUBLE_WOODEN_SLAB:
            case WOODEN_SLAB:
            case WOOD_STAIRS:
            case SPRUCE_WOOD_STAIRS:
            case BIRCH_WOOD_STAIRS:
            case JUNGLE_WOOD_STAIRS:
            case ACACIA_WOOD_STAIRS:
            case DARK_OAK_WOOD_STAIRS:
            case FENCE:
            case FENCE_GATE:
            case FENCE_GATE_SPRUCE:
            case FENCE_GATE_BIRCH:
            case FENCE_GATE_JUNGLE:
            case FENCE_GATE_DARK_OAK:
            case FENCE_GATE_ACACIA:
            case DOOR_BLOCK:
            case SPRUCE_DOOR_BLOCK:
            case BIRCH_DOOR_BLOCK:
            case JUNGLE_DOOR_BLOCK:
            case ACACIA_DOOR_BLOCK:
            case DARK_OAK_DOOR_BLOCK:
            case WOODEN_PRESSURE_PLATE:
            case TRAPDOOR:
            case SIGN_POST:
            case WALL_SIGN:
            case NOTEBLOCK:
            case BOOKSHELF:
            case CHEST:
            case TRAPPED_CHEST:
            case CRAFTING_TABLE:
            case JUKEBOX:
            case BROWN_MUSHROOM_BLOCK:
            case RED_MUSHROOM_BLOCK:
            case DAYLIGHT_DETECTOR:
            case DAYLIGHT_DETECTOR_INVERTED:
            case STANDING_BANNER:
            case WALL_BANNER:
                return Instrument.BASS;
            case SAND:
            case GRAVEL:
            case CONCRETE_POWDER:
                return Instrument.DRUM;
            case GLASS:
            case GLASS_PANEL:
            case STAINED_GLASS_PANE:
            case STAINED_GLASS:
            case BEACON:
            case SEA_LANTERN:
                return Instrument.STICKS;
            case STONE:
            case SANDSTONE:
            case RED_SANDSTONE:
            case COBBLESTONE:
            case MOSSY_STONE:
            case BRICKS:
            case STONE_BRICKS:
            case NETHER_BRICK_BLOCK:
            case RED_NETHER_BRICK:
            case QUARTZ_BLOCK:
            case DOUBLE_SLAB:
            case SLAB:
            case DOUBLE_RED_SANDSTONE_SLAB:
            case RED_SANDSTONE_SLAB:
            case COBBLE_STAIRS:
            case BRICK_STAIRS:
            case STONE_BRICK_STAIRS:
            case NETHER_BRICKS_STAIRS:
            case SANDSTONE_STAIRS:
            case QUARTZ_STAIRS:
            case RED_SANDSTONE_STAIRS:
            case PURPUR_STAIRS:
            case COBBLE_WALL:
            case NETHER_BRICK_FENCE:
            case BEDROCK:
            case GOLD_ORE:
            case IRON_ORE:
            case COAL_ORE:
            case LAPIS_ORE:
            case DIAMOND_ORE:
            case REDSTONE_ORE:
            case GLOWING_REDSTONE_ORE:
            case EMERALD_ORE:
            case DROPPER:
            case DISPENSER:
            case FURNACE:
            case BURNING_FURNACE:
            case OBSIDIAN:
            case GLOWING_OBSIDIAN:
            case MONSTER_SPAWNER:
            case STONE_PRESSURE_PLATE:
            case NETHERRACK:
            case QUARTZ_ORE:
            case ENCHANTING_TABLE:
            case END_PORTAL_FRAME:
            case END_STONE:
            case END_BRICKS:
            case ENDER_CHEST:
            case STAINED_TERRACOTTA:
            case TERRACOTTA:
            case PRISMARINE:
            case COAL_BLOCK:
            case PURPUR_BLOCK:
            case MAGMA:
            case CONCRETE:
            case STONECUTTER:
            case OBSERVER:
                return Instrument.BASS_DRUM;
            default:
                return Instrument.PIANO;
        }
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
        return (BlockEntityMusic) BlockEntity.createBlockEntity(BlockEntity.MUSIC, this.getLevel().getChunk(this.getFloorX() >> 4, this.getFloorZ() >> 4),
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

    @Override
    public BlockColor getColor() {
        return BlockColor.WOOD_BLOCK_COLOR;
    }
}
