package cn.nukkit.block;

import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.player.Player;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Identifier;
import com.nukkitx.math.vector.Vector3f;

/**
 * Created by Pub4Game on 15.01.2016.
 */
public class BlockVine extends BlockTransparent {
    public static final int SOUTH = 1;
    public static final int WEST  = 2;
    public static final int NORTH = 4;
    public static final int EAST  = 8;

    public static BlockFace getFace(int meta) {
        if ((meta & EAST) != 0) {
            return BlockFace.EAST;
        } else if ((meta & NORTH) != 0) {
            return BlockFace.NORTH;
        } else if ((meta & WEST) != 0)  {
            return BlockFace.WEST;
        } else {
            return BlockFace.SOUTH;
        }
    }

    public static int getMeta(BlockFace face) {
        switch (face) {
            case SOUTH:
            default:
                return SOUTH;
            case WEST:
                return WEST;
            case NORTH:
                return NORTH;
            case EAST:
                return EAST;
        }
    }

    public BlockVine(Identifier id) {
        super(id);
    }

    @Override
    public float getHardness() {
        return 0.2f;
    }

    @Override
    public float getResistance() {
        return 1;
    }

    @Override
    public boolean canPassThrough() {
        return true;
    }

    @Override
    public boolean hasEntityCollision() {
        return true;
    }

    @Override
    public boolean canBeReplaced() {
        return true;
    }

    @Override
    public boolean canBeClimbed() {
        return true;
    }

    @Override
    public void onEntityCollide(Entity entity) {
        entity.resetFallDistance();
        entity.setOnGround(true);
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        float f1 = 1;
        float f2 = 1;
        float f3 = 1;
        float f4 = 0;
        float f5 = 0;
        float f6 = 0;
        boolean flag = this.getMeta() > 0;
        if ((this.getMeta() & WEST) != 0) {
            f4 = Math.max(f4, 0.0625f);
            f1 = 0;
            f2 = 0;
            f5 = 1;
            f3 = 0;
            f6 = 1;
            flag = true;
        }
        if ((this.getMeta() & EAST) != 0) {
            f1 = Math.min(f1, 0.9375f);
            f4 = 1;
            f2 = 0;
            f5 = 1;
            f3 = 0;
            f6 = 1;
            flag = true;
        }
        if ((this.getMeta() & SOUTH) != 0) {
            f3 = Math.min(f3, 0.9375f);
            f6 = 1;
            f1 = 0;
            f4 = 1;
            f2 = 0;
            f5 = 1;
            flag = true;
        }
        if (!flag && this.up().isSolid()) {
            f2 = Math.min(f2, 0.9375f);
            f5 = 1;
            f1 = 0;
            f4 = 1;
            f3 = 0;
            f6 = 1;
        }
        return new SimpleAxisAlignedBB(
                this.getX() + f1,
                this.getY() + f2,
                this.getZ() + f3,
                this.getX() + f4,
                this.getY() + f5,
                this.getZ() + f6
        );
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, Vector3f clickPos, Player player) {
        if (target.isSolid() && face.getHorizontalIndex() != -1) {
            this.setMeta(getMeta(face.getOpposite()));
            this.getLevel().setBlock(block.getPosition(), this, true, true);
            return true;
        }

        return false;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isShears()) {
            return new Item[]{
                    toItem()
            };
        } else {
            return new Item[0];
        }
    }

    @Override
    public Item toItem() {
        return Item.get(id, 0);
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!this.getSide(getFace(this.getMeta())).isSolid()) {
                Block up = this.up();
                if (up.getId() != this.getId() || up.getMeta() != this.getMeta()) {
                    this.getLevel().useBreakOn(this.getPosition(), null, null, true);
                    return Level.BLOCK_UPDATE_NORMAL;
                }
            }
        }
        return 0;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_SHEARS;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.FOLIAGE_BLOCK_COLOR;
    }

    @Override
    public boolean canWaterlogSource() {
        return true;
    }
}
