package cn.nukkit.server.block;

import cn.nukkit.server.Player;
import cn.nukkit.server.blockentity.BlockEntity;
import cn.nukkit.server.blockentity.BlockEntityHopper;
import cn.nukkit.server.inventory.ContainerInventory;
import cn.nukkit.server.item.Item;
import cn.nukkit.server.item.ItemHopper;
import cn.nukkit.server.item.ItemTool;
import cn.nukkit.server.level.Level;
import cn.nukkit.server.math.BlockFace;
import cn.nukkit.server.nbt.tag.CompoundTag;
import cn.nukkit.server.nbt.tag.ListTag;

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
    public double getHardness() {
        return 3;
    }

    @Override
    public double getResistance() {
        return 24;
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

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[]{toItem()};
        }

        return new Item[0];
    }

    @Override
    public Item toItem() {
        return new ItemHopper();
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
