package cn.nukkit.block;

import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.projectile.EntityArrow;
import cn.nukkit.event.block.BlockBurnEvent;
import cn.nukkit.event.block.BlockFadeEvent;
import cn.nukkit.event.block.BlockIgniteEvent;
import cn.nukkit.event.entity.EntityCombustByBlockEvent;
import cn.nukkit.event.entity.EntityDamageByBlockEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.potion.Effect;
import cn.nukkit.utils.BlockColor;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockFire extends BlockFlowable {

    public BlockFire() {
        this(0);
    }

    public BlockFire(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return FIRE;
    }

    @Override
    public boolean hasEntityCollision() {
        return true;
    }

    @Override
    public String getName() {
        return "Fire Block";
    }

    @Override
    public int getLightLevel() {
        return 15;
    }

    @Override
    public boolean isBreakable(Item item) {
        return false;
    }

    @Override
    public boolean canBeReplaced() {
        return true;
    }

    @Override
    public void onEntityCollide(Entity entity) {
        if (!entity.hasEffect(Effect.FIRE_RESISTANCE)) {
            entity.attack(new EntityDamageByBlockEvent(this, entity, DamageCause.FIRE, 1));
        }

        EntityCombustByBlockEvent ev = new EntityCombustByBlockEvent(this, entity, 8);
        if (entity instanceof EntityArrow) {
            ev.setCancelled();
        }
        Server.getInstance().getPluginManager().callEvent(ev);
        if (!ev.isCancelled() && entity.isAlive() && entity.noDamageTicks == 0) {
            entity.setOnFire(ev.getDuration());
        }
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[0];
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL || type == Level.BLOCK_UPDATE_RANDOM) {
            if (!this.isBlockTopFacingSurfaceSolid(this.down()) && !this.canNeighborBurn()) {
                BlockFadeEvent event = new BlockFadeEvent(this, get(AIR));
                level.getServer().getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    level.setBlock(this, event.getNewState(), true);
                }
            }

            return Level.BLOCK_UPDATE_NORMAL;
        } else if (type == Level.BLOCK_UPDATE_SCHEDULED && this.level.gameRules.getBoolean(GameRule.DO_FIRE_TICK)) {
            boolean forever = this.down().getId() == Block.NETHERRACK || this.down().getId() == Block.MAGMA;

            ThreadLocalRandom random = ThreadLocalRandom.current();

            //TODO: END

            if (!this.isBlockTopFacingSurfaceSolid(this.down()) && !this.canNeighborBurn()) {
                BlockFadeEvent event = new BlockFadeEvent(this, get(AIR));
                level.getServer().getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    level.setBlock(this, event.getNewState(), true);
                }
            }

            if (!forever && this.getLevel().isRaining() &&
                    (this.getLevel().canBlockSeeSky(this) ||
                            this.getLevel().canBlockSeeSky(this.east()) ||
                            this.getLevel().canBlockSeeSky(this.west()) ||
                            this.getLevel().canBlockSeeSky(this.south()) ||
                            this.getLevel().canBlockSeeSky(this.north()))
                    ) {
                BlockFadeEvent event = new BlockFadeEvent(this, get(AIR));
                level.getServer().getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    level.setBlock(this, event.getNewState(), true);
                }
            } else {
                int meta = this.getDamage();

                if (meta < 15) {
                    int newMeta = meta + random.nextInt(3);
                    this.setDamage(newMeta > 15 ? 15 : newMeta);
                    this.getLevel().setBlock(this, this, true);
                }

                this.getLevel().scheduleUpdate(this, this.tickRate() + random.nextInt(10));

                if (!forever && !this.canNeighborBurn()) {
                    if (!this.isBlockTopFacingSurfaceSolid(this.down()) || meta > 3) {
                        BlockFadeEvent event = new BlockFadeEvent(this, get(AIR));
                        level.getServer().getPluginManager().callEvent(event);
                        if (!event.isCancelled()) {
                            level.setBlock(this, event.getNewState(), true);
                        }
                    }
                } else if (!forever && !(this.down().getBurnAbility() > 0) && meta == 15 && random.nextInt(4) == 0) {
                    BlockFadeEvent event = new BlockFadeEvent(this, get(AIR));
                    level.getServer().getPluginManager().callEvent(event);
                    if (!event.isCancelled()) {
                        level.setBlock(this, event.getNewState(), true);
                    }
                } else {
                    int o = 0;

                    //TODO: decrease the o if the rainfall values are high

                    this.tryToCatchBlockOnFire(this.east(), 300 + o, meta);
                    this.tryToCatchBlockOnFire(this.west(), 300 + o, meta);
                    this.tryToCatchBlockOnFire(this.down(), 250 + o, meta);
                    this.tryToCatchBlockOnFire(this.up(), 250 + o, meta);
                    this.tryToCatchBlockOnFire(this.south(), 300 + o, meta);
                    this.tryToCatchBlockOnFire(this.north(), 300 + o, meta);

                    for (int x = (int) (this.x - 1); x <= (int) (this.x + 1); ++x) {
                        for (int z = (int) (this.z - 1); z <= (int) (this.z + 1); ++z) {
                            for (int y = (int) (this.y - 1); y <= (int) (this.y + 4); ++y) {
                                if (x != (int) this.x || y != (int) this.y || z != (int) this.z) {
                                    int k = 100;

                                    if (y > this.y + 1) {
                                        k += (y - (this.y + 1)) * 100;
                                    }

                                    Block block = this.getLevel().getBlock(new Vector3(x, y, z));
                                    int chance = this.getChanceOfNeighborsEncouragingFire(block);

                                    if (chance > 0) {
                                        int t = (chance + 40 + this.getLevel().getServer().getDifficulty() * 7) / (meta + 30);

                                        //TODO: decrease the t if the rainfall values are high

                                        if (t > 0 && random.nextInt(k) <= t) {
                                            int damage = meta + random.nextInt(5) / 4;

                                            if (damage > 15) {
                                                damage = 15;
                                            }

                                            BlockIgniteEvent e = new BlockIgniteEvent(block, this, null, BlockIgniteEvent.BlockIgniteCause.SPREAD);
                                            this.level.getServer().getPluginManager().callEvent(e);

                                            if (!e.isCancelled()) {
                                                this.getLevel().setBlock(block, new BlockFire(damage), true);
                                                this.getLevel().scheduleUpdate(block, this.tickRate());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return 0;
    }

    private void tryToCatchBlockOnFire(Block block, int bound, int damage) {
        int burnAbility = block.getBurnAbility();

        Random random = ThreadLocalRandom.current();

        if (random.nextInt(bound) < burnAbility) {

            if (random.nextInt(damage + 10) < 5) {
                int meta = damage + random.nextInt(5) / 4;

                if (meta > 15) {
                    meta = 15;
                }

                BlockIgniteEvent e = new BlockIgniteEvent(block, this, null, BlockIgniteEvent.BlockIgniteCause.SPREAD);
                this.level.getServer().getPluginManager().callEvent(e);

                if (!e.isCancelled()) {
                    this.getLevel().setBlock(block, new BlockFire(meta), true);
                    this.getLevel().scheduleUpdate(block, this.tickRate());
                }
            } else {
                BlockBurnEvent ev = new BlockBurnEvent(block);
                this.getLevel().getServer().getPluginManager().callEvent(ev);

                if (!ev.isCancelled()) {
                    this.getLevel().setBlock(block, new BlockAir(), true);
                }
            }

            if (block instanceof BlockTNT) {
                ((BlockTNT) block).prime();
            }
        }
    }

    private int getChanceOfNeighborsEncouragingFire(Block block) {
        if (block.getId() != AIR) {
            return 0;
        } else {
            int chance = 0;
            chance = Math.max(chance, block.east().getBurnChance());
            chance = Math.max(chance, block.west().getBurnChance());
            chance = Math.max(chance, block.down().getBurnChance());
            chance = Math.max(chance, block.up().getBurnChance());
            chance = Math.max(chance, block.south().getBurnChance());
            chance = Math.max(chance, block.north().getBurnChance());
            return chance;
        }
    }

    public boolean canNeighborBurn() {
        for (BlockFace face : BlockFace.values()) {
            if (this.getSide(face).getBurnChance() > 0) {
                return true;
            }
        }

        return false;
    }

    public boolean isBlockTopFacingSurfaceSolid(Block block) {
        if (block != null) {
            if (block.isSolid()) {
                return true;
            } else {
                if (block instanceof BlockStairs &&
                        (block.getDamage() & 4) == 4) {

                    return true;
                } else if (block instanceof BlockSlab &&
                        (block.getDamage() & 8) == 8) {

                    return true;
                } else if (block instanceof BlockSnowLayer &&
                        (block.getDamage() & 7) == 7) {

                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public int tickRate() {
        return 30;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.AIR_BLOCK_COLOR;
    }

    @Override
    protected AxisAlignedBB recalculateCollisionBoundingBox() {
        return this;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(new BlockAir());
    }
}
