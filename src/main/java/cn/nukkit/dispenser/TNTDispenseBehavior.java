package cn.nukkit.dispenser;

import cn.nukkit.block.BlockDispenser;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityPrimedTNT;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;

public class TNTDispenseBehavior extends DefaultDispenseBehavior {

    @Override
    public Item dispense(BlockDispenser block, BlockFace face, Item item) {
        Vector3 pos = block.getSide(face).add(0.5, 0, 0.5);

        Entity.createEntity(EntityPrimedTNT.NETWORK_ID,
                block.getLevel().getChunk(pos.getChunkX(), pos.getChunkZ()),
                Entity.getDefaultNBT(pos)).spawnToAll();

        return null;
    }
}
