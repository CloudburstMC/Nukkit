package cn.nukkit.block;

import cn.nukkit.blockentity.Barrel;
import cn.nukkit.blockentity.BlockEntity;
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

import static cn.nukkit.blockentity.BlockEntityTypes.BARREL;

public class BlockBarrel extends BlockSolid implements Faceable {

    public BlockBarrel(Identifier id) {
        super(id);
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, Vector3f clickPos, Player player) {
        if (Math.abs(player.getX() - this.getX()) < 2 && Math.abs(player.getZ() - this.getZ()) < 2) {
            float y = player.getY() + player.getEyeHeight();

            if (y - this.getY() > 2) {
                this.setMeta(BlockFace.UP.getIndex());
            } else if (this.getY() - y > 0) {
                this.setMeta(BlockFace.DOWN.getIndex());
            } else {
                this.setMeta(player.getHorizontalFacing().getOpposite().getIndex());
            }
        } else {
            this.setMeta(player.getHorizontalFacing().getOpposite().getIndex());
        }

        this.level.setBlock(block.getPosition(), this, true, false);

        Barrel barrel = BlockEntityRegistry.get().newEntity(BARREL, this.getChunk(), this.getPosition());
        barrel.loadAdditionalData(item.getTag());

        return true;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (player == null) {
            return false;
        }

        BlockEntity blockEntity = level.getBlockEntity(this.getPosition());
        Barrel barrel;
        if (blockEntity instanceof Barrel) {
            barrel = (Barrel) blockEntity;
        } else {
            barrel = BlockEntityRegistry.get().newEntity(BARREL, this.getChunk(), this.getPosition());
        }

        player.addWindow(barrel.getInventory());

        return true;
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
    public BlockColor getColor() {
        return BlockColor.WOOD_BLOCK_COLOR;
    }

    @Override
    public Item toItem() {
        return Item.get(this.id, 0);
    }

    @Override
    public BlockFace getBlockFace() {
        int index = getMeta() & 0x7;
        return BlockFace.fromIndex(index);
    }

    public void setBlockFace(BlockFace face) {
        setMeta((getMeta() & 0x8) | (face.getIndex() & 0x7));
    }

    public boolean isOpen() {
        return (getMeta() & 0x8) == 0x8;
    }

    public void setOpen(boolean open) {
        setMeta((getMeta() & 0x7) | (open ? 0x8 : 0x0));
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride() {
        BlockEntity blockEntity = this.level.getBlockEntity(this.getPosition());

        if (blockEntity instanceof Barrel) {
            return ContainerInventory.calculateRedstone(((Barrel) blockEntity).getInventory());
        }

        return super.getComparatorInputOverride();
    }
}