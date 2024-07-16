package cn.nukkit.block;

import cn.nukkit.Player;
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

import java.util.Map;

public class BlockBarrel extends BlockSolidMeta implements Faceable {

    public BlockBarrel() {
        this(0);
    }

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

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
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

        this.level.setBlock(block, this, true, true);

        CompoundTag nbt = new CompoundTag("")
                .putList(new ListTag<>("Items"))
                .putString("id", BlockEntity.BARREL)
                .putInt("x", (int) this.x)
                .putInt("y", (int) this.y)
                .putInt("z", (int) this.z);

        if (item.hasCustomName()) {
            nbt.putString("CustomName", item.getCustomName());
        }

        if (item.hasCustomBlockData()) {
            Map<String, Tag> customData = item.getCustomBlockData().getTags();
            for (Map.Entry<String, Tag> tag : customData.entrySet()) {
                nbt.put(tag.getKey(), tag.getValue());
            }
        }

        BlockEntity.createBlockEntity(BlockEntity.BARREL, this.getChunk(), nbt);
        return true;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (player == null) {
            return false;
        }

        BlockEntity blockEntity = level.getBlockEntity(this);
        if (!(blockEntity instanceof BlockEntityBarrel)) {
            return false;
        }

        BlockEntityBarrel barrel = (BlockEntityBarrel) blockEntity;

        if (barrel.namedTag.contains("Lock") && barrel.namedTag.get("Lock") instanceof StringTag) {
            if (!barrel.namedTag.getString("Lock").equals(item.getCustomName())) {
                return true;
            }
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
        return new ItemBlock(Block.get(BARREL));
    }

    @Override
    public BlockFace getBlockFace() {
        int index = getDamage() & 0x7;
        return BlockFace.fromIndex(index);
    }

    public void setBlockFace(BlockFace face) {
        this.setDamage((this.getDamage() & 0x8) | (face.getIndex() & 0x7));
    }

    public boolean isOpen() {
        return (this.getDamage() & 0x8) == 0x8;
    }

    public void setOpen(boolean open) {
        this.setDamage((this.getDamage() & 0x7) | (open? 0x8 : 0x0));
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride() {
        BlockEntity blockEntity = this.level.getBlockEntity(this);

        if (blockEntity instanceof BlockEntityBarrel) {
            return ContainerInventory.calculateRedstone(((BlockEntityBarrel) blockEntity).getInventory());
        }

        return super.getComparatorInputOverride();
    }

    @Override
    public boolean canBePushed() {
        return false; // prevent item loss issue with pistons until a working implementation
    }
}
