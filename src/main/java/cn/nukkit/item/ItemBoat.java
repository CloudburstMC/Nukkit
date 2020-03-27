package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockWater;
import cn.nukkit.entity.EntityTypes;
import cn.nukkit.entity.vehicle.Boat;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.math.BlockFace;
import cn.nukkit.player.Player;
import cn.nukkit.registry.EntityRegistry;
import cn.nukkit.utils.Identifier;
import com.nukkitx.math.vector.Vector3f;

/**
 * Created by yescallop on 2016/2/13.
 */
public class ItemBoat extends Item {

    public ItemBoat(Identifier id) {
        super(id);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Level level, Player player, Block block, Block target, BlockFace face, Vector3f clickPos) {
        if (face != BlockFace.UP) return false;
        Vector3f spawnPos = Vector3f.from(block.getX() + 0.5,
                block.getY() - (target instanceof BlockWater ? 0.0625 : 0), block.getZ());
        Boat boat = EntityRegistry.get().newEntity(EntityTypes.BOAT, Location.from(spawnPos, level));
        boat.setRotation((player.getYaw() + 90f) % 360, 0);
        boat.setWoodType(this.getMeta());

        if (player.isSurvival()) {
            Item item = player.getInventory().getItemInHand();
            item.setCount(item.getCount() - 1);
            player.getInventory().setItemInHand(item);
        }

        boat.spawnToAll();
        return true;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
