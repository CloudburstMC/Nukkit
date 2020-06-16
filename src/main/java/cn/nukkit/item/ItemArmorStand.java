package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockWater;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityArmorStand;
import cn.nukkit.entity.item.EntityBoat;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;

/**
 * Created by yescallop on 2016/2/13.
 */
public class ItemArmorStand extends Item {

    public ItemArmorStand() {
        this(0, 1);
    }

    public ItemArmorStand(Integer meta) {
        this(meta, 1);
    }

    public ItemArmorStand(Integer meta, int count) {
        super(ARMOR_STAND, meta, count, "ArmorStand");
    }
    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Level level, Player player, Block block, Block target, BlockFace face, double fx, double fy, double fz) {
        if (face != BlockFace.UP) return false;
        EntityArmorStand stand = (EntityArmorStand) Entity.createEntity("ArmorStand",
                level.getChunk(block.getFloorX() >> 4, block.getFloorZ() >> 4), new CompoundTag("")
                .putList(new ListTag<DoubleTag>("Pos")
                        .add(new DoubleTag("", block.getX() + 0.5))
                        .add(new DoubleTag("", block.getY() - (target instanceof BlockWater ? 0.0625 : 0)))
                        .add(new DoubleTag("", block.getZ() + 0.5)))
                .putList(new ListTag<DoubleTag>("Motion")
                        .add(new DoubleTag("", 0))
                        .add(new DoubleTag("", 0))
                        .add(new DoubleTag("", 0)))
                .putList(new ListTag<FloatTag>("Rotation")
                        .add(new FloatTag("", (float) ((player.yaw + 90f) % 360)))
                        .add(new FloatTag("", 0)))
        );

        if (stand == null) {
            return false;
        }

        if (player.isSurvival()) {
            Item item = player.getInventory().getItemInHand();
            item.setCount(item.getCount() - 1);
            player.getInventory().setItemInHand(item);
        }

        stand.setNameTag("saedas");
        stand.setNameTagAlwaysVisible(true);
        stand.setNameTagVisible(true);
        stand.setDataFlag(Entity.DATA_FLAGS,Entity.DATA_FLAG_HIDDEN_WHEN_INVISIBLE,true);
        player.sendMessage("test!");
        stand.spawnToAll();
        return true;
    }

    @Override
    public int getMaxStackSize() {
        return 64;
    }
}
