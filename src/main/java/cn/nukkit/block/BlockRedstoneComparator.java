package cn.nukkit.block;

import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.Comparator;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemIds;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.math.BlockFace;
import cn.nukkit.player.Player;
import cn.nukkit.registry.BlockEntityRegistry;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Identifier;
import com.nukkitx.math.vector.Vector3f;

import static cn.nukkit.block.BlockIds.POWERED_COMPARATOR;
import static cn.nukkit.block.BlockIds.UNPOWERED_COMPARATOR;
import static cn.nukkit.blockentity.BlockEntityTypes.COMPARATOR;

/**
 * @author CreeperFace
 */
public abstract class BlockRedstoneComparator extends BlockRedstoneDiode {

    public BlockRedstoneComparator(Identifier id) {
        super(id);
    }

    @Override
    protected int getDelay() {
        return 2;
    }

    @Override
    public BlockFace getFacing() {
        return BlockFace.fromHorizontalIndex(this.getMeta());
    }

    public Mode getMode() {
        return (getMeta() & 4) > 0 ? Mode.SUBTRACT : Mode.COMPARE;
    }

    @Override
    protected Block getUnpowered() {
        return Block.get(UNPOWERED_COMPARATOR, this.getMeta());
    }

    @Override
    protected Block getPowered() {
        return Block.get(POWERED_COMPARATOR, this.getMeta());
    }

    @Override
    protected int getRedstoneSignal() {
        BlockEntity blockEntity = this.level.getBlockEntity(this.getPosition());

        return blockEntity instanceof Comparator ? ((Comparator) blockEntity).getOutputSignal() : 0;
    }

    @Override
    public void updateState() {
        if (!this.level.isBlockTickPending(this.getPosition(), this)) {
            int output = this.calculateOutput();
            BlockEntity blockEntity = this.level.getBlockEntity(this.getPosition());
            int power = blockEntity instanceof Comparator ? ((Comparator) blockEntity).getOutputSignal() : 0;

            if (output != power || this.isPowered() != this.shouldBePowered()) {
                /*if(isFacingTowardsRepeater()) {
                    this.level.scheduleUpdate(this, this, 2, -1);
                } else {
                    this.level.scheduleUpdate(this, this, 2, 0);
                }*/

                //System.out.println("schedule update 0");
                this.level.scheduleUpdate(this, this.getPosition(), 2);
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
            this.setMeta(this.getMeta() - 4);
        } else {
            this.setMeta(this.getMeta() + 4);
        }

        this.level.addSound(this.getPosition(), Sound.RANDOM_CLICK, 1, getMode() == Mode.SUBTRACT ? 0.55F : 0.5F);
        this.level.setBlock(this.getPosition(), this, true, false);
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
        int output = this.calculateOutput();
        BlockEntity blockEntity = this.level.getBlockEntity(this.getPosition());
        int currentOutput = 0;

        if (blockEntity instanceof Comparator) {
            Comparator blockEntityComparator = (Comparator) blockEntity;
            currentOutput = blockEntityComparator.getOutputSignal();
            blockEntityComparator.setOutputSignal(output);
        }

        if (currentOutput != output || getMode() == Mode.COMPARE) {
            boolean shouldBePowered = this.shouldBePowered();
            boolean isPowered = this.isPowered();

            if (isPowered && !shouldBePowered) {
                this.level.setBlock(this.getPosition(), getUnpowered(), true, false);
            } else if (!isPowered && shouldBePowered) {
                this.level.setBlock(this.getPosition(), getPowered(), true, false);
            }

            this.level.updateAroundRedstone(this.getPosition(), null);
        }
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, Vector3f clickPos, Player player) {
        if (super.place(item, block, target, face, clickPos, player)) {
            BlockEntityRegistry.get().newEntity(COMPARATOR, this.getChunk(), this.getPosition());

            this.onUpdate(Level.BLOCK_UPDATE_REDSTONE);
            return true;
        }

        return false;
    }

    @Override
    public boolean isPowered() {
        return this.isPowered || (this.getMeta() & 8) > 0;
    }

    @Override
    public Item toItem() {
        return Item.get(ItemIds.COMPARATOR);
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
