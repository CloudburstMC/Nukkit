package cn.nukkit.block;

import cn.nukkit.event.block.DoorToggleEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.math.BlockFace;
import cn.nukkit.player.Player;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;
import cn.nukkit.utils.Identifier;
import com.nukkitx.math.vector.Vector3f;

/**
 * Created on 2015/11/23 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockFenceGate extends BlockTransparent implements Faceable {

    public BlockFenceGate(Identifier id) {
        super(id);
    }

    private static final float[] offMinX = new float[2];
    private static final float[] offMinZ = new float[2];

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    private static final float[] offMaxX = new float[2];
    private static final float[] offMaxZ = new float[2];

    static {
        offMinX[0] = 0;
        offMinZ[0] = 0.375f;
        offMaxX[0] = 1;
        offMaxZ[0] = 0.625f;

        offMinX[1] = 0.375f;
        offMinZ[1] = 0;
        offMaxX[1] = 0.625f;
        offMaxZ[1] = 1;
    }

    @Override
    public float getHardness() {
        return 2;
    }

    @Override
    public float getResistance() {
        return 15;
    }

    private int getOffsetIndex() {
        switch (this.getMeta() & 0x03) {
            case 0:
            case 2:
                return 0;
            default:
                return 1;
        }
    }

    @Override
    public float getMinX() {
        return this.getX() + offMinX[getOffsetIndex()];
    }

    @Override
    public float getMinZ() {
        return this.getZ() + offMinZ[getOffsetIndex()];
    }

    @Override
    public float getMaxX() {
        return this.getX() + offMaxX[getOffsetIndex()];
    }

    @Override
    public float getMaxZ() {
        return this.getZ() + offMaxZ[getOffsetIndex()];
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, Vector3f clickPos, Player player) {
        this.setMeta(player != null ? player.getDirection().getHorizontalIndex() : 0);
        this.getLevel().setBlock(block.getPosition(), this, true, true);

        return true;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (player == null) {
            return false;
        }

        if (!this.toggle(player)) {
            return false;
        }

        this.level.addSound(this.getPosition(), isOpen() ? Sound.RANDOM_DOOR_OPEN : Sound.RANDOM_DOOR_CLOSE);
        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WOOD_BLOCK_COLOR;
    }

    public boolean toggle(Player player) {
        DoorToggleEvent event = new DoorToggleEvent(this, player);
        this.getLevel().getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return false;
        }

        player = event.getPlayer();

        int direction;

        if (player != null) {
            float yaw = player.getYaw();
            float rotation = (yaw - 90) % 360;

            if (rotation < 0) {
                rotation += 360.0f;
            }

            int originDirection = this.getMeta() & 0x01;

            if (originDirection == 0) {
                if (rotation >= 0 && rotation < 180) {
                    direction = 2;
                } else {
                    direction = 0;
                }
            } else {
                if (rotation >= 90 && rotation < 270) {
                    direction = 3;
                } else {
                    direction = 1;
                }
            }
        } else {
            int originDirection = this.getMeta() & 0x01;

            if (originDirection == 0) {
                direction = 0;
            } else {
                direction = 1;
            }
        }

        this.setMeta(direction | ((~this.getMeta()) & 0x04));
        this.level.setBlock(this.getPosition(), this, false, false);
        return true;
    }

    public boolean isOpen() {
        return (this.getMeta() & 0x04) > 0;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_REDSTONE) {
            if ((!isOpen() && this.level.isBlockPowered(this.getPosition())) || (isOpen() && !this.level.isBlockPowered(this.getPosition()))) {
                this.toggle(null);
                return type;
            }
        }

        return 0;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(this.getMeta() & 0x07);
    }

    @Override
    public boolean canWaterlogSource() {
        return true;
    }
}
