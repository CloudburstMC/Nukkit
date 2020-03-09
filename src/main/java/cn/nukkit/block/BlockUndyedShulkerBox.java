/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.nukkit.block;

import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.ShulkerBox;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.player.Player;
import cn.nukkit.registry.BlockEntityRegistry;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Identifier;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.nbt.CompoundTagBuilder;
import com.nukkitx.nbt.tag.CompoundTag;

import static cn.nukkit.blockentity.BlockEntityTypes.SHULKER_BOX;

/**
 * @author Reece Mackie
 */
public class BlockUndyedShulkerBox extends BlockTransparent {

    public BlockUndyedShulkerBox(Identifier id) {
        super(id);
    }

    @Override
    public float getHardness() {
        return 2;
    }

    @Override
    public float getResistance() {
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

        ShulkerBox shulkerBox = (ShulkerBox) this.getLevel().getBlockEntity(this.getPosition());

        CompoundTag tag = CompoundTag.EMPTY;
        if (shulkerBox != null) {
            CompoundTagBuilder tagBuilder = CompoundTag.builder();
            shulkerBox.saveAdditionalData(tagBuilder);
            tag = tagBuilder.buildRootTag();
        }

        return Item.get(this.id, 0, 1, tag);
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, Vector3f clickPos, Player player) {
        this.getLevel().setBlock(block.getPosition(), this, true);

        ShulkerBox shulkerBox = BlockEntityRegistry.get().newEntity(SHULKER_BOX, this.getChunk(), this.getPosition());
        shulkerBox.loadAdditionalData(item.getTag());
        if (item.hasCustomName()) {
            shulkerBox.setCustomName(item.getCustomName());
        }
        return true;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (player != null) {
            BlockEntity t = this.getLevel().getBlockEntity(this.getPosition());
            ShulkerBox box;
            if (t instanceof ShulkerBox) {
                box = (ShulkerBox) t;
            } else {

                box = BlockEntityRegistry.get().newEntity(SHULKER_BOX, this.getChunk(), this.getPosition());
            }

            player.addWindow(box.getInventory());
        }

        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.PURPLE_BLOCK_COLOR;
    }

    @Override
    public boolean canWaterlogSource() {
        return true;
    }
}
