package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityHopper;
import cn.nukkit.inventory.ContainerInventory;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;

/**
 * @author CreeperFace
 */
public class BlockHopper extends BlockTransparent {

    public BlockHopper() {
        this(0);
    }

    public BlockHopper(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return HOPPER_BLOCK;
    }

    @Override
    public String getName() {
        return "Hopper Block";
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        BlockFace facing = face.getOpposite();

        if (facing == BlockFace.UP) {
            facing = BlockFace.DOWN;
        }

        this.meta = facing.getIndex();

        boolean powered = this.level.isBlockPowered(this);

        if (powered == this.isEnabled()) {
            this.setEnabled(!powered);
        }

        this.level.setBlock(this, this);

        CompoundTag nbt = new CompoundTag()
                .putList(new ListTag<>("Items"))
                .putString("id", BlockEntity.HOPPER)
                .putInt("x", (int) this.x)
                .putInt("y", (int) this.y)
                .putInt("z", (int) this.z);

        new BlockEntityHopper(this.level.getChunk(this.getFloorX() >> 4, this.getFloorZ() >> 4), nbt);
        return true;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        BlockEntity blockEntity = this.level.getBlockEntity(this);

        if (blockEntity instanceof BlockEntityHopper) {
            return player.addWindow(((BlockEntityHopper) blockEntity).getInventory()) != -1;
        }

        return false;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride() {
        BlockEntity blockEntity = this.level.getBlockEntity(this);

        if (blockEntity instanceof BlockEntityHopper) {
            return ContainerInventory.calculateRedstone(((BlockEntityHopper) blockEntity).getInventory());
        }

        return super.getComparatorInputOverride();
    }

    public BlockFace getFacing() {
        return BlockFace.fromIndex(this.meta & 7);
    }

    public boolean isEnabled() {
        return (this.meta & 0x08) != 8;
    }

    public void setEnabled(boolean enabled) {
        if (isEnabled() != enabled) {
            this.meta ^= 0x08;
        }
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            boolean powered = this.level.isBlockPowered(this);

            if (powered == this.isEnabled()) {
                this.setEnabled(!powered);
                this.level.setBlock(this, this, true, false);
            }

            return type;
        }

        return 0;
    }
}