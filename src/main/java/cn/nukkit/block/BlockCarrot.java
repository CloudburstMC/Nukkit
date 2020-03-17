package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemIds;
import cn.nukkit.utils.Identifier;

import java.util.Random;

/**
 * @author Nukkit Project Team
 */
public class BlockCarrot extends BlockCrops {

    public BlockCarrot(Identifier id) {
        super(id);
    }

    @Override
    public Item[] getDrops(Item item) {
        if (getMeta() >= 0x07) {
            return new Item[]{
                    Item.get(ItemIds.CARROT, 0, new Random().nextInt(3) + 1)
            };
        }
        return new Item[]{
                Item.get(ItemIds.CARROT)
        };
    }

    @Override
    public Item toItem() {
        return Item.get(ItemIds.CARROT);
    }
}
