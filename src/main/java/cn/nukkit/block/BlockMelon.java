package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemIds;
import cn.nukkit.item.ItemTool;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Identifier;

import java.util.Random;

/**
 * Created on 2015/12/11 by Pub4Game.
 * Package cn.nukkit.block in project Nukkit .
 */

public class BlockMelon extends BlockSolid {

    public BlockMelon(Identifier id) {
        super(id);
    }

    public float getHardness() {
        return 1;
    }

    @Override
    public float getResistance() {
        return 5;
    }

    @Override
    public Item[] getDrops(Item item) {
        Random random = new Random();
        int count = 3 + random.nextInt(5);

        Enchantment fortune = item.getEnchantment(Enchantment.ID_FORTUNE_DIGGING);
        if (fortune != null && fortune.getLevel() >= 1) {
            count += random.nextInt(fortune.getLevel() + 1);
        }

        return new Item[]{
                Item.get(ItemIds.MELON, 0, Math.min(9, count))
        };
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.LIME_BLOCK_COLOR;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }
}
