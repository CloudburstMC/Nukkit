package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.network.protocol.BlockEventPacket;

/**
 * Created by Snake1999 on 2016/1/17.
 * Package cn.nukkit.block in project nukkit.
 */
public class BlockNoteblock extends BlockSolidMeta {

    public BlockNoteblock() {
        this(0);
    }

    public BlockNoteblock(int meta) {
        super(meta);
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

    public boolean canBeActivated() {
        return true;
    }

    public int getStrength() {
        return this.getDamage();
    }

    public void increaseStrength() {
        if (this.getDamage() < 24) {
            this.setDamage(this.getDamage() + 1);
        } else {
            this.setDamage(0);
        }
    }

    public Instrument getInstrument() {
        Block below = this.down();
        switch (below.getId()) {
            case WOODEN_PLANK:
            case NOTEBLOCK:
            case CRAFTING_TABLE:
                return Instrument.BASS;
            case SAND:
            case SANDSTONE:
            case SOUL_SAND:
                return Instrument.DRUM;
            case GLASS:
            case GLASS_PANEL:
            case GLOWSTONE_BLOCK:
                return Instrument.STICKS;
            case COAL_ORE:
            case DIAMOND_ORE:
            case EMERALD_ORE:
            case GLOWING_REDSTONE_ORE:
            case GOLD_ORE:
            case IRON_ORE:
            case LAPIS_ORE:
            case REDSTONE_ORE:
                return Instrument.BASS_DRUM;
            default:
                return Instrument.PIANO;
        }
    }

    public void emitSound() {
        Instrument instrument = getInstrument();

        BlockEventPacket pk = new BlockEventPacket();
        pk.x = (int) this.x;
        pk.y = (int) this.y;
        pk.z = (int) this.z;
        pk.case1 = instrument.ordinal();
        pk.case2 = this.getStrength();
        this.getLevel().addChunkPacket((int) this.x >> 4, (int) this.z >> 4, pk);

        this.getLevel().addSound(this, instrument.getSound(), 1, this.getStrength()); //TODO: correct pitch
    }

    public boolean onActivate(Item item) {
        return this.onActivate(item, null);
    }

    public boolean onActivate(Item item, Player player) {
        Block up = this.up();
        if (up.getId() == Block.AIR) {
            this.increaseStrength();
            this.emitSound();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL || type == Level.BLOCK_UPDATE_REDSTONE) {
            //TODO: redstone
        }

        return 0;
    }

    public enum Instrument {
        PIANO(Sound.NOTE_HARP),
        BASS_DRUM(Sound.NOTE_BD),
        STICKS(Sound.NOTE_HAT),
        DRUM(Sound.NOTE_SNARE),
        BASS(Sound.NOTE_BASS);

        private final Sound sound;

        Instrument(Sound sound) {
            this.sound = sound;
        }

        public Sound getSound() {
            return sound;
        }
    }

    @Override
    public Item toItem() {
        return new ItemBlock(this, 0);
    }
}
