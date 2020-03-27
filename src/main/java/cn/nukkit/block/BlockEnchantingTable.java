package cn.nukkit.block;

import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.EnchantingTable;
import cn.nukkit.inventory.EnchantInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.network.protocol.types.ContainerIds;
import cn.nukkit.player.Player;
import cn.nukkit.registry.BlockEntityRegistry;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Identifier;
import com.nukkitx.math.vector.Vector3f;

import static cn.nukkit.blockentity.BlockEntityTypes.ENCHANTING_TABLE;

/**
 * Created on 2015/11/22 by CreeperFace.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockEnchantingTable extends BlockTransparent {
    public BlockEnchantingTable(Identifier id) {
        super(id);
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public float getHardness() {
        return 5;
    }

    @Override
    public float getResistance() {
        return 6000;
    }

    @Override
    public int getLightLevel() {
        return 12;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[]{
                    toItem()
            };
        } else {
            return new Item[0];
        }
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, Vector3f clickPos, Player player) {
        this.getLevel().setBlock(block.getPosition(), this, true, true);

        EnchantingTable enchantingTable = BlockEntityRegistry.get().newEntity(ENCHANTING_TABLE, this.getChunk(), this.getPosition());
        enchantingTable.loadAdditionalData(item.getTag());
        if (item.hasCustomName()) {
            enchantingTable.setCustomName(item.getCustomName());
        }
        return true;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (player != null) {
            BlockEntity blockEntity = this.getLevel().getBlockEntity(this.getPosition());
            if (!(blockEntity instanceof EnchantingTable)) {
                BlockEntityRegistry.get().newEntity(ENCHANTING_TABLE, this.getChunk(), this.getPosition());
            }

            player.addWindow(new EnchantInventory(player.getUIInventory(), this), ContainerIds.ENCHANTING_TABLE);
        }

        return true;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.RED_BLOCK_COLOR;
    }

    @Override
    public boolean canWaterlogSource() {
        return true;
    }
}
