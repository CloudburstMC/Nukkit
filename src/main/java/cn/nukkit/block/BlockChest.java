package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityChest;
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

/**
 * @author Angelic47
 * Nukkit Project
 */
public class BlockChest extends BlockTransparentMeta implements Faceable {

    public BlockChest() {
        this(0);
    }

    public BlockChest(int meta) {
        super(meta);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public int getId() {
        return CHEST;
    }

    @Override
    public String getName() {
        return "Chest";
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
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        BlockEntityChest chest = null;
        this.setDamage(Block.FACES2534[player != null ? player.getDirection().getHorizontalIndex() : 0]);

        for (BlockFace side : BlockFace.Plane.HORIZONTAL) {
            if ((this.getDamage() == 4 || this.getDamage() == 5) && (side == BlockFace.WEST || side == BlockFace.EAST)) {
                continue;
            } else if ((this.getDamage() == 3 || this.getDamage() == 2) && (side == BlockFace.NORTH || side == BlockFace.SOUTH)) {
                continue;
            }
            Block c = this.getSide(side);
            if (c instanceof BlockChest &&
                    (c.getDamage() == this.getDamage() || (c.getDamage() == 0 && this.getDamage() == 2))) { // leveldb states, idk

                BlockEntity blockEntity = this.getLevel().getBlockEntity(c);
                if (blockEntity instanceof BlockEntityChest && !((BlockEntityChest) blockEntity).isPaired()) {
                    chest = (BlockEntityChest) blockEntity;
                    break;
                }
            }
        }

        this.getLevel().setBlock(this, this, true, true);

        CompoundTag nbt = new CompoundTag("")
                .putList(new ListTag<>("Items"))
                .putString("id", BlockEntity.CHEST)
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

        BlockEntityChest blockEntity = (BlockEntityChest) BlockEntity.createBlockEntity(BlockEntity.CHEST, this.getChunk(), nbt);

        if (chest != null) {
            chest.pairWith(blockEntity);
            blockEntity.pairWith(chest);
        }

        return true;
    }

    @Override
    public boolean onBreak(Item item) {
        BlockEntity t = this.getLevel().getBlockEntity(this);
        if (t instanceof BlockEntityChest) {
            ((BlockEntityChest) t).unpair();
        }
        this.getLevel().setBlock(this, Block.get(BlockID.AIR), true, true);

        return true;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (player != null) {
            Block top = this.up();
            if (!(top instanceof BlockStairs)) { // Stairs don't block chest on vanilla
                if (!top.isTransparent()) {
                    return true;
                }
            }

            BlockEntity t = this.getLevel().getBlockEntity(this);
            if (!(t instanceof BlockEntityChest)) {
                return false;
            }

            BlockEntityChest chest = (BlockEntityChest) t;
            if (chest.namedTag.contains("Lock") && chest.namedTag.get("Lock") instanceof StringTag) {
                if (!chest.namedTag.getString("Lock").equals(item.getCustomName())) {
                    return true;
                }
            }

            player.addWindow(chest.getInventory());
        }

        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WOOD_BLOCK_COLOR;
    }

    public boolean hasComparatorInputOverride() {
        return true;
    }

    public int getComparatorInputOverride() {
        BlockEntity blockEntity = this.level.getBlockEntity(this);

        if (blockEntity instanceof BlockEntityChest) {
            return ContainerInventory.calculateRedstone(((BlockEntityChest) blockEntity).getInventory());
        }

        return super.getComparatorInputOverride();
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(this.getId(), 0), 0);
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(this.getDamage() & 0x7);
    }

    @Override
    public boolean canBePushed() {
        return false; // prevent item loss issue with pistons until a working implementation
    }

    @Override
    public WaterloggingType getWaterloggingType() {
        return WaterloggingType.WHEN_PLACED_IN_WATER;
    }
}
