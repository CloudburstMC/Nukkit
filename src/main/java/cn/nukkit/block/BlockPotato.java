package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemPotato;

import java.util.Random;

/**
 * Created by Pub4Game on 15.01.2016.
 */
public class BlockPotato extends BlockCrops {

    public BlockPotato(int id, int meta) {
        super(id, meta);
    }

    @Override
    public String getName() {
        return "Potato Block";
    }

    @Override
    public Item toItem() {
        return new ItemPotato();
    }

    @Override
    public Item[] getDrops(Item item) {
        if (getDamage() >= 0x07) {
            return new Item[]{
                    new ItemPotato(0, new Random().nextInt(3) + 1)
            };
        } else {
            return new Item[]{
                    new ItemPotato()
            };
        }
    }
}
