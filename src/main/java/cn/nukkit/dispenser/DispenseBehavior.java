package cn.nukkit.dispenser;

import cn.nukkit.block.Block;
import cn.nukkit.item.Item;

/**
 * @author CreeperFace
 */
public interface DispenseBehavior {

    void dispense(Block block, Item stack);

}
