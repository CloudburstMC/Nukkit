package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.inventory.EnchantInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.tile.EnchantTable;
import cn.nukkit.tile.Tile;

import java.util.Iterator;
import java.util.Map;

/**
 * Created on 2015/11/22 by CreeperFace.
 * Package cn.nukkit.block in project Nukkit .
 */
public class EnchantingTable extends Solid {
    public EnchantingTable() {
        this(0);
    }

    public EnchantingTable(int meta) {
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
        return Tool.TYPE_PICKAXE;
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
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public int[][] getDrops(Item item) {
        if (item.isPickaxe()) {
            return new int[][]{{Item.ENCHANTING_TABLE, 0, 1}};
        }
        return new int[][]{{}};
    }

    @Override
    public boolean place(Item item, Block block, Block target, int face, double fx, double fy, double fz, Player player) {
        CompoundTag nbt = new CompoundTag()
                .putString("id", Tile.ENCHANT_TABLE)
                .putInt("x", (int) this.x)
                .putInt("y", (int) this.y)
                .putInt("z", (int) this.z);

        if (item.hasCustomName()) {
            nbt.putString("CustomName", item.getCustomName());
        }

        if (item.hasCustomBlockData()) {
            Map<String, Tag> customData = item.getCustomBlockData().getTags();
            Iterator iter = customData.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry tag = (Map.Entry) iter.next();
                nbt.put((String) tag.getKey(), (Tag) tag.getValue());
            }
        }

        Tile.createTile(Tile.ENCHANT_TABLE, getLevel().getChunk((int) this.x >> 4, (int) this.z >> 4), nbt);

        return true;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (player != null) {
            Tile t = getLevel().getTile(this);
            EnchantTable enchant;
            if (t instanceof EnchantTable) {
                enchant = (EnchantTable) t;
            } else {
                CompoundTag nbt = new CompoundTag()
                        .putString("id", Tile.ENCHANT_TABLE)
                        .putInt("x", (int) this.x)
                        .putInt("y", (int) this.y)
                        .putInt("z", (int) this.z);
                enchant = new EnchantTable(this.getLevel().getChunk((int) (this.x) >> 4, (int) (this.z) >> 4), nbt);
            }

            if (enchant.namedTag.contains("Lock") && enchant.namedTag.get("Lock") instanceof StringTag) {
                if (!enchant.namedTag.getString("Lock").equals(item.getCustomName())) {
                    return false;
                }
            }

            if (player.isCreative()) {
                return false;
            }

            player.addWindow(new EnchantInventory(this));
        }

        return true;
    }
}
