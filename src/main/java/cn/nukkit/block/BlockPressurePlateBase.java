package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.Event;
import cn.nukkit.event.block.BlockRedstoneEvent;
import cn.nukkit.event.entity.EntityInteractEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerInteractEvent.Action;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.network.protocol.WorldSoundEventPacket;
import cn.nukkit.world.GlobalBlockPalette;
import cn.nukkit.world.World;

/**
 * Created by Snake1999 on 2016/1/11.
 * Package cn.nukkit.block in project nukkit
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
    public double getMinX() {
        return this.x + 0.625;
    }

    @Override
    public double getMinZ() {
        return this.z + 0.625;
    }

    @Override
    public double getMinY() {
        return this.y + 0;
    }

    @Override
    public double getMaxX() {
        return this.x + 0.9375;
    }

    @Override
    public double getMaxZ() {
        return this.z + 0.9375;
    }

    @Override
    public double getMaxY() {
        return isActivated() ? this.y + 0.03125 : this.y + 0.0625;
    }

    @Override
    public boolean isPowerSource() {
        return true;
    }

    public boolean isActivated() {
        return this.getDamage() == 0;
    }

    @Override
    public int onUpdate(int type) {
        if (type == World.BLOCK_UPDATE_NORMAL) {
            if (this.down().isTransparent()) {
                this.world.useBreakOn(this);
            }
        } else if (type == World.BLOCK_UPDATE_SCHEDULED) {
            int power = this.getRedstonePower();

            if (power > 0) {
                this.updateState(power);
            }
        }

        return 0;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (block.down().isTransparent()) {
            return false;
        }

        this.world.setBlock(block, this, true, true);
        return true;
    }

    @Override
    protected AxisAlignedBB recalculateCollisionBoundingBox() {
        return new SimpleAxisAlignedBB(this.x + 0.125, this.y, this.z + 0.125, this.x + 0.875, this.y + 0.25, this.z + 0.875D);
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

            this.world.getServer().getPluginManager().callEvent(ev);

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
            this.world.setBlock(this, this, false, false);

            this.world.updateAroundRedstone(this, null);
            this.world.updateAroundRedstone(this.getLocation().down(), null);

            if (!isPowered && wasPowered) {
                this.playOffSound();
                this.world.getServer().getPluginManager().callEvent(new BlockRedstoneEvent(this, 15, 0));
            } else if (isPowered && !wasPowered) {
                this.playOnSound();
                this.world.getServer().getPluginManager().callEvent(new BlockRedstoneEvent(this, 0, 15));
            }
        }

        if (isPowered) {
            this.world.scheduleUpdate(this, 20);
        }
    }

    @Override
    public boolean onBreak(Item item) {
        this.world.setBlock(this, Block.get(BlockID.AIR), true, true);

        if (this.getRedstonePower() > 0) {
            this.world.updateAroundRedstone(this, null);
            this.world.updateAroundRedstone(this.getLocation().down(), null);
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
        return this.getDamage();
    }

    public void setRedstonePower(int power) {
        this.setDamage(power);
    }

    protected void playOnSound() {
        this.world.addLevelSoundEvent(this.add(0.5, 0.1, 0.5), WorldSoundEventPacket.SOUND_POWER_ON, GlobalBlockPalette.getOrCreateRuntimeId(this.getId(), this.getDamage()));
    }

    protected void playOffSound() {
        this.world.addLevelSoundEvent(this.add(0.5, 0.1, 0.5), WorldSoundEventPacket.SOUND_POWER_OFF, GlobalBlockPalette.getOrCreateRuntimeId(this.getId(), this.getDamage()));
    }

    protected abstract int computeRedstoneStrength();

    @Override
    public Item toItem() {
        return new ItemBlock(this, 0, 1);
    }
}
