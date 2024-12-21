package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.block.*;
import cn.nukkit.event.block.BlockIgniteEvent;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.math.BlockFace;

import java.util.concurrent.ThreadLocalRandom;

public class ItemFireCharge extends Item {

    public ItemFireCharge() {
        this(0, 1);
    }

    public ItemFireCharge(Integer meta) {
        this(meta, 1);
    }

    public ItemFireCharge(Integer meta, int count) {
        super(FIRE_CHARGE, 0, count, "Fire Charge");
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Level level, Player player, Block block, Block target, BlockFace face, double fx, double fy, double fz) {
        if (player.isAdventure()) {
            return false;
        }

        if (block.getId() == AIR && (target instanceof BlockSolid || target instanceof BlockSolidMeta || target instanceof BlockLeaves)) {
            if (target.getId() == OBSIDIAN) {
                if (level.createPortal(target)) {
                    level.addSound(target, Sound.MOB_GHAST_FIREBALL);
                    return true;
                }
            }

            int did;
            BlockFire fire = (BlockFire) Block.get(((did = block.down().getId()) == SOUL_SAND || did == SOUL_SOIL) ? BlockID.SOUL_FIRE : BlockID.FIRE);
            fire.x = block.x;
            fire.y = block.y;
            fire.z = block.z;
            fire.level = level;

            if (fire.isBlockTopFacingSurfaceSolid(fire.down()) || fire.canNeighborBurn()) {
                BlockIgniteEvent e = new BlockIgniteEvent(block, null, player, BlockIgniteEvent.BlockIgniteCause.FIREBALL);
                block.getLevel().getServer().getPluginManager().callEvent(e);

                if (!e.isCancelled()) {
                    level.setBlock(fire, fire, true);
                    level.scheduleUpdate(fire, (fire.tickRate() + ThreadLocalRandom.current().nextInt(10)));
                    level.addSound(block, Sound.MOB_GHAST_FIREBALL);

                    if (!player.isCreative()) {
                        this.count--;
                    }
                }
                return true;
            }
        }
        return false;
    }
}
