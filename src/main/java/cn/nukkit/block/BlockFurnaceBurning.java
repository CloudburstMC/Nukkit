package cn.nukkit.block;

import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityFurnace;
import cn.nukkit.inventory.ContainerInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.player.Player;
import cn.nukkit.utils.Faceable;
import cn.nukkit.utils.Identifier;

import java.util.Map;

import static cn.nukkit.block.BlockIds.*;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class BlockFurnaceBurning extends BlockSolid implements Faceable {

    public BlockFurnaceBurning(Identifier id) {
        super(id);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public double getHardness() {
        return 3.5;
    }

    @Override
    public double getResistance() {
        return 17.5;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public int getLightLevel() {
        return 13;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, Vector3f clickPos, Player player) {
        int[] faces = {2, 5, 3, 4};
        this.setDamage(faces[player != null ? player.getDirection().getHorizontalIndex() : 0]);
        this.getLevel().setBlock(block, this, true, true);
        CompoundTag nbt = new CompoundTag()
                .putList(new ListTag<>("Items"))
                .putString("id", getBlockEntityID())
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

        BlockEntityFurnace furnace = (BlockEntityFurnace) BlockEntity.createBlockEntity(getBlockEntityID(), this.getLevel().getChunk(this.getChunkX(), this.getChunkZ()), nbt);
        return furnace != null;
    }

    protected String getBlockEntityID() {
        if (getId() == BLAST_FURNACE || getId() == LIT_BLAST_FURNACE) {
            return BlockEntity.BLAST_FURNACE;
        } else if (getId() == SMOKER || getId() == LIT_SMOKER) {
            return BlockEntity.SMOKER;
        } else {
            return BlockEntity.FURNACE;
        }
    }

    @Override
    public boolean onBreak(Item item) {
        this.getLevel().setBlock(this, Block.get(AIR), true, true);
        return true;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (player != null) {
            BlockEntity t = this.getLevel().getBlockEntity(this);
            BlockEntityFurnace furnace;
            if (t instanceof BlockEntityFurnace) {
                furnace = (BlockEntityFurnace) t;
            } else {
                CompoundTag nbt = new CompoundTag()
                        .putList(new ListTag<>("Items"))
                        .putString("id", getBlockEntityID())
                        .putInt("x", this.x)
                        .putInt("y", this.y)
                        .putInt("z", this.z);
                furnace = (BlockEntityFurnace) BlockEntity.createBlockEntity(getBlockEntityID(), this.getLevel().getChunk(this.getChunkX(), this.getChunkZ()), nbt);
                if (furnace == null) {
                    return false;
                }
            }

            player.addWindow(furnace.getInventory());
        }

        return true;
    }

    @Override
    public Item toItem() {
        return Item.get(getId(), 0);
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[]{
                    this.toItem()
            };
        } else {
            return new Item[0];
        }
    }

    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride() {
        BlockEntity blockEntity = this.level.getBlockEntity(this);

        if (blockEntity instanceof BlockEntityFurnace) {
            return ContainerInventory.calculateRedstone(((BlockEntityFurnace) blockEntity).getInventory());
        }

        return super.getComparatorInputOverride();
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(this.getDamage() & 0x7);
    }
}
