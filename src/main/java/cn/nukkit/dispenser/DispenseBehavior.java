package cn.nukkit.dispenser;

import cn.nukkit.block.BlockDispenser;
import cn.nukkit.item.Item;

/**
 * @author CreeperFace
 */
public interface DispenseBehavior {

    void dispense(BlockDispenser block, Item item);

}
