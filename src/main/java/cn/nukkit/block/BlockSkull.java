package cn.nukkit.block;

/**
 * @author Justin
 */

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySkull;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemSkull;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Implements BlockEntityHolder only in PowerNukkit")
public class BlockSkull extends BlockTransparentMeta implements BlockEntityHolder<BlockEntitySkull> {

    public BlockSkull() {
        this(0);
    }

    public BlockSkull(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return SKULL_BLOCK;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    @Override
    public String getBlockEntityType() {
        return BlockEntity.SKULL;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public Class<? extends BlockEntitySkull> getBlockEntityClass() {
        return BlockEntitySkull.class;
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
    public boolean isSolid() {
        return false;
    }

    @Since("1.3.0.0-PN")
    @PowerNukkitOnly
    @Override
    public boolean isSolid(BlockFace side) {
        return false;
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
    public String getName() {
        int itemMeta = 0;
        
        if (this.level != null) {
            BlockEntitySkull blockEntity = getBlockEntity();
            if (blockEntity != null) {
                itemMeta = blockEntity.namedTag.getByte("SkullType");
            }
        }

        return ItemSkull.getItemSkullName(itemMeta);
    }

    @Override
    public boolean place(@Nonnull Item item, @Nonnull Block block, @Nonnull Block target, @Nonnull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        switch (face) {
            case NORTH:
            case SOUTH:
            case EAST:
            case WEST:
            case UP:
                this.setDamage(face.getIndex());
                break;
            case DOWN:
            default:
                return false;
        }
        CompoundTag nbt = new CompoundTag()
                .putByte("SkullType", item.getDamage())
                .putByte("Rot", (int) Math.floor((player.yaw * 16 / 360) + 0.5) & 0x0f);
        if (item.hasCustomBlockData()) {
            for (Tag aTag : item.getCustomBlockData().getAllTags()) {
                nbt.put(aTag.getName(), aTag);
            }
        }
        return BlockEntityHolder.setBlockAndCreateEntity(this, true, true, nbt) != null;
        // TODO: 2016/2/3 SPAWN WITHER
    }

    @Override
    public Item[] getDrops(Item item) {
        BlockEntitySkull entitySkull = getBlockEntity();
        int dropMeta = 0;
        if (entitySkull != null) {
            dropMeta = entitySkull.namedTag.getByte("SkullType");
        }
        return new Item[]{
                new ItemSkull(dropMeta)
        };
    }

    @Override
    public Item toItem() {
        BlockEntitySkull blockEntity = getBlockEntity();
        int itemMeta = 0;
        if (blockEntity != null) {
            itemMeta = blockEntity.namedTag.getByte("SkullType");
        }
        return new ItemSkull(itemMeta);
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.AIR_BLOCK_COLOR;
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
