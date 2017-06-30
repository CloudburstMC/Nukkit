package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityEnchantTable;
import cn.nukkit.inventory.EnchantInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.nbt.tag.Tag;

import java.util.Map;

/**
 * Created on 2015/11/22 by CreeperFace.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockEnchantingTable extends BlockTransparent {
    public BlockEnchantingTable() {
        this(0);
    }

    public BlockEnchantingTable(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return ENCHANTING_TABLE;
    }

    @Override
    public String getName() {
        return "Enchanting Table";
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
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        this.getLevel().setBlock(block, this, true, true);

        CompoundTag nbt = new CompoundTag()
                .putString("id", BlockEntity.ENCHANT_TABLE)
                .putInt("x", (int) this.x)
                .putInt("y", (int) this.y)
                .putInt("z", (int) this.z);

        if (item.hasCustomName()) {
            nbt.putString("CustomName", item.getCustomName());
        }

        if (item.hasCustomBlockData()) {
            Map<String, Tag> customData = item.getCustomBlockData().getTags();
            for (Map.Entry<String, Tag> tag : customData.entrySet()) {
                nbt.put(tag.getKey(), tag.getValue());
            }
        }

        BlockEntity.createBlockEntity(BlockEntity.ENCHANT_TABLE, getLevel().getChunk((int) this.x >> 4, (int) this.z >> 4), nbt);

        return true;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (player != null) {
            BlockEntity t = this.getLevel().getBlockEntity(this);
            BlockEntityEnchantTable enchantTable;
            if (t instanceof BlockEntityEnchantTable) {
                enchantTable = (BlockEntityEnchantTable) t;
            } else {
                CompoundTag nbt = new CompoundTag()
                        .putList(new ListTag<>("Items"))
                        .putString("id", BlockEntity.ENCHANT_TABLE)
                        .putInt("x", (int) this.x)
                        .putInt("y", (int) this.y)
                        .putInt("z", (int) this.z);
                enchantTable = new BlockEntityEnchantTable(this.getLevel().getChunk((int) (this.x) >> 4, (int) (this.z) >> 4), nbt);
            }

            if (enchantTable.namedTag.contains("Lock") && enchantTable.namedTag.get("Lock") instanceof StringTag) {
                if (!enchantTable.namedTag.getString("Lock").equals(item.getCustomName())) {
                    return true;
                }
            }

            player.addWindow(new EnchantInventory(this.getLocation()));
        }

        return true;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
