package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityComparator;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemRedstoneComparator;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.utils.BlockColor;

/**
 * @author CreeperFace
 */
public abstract class BlockRedstoneComparator extends BlockRedstoneDiode {

    public BlockRedstoneComparator() {
        this(0);
    }

    public BlockRedstoneComparator(int meta) {
        super(meta);
    }

    @Override
    protected int getDelay() {
        return 2;
    }

    @Override
    public BlockFace getFacing() {
        return BlockFace.fromHorizontalIndex(this.getDamage());
    }

    public Mode getMode() {
        return (getDamage() & 4) > 0 ? Mode.SUBTRACT : Mode.COMPARE;
    }

    @Override
    protected BlockRedstoneComparator getUnpowered() {
        return (BlockRedstoneComparator) Block.get(BlockID.UNPOWERED_COMPARATOR, this.getDamage());
    }

    @Override
    protected BlockRedstoneComparator getPowered() {
        return (BlockRedstoneComparator) Block.get(BlockID.POWERED_COMPARATOR, this.getDamage());
    }

    @Override
    protected int getRedstoneSignal() {
        BlockEntity blockEntity = this.level.getBlockEntity(this);

        return blockEntity instanceof BlockEntityComparator ? ((BlockEntityComparator) blockEntity).getOutputSignal() : 0;
    }

    @Override
    public void updateState() {
        if (!this.level.isBlockTickPending(this, this)) {
            int output = this.calculateOutput();
            BlockEntity blockEntity = this.level.getBlockEntity(this);
            int power = blockEntity instanceof BlockEntityComparator ? ((BlockEntityComparator) blockEntity).getOutputSignal() : 0;

            if (output != power || this.isPowered() != this.shouldBePowered()) {
                /*if(isFacingTowardsRepeater()) {
                    this.level.scheduleUpdate(this, this, 2, -1);
                } else {
                    this.level.scheduleUpdate(this, this, 2, 0);
                }*/

                this.level.scheduleUpdate(this, this, 2);
            }
        }
    }

    protected int calculateInputStrength() {
        int power = super.calculateInputStrength();
        BlockFace face = getFacing();
        Block block = this.getSide(face);

        if (block.hasComparatorInputOverride()) {
            power = block.getComparatorInputOverride();
        } else if (power < 15 && block.isNormalBlock()) {
            block = block.getSide(face);

            if (block.hasComparatorInputOverride()) {
                power = block.getComparatorInputOverride();
            }
        }

        return power;
    }

    protected boolean shouldBePowered() {
        int input = this.calculateInputStrength();

        if (input >= 15) {
            return true;
        } else if (input == 0) {
            return false;
        } else {
            int sidePower = this.getPowerOnSides();
            return sidePower == 0 || input >= sidePower;
        }
    }

    private int calculateOutput() {
        return getMode() == Mode.SUBTRACT ? Math.max(this.calculateInputStrength() - this.getPowerOnSides(), 0) : this.calculateInputStrength();
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (getMode() == Mode.SUBTRACT) {
            this.setDamage(this.getDamage() - 4);
        } else {
            this.setDamage(this.getDamage() + 4);
        }

        this.level.addSound(this, Sound.RANDOM_CLICK, 1, getMode() == Mode.SUBTRACT ? 0.55F : 0.5F);
        this.level.setBlock(this, this, true, false);
        //bug?

        this.onChange();
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            this.onChange();
            return type;
        }

        return super.onUpdate(type);
    }

    private void onChange() {
        if (!this.level.getServer().isRedstoneEnabled()) {
            return;
        }

        int output = this.calculateOutput();
        BlockEntity blockEntity = this.level.getBlockEntity(this);
        int currentOutput = 0;

        if (blockEntity instanceof BlockEntityComparator) {
            BlockEntityComparator blockEntityComparator = (BlockEntityComparator) blockEntity;
            currentOutput = blockEntityComparator.getOutputSignal();
            blockEntityComparator.setOutputSignal(output);
        }

        if (currentOutput != output || getMode() == Mode.COMPARE) {
            boolean shouldBePowered = this.shouldBePowered();
            boolean isPowered = this.isPowered();

            if (isPowered && !shouldBePowered) {
                this.level.setBlock(this, getUnpowered(), true, false);
            } else if (!isPowered && shouldBePowered) {
                this.level.setBlock(this, getPowered(), true, false);
            }

            Block side = this.getSide(getFacing().getOpposite());
            side.onUpdate(Level.BLOCK_UPDATE_REDSTONE);
            this.level.updateAroundRedstone(side, null);
        }
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (super.place(item, block, target, face, fx, fy, fz, player)) {
            CompoundTag nbt = new CompoundTag()
                    .putList(new ListTag<>("Items"))
                    .putString("id", BlockEntity.COMPARATOR)
                    .putInt("x", (int) this.x)
                    .putInt("y", (int) this.y)
                    .putInt("z", (int) this.z);
            BlockEntityComparator comparator = (BlockEntityComparator) BlockEntity.createBlockEntity(BlockEntity.COMPARATOR, this.level.getChunk((int) this.x >> 4, (int) this.z >> 4), nbt);
            if (comparator == null) {
                return false;
            }
            onUpdate(Level.BLOCK_UPDATE_REDSTONE);
            return true;
        }

        return false;
    }

    @Override
    public boolean isPowered() {
        return this.isPowered || (this.getDamage() & 8) > 0;
    }

    @Override
    public Item toItem() {
        return new ItemRedstoneComparator();
    }

    public enum Mode {
        COMPARE,
        SUBTRACT
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.AIR_BLOCK_COLOR;
    }
}
