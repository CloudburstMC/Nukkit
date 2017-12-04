package cn.nukkit.server.block;

import cn.nukkit.server.Player;
import cn.nukkit.server.item.Item;
import cn.nukkit.server.item.ItemTool;
import cn.nukkit.server.level.Level;
import cn.nukkit.server.level.sound.NoteBoxSound;
import cn.nukkit.server.network.protocol.BlockEventPacket;

/**
 * Created by Snake1999 on 2016/1/17.
 * Package cn.nukkit.server.block in project nukkit.
 */
public class BlockNoteblock extends BlockSolid {

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
        return this.meta;
    }

    public void increaseStrength() {
        if (this.meta < 24) {
            this.meta++;
        } else {
            this.meta = 0;
        }
    }

    public int getInstrument() {
        Block below = this.down();
        switch (below.getId()) {
            case WOODEN_PLANK:
            case NOTEBLOCK:
            case CRAFTING_TABLE:
                return NoteBoxSound.INSTRUMENT_BASS;
            case SAND:
            case SANDSTONE:
            case SOUL_SAND:
                return NoteBoxSound.INSTRUMENT_TABOUR;
            case GLASS:
            case GLASS_PANEL:
            case GLOWSTONE_BLOCK:
                return NoteBoxSound.INSTRUMENT_CLICK;
            case COAL_ORE:
            case DIAMOND_ORE:
            case EMERALD_ORE:
            case GLOWING_REDSTONE_ORE:
            case GOLD_ORE:
            case IRON_ORE:
            case LAPIS_ORE:
            case REDSTONE_ORE:
                return NoteBoxSound.INSTRUMENT_BASS_DRUM;
            default:
                return NoteBoxSound.INSTRUMENT_PIANO;
        }
    }

    public void emitSound() {
        BlockEventPacket pk = new BlockEventPacket();
        pk.x = (int) this.x;
        pk.y = (int) this.y;
        pk.z = (int) this.z;
        pk.case1 = this.getInstrument();
        pk.case2 = this.getStrength();
        this.getLevel().addChunkPacket((int) this.x >> 4, (int) this.z >> 4, pk);

        this.getLevel().addSound(new NoteBoxSound(this, this.getInstrument(), this.getStrength()));
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
}
