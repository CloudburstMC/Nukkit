package cn.nukkit.block;

import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.Chest;
import cn.nukkit.inventory.ContainerInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.player.Player;
import cn.nukkit.registry.BlockEntityRegistry;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;
import cn.nukkit.utils.Identifier;
import com.nukkitx.math.vector.Vector3f;
import lombok.extern.log4j.Log4j2;

import static cn.nukkit.block.BlockIds.AIR;
import static cn.nukkit.blockentity.BlockEntityTypes.CHEST;

/**
 * author: Angelic47
 * Nukkit Project
 */
@Log4j2
public class BlockChest extends BlockTransparent implements Faceable {

    public BlockChest(Identifier id) {
        super(id);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public float getHardness() {
        return 2.5f;
    }

    @Override
    public float getResistance() {
        return 12.5f;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public float getMinX() {
        return this.getX() + 0.0625f;
    }

    @Override
    public float getMinY() {
        return this.getY();
    }

    @Override
    public float getMinZ() {
        return this.getZ() + 0.0625f;
    }

    @Override
    public float getMaxX() {
        return this.getX() + 0.9375f;
    }

    @Override
    public float getMaxY() {
        return this.getY() + 0.9475f;
    }

    @Override
    public float getMaxZ() {
        return this.getZ() + 0.9375f;
    }


    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, Vector3f clickPos, Player player) {
        Chest chest = null;
        int[] faces = {2, 5, 3, 4};
        this.setDamage(faces[player != null ? player.getDirection().getHorizontalIndex() : 0]);

        for (int side = 2; side <= 5; ++side) {
            if ((this.getMeta() == 4 || this.getMeta() == 5) && (side == 4 || side == 5)) {
                continue;
            } else if ((this.getMeta() == 3 || this.getMeta() == 2) && (side == 2 || side == 3)) {
                continue;
            }
            Block c = this.getSide(BlockFace.fromIndex(side));
            if (c instanceof BlockChest && c.getMeta() == this.getMeta()) {
                BlockEntity blockEntity = this.getLevel().getBlockEntity(c.getPosition());
                if (blockEntity instanceof Chest && !((Chest) blockEntity).isPaired()) {
                    chest = (Chest) blockEntity;
                    break;
                }
            }
        }

        this.getLevel().setBlock(block.getPosition(), this, true, true);

        Chest chest1 = BlockEntityRegistry.get().newEntity(CHEST, this.getChunk(), this.getPosition());
        chest1.loadAdditionalData(item.getTag());
        if (item.hasCustomName()) {
            chest1.setCustomName(item.getCustomName());
        }

        if (chest != null) {
            chest.pairWith(chest1);
            chest1.pairWith(chest);
        }

        return true;
    }

    @Override
    public boolean onBreak(Item item) {
        BlockEntity t = this.getLevel().getBlockEntity(this.getPosition());
        if (t instanceof Chest) {
            ((Chest) t).unpair();
        }
        this.getLevel().setBlock(this.getPosition(), Block.get(AIR), true, true);

        return true;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (player != null) {
            Block top = up();
            if (!top.isTransparent()) {
                return true;
            }

            BlockEntity t = this.getLevel().getBlockEntity(this.getPosition());
            Chest chest;
            if (t instanceof Chest) {
                chest = (Chest) t;
            } else {
                chest = BlockEntityRegistry.get().newEntity(CHEST, this.getChunk(), this.getPosition());
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
        BlockEntity blockEntity = this.level.getBlockEntity(this.getPosition());

        if (blockEntity instanceof Chest) {
            return ContainerInventory.calculateRedstone(((Chest) blockEntity).getInventory());
        }

        return super.getComparatorInputOverride();
    }

    @Override
    public Item toItem() {
        return Item.get(id, 0);
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(this.getMeta() & 0x7);
    }
}
