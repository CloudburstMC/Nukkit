package cn.nukkit.block;

import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityTypes;
import cn.nukkit.blockentity.Furnace;
import cn.nukkit.inventory.ContainerInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.player.Player;
import cn.nukkit.registry.BlockEntityRegistry;
import cn.nukkit.utils.Faceable;
import cn.nukkit.utils.Identifier;
import com.nukkitx.math.vector.Vector3f;

import static cn.nukkit.block.BlockIds.AIR;

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
    public float getHardness() {
        return 3.5f;
    }

    @Override
    public float getResistance() {
        return 17.5f;
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
        this.setMeta(faces[player != null ? player.getDirection().getHorizontalIndex() : 0]);
        this.getLevel().setBlock(block.getPosition(), this, true, true);

        Furnace furnace = BlockEntityRegistry.get().newEntity(BlockEntityTypes.FURNACE, this.getChunk(), this.getPosition());
        furnace.loadAdditionalData(item.getTag());
        if (item.hasCustomName()) {
            furnace.setCustomName(item.getCustomName());
        }

        return true;
    }

    @Override
    public boolean onBreak(Item item) {
        this.getLevel().setBlock(this.getPosition(), Block.get(AIR), true, true);
        return true;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (player != null) {
            BlockEntity blockEntity = this.getLevel().getBlockEntity(this.getPosition());
            Furnace furnace;
            if (blockEntity instanceof Furnace) {
                furnace = (Furnace) blockEntity;
            } else {
                furnace = BlockEntityRegistry.get().newEntity(BlockEntityTypes.FURNACE, this.getChunk(), this.getPosition());
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
        BlockEntity blockEntity = this.level.getBlockEntity(this.getPosition());

        if (blockEntity instanceof Furnace) {
            return ContainerInventory.calculateRedstone(((Furnace) blockEntity).getInventory());
        }

        return super.getComparatorInputOverride();
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(this.getMeta() & 0x7);
    }
}
