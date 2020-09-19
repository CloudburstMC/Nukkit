package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityEnchantTable;
import cn.nukkit.inventory.EnchantInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

/**
 * @author CreeperFace
 * @since 2015/11/22
 */
@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Implements BlockEntityHolder only in PowerNukkit")
public class BlockEnchantingTable extends BlockTransparent implements BlockEntityHolder<BlockEntityEnchantTable> {
    public BlockEnchantingTable() {
        // Does nothing
    }

    @Override
    public int getId() {
        return ENCHANTING_TABLE;
    }

    @Override
    public String getName() {
        return "Enchanting Table";
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    @Override
    public String getBlockEntityType() {
        return BlockEntity.ENCHANT_TABLE;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public Class<? extends BlockEntityEnchantTable> getBlockEntityClass() {
        return BlockEntityEnchantTable.class;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public double getHardness() {
        return 5;
    }

    @Override
    public double getResistance() {
        return 6000;
    }

    @PowerNukkitOnly
    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    public int getLightLevel() {
        return 12;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Will return the right BB height")
    @Override
    public double getMaxY() {
        return getY() + 12/16.0;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public boolean place(@Nonnull Item item, @Nonnull Block block, @Nonnull Block target, @Nonnull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {

        CompoundTag nbt = new CompoundTag();

        if (item.hasCustomName()) {
            nbt.putString("CustomName", item.getCustomName());
        }

        if (item.hasCustomBlockData()) {
            Map<String, Tag> customData = item.getCustomBlockData().getTags();
            for (Map.Entry<String, Tag> tag : customData.entrySet()) {
                nbt.put(tag.getKey(), tag.getValue());
            }
        }
        
        return BlockEntityHolder.setBlockAndCreateEntity(this, true, true, nbt) != null;
    }

    @Override
    public boolean onActivate(@Nonnull Item item, @Nullable Player player) {
        if (player == null) {
            return true;
        }
        
        BlockEntityEnchantTable enchantTable = getOrCreateBlockEntity();
        if (enchantTable.namedTag.contains("Lock") && enchantTable.namedTag.get("Lock") instanceof StringTag 
                && !enchantTable.namedTag.getString("Lock").equals(item.getCustomName())) {
            return false;
        }

        player.addWindow(new EnchantInventory(player.getUIInventory(), this.getLocation()), Player.ENCHANT_WINDOW_ID);

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

    @Since("1.3.0.0-PN")
    @PowerNukkitOnly
    @Override
    public boolean isSolid(BlockFace side) {
        return false;
    }
}
