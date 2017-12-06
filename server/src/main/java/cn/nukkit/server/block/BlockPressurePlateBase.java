package cn.nukkit.server.block;

import cn.nukkit.api.event.block.BlockRedstoneEvent;
import cn.nukkit.api.event.entity.EntityInteractEvent;
import cn.nukkit.api.event.player.PlayerInteractEvent;
import cn.nukkit.api.event.player.PlayerInteractEvent.Action;
import cn.nukkit.server.Player;
import cn.nukkit.server.entity.Entity;
import cn.nukkit.server.item.Item;
import cn.nukkit.server.item.ItemBlock;
import cn.nukkit.server.level.Level;
import cn.nukkit.server.level.sound.ClickSound;
import cn.nukkit.server.math.AxisAlignedBB;
import cn.nukkit.server.math.BlockFace;

/**
 * Created by Snake1999 on 2016/1/11.
 * Package cn.nukkit.server.block in project nukkit
 */
public abstract class BlockPressurePlateBase extends BlockFlowable {

    protected float onPitch;
    protected float offPitch;

    protected BlockPressurePlateBase() {
        this(0);
    }

    protected BlockPressurePlateBase(int meta) {
        super(meta);
    }

    @Override
    public boolean canPassThrough() {
        return true;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        if (isActivated()) {
            return new AxisAlignedBB(this.x + 0.0625, this.y, this.z + 0.0625, this.x + 0.9375, this.y + 0.03125, this.z + 0.9375);
        } else {
            return new AxisAlignedBB(this.x + 0.0625, this.y, this.z + 0.0625, this.x + 0.9375, this.y + 0.0625, this.z + 0.9375);
        }
    }

    @Override
    public boolean isPowerSource() {
        return true;
    }

    public boolean isActivated() {
        return this.meta == 0;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (this.down().isTransparent()) {
                this.level.useBreakOn(this);
            }
        } else if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            int power = this.getRedstonePower();

            if (power > 0) {
                this.updateState(power);
            }
        }

        return 0;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz) {
//        System.out.println("place");
        if (block.down().isTransparent()) {
            return false;
        }

        this.level.setBlock(block, this, true, true);
        return true;
    }

    @Override
    protected AxisAlignedBB recalculateCollisionBoundingBox() {
        return new AxisAlignedBB(this.x + 0.125, this.y, this.z + 0.125, this.x + 0.875, this.y + 0.25, this.z + 0.875D);
    }

    @Override
    public void onEntityCollide(Entity entity) {
        int power = getRedstonePower();

        if (power == 0) {
            Event ev;

            if (entity instanceof Player) {
                ev = new PlayerInteractEvent((Player) entity, null, this, null, Action.PHYSICAL);
            } else {
                ev = new EntityInteractEvent(entity, this);
            }

            this.level.getServer().getPluginManager().callEvent(ev);

            if (!ev.isCancelled()) {
                updateState(power);
            }
        }
    }

    protected void updateState(int oldStrength) {
        int strength = this.computeRedstoneStrength();
        boolean wasPowered = oldStrength > 0;
        boolean isPowered = strength > 0;

        if (oldStrength != strength) {
            this.setRedstonePower(strength);
            this.level.setBlock(this, this, false, false);

            this.level.updateAroundRedstone(this, null);
            this.level.updateAroundRedstone(this.getLocation().down(), null);

            if (!isPowered && wasPowered) {
                this.playOffSound();
                this.level.getServer().getPluginManager().callEvent(new BlockRedstoneEvent(this, 15, 0));
            } else if (isPowered && !wasPowered) {
                this.playOnSound();
                this.level.getServer().getPluginManager().callEvent(new BlockRedstoneEvent(this, 0, 15));
            }
        }

        if (isPowered) {
            this.level.scheduleUpdate(this, 20);
        }
    }

    @Override
    public boolean onBreak(Item item) {
        this.level.setBlock(this, new BlockAir(), true, true);

        if (this.getRedstonePower() > 0) {
            this.level.updateAroundRedstone(this, null);
            this.level.updateAroundRedstone(this.getLocation().down(), null);
        }

        return true;
    }

    @Override
    public int getWeakPower(BlockFace side) {
        return getRedstonePower();
    }

    @Override
    public int getStrongPower(BlockFace side) {
        return side == BlockFace.UP ? this.getRedstonePower() : 0;
    }

    public int getRedstonePower() {
        return this.meta;
    }

    public void setRedstonePower(int power) {
        this.meta = power;
    }

    protected void playOnSound() {
        this.level.addSound(new ClickSound(this, onPitch));
    }

    protected void playOffSound() {
        this.level.addSound(new ClickSound(this, offPitch));
    }

    protected abstract int computeRedstoneStrength();

    @Override
    public Item toItem() {
        return new ItemBlock(this, 0, 1);
    }
}