package cn.nukkit.block;

import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityBanner;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemIds;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.IntTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.player.Player;
import cn.nukkit.utils.Faceable;
import cn.nukkit.utils.Identifier;

import static cn.nukkit.block.BlockIds.AIR;
import static cn.nukkit.block.BlockIds.WALL_BANNER;

/**
 * Created by PetteriM1
 */
public class BlockBanner extends BlockTransparent implements Faceable {

    public BlockBanner(Identifier id) {
        super(id);
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
                this.getLevel().setBlock(block, Block.get(WALL_BANNER, this.getDamage()), true);
            }

            CompoundTag nbt = BlockEntity.getDefaultCompound(this, BlockEntity.BANNER)
                    .putInt("Base", item.getDamage() & 0xf);

            Tag type = item.getNamedTagEntry("Type");
            if (type instanceof IntTag) {
                nbt.put("Type", type);
            }

            new BlockEntityBanner(this.getChunk(), nbt);

            return true;
        }
        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (this.down().getId() == AIR) {
                this.getLevel().useBreakOn(this);

                return Level.BLOCK_UPDATE_NORMAL;
            }
        }

        return 0;
    }

    @Override
    public Item toItem() {
        BlockEntity blockEntity = this.getLevel().getBlockEntity(this);
        Item item = Item.get(ItemIds.BANNER);
        if (blockEntity instanceof BlockEntityBanner) {
            BlockEntityBanner banner = (BlockEntityBanner) blockEntity;
            item.setDamage(banner.getBaseColor() & 0xf);
            int type = banner.namedTag.getInt("Type");
            if (type > 0) {
                item.setNamedTag(new CompoundTag()
                        .putInt("Type", banner.namedTag.getInt("Type")));
            }
        }
        return item;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(this.getDamage() & 0x7);
    }
}
