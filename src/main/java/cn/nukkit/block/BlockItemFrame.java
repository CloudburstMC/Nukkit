package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityItemFrame;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.network.protocol.LevelEventPacket;
import cn.nukkit.utils.Faceable;

/**
 * Created by Pub4Game on 03.07.2016.
 */
public class BlockItemFrame extends BlockTransparentMeta implements Faceable {

    protected final static int[] FACING = {8, 9, 3, 2, 1, 0};

    public BlockItemFrame() {
        this(0);
    }

    public BlockItemFrame(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return ITEM_FRAME_BLOCK;
    }

    @Override
    public String getName() {
        return "Item Frame";
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!this.getSide(getFacing()).isSolid()) {
                this.level.useBreakOn(this);
                return type;
            }
        }

        return 0;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        BlockEntity blockEntity = this.getLevel().getBlockEntity(this);
        if (!(blockEntity instanceof BlockEntityItemFrame)) {
            return false;
        }

        BlockEntityItemFrame itemFrame = (BlockEntityItemFrame) blockEntity;
        if (itemFrame.getItem() == null) {
            return true;
        }
        if (itemFrame.getItem().getId() == Item.AIR) {
            Item itemToFrame = item.clone();
            if (player != null && !player.isCreative()) {
                item.count--;
            }
            itemToFrame.setCount(1);
            itemFrame.setItem(itemToFrame);
            this.getLevel().addLevelEvent(this, LevelEventPacket.EVENT_SOUND_ITEM_FRAME_ITEM_ADDED);
        } else {
            itemFrame.setItemRotation((itemFrame.getItemRotation() + 1) % 8);
            this.getLevel().addLevelEvent(this, LevelEventPacket.EVENT_SOUND_ITEM_FRAME_ITEM_ROTATED);
        }
        return true;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (target.isSolid() && (!block.isSolid() || block.canBeReplaced())) {
            this.setDamage(FACING[face.getIndex()]);

            this.getLevel().setBlock(this, this, true, true);

            CompoundTag nbt = new CompoundTag()
                    .putString("id", BlockEntity.ITEM_FRAME)
                    .putInt("x", (int) block.x)
                    .putInt("y", (int) block.y)
                    .putInt("z", (int) block.z)
                    .putByte("ItemRotation", 0)
                    .putFloat("ItemDropChance", 1.0f);
            if (item.hasCustomBlockData()) {
                for (Tag aTag : item.getCustomBlockData().getAllTags()) {
                    nbt.put(aTag.getName(), aTag);
                }
            }
            BlockEntity.createBlockEntity(BlockEntity.ITEM_FRAME, this.getChunk(), nbt);
            this.getLevel().addLevelEvent(this, LevelEventPacket.EVENT_SOUND_ITEM_FRAME_PLACED);
            return true;
        }
        return false;
    }

    @Override
    public boolean onBreak(Item item) {
        this.getLevel().setBlock(this, Block.get(BlockID.AIR), true, true);
        this.getLevel().addLevelEvent(this, LevelEventPacket.EVENT_SOUND_ITEM_FRAME_REMOVED);
        return true;
    }

    @Override
    public Item toItem() {
        return Item.get(Item.ITEM_FRAME);
    }

    @Override
    public boolean canPassThrough() {
        return true;
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride() {
        BlockEntity blockEntity = this.level.getBlockEntity(this);

        if (blockEntity instanceof BlockEntityItemFrame) {
            return ((BlockEntityItemFrame) blockEntity).getAnalogOutput();
        }

        return super.getComparatorInputOverride();
    }

    public BlockFace getFacing() {
        switch (this.getDamage()) {
            case 0:
            case 4:
                return BlockFace.WEST;
            case 1:
            case 5:
                return BlockFace.EAST;
            case 2:
            case 6:
                return BlockFace.NORTH;
            case 3:
            case 7:
                return BlockFace.SOUTH;
            case 8:
                return BlockFace.UP;
            case 9:
                return BlockFace.DOWN;
        }

        return BlockFace.UP;
    }

    @Override
    public double getHardness() {
        return 0.25;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean breakWhenPushed() {
        return true;
    }

    @Override
    public BlockFace getBlockFace() {
        return this.getFacing().getOpposite();
    }

    @Override
    public WaterloggingType getWaterloggingType() {
        return WaterloggingType.WHEN_PLACED_IN_WATER;
    }

    @Override
    public boolean canBePushed() {
        return false; // prevent item loss issue with pistons until a working implementation
    }
}
