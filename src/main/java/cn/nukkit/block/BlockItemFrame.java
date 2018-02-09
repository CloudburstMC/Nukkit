package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityItemFrame;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemItemFrame;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;

import java.util.Random;

/**
 * Created by Pub4Game on 03.07.2016.
 */
public class BlockItemFrame extends BlockTransparentMeta {

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
            if (this.getSide(getFacing()).isTransparent()) {
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
        BlockEntityItemFrame itemFrame = (BlockEntityItemFrame) blockEntity;
        if (itemFrame.getItem().getId() == Item.AIR) {
            // We can't use Item.get(item.getId(), item.getDamage(), 1) because
            // we need to keep the item's NBT tags
            Item itemOnFrame = item.clone(); // So we clone the item
            itemOnFrame.setCount(1); // Change it to only one item (if we keep +1, visual glitches will happen)
            itemFrame.setItem(itemOnFrame); // And then we set it on the item frame
            // The item will be removed from the player's hand a few lines ahead
            this.getLevel().addSound(this, Sound.BLOCK_ITEMFRAME_ADD_ITEM);
            if (player != null && player.isSurvival()) {
                int count = item.getCount();
                if (count-- <= 0) {
                    player.getInventory().setItemInHand(new ItemBlock(new BlockAir(), 0, 0));
                    return true;
                }
                item.setCount(count);
                player.getInventory().setItemInHand(item);
            }
        } else {
            int itemRot = itemFrame.getItemRotation();
            if (itemRot >= 7) {
                itemRot = 0;
            } else {
                itemRot++;
            }
            itemFrame.setItemRotation(itemRot);
            this.getLevel().addSound(this, Sound.BLOCK_ITEMFRAME_ROTATE_ITEM);
        }
        return true;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (!target.isTransparent() && face.getIndex() > 1 && !block.isSolid()) {
            switch (face) {
                case NORTH:
                    this.setDamage(3);
                    break;
                case SOUTH:
                    this.setDamage(2);
                    break;
                case WEST:
                    this.setDamage(1);
                    break;
                case EAST:
                    this.setDamage(0);
                    break;
                default:
                    return false;
            }
            this.getLevel().setBlock(block, this, true, true);
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
            new BlockEntityItemFrame(this.getLevel().getChunk((int) this.x >> 4, (int) this.z >> 4), nbt);
            this.getLevel().addSound(this, Sound.BLOCK_ITEMFRAME_PLACE);
            return true;
        }
        return false;
    }

    @Override
    public boolean onBreak(Item item) {
        this.getLevel().setBlock(this, new BlockAir(), true, true);
        this.getLevel().addSound(this, Sound.BLOCK_ITEMFRAME_REMOVE_ITEM);
        return true;
    }

    @Override
    public Item[] getDrops(Item item) {
        BlockEntity blockEntity = this.getLevel().getBlockEntity(this);
        BlockEntityItemFrame itemFrame = (BlockEntityItemFrame) blockEntity;
        int chance = new Random().nextInt(100) + 1;
        if (itemFrame != null && chance <= (itemFrame.getItemDropChance() * 100)) {
            return new Item[]{
                    toItem(), Item.get(itemFrame.getItem().getId(), itemFrame.getItem().getDamage(), 1)
            };
        } else {
            return new Item[]{
                    toItem()
            };
        }
    }

    @Override
    public Item toItem() {
        return new ItemItemFrame();
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
        switch (this.getDamage() % 8) {
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
}
