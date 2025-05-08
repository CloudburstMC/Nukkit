package cn.nukkit.dispenser;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockDispenser;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockWater;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityBoat;
import cn.nukkit.entity.item.EntityChestBoat;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.Location;
import cn.nukkit.math.BlockFace;

public class ChestBoatDispenseBehavior extends DefaultDispenseBehavior {

    @Override
    public Item dispense(BlockDispenser block, BlockFace face, Item item) {
        Block target = block.getSide(face);

        if (!(target instanceof BlockWater)) {
            if (target.getId() != BlockID.AIR || !(target.down() instanceof BlockWater)) {
                return super.dispense(block, face, item);
            }
        }

        Location pos = target.getLocation().setYaw(face.getHorizontalAngle());

        EntityBoat boat = (EntityChestBoat) Entity.createEntity(EntityChestBoat.NETWORK_ID, block.level.getChunk(pos.getChunkX(), pos.getChunkZ()),
                Entity.getDefaultNBT(pos)
                        .putByte("Variant", getBoatId(item))
        );

        boat.spawnToAll();

        return null;
    }

    private static int getBoatId(Item item) {
        switch (item.getId()) {
            case ItemID.SPRUCE_CHEST_BOAT:
                return 1;
            case ItemID.BIRCH_CHEST_BOAT:
                return 2;
            case ItemID.JUNGLE_CHEST_BOAT:
                return 3;
            case ItemID.ACACIA_CHEST_BOAT:
                return 4;
            case ItemID.DARK_OAK_CHEST_BOAT:
                return 5;
            case ItemID.MANGROVE_CHEST_BOAT:
                return 6;
            case ItemID.BAMBOO_CHEST_RAFT:
                return 7;
            case ItemID.CHERRY_CHEST_BOAT:
                return 8;
            case ItemID.PALE_OAK_CHEST_BOAT:
                return 9;
        }
        return 0;
    }
}
