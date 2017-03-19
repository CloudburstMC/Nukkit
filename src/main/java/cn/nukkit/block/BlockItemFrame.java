package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityItemFrame;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.sound.ItemFrameItemAddedSound;
import cn.nukkit.level.sound.ItemFrameItemRotated;
import cn.nukkit.level.sound.ItemFramePlacedSound;
import cn.nukkit.level.sound.ItemFrameRemovedSound;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;

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
        return "Item Frame";
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
    public boolean place(Item item, Block block, Block target, int face, double fx, double fy, double fz, Player player) {
        if (!target.isTransparent() && face > 1 && !block.isSolid()) {
            switch (face) {
                case Vector3.SIDE_NORTH:
                    this.meta = 3;
                    break;
                case Vector3.SIDE_SOUTH:
                    this.meta = 2;
                    break;
                case Vector3.SIDE_WEST:
                    this.meta = 1;
                    break;
                case Vector3.SIDE_EAST:
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
    public int[][] getDrops(Item item) {
        BlockEntity blockEntity = this.getLevel().getBlockEntity(this);
        BlockEntityItemFrame itemFrame = (BlockEntityItemFrame) blockEntity;
        int chance = new Random().nextInt(100) + 1;
        if (itemFrame != null && chance <= (itemFrame.getItemDropChance() * 100)) {
            return new int[][]{
                    {Item.ITEM_FRAME, 0, 1}, {itemFrame.getItem().getId(), itemFrame.getItem().getDamage(), 1}
            };
        } else {
            return new int[][]{
                    {Item.ITEM_FRAME, 0, 1}
            };
        }
    }

    @Override
    public boolean canPassThrough() {
        return true;
    }
}
