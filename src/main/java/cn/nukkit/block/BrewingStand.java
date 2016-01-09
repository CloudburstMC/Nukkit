package cn.nukkit.block;


import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.tile.Tile;

import java.util.Iterator;
import java.util.Map;

public class BrewingStand extends Solid {
    protected int id = BREWING_STAND_BLOCK;

    public BrewingStand() {
        this(0);
    }

    public BrewingStand(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Brewing Stand";
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public double getHardness() {
        return 0.5;
    }

    @Override
    public int getToolType() {
        return Tool.TYPE_PICKAXE;
    }

    @Override
    public int getLightLevel() {
        return 1;
    }

    @Override
    public boolean place(Item item, Block block, Block target, int face, double fx, double fy, double fz, Player player) {
        int faces[] = {4, 2, 5, 3};

        meta = faces[player != null ? player.getDirection() : 0];
        getLevel().setBlock(block, this, true, true);

        CompoundTag nbt = new CompoundTag()
                .putList(new ListTag<>("Items"))
                .putString("id", Tile.BREWING_STAND)
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

        Tile.createTile("BrewingStand", getLevel().getChunk((int) this.x >> 4, (int) this.z >> 4), nbt);

        return true;
    }

    @Override
    public boolean onBreak(Item item) {
        this.getLevel().setBlock(this, new Air(), true, true);
        return true;
    }

    @Override
    public int getId() {
        return Block.BREWING_STAND_BLOCK;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (player != null) {
            Tile t = getLevel().getTile(this);
            cn.nukkit.tile.BrewingStand brewing = null;
            if (t instanceof cn.nukkit.tile.BrewingStand) {
                brewing = (cn.nukkit.tile.BrewingStand) t;
            } else {
                CompoundTag nbt = new CompoundTag()
                        .putList(new ListTag<>("Items"))
                        .putString("id", Tile.BREWING_STAND)
                        .putInt("x", (int) this.x)
                        .putInt("y", (int) this.y)
                        .putInt("z", (int) this.z);
                brewing = new cn.nukkit.tile.BrewingStand(this.getLevel().getChunk((int) (this.x) >> 4, (int) (this.z) >> 4), nbt);
            }

            if (brewing.namedTag.contains("Lock") && brewing.namedTag.get("Lock") instanceof StringTag) {
                if (!brewing.namedTag.getString("Lock").equals(item.getCustomName())) {
                    return false;
                }
            }

            if (player.isCreative()) {
                return false;
            }

            player.addWindow(brewing.getInventory());
        }

        return true;
    }

    @Override
    public int[][] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= Tool.TIER_WOODEN) {
            return new int[][]{new int[]{Item.BREWING_STAND, 0, 1}};
        } else {
            return new int[0][];
        }
    }
}
