package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockRail;
import cn.nukkit.entity.EntityTypes;
import cn.nukkit.entity.vehicle.TntMinecart;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.player.Player;
import cn.nukkit.registry.EntityRegistry;
import cn.nukkit.utils.Identifier;
import cn.nukkit.utils.Rail;

public class ItemMinecartTNT extends Item {

    public ItemMinecartTNT(Identifier id) {
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
            TntMinecart minecart = EntityRegistry.get().newEntity(EntityTypes.TNT_MINECART,
                    level.getChunk(target.getChunkX(), target.getChunkZ()), new CompoundTag("")
                            .putList(new ListTag<>("Pos")
                                    .add(new DoubleTag("", target.getX() + 0.5))
                                    .add(new DoubleTag("", target.getY() + 0.0625D + adjacent))
                                    .add(new DoubleTag("", target.getZ() + 0.5)))
                            .putList(new ListTag<>("Motion")
                                    .add(new DoubleTag("", 0))
                                    .add(new DoubleTag("", 0))
                                    .add(new DoubleTag("", 0)))
                            .putList(new ListTag<>("Rotation")
                                    .add(new FloatTag("", 0))
                            .add(new FloatTag("", 0)))
            );
            minecart.spawnToAll();
            decrementCount();
            return true;
        }
        return false;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
