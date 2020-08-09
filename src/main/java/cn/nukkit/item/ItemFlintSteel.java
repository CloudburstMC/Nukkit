package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockFire;
import cn.nukkit.block.BlockID;
import cn.nukkit.event.block.BlockIgniteEvent;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

import java.util.concurrent.ThreadLocalRandom;

/**
 * author: MagicDroidX
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
        if (block.getId() == AIR && (target.isSolid() || target.getBurnChance() > 0)) {
            if (target.getId() == OBSIDIAN) {
                if (level.createPortal(target)) {
                    return true;
                }
            }

            BlockFire fire = (BlockFire) Block.get(BlockID.FIRE);
            fire.x = block.x;
            fire.y = block.y;
            fire.z = block.z;
            fire.level = level;

            if (fire.isBlockTopFacingSurfaceSolid(fire.down()) || fire.canNeighborBurn()) {
                BlockIgniteEvent e = new BlockIgniteEvent(block, null, player, BlockIgniteEvent.BlockIgniteCause.FLINT_AND_STEEL);
                block.getLevel().getServer().getPluginManager().callEvent(e);

                if (!e.isCancelled()) {
                    level.setBlock(fire, fire, true);
                    level.addLevelSoundEvent(block, LevelSoundEventPacket.SOUND_IGNITE);
                    level.scheduleUpdate(fire, fire.tickRate() + ThreadLocalRandom.current().nextInt(10));
                }
                return true;
            }

            if ((player.gamemode & 0x01) == 0 && this.useOn(block)) {
                if (this.getDamage() >= this.getMaxDurability()) {
                    this.count = 0;
                } else {
                    this.meta++;
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
