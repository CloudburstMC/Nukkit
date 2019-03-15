package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityBanner;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBanner;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Faceable;

/**
 * Created by PetteriM1
 */
public class BlockBanner extends BlockTransparentMeta implements Faceable {

    public BlockBanner() {
        this(0);
    }

    public BlockBanner(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return STANDING_BANNER;
    }

    @Override
    public double getHardness() {
        return 1;
    }

    @Override
    public double getResistance() {
        return 5;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public String getName() {
        return "Banner";
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        return null;
    }

    @Override
    public boolean canPassThrough() {
        return true;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (face != BlockFace.DOWN) {
            if (face == BlockFace.UP) {
                this.setDamage(NukkitMath.floorDouble(((player.yaw + 180) * 16 / 360) + 0.5) & 0x0f);
                this.getLevel().setBlock(block, this, true);
            } else {
                this.setDamage(face.getIndex());
                this.getLevel().setBlock(block, new BlockWallBanner(this.getDamage()), true);
            }

            CompoundTag nbt = new CompoundTag("")
                    .putString("id", BlockEntity.BANNER)
                    .putInt("x", (int) this.x)
                    .putInt("y", (int) this.y)
                    .putInt("z", (int) this.z)
                    .putInt("Base", item.getDamage() & 0x0f);
            new BlockEntityBanner(this.level.getChunk((int) this.x >> 4, (int) this.z >> 4), nbt);

            return true;
        }
        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (this.down().getId() == Block.AIR) {
                this.getLevel().useBreakOn(this);

                return Level.BLOCK_UPDATE_NORMAL;
            }
        }

        return 0;
    }

    @Override
    public Item toItem() {
        return new ItemBanner(this.getDamage() & 0x0f);
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(this.getDamage() & 0x7);
    }
}
