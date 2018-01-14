package cn.nukkit.server.block;

import cn.nukkit.server.item.Item;
import cn.nukkit.server.item.ItemGlowstoneDust;
import cn.nukkit.server.item.enchantment.Enchantment;
import cn.nukkit.server.math.MathHelper;
import cn.nukkit.server.util.BlockColor;

import java.util.Random;

/**
 * Created on 2015/12/6 by xtypr.
 * Package cn.nukkit.server.block in project Nukkit .
 */
public class BlockGlowstone extends BlockTransparent {
    public BlockGlowstone() {
        this(0);
    }

    public BlockGlowstone(int meta) {
        super(0);
    }

    @Override
    public String getName() {
        return "Glowstone";
    }

    @Override
    public int getId() {
        return GLOWSTONE_BLOCK;
    }

    @Override
    public double getResistance() {
        return 1.5;
    }

    @Override
    public double getHardness() {
        return 0.3;
    }

    @Override
    public int getLightLevel() {
        return 15;
    }

    @Override
    public Item[] getDrops(Item item) {
        Random random = new Random();
        int count = 2 + random.nextInt(3);

        Enchantment fortune = item.getEnchantment(Enchantment.ID_FORTUNE_DIGGING);
        if (fortune != null && fortune.getLevel() >= 1) {
            count += random.nextInt(fortune.getLevel() + 1);
        }

        return new Item[]{
                new ItemGlowstoneDust(0, MathHelper.clamp(count, 1, 4))
        };
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.AIR_BLOCK_COLOR;
    }
}
