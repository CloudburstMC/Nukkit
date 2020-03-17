package cn.nukkit.block;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.Event;
import cn.nukkit.event.block.BlockRedstoneEvent;
import cn.nukkit.event.entity.EntityInteractEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerInteractEvent.Action;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.player.Player;
import cn.nukkit.utils.Identifier;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.protocol.bedrock.data.SoundEvent;

import static cn.nukkit.block.BlockIds.AIR;

/**
 * Created by Snake1999 on 2016/1/11.
 * Package cn.nukkit.block in project nukkit
 */
public abstract class BlockPressurePlateBase extends FloodableBlock {

    protected float onPitch;
    protected float offPitch;

    protected BlockPressurePlateBase(Identifier id) {
        super(id);
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
    public float getMinX() {
        return this.getX() + 0.625f;
    }

    @Override
    public float getMinZ() {
        return this.getZ() + 0.625f;
    }

    @Override
    public float getMinY() {
        return this.getY() + 0;
    }

    @Override
    public float getMaxX() {
        return this.getX() + 0.9375f;
    }

    @Override
    public float getMaxZ() {
        return this.getZ() + 0.9375f;
    }

    @Override
    public float getMaxY() {
        return isActivated() ? this.getY() + 0.03125f : this.getY() + 0.0625f;
    }

    @Override
    public boolean isPowerSource() {
        return true;
    }

    public boolean isActivated() {
        return this.getMeta() == 0;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (this.down().isTransparent()) {
                this.level.useBreakOn(this.getPosition());
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
    public boolean place(Item item, Block block, Block target, BlockFace face, Vector3f clickPos, Player player) {
        if (block.down().isTransparent()) {
            return false;
        }

        this.level.setBlock(block.getPosition(), this, true, true);
        return true;
    }

    @Override
    protected AxisAlignedBB recalculateCollisionBoundingBox() {
        return new SimpleAxisAlignedBB(this.getX() + 0.125f, this.getY(), this.getZ() + 0.125f, this.getX() + 0.875f, this.getY() + 0.25f, this.getZ() + 0.875f);
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
            this.level.setBlock(this.getPosition(), this, false, false);

            this.level.updateAroundRedstone(this.getPosition(), null);
            this.level.updateAroundRedstone(this.getPosition().down(), null);

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
        super.onBreak(item);

        if (this.getRedstonePower() > 0) {
            this.level.updateAroundRedstone(this.getPosition(), null);
            this.level.updateAroundRedstone(this.getPosition().down(), null);
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
        return this.getMeta();
    }

    public void setRedstonePower(int power) {
        this.setMeta(power);
    }

    protected void playOnSound() {
        this.level.addLevelSoundEvent(this.getPosition(), SoundEvent.POWER_ON);
    }

    protected void playOffSound() {
        this.level.addLevelSoundEvent(this.getPosition(), SoundEvent.POWER_OFF);
    }

    protected abstract int computeRedstoneStrength();

    @Override
    public Item toItem() {
        return Item.get(AIR, 0, 0);
    }

    @Override
    public boolean canWaterlogSource() {
        return true;
    }
}
