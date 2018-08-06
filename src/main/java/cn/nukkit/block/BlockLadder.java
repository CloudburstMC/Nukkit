package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

/**
 * Created on 2015/12/8 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockLadder extends BlockTransparentMeta {

    public BlockLadder() {
        this(0);
    }

    public BlockLadder(int meta) {
        super(meta);
        calculateOffsets();
    }

    @Override
    public String getName() {
        return "Ladder";
    }

    @Override
    public int getId() {
        return LADDER;
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

    @Override
    public double getHardness() {
        return 0.4;
    }

    @Override
    public double getResistance() {
        return 2;
    }

    @Override
    public void onEntityCollide(Entity entity) {
        entity.resetFallDistance();
    }

    private double offMinX;
    private double offMinZ;
    private double offMaxX;
    private double offMaxZ;

    private void calculateOffsets() {
        double f = 0.1875;

        switch (this.getDamage()) {
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
    public double getMinX() {
        return this.x + offMinX;
    }

    @Override
    public double getMinZ() {
        return this.z + offMinZ;
    }

    @Override
    public double getMaxX() {
        return this.x + offMaxX;
    }

    @Override
    public double getMaxZ() {
        return this.z + offMaxZ;
    }

    @Override
    protected AxisAlignedBB recalculateCollisionBoundingBox() {
        return super.recalculateBoundingBox();
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (!target.isTransparent()) {
            if (face.getIndex() >= 2 && face.getIndex() <= 5) {
                this.setDamage(face.getIndex());
                this.getLevel().setBlock(block, this, true, true);
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
            if (!this.getSide(BlockFace.fromIndex(faces[this.getDamage()])).isSolid()) {
                this.getLevel().useBreakOn(this);
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
            Item.get(Item.LADDER, 0, 1)
        };
    }
}
