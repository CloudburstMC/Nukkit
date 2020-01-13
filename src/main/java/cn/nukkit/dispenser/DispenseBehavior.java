package cn.nukkit.dispenser;

import cn.nukkit.block.BlockDispenser;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;

/**
 * @author CreeperFace
 */
public interface DispenseBehavior {

    Item dispense(BlockDispenser block, BlockFace face, Item item);

}
