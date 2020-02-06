package cn.nukkit.block;

import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityBarrel;
import cn.nukkit.inventory.ContainerInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.player.Player;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;
import cn.nukkit.utils.Identifier;

import java.util.Map;

public class BlockBarrel extends BlockSolid implements Faceable {

    public BlockBarrel(Identifier id) {
        super(id);
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, Vector3f clickPos, Player player) {
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

        this.level.setBlock(block, this, true, false);

        CompoundTag nbt = new CompoundTag("")
                .putList(new ListTag<>("Items"))
                .putString("id", BlockEntity.BARREL)
                .putInt("x", this.x)
                .putInt("y", this.y)
                .putInt("z", this.z);

        if (item.hasCustomName()) {
            nbt.putString("CustomName", item.getCustomName());
        }

        if (item.hasCustomBlockData()) {
            Map<String, Tag> customData = item.getCustomBlockData().getTags();
            for (Map.Entry<String, Tag> tag : customData.entrySet()) {
                nbt.put(tag.getKey(), tag.getValue());
            }
        }

        BlockEntity.createBlockEntity(BlockEntity.BARREL, this.getLevel().getChunk(this.x >> 4, this.z >> 4), nbt);
        return true;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (player == null) {
            return false;
        }

        BlockEntity blockEntity = level.getBlockEntity(this);
        if (!(blockEntity instanceof BlockEntityBarrel)) {
            CompoundTag nbt = new CompoundTag("")
                    .putList(new ListTag<>("Items"))
                    .putString("id", BlockEntity.BARREL)
                    .putInt("x", this.x)
                    .putInt("y", this.y)
                    .putInt("z", this.z);

            blockEntity = BlockEntity.createBlockEntity(BlockEntity.BARREL, this.getLevel().getChunk(this.x >> 4, this.z >> 4), nbt);
            if (blockEntity instanceof BlockEntityBarrel) {
                ((BlockEntityBarrel) blockEntity).spawnToAll();
            }
        }

        if (!(blockEntity instanceof BlockEntityBarrel)) {
            return false;
        }

        BlockEntityBarrel barrelEntity = (BlockEntityBarrel) blockEntity;

        if (barrelEntity.namedTag.contains("Lock") && barrelEntity.namedTag.get("Lock") instanceof StringTag) {
            if (!barrelEntity.namedTag.getString("Lock").equals(item.getCustomName())) {
                return true;
            }
        }

        player.addWindow(barrelEntity.getInventory());

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
        return Item.get(this.id, 0);
    }

    @Override
    public BlockFace getBlockFace() {
        int index = getDamage() & 0x7;
        return BlockFace.fromIndex(index);
    }

    public void setBlockFace(BlockFace face) {
        setDamage((getDamage() & 0x8) | (face.getIndex() & 0x7));
    }

    public boolean isOpen() {
        return (getDamage() & 0x8) == 0x8;
    }

    public void setOpen(boolean open) {
        setDamage((getDamage() & 0x7) | (open? 0x8 : 0x0));
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

}