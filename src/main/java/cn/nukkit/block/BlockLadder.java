package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.player.Player;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;
import cn.nukkit.utils.Identifier;
import com.nukkitx.math.vector.Vector3f;

import static cn.nukkit.block.BlockIds.LADDER;

/**
 * Created on 2015/12/8 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockLadder extends BlockTransparent implements Faceable {

    public BlockLadder(Identifier id) {
        super(id);
        calculateOffsets();
    }

    @Override
    public boolean hasEntityCollision() {
        return true;
    }

    @Override
    public boolean canBeClimbed() {
        return true;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    private float offMinX;
    private float offMinZ;
    private float offMaxX;
    private float offMaxZ;

    @Override
    public float getHardness() {
        return 0.4f;
    }

    @Override
    public float getResistance() {
        return 2;
    }

    private void calculateOffsets() {
        float f = 0.1875f;

        switch (this.getMeta()) {
            case 2:
                this.offMinX = 0;
                this.offMinZ = 1 - f;
                this.offMaxX = 1;
                this.offMaxZ = 1;
                break;
            case 3:
                this.offMinX = 0;
                this.offMinZ = 0;
                this.offMaxX = 1;
                this.offMaxZ = f;
                break;
            case 4:
                this.offMinX = 1 - f;
                this.offMinZ = 0;
                this.offMaxX = 1;
                this.offMaxZ = 1;
                break;
            case 5:
                this.offMinX = 0;
                this.offMinZ = 0;
                this.offMaxX = f;
                this.offMaxZ = 1;
                break;
            default:
                this.offMinX = 0;
                this.offMinZ = 1 ;
                this.offMaxX = 1;
                this.offMaxZ = 1;
                break;
        }
    }

    @Override
    public void setDamage(int meta) {
        super.setDamage(meta);
        calculateOffsets();
    }

    @Override
    public float getMinX() {
        return this.getX() + offMinX;
    }

    @Override
    public float getMinZ() {
        return this.getZ() + offMinZ;
    }

    @Override
    public float getMaxX() {
        return this.getX() + offMaxX;
    }

    @Override
    public float getMaxZ() {
        return this.getZ() + offMaxZ;
    }

    @Override
    protected AxisAlignedBB recalculateCollisionBoundingBox() {
        return super.recalculateBoundingBox();
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, Vector3f clickPos, Player player) {
        if (!target.isTransparent()) {
            if (face.getIndex() >= 2 && face.getIndex() <= 5) {
                this.setDamage(face.getIndex());
                this.getLevel().setBlock(block.getPosition(), this, true, true);
                return true;
            }
        }
        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            int[] faces = {
                    0, //never use
                    1, //never use
                    3,
                    2,
                    5,
                    4
            };
            if (!this.getSide(BlockFace.fromIndex(faces[this.getMeta()])).isSolid()) {
                this.getLevel().useBreakOn(this.getPosition());
                return Level.BLOCK_UPDATE_NORMAL;
            }
        }
        return 0;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.AIR_BLOCK_COLOR;
    }
    
    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{
                Item.get(LADDER, 0, 1)
        };
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(this.getMeta() & 0x07);
    }
}
