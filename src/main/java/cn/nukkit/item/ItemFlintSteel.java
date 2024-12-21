package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.block.*;
import cn.nukkit.event.block.BlockIgniteEvent;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.level.particle.ItemBreakParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public class ItemFlintSteel extends ItemTool {

    public ItemFlintSteel() {
        this(0, 1);
    }

    public ItemFlintSteel(Integer meta) {
        this(meta, 1);
    }

    public ItemFlintSteel(Integer meta, int count) {
        super(FLINT_STEEL, meta, count, "Flint and Steel");
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

        // 1.18 vanilla allows flint & steel to be used even if fire exists
        if ((block.getId() == AIR || block.getId() == FIRE || block.getId() == SOUL_FIRE) && (target instanceof BlockSolid || target instanceof BlockSolidMeta || target instanceof BlockLeaves)) {
            if (target.getId() == OBSIDIAN) {
                if (level.createPortal(target)) {
                    level.addLevelSoundEvent(target, LevelSoundEventPacket.SOUND_IGNITE);
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
                BlockIgniteEvent e = new BlockIgniteEvent(block, null, player, BlockIgniteEvent.BlockIgniteCause.FLINT_AND_STEEL);
                block.getLevel().getServer().getPluginManager().callEvent(e);

                if (!e.isCancelled()) {
                    level.setBlock(fire, fire, true);
                    level.scheduleUpdate(fire, fire.tickRate() + ThreadLocalRandom.current().nextInt(10));
                    level.addLevelSoundEvent(block, LevelSoundEventPacket.SOUND_IGNITE);

                    if (!player.isCreative()) {
                        this.useOn(block);
                        if (this.getDamage() >= DURABILITY_FLINT_STEEL) {
                            this.count = 0;

                            player.level.addSound(player, Sound.RANDOM_BREAK);
                            player.level.addParticle(new ItemBreakParticle(player, this));
                        }

                        player.getInventory().setItemInHand(this);
                    }
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_FLINT_STEEL;
    }
}
