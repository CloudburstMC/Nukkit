package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.math.NukkitMath;

import java.util.concurrent.ThreadLocalRandom;

public class BlockAmethystCluster extends BlockAmethystBud {

    public BlockAmethystCluster() {
        this(0);
    }

    public BlockAmethystCluster(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return AMETHYST_CLUSTER;
    }

    @Override
    protected int getCrystalHeight() {
        return 7;
    }

    @Override
    protected int getCrystalOffset() {
        return 7;
    }

    @Override
    protected String getSizeName() {
        return "Cluster";
    }

    @Override
    public String getName() {
        return "Amethyst Cluster";
    }

    @Override
    public int getLightLevel() {
        return 5;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (!item.isPickaxe()) {
            return new Item[]{Item.get(ItemID.AMETHYST_SHARD, 0, 2)};
        }

        if (item.hasEnchantment(Enchantment.ID_SILK_TOUCH)) {
            return new Item[]{this.toItem()};
        }

        int amount = 4;
        int fortuneLevel = NukkitMath.clamp(item.getEnchantmentLevel(Enchantment.ID_FORTUNE_DIGGING), 0, 3);
        if (fortuneLevel > 0) {
            int rand = ThreadLocalRandom.current().nextInt(100);
            if (fortuneLevel == 1 && rand <= 33) {
                amount = 8;
            } else if (fortuneLevel == 2 && rand <= 25) {
                amount = rand <= 12 ? 8 : 12;
            } else if (fortuneLevel == 3 && rand <= 20) {
                if (rand <= 6) {
                    amount = 8;
                } else if (rand <= 13) {
                    amount = 12;
                } else {
                    amount = 16;
                }
            }
        }
        return new Item[]{Item.get(ItemID.AMETHYST_SHARD, 0, amount)};
    }
}
