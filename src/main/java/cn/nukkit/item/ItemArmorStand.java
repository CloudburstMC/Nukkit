package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.LevelEventPacket;
import cn.nukkit.entity.item.EntityArmorStand;

public class ItemArmorStand extends Item {

    public ItemArmorStand() {
        this(0);
    }

    public ItemArmorStand(Integer meta) {
        this(meta, 1);
    }

    public ItemArmorStand(Integer meta, int count) {
        super(ARMOR_STAND, meta, count, "Armor Stand");
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public int getMaxStackSize() {
        return 64;
    }

    @Override
    public boolean onActivate(Level level, Player player, Block block, Block target, BlockFace face, double fx, double fy, double fz) {
        if (block.canPassThrough()) {
            CompoundTag nbt = new CompoundTag()
                    .putList(new ListTag<DoubleTag>("Pos")
                            .add(new DoubleTag("", block.x + 0.5))
                            .add(new DoubleTag("", block.y))
                            .add(new DoubleTag("", block.z + 0.5)))
                    .putList(new ListTag<DoubleTag>("Motion")
                            .add(new DoubleTag("", 0))
                            .add(new DoubleTag("", 0))
                            .add(new DoubleTag("", 0)))
                    .putList(new ListTag<FloatTag>("Rotation")
                            .add(new FloatTag("", this.getDirection((float) player.getYaw())))
                            .add(new FloatTag("", 0)));

            EntityArmorStand entityArmorStand = new EntityArmorStand(level.getChunk(block.getFloorX() >> 4, block.getFloorZ() >> 4), nbt);

            if(player.isSurvival()){
                player.getInventory().decreaseCount(player.getInventory().getHeldItemIndex());
            }

            entityArmorStand.spawnToAll();
            player.getLevel().addLevelSoundEvent(entityArmorStand, LevelEventPacket.EVENT_SOUND_ARMOR_STAND_PLACE);
            return true;
        }
        return false;
    }


    public float getDirection(float yaw) {
        return (Math.round(yaw / 22.5f / 2) * 45) - 180;
    }
}
