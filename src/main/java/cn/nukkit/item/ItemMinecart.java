package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockRail;
import cn.nukkit.entity.EntityTypes;
import cn.nukkit.entity.vehicle.Minecart;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.math.BlockFace;
import cn.nukkit.player.Player;
import cn.nukkit.registry.EntityRegistry;
import cn.nukkit.utils.Identifier;
import cn.nukkit.utils.Rail;
import com.nukkitx.math.vector.Vector3f;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemMinecart extends Item {

    public ItemMinecart(Identifier id) {
        super(id);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Level level, Player player, Block block, Block target, BlockFace face, Vector3f clickPos) {
        if (Rail.isRailBlock(target)) {
            Rail.Orientation type = ((BlockRail) target).getOrientation();
            double adjacent = 0.0D;
            if (type.isAscending()) {
                adjacent = 0.5D;
            }
            Vector3f pos = target.getPosition().toFloat().add(0.5, 0.0625 + adjacent, 0.5);
            Minecart minecart = EntityRegistry.get().newEntity(EntityTypes.MINECART, Location.from(pos, level));
            minecart.spawnToAll();

            if (player.isSurvival()) {
                Item item = player.getInventory().getItemInHand();
                item.decrementCount();
                player.getInventory().setItemInHand(item);
            }

            return true;
        }
        return false;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
