package cn.nukkit.dispenser;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockDispenser;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import com.nukkitx.math.vector.Vector3i;

public class PumpkinDispenseBehavior extends DefaultDispenseBehavior {

    @Override
    public Item dispense(Vector3i position, BlockDispenser block, BlockFace face, Item item) {
        Block target = block.getSide(face);

        //TODO: snowman / golem
        return null;
    }
}
