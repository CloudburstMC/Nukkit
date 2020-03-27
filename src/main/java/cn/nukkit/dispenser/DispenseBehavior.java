package cn.nukkit.dispenser;

import cn.nukkit.block.BlockDispenser;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import com.nukkitx.math.vector.Vector3i;

/**
 * @author CreeperFace
 */
public interface DispenseBehavior {

    Item dispense(Vector3i pos, BlockDispenser block, BlockFace face, Item item);

}
