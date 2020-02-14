package cn.nukkit.block;

import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.ItemFrame;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemIds;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.math.BlockFace;
import cn.nukkit.player.Player;
import cn.nukkit.registry.BlockEntityRegistry;
import cn.nukkit.utils.Identifier;
import com.nukkitx.math.vector.Vector3f;

import java.util.Random;

import static cn.nukkit.block.BlockIds.AIR;
import static cn.nukkit.blockentity.BlockEntityTypes.ITEM_FRAME;

/**
 * Created by Pub4Game on 03.07.2016.
 */
public class BlockItemFrame extends BlockTransparent {

    public BlockItemFrame(Identifier id) {
        super(id);
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (this.getSide(getFacing()).isTransparent()) {
                this.level.useBreakOn(this.getPosition());
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
        BlockEntity blockEntity = this.getLevel().getBlockEntity(this.getPosition());
        ItemFrame itemFrame = (ItemFrame) blockEntity;
        if (itemFrame.getItem() == null || itemFrame.getItem().getId() == AIR) {
            Item itemOnFrame = item.clone();
            if (player != null && player.isSurvival()) {
                itemOnFrame.setCount(itemOnFrame.getCount() - 1);
                player.getInventory().setItemInHand(itemOnFrame);
            }
            itemOnFrame.setCount(1);
            itemFrame.setItem(itemOnFrame);
            this.getLevel().addSound(this.getPosition(), Sound.BLOCK_ITEMFRAME_ADD_ITEM);
        } else {
            itemFrame.setItemRotation((itemFrame.getItemRotation() + 1) % 8);
            this.getLevel().addSound(this.getPosition(), Sound.BLOCK_ITEMFRAME_ROTATE_ITEM);
        }
        return true;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, Vector3f clickPos, Player player) {
        if (!target.isTransparent() && face.getIndex() > 1 && !block.isSolid()) {
            switch (face) {
                case NORTH:
                    this.setMeta(3);
                    break;
                case SOUTH:
                    this.setMeta(2);
                    break;
                case WEST:
                    this.setMeta(1);
                    break;
                case EAST:
                    this.setMeta(0);
                    break;
                default:
                    return false;
            }
            this.getLevel().setBlock(block.getPosition(), this, true, true);

            ItemFrame frame = BlockEntityRegistry.get().newEntity(ITEM_FRAME, this.getChunk(), this.getPosition());
            frame.loadAdditionalData(item.getTag());

            this.getLevel().addSound(this.getPosition(), Sound.BLOCK_ITEMFRAME_PLACE);
            return true;
        }
        return false;
    }

    @Override
    public boolean onBreak(Item item) {
        super.onBreak(item);
        this.getLevel().addSound(this.getPosition(), Sound.BLOCK_ITEMFRAME_REMOVE_ITEM);
        return true;
    }

    @Override
    public Item[] getDrops(Item item) {
        BlockEntity blockEntity = this.getLevel().getBlockEntity(this.getPosition());
        ItemFrame itemFrame = (ItemFrame) blockEntity;
        int chance = new Random().nextInt(100) + 1;
        if (itemFrame != null && chance <= (itemFrame.getItemDropChance() * 100)) {
            return new Item[]{
                    toItem(), itemFrame.getItem().clone()
            };
        } else {
            return new Item[]{
                    toItem()
            };
        }
    }

    @Override
    public Item toItem() {
        return Item.get(ItemIds.FRAME);
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
        BlockEntity blockEntity = this.level.getBlockEntity(this.getPosition());

        if (blockEntity instanceof ItemFrame) {
            return ((ItemFrame) blockEntity).getAnalogOutput();
        }

        return super.getComparatorInputOverride();
    }

    public BlockFace getFacing() {
        switch (this.getMeta() & 3) {
            case 0:
                return BlockFace.WEST;
            case 1:
                return BlockFace.EAST;
            case 2:
                return BlockFace.NORTH;
            case 3:
                return BlockFace.SOUTH;
        }

        return null;
    }

    @Override
    public float getHardness() {
        return 0.25f;
    }

    @Override
    public boolean canWaterlogSource() {
        return true;
    }
}
