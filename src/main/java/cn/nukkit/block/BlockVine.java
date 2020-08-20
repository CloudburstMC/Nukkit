package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nonnull;

/**
 * @author Pub4Game
 * @since 15.01.2016
 */
public class BlockVine extends BlockTransparentMeta {

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

    @PowerNukkitOnly
    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    public boolean canBeFlowedInto() {
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

    @Since("1.3.0.0-PN")
    @PowerNukkitOnly
    @Override
    public boolean isSolid(BlockFace side) {
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
        boolean flag = this.getDamage() > 0;
        if ((this.getDamage() & 0x02) > 0) {
            f4 = Math.max(f4, 0.0625);
            f1 = 0;
            f2 = 0;
            f5 = 1;
            f3 = 0;
            f6 = 1;
            flag = true;
        }
        if ((this.getDamage() & 0x08) > 0) {
            f1 = Math.min(f1, 0.9375);
            f4 = 1;
            f2 = 0;
            f5 = 1;
            f3 = 0;
            f6 = 1;
            flag = true;
        }
        if ((this.getDamage() & 0x01) > 0) {
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
        return new SimpleAxisAlignedBB(
                this.x + f1,
                this.y + f2,
                this.z + f3,
                this.x + f4,
                this.y + f5,
                this.z + f6
        );
    }

    @Override
    public boolean place(@Nonnull Item item, @Nonnull Block block, @Nonnull Block target, @Nonnull BlockFace face, double fx, double fy, double fz, Player player) {
        if (target.isSolid() && face.getHorizontalIndex() != -1) {
            this.setDamage(getMetaFromFace(face.getOpposite()));
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
            if (!this.getSide(getFace()).isSolid()) {
                Block up = this.up();
                if (up.getId() != this.getId() || up.getDamage() != this.getDamage()) {
                    this.getLevel().useBreakOn(this, null, null, true);
                    return Level.BLOCK_UPDATE_NORMAL;
                }
            }
        }
        return 0;
    }

    private BlockFace getFace() {
        int meta = this.getDamage();
        if ((meta & 1) > 0) {
            return BlockFace.SOUTH;
        } else if ((meta & 2) > 0) {
            return BlockFace.WEST;
        } else if ((meta & 4) > 0) {
            return BlockFace.NORTH;
        } else if ((meta & 8) > 0) {
            return BlockFace.EAST;
        }

        return BlockFace.SOUTH;
    }

    private int getMetaFromFace(BlockFace face) {
        switch (face) {
            case SOUTH:
            default:
                return 0x01;
            case WEST:
                return 0x02;
            case NORTH:
                return 0x04;
            case EAST:
                return 0x08;
        }
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
    public boolean breaksWhenMoved() {
        return true;
    }

    @Override
    public boolean sticksToPiston() {
        return false;
    }
}
