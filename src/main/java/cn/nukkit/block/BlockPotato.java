package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.utils.Utils;

/**
 * Created by Pub4Game on 15.01.2016.
 */
public class BlockPotato extends BlockCrops {

    public BlockPotato(int meta) {
        super(meta);
    }

    public BlockPotato() {
        this(0);
    }

    @Override
    public String getName() {
        return "Potato Block";
    }

    @Override
    public int getId() {
        return POTATO_BLOCK;
    }

    @Override
    public Item toItem() {
        return Item.get(Item.POTATO);
    }

    @Override
    public Item[] getDrops(Item item) {
        if (getDamage() >= 0x07) {
            if (Utils.random.nextInt(100) < 2) {
                return new Item[]{
                        Item.get(Item.POTATO, 0, Utils.random.nextInt(3) + 2),
                        Item.get(Item.POISONOUS_POTATO, 0, 1)
                };
            } else {
                return new Item[]{
                        Item.get(Item.POTATO, 0, Utils.random.nextInt(3) + 2)
                };
            }
        } else {
            return new Item[]{
                    Item.get(Item.POTATO)
            };
        }
    }
}
