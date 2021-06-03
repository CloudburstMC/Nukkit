package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityBarrel;
import cn.nukkit.inventory.ContainerInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;

import javax.annotation.Nonnull;
import java.util.Map;

@PowerNukkitOnly
public class BlockBarrel extends BlockSolidMeta implements Faceable, BlockEntityHolder<BlockEntityBarrel> {

    @PowerNukkitOnly
    public BlockBarrel() {
        this(0);
    }

    @PowerNukkitOnly
    public BlockBarrel(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Barrel";
    }

    @Override
    public int getId() {
        return BARREL;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    @Override
    public String getBlockEntityType() {
        return BlockEntity.BARREL;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public Class<? extends BlockEntityBarrel> getBlockEntityClass() {
        return BlockEntityBarrel.class;
    }

    @Override
    public boolean place(@Nonnull Item item, @Nonnull Block block, @Nonnull Block target, @Nonnull BlockFace face, double fx, double fy, double fz, Player player) {
        if (Math.abs(player.x - this.x) < 2 && Math.abs(player.z - this.z) < 2) {
            double y = player.y + player.getEyeHeight();

            if (y - this.y > 2) {
                this.setDamage(BlockFace.UP.getIndex());
            } else if (this.y - y > 0) {
                this.setDamage(BlockFace.DOWN.getIndex());
            } else {
                this.setDamage(player.getHorizontalFacing().getOpposite().getIndex());
            }
        } else {
            this.setDamage(player.getHorizontalFacing().getOpposite().getIndex());
        }

        CompoundTag nbt = new CompoundTag().putList(new ListTag<>("Items"));

        if (item.hasCustomName()) {
            nbt.putString("CustomName", item.getCustomName());
        }

        if (item.hasCustomBlockData()) {
            Map<String, Tag> customData = item.getCustomBlockData().getTags();
            for (Map.Entry<String, Tag> tag : customData.entrySet()) {
                nbt.put(tag.getKey(), tag.getValue());
            }
        }
        
        return BlockEntityHolder.setBlockAndCreateEntity(this, true, true, nbt) != null;
    }

    @Override
    public boolean onActivate(@Nonnull Item item, Player player) {
        if (player == null) {
            return false;
        }

        BlockEntityBarrel barrel = getOrCreateBlockEntity();

        if (barrel.namedTag.contains("Lock") && barrel.namedTag.get("Lock") instanceof StringTag 
                && !barrel.namedTag.getString("Lock").equals(item.getCustomName())) {
            return false;
        }

        player.addWindow(barrel.getInventory());
        return true;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public double getHardness() {
        return 2.5;
    }

    @Override
    public double getResistance() {
        return 12.5;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WOOD_BLOCK_COLOR;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(new BlockBarrel());
    }

    @Override
    public BlockFace getBlockFace() {
        int index = getDamage() & 0x7;
        return BlockFace.fromIndex(index);
    }

    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    @Override
    public void setBlockFace(BlockFace face) {
        setDamage((getDamage() & 0x8) | (face.getIndex() & 0x7));
    }

    @PowerNukkitOnly
    public boolean isOpen() {
        return (getDamage() & 0x8) == 0x8;
    }

    @PowerNukkitOnly
    public void setOpen(boolean open) {
        setDamage((getDamage() & 0x7) | (open? 0x8 : 0x0));
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride() {
        BlockEntityBarrel blockEntity = getBlockEntity();

        if (blockEntity != null) {
            return ContainerInventory.calculateRedstone(blockEntity.getInventory());
        }

        return super.getComparatorInputOverride();
    }
}
