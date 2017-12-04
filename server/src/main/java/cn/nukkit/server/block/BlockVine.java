package cn.nukkit.server.block;

import cn.nukkit.server.Player;
import cn.nukkit.server.entity.Entity;
import cn.nukkit.server.item.Item;
import cn.nukkit.server.item.ItemBlock;
import cn.nukkit.server.item.ItemTool;
import cn.nukkit.server.level.Level;
import cn.nukkit.server.math.AxisAlignedBB;
import cn.nukkit.server.math.BlockFace;

/**
 * Created by Pub4Game on 15.01.2016.
 */
public class BlockVine extends BlockTransparent {

    public BlockVine(int meta) {
        super(meta);
    }

    public BlockVine() {
        this(0);
    }

    @Override
    public String getName() {
        return "Vines";
    }

    @Override
    public int getId() {
        return VINE;
    }

    @Override
    public double getHardness() {
        return 0.2;
    }

    @Override
    public double getResistance() {
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
        entity.onGround = true;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        double f1 = 1;
        double f2 = 1;
        double f3 = 1;
        double f4 = 0;
        double f5 = 0;
        double f6 = 0;
        boolean flag = this.meta > 0;
        if ((this.meta & 0x02) > 0) {
            f4 = Math.max(f4, 0.0625);
            f1 = 0;
            f2 = 0;
            f5 = 1;
            f3 = 0;
            f6 = 1;
            flag = true;
        }
        if ((this.meta & 0x08) > 0) {
            f1 = Math.min(f1, 0.9375);
            f4 = 1;
            f2 = 0;
            f5 = 1;
            f3 = 0;
            f6 = 1;
            flag = true;
        }
        if ((this.meta & 0x01) > 0) {
            f3 = Math.min(f3, 0.9375);
            f6 = 1;
            f1 = 0;
            f4 = 1;
            f2 = 0;
            f5 = 1;
            flag = true;
        }
        if (!flag && this.up().isSolid()) {
            f2 = Math.min(f2, 0.9375);
            f5 = 1;
            f1 = 0;
            f4 = 1;
            f3 = 0;
            f6 = 1;
        }
        return new AxisAlignedBB(
                this.x + f1,
                this.y + f2,
                this.z + f3,
                this.x + f4,
                this.y + f5,
                this.z + f6
        );
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (target.isSolid()) {
            int[] faces = new int[]{
                    0,
                    0,
                    1,
                    4,
                    8,
                    2
            };
            this.meta = faces[face.getIndex()];
            this.getLevel().setBlock(block, this, true, true);
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
        return new ItemBlock(this, 0);
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            BlockFace[] faces = {
                    BlockFace.DOWN,
                    BlockFace.SOUTH,
                    BlockFace.WEST,
                    BlockFace.DOWN,
                    BlockFace.NORTH,
                    BlockFace.DOWN,
                    BlockFace.DOWN,
                    BlockFace.DOWN,
                    BlockFace.EAST
            };
            if (!this.getSide(faces[this.meta]).isSolid()) {
                Block up = this.up();
                if (up.getId() != this.getId() || up.meta != this.meta) {
                    this.getLevel().useBreakOn(this);
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
}
