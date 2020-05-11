package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockFire;
import cn.nukkit.block.BlockIds;
import cn.nukkit.block.BlockLeaves;
import cn.nukkit.block.BlockSolid;
import cn.nukkit.event.block.BlockIgniteEvent;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.player.Player;
import cn.nukkit.utils.Identifier;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.protocol.bedrock.data.SoundEvent;

import java.util.concurrent.ThreadLocalRandom;

import static cn.nukkit.block.BlockIds.AIR;
import static cn.nukkit.block.BlockIds.OBSIDIAN;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemFlintSteel extends ItemTool {
    /**
     * The maximum possible size of the outside of a nether portal
     * 23x23 in vanilla
     */
    private static final int MAX_PORTAL_SIZE = 23;

    public ItemFlintSteel(Identifier id) {
        super(id);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Level level, Player player, Block block, Block target, BlockFace face, Vector3f clickPos) {
        if (block.getId() == AIR && target instanceof BlockSolid || target instanceof BlockLeaves) {
            PORTAL:
            if (target.getId() == OBSIDIAN) {
                final int targX = target.getX();
                final int targY = target.getY();
                final int targZ = target.getZ();
                //check if there's air above (at least 3 blocks)
                for (int i = 1; i < 4; i++) {
                    if (level.getBlockId(targX, targY + i, targZ) != AIR) {
                        break PORTAL;
                    }
                }
                int sizePosX = 0;
                int sizeNegX = 0;
                int sizePosZ = 0;
                int sizeNegZ = 0;
                for (int i = 1; i < MAX_PORTAL_SIZE; i++) {
                    if (level.getBlockId(targX + i, targY, targZ) == OBSIDIAN) {
                        sizePosX++;
                    } else {
                        break;
                    }
                }
                for (int i = 1; i < MAX_PORTAL_SIZE; i++) {
                    if (level.getBlockId(targX - i, targY, targZ) == OBSIDIAN) {
                        sizeNegX++;
                    } else {
                        break;
                    }
                }
                for (int i = 1; i < MAX_PORTAL_SIZE; i++) {
                    if (level.getBlockId(targX, targY, targZ + i) == OBSIDIAN) {
                        sizePosZ++;
                    } else {
                        break;
                    }
                }
                for (int i = 1; i < MAX_PORTAL_SIZE; i++) {
                    if (level.getBlockId(targX, targY, targZ - i) == OBSIDIAN) {
                        sizeNegZ++;
                    } else {
                        break;
                    }
                }
                //plus one for target block
                int sizeX = sizePosX + sizeNegX + 1;
                int sizeZ = sizePosZ + sizeNegZ + 1;
                if (sizeX >= 2 && sizeX <= MAX_PORTAL_SIZE) {
                    //start scan from 1 block above base
                    //find pillar or end of portal to start scan
                    int scanX = targX;
                    int scanY = targY + 1;
                    int scanZ = targZ;
                    for (int i = 0; i < sizePosX + 1; i++) {
                        //this must be air
                        if (level.getBlockId(scanX + i, scanY, scanZ) != AIR) {
                            break PORTAL;
                        }
                        if (level.getBlockId(scanX + i + 1, scanY, scanZ) == OBSIDIAN) {
                            scanX += i;
                            break;
                        }
                    }
                    //make sure that the above loop finished
                    if (level.getBlockId(scanX + 1, scanY, scanZ) != OBSIDIAN) {
                        break PORTAL;
                    }

                    int innerWidth = 0;
                    for (int i = 0; i < MAX_PORTAL_SIZE - 2; i++) {
                        Identifier id = level.getBlockId(scanX - i, scanY, scanZ);
                        if (id == AIR) {
                            innerWidth++;
                        } else if (id == OBSIDIAN) {
                            break;
                        } else {
                            break PORTAL;
                        }
                    }
                    int innerHeight = 0;
                    for (int i = 0; i < MAX_PORTAL_SIZE - 2; i++) {
                        Identifier id = level.getBlockId(scanX, scanY + i, scanZ);
                        if (id == AIR) {
                            innerHeight++;
                        } else if (id == OBSIDIAN) {
                            break;
                        } else {
                            break PORTAL;
                        }
                    }
                    if (!(innerWidth <= MAX_PORTAL_SIZE - 2
                            && innerWidth >= 2
                            && innerHeight <= MAX_PORTAL_SIZE - 2
                            && innerHeight >= 3)) {
                        break PORTAL;
                    }

                    for (int height = 0; height < innerHeight + 1; height++) {
                        if (height == innerHeight) {
                            for (int width = 0; width < innerWidth; width++) {
                                if (level.getBlockId(scanX - width, scanY + height, scanZ) != OBSIDIAN) {
                                    break PORTAL;
                                }
                            }
                        } else {
                            if (level.getBlockId(scanX + 1, scanY + height, scanZ) != OBSIDIAN
                                    || level.getBlockId(scanX - innerWidth, scanY + height, scanZ) != OBSIDIAN) {
                                break PORTAL;
                            }

                            for (int width = 0; width < innerWidth; width++) {
                                if (level.getBlockId(scanX - width, scanY + height, scanZ) != AIR) {
                                    break PORTAL;
                                }
                            }
                        }
                    }

                    for (int height = 0; height < innerHeight; height++)    {
                        for (int width = 0; width < innerWidth; width++)    {
                            level.setBlock(Vector3i.from(scanX - width, scanY + height, scanZ), Block.get(BlockIds.PORTAL));
                        }
                    }

                    level.addLevelSoundEvent(block.getPosition(), SoundEvent.IGNITE);
                    return true;
                } else if (sizeZ >= 2 && sizeZ <= MAX_PORTAL_SIZE) {
                    //start scan from 1 block above base
                    //find pillar or end of portal to start scan
                    int scanX = targX;
                    int scanY = targY + 1;
                    int scanZ = targZ;
                    for (int i = 0; i < sizePosZ + 1; i++) {
                        //this must be air
                        if (level.getBlockId(scanX, scanY, scanZ + i) != AIR) {
                            break PORTAL;
                        }
                        if (level.getBlockId(scanX, scanY, scanZ + i + 1) == OBSIDIAN) {
                            scanZ += i;
                            break;
                        }
                    }
                    //make sure that the above loop finished
                    if (level.getBlockId(scanX, scanY, scanZ + 1) != OBSIDIAN) {
                        break PORTAL;
                    }

                    int innerWidth = 0;
                    for (int i = 0; i < MAX_PORTAL_SIZE - 2; i++) {
                        Identifier id = level.getBlockId(scanX, scanY, scanZ - i);
                        if (id == AIR) {
                            innerWidth++;
                        } else if (id == OBSIDIAN) {
                            break;
                        } else {
                            break PORTAL;
                        }
                    }
                    int innerHeight = 0;
                    for (int i = 0; i < MAX_PORTAL_SIZE - 2; i++) {
                        Identifier id = level.getBlockId(scanX, scanY + i, scanZ);
                        if (id == AIR) {
                            innerHeight++;
                        } else if (id == OBSIDIAN) {
                            break;
                        } else {
                            break PORTAL;
                        }
                    }
                    if (!(innerWidth <= MAX_PORTAL_SIZE - 2
                            && innerWidth >= 2
                            && innerHeight <= MAX_PORTAL_SIZE - 2
                            && innerHeight >= 3)) {
                        break PORTAL;
                    }

                    for (int height = 0; height < innerHeight + 1; height++) {
                        if (height == innerHeight) {
                            for (int width = 0; width < innerWidth; width++) {
                                if (level.getBlockId(scanX, scanY + height, scanZ - width) != OBSIDIAN) {
                                    break PORTAL;
                                }
                            }
                        } else {
                            if (level.getBlockId(scanX, scanY + height, scanZ + 1) != OBSIDIAN
                                    || level.getBlockId(scanX, scanY + height, scanZ - innerWidth) != OBSIDIAN) {
                                break PORTAL;
                            }

                            for (int width = 0; width < innerWidth; width++) {
                                if (level.getBlockId(scanX, scanY + height, scanZ - width) != AIR) {
                                    break PORTAL;
                                }
                            }
                        }
                    }

                    for (int height = 0; height < innerHeight; height++)    {
                        for (int width = 0; width < innerWidth; width++) {
                            level.setBlock(Vector3i.from(scanX, scanY + height, scanZ - width), Block.get(BlockIds.PORTAL));
                        }
                    }

                    level.addLevelSoundEvent(block.getPosition(), SoundEvent.IGNITE);
                    return true;
                }
            }
            BlockFire fire = (BlockFire) Block.get(BlockIds.FIRE);
            fire.setPosition(block.getPosition());
            fire.setLevel(level);

            if (fire.isBlockTopFacingSurfaceSolid(fire.down()) || fire.canNeighborBurn()) {
                BlockIgniteEvent e = new BlockIgniteEvent(block, null, player, BlockIgniteEvent.BlockIgniteCause.FLINT_AND_STEEL);
                block.getLevel().getServer().getPluginManager().callEvent(e);

                if (!e.isCancelled()) {
                    level.setBlock(fire.getPosition(), fire, true);
                    level.addLevelSoundEvent(block.getPosition(), SoundEvent.IGNITE);
                    level.scheduleUpdate(fire, fire.tickRate() + ThreadLocalRandom.current().nextInt(10));
                }
                return true;
            }

            if ((player.getGamemode() & 0x01) == 0 && this.useOn(block)) {
                if (this.getMeta() >= this.getMaxDurability()) {
                    this.setCount(0);
                } else {
                    this.setMeta(this.getMeta() + 1);
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
