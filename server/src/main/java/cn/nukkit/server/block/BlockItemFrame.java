package cn.nukkit.server.block;

import cn.nukkit.server.Player;
import cn.nukkit.server.blockentity.BlockEntity;
import cn.nukkit.server.blockentity.BlockEntityItemFrame;
import cn.nukkit.server.item.Item;
import cn.nukkit.server.item.ItemBlock;
import cn.nukkit.server.item.ItemItemFrame;
import cn.nukkit.server.level.Level;
import cn.nukkit.server.level.sound.ItemFrameItemAddedSound;
import cn.nukkit.server.level.sound.ItemFrameItemRotated;
import cn.nukkit.server.level.sound.ItemFramePlacedSound;
import cn.nukkit.server.level.sound.ItemFrameRemovedSound;
import cn.nukkit.server.math.BlockFace;
import cn.nukkit.server.nbt.tag.CompoundTag;
import cn.nukkit.server.nbt.tag.Tag;

import java.util.Random;

/**
 * Created by Pub4Game on 03.07.2016.
 */
public class BlockItemFrame extends BlockTransparent {

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
        return "ItemUse Frame";
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
            // We can't use ItemUse.get(item.getId(), item.getDamage(), 1) because
            // we need to keep the item's NBT tags
            Item itemOnFrame = item.clone(); // So we clone the item
            itemOnFrame.setCount(1); // Change it to only one item (if we keep +1, visual glitches will happen)
            itemFrame.setItem(itemOnFrame); // And then we set it on the item frame
            // The item will be removed from the player's hand a few lines ahead
            this.getLevel().addSound(new ItemFrameItemAddedSound(this));
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
            this.getLevel().addSound(new ItemFrameItemRotated(this));
        }
        return true;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (!target.isTransparent() && face.getIndex() > 1 && !block.isSolid()) {
            switch (face) {
                case NORTH:
                    this.meta = 3;
                    break;
                case SOUTH:
                    this.meta = 2;
                    break;
                case WEST:
                    this.meta = 1;
                    break;
                case EAST:
                    this.meta = 0;
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
            this.getLevel().addSound(new ItemFramePlacedSound(this));
            return true;
        }
        return false;
    }

    @Override
    public boolean onBreak(Item item) {
        this.getLevel().setBlock(this, new BlockAir(), true, true);
        this.getLevel().addSound(new ItemFrameRemovedSound(this));
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
        switch (this.meta % 8) {
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
