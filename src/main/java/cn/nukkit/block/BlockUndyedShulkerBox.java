/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.nukkit.block;

import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityShulkerBox;
import cn.nukkit.inventory.ShulkerBoxInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.player.Player;
import cn.nukkit.utils.BlockColor;

/**
 *
 * @author Reece Mackie
 */
public class BlockUndyedShulkerBox extends BlockTransparent {

    public BlockUndyedShulkerBox() {
        super();
    }

    @Override
    public int getId() {
        return UNDYED_SHULKER_BOX;
    }

    @Override
    public String getName() {
        return "Shulker Box";
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 10;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public Item toItem() {
        ItemBlock item = new ItemBlock(this);

        BlockEntityShulkerBox t = (BlockEntityShulkerBox) this.getLevel().getBlockEntity(this);

        if (t != null) {
            ShulkerBoxInventory i = t.getRealInventory();

            if (!i.isEmpty()) {
                CompoundTag nbt = item.getNamedTag();
                if (nbt == null)
                    nbt = new CompoundTag("");

                ListTag<CompoundTag> items = new ListTag<>();

                for (int it = 0; it < i.getSize(); it++) {
                    if (i.getItem(it).getId() != Item.AIR) {
                        CompoundTag d = NBTIO.putItemHelper(i.getItem(it), it);
                        items.add(d);
                    }
                }

                nbt.put("Items", items);

                item.setCompoundTag(nbt);
            }

            if (t.hasName()) {
                item.setCustomName(t.getName());
            }
        }

        return item;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[]{
                    this.toItem()
            };
        }
        return new Item[0];
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        this.getLevel().setBlock(block, this, true);
        CompoundTag nbt = BlockEntity.getDefaultCompound(this, BlockEntity.SHULKER_BOX)
                .putByte("facing", face.getIndex());

        if (item.hasCustomName()) {
            nbt.putString("CustomName", item.getCustomName());
        }

        CompoundTag t = item.getNamedTag();

        if (t != null) {
            if (t.contains("Items")) {
                nbt.putList(t.getList("Items"));
            }
        }

        new BlockEntityShulkerBox(this.getLevel().getChunk(this.getFloorX() >> 4, this.getFloorZ() >> 4), nbt);

        return true;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (player != null) {
            BlockEntity t = this.getLevel().getBlockEntity(this);
            BlockEntityShulkerBox box;
            if (t instanceof BlockEntityShulkerBox) {
                box = (BlockEntityShulkerBox) t;
            } else {
                CompoundTag nbt = BlockEntity.getDefaultCompound(this, BlockEntity.SHULKER_BOX);
                box = new BlockEntityShulkerBox(this.getLevel().getChunk(this.getFloorX() >> 4, this.getFloorZ() >> 4), nbt);
            }

            Block block = this.getSide(BlockFace.fromIndex(box.namedTag.getByte("facing")));
            if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid) && !(block instanceof BlockFlowable)) {
                return true;
            }

            player.addWindow(box.getInventory());
        }

        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.PURPLE_BLOCK_COLOR;
    }
}
