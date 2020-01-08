package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockRail;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityMinecartHopper;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.utils.Rail;

public class ItemMinecartHopper extends Item {

    public ItemMinecartHopper() {
        this(0, 1);
    }

    public ItemMinecartHopper(Integer meta) {
        this(meta, 1);
    }

    public ItemMinecartHopper(Integer meta, int count) {
        super(MINECART_WITH_HOPPER, meta, count, "Minecart with Hopper");
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Level level, Player player, Block block, Block target, BlockFace face, double fx, double fy, double fz) {
        if (Rail.isRailBlock(target)) {
            Rail.Orientation type = ((BlockRail) target).getOrientation();
            double adjacent = 0.0D;
            if (type.isAscending()) {
                adjacent = 0.5D;
            }
            EntityMinecartHopper minecart = (EntityMinecartHopper) Entity.createEntity("MinecartHopper",
                    level.getChunk(target.getFloorX() >> 4, target.getFloorZ() >> 4), new CompoundTag("")
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

            if(minecart == null) {
                return false;
            }

            if (player.isSurvival()) {
                Item item = player.getInventory().getItemInHand();
                item.setCount(item.getCount() - 1);
                player.getInventory().setItemInHand(item);
            }

            minecart.spawnToAll();
            return true;
        }
        return false;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
