package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.sound.ButtonClickSound;

/**
 * Created by CreeperFace on 27. 11. 2016.
 */
public abstract class BlockButton extends BlockTransparent {

    public BlockButton() {
        this(0);
    }

    public BlockButton(int meta) {
        super(meta);
    }

    @Override
    public double getResistance() {
        return 2.5;
    }

    @Override
    public double getHardness() {
        return 0.5;
    }

    @Override
    public boolean place(Item item, Block block, Block target, int face, double fx, double fy, double fz, Player player) {
        if (target.isTransparent()) {
            return false;
        }

        this.meta = face;
        this.level.setBlock(this, this, true, false);
        return true;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item) {
        if (this.isActivated()) {
            return false;
        }

        this.meta ^= 0x08;
        this.level.setBlock(this, this, true, false);
        this.level.addSound(new ButtonClickSound(this.add(0.5, 0.5, 0.5)));
        this.level.scheduleUpdate(this, 30);
        //TODO: redstone
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            int side = this.meta;
            if (this.isActivated()) side ^= 0x08;

            int[] faces = new int[]{1, 0, 3, 2, 5, 4};

            if (this.getSide(faces[side]).isTransparent()) {
                this.level.useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        } else if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            if (this.isActivated()) {
                this.meta ^= 0x08;
                this.level.setBlock(this, this, true, false);
                this.level.addSound(new ButtonClickSound(this.add(0.5, 0.5, 0.5)));
            }

            return Level.BLOCK_UPDATE_SCHEDULED;
        }

        return 0;
    }

    public boolean isActivated() {
        return ((this.meta & 0x08) == 0x08);
    }
}