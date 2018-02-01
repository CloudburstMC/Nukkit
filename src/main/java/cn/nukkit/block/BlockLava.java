package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.block.BlockIgniteEvent;
import cn.nukkit.event.entity.EntityCombustByBlockEvent;
import cn.nukkit.event.entity.EntityDamageByBlockEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.item.Item;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.potion.Effect;
import cn.nukkit.utils.BlockColor;

import java.util.Random;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockLava extends BlockLiquid {

    public BlockLava() {
        this(0);
    }

    public BlockLava(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return LAVA;
    }

    @Override
    public int getLightLevel() {
        return 15;
    }

    @Override
    public String getName() {
        return "Lava";
    }

    @Override
    public void onEntityCollide(Entity entity) {
        entity.highestPosition -= (entity.highestPosition - entity.y) * 0.5;
        if (!entity.hasEffect(Effect.FIRE_RESISTANCE)) {
            entity.attack(new EntityDamageByBlockEvent(this, entity, DamageCause.LAVA, 4));
        }

        // Always setting the duration to 15 seconds? TODO
        EntityCombustByBlockEvent ev = new EntityCombustByBlockEvent(this, entity, 15);
        Server.getInstance().getPluginManager().callEvent(ev);
        if (!ev.isCancelled()
                // Making sure the entity is acutally alive and not invulnerable.
                && entity.isAlive()
                && entity.noDamageTicks == 0) {
            entity.setOnFire(ev.getDuration());
        }

        super.onEntityCollide(entity);
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz) {
        return this.place(item, block, target, face, fx, fy, fz, null);
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        boolean ret = this.getLevel().setBlock(this, this, true, false);
        this.getLevel().scheduleUpdate(this, this.tickRate());

        return ret;
    }

    @Override
    public int onUpdate(int type) {
        int result = super.onUpdate(type);

        if (type == Level.BLOCK_UPDATE_RANDOM && this.level.gameRules.getBoolean(GameRule.DO_FIRE_TICK)) {
            Random random = this.getLevel().rand;

            int i = random.nextInt(3);

            if (i > 0) {
                for (int k = 0; k < i; ++k) {
                    Vector3 v = this.add(random.nextInt(3) - 1, 1, random.nextInt(3) - 1);
                    Block block = this.getLevel().getBlock(v);

                    if (block.getId() == AIR) {
                        if (this.isSurroundingBlockFlammable(block)) {
                            BlockIgniteEvent e = new BlockIgniteEvent(block, this, null, BlockIgniteEvent.BlockIgniteCause.LAVA);
                            this.level.getServer().getPluginManager().callEvent(e);

                            if (!e.isCancelled()) {
                                BlockFire fire = new BlockFire();
                                this.getLevel().setBlock(v, fire, true);
                                this.getLevel().scheduleUpdate(fire, fire.tickRate());
                                return Level.BLOCK_UPDATE_RANDOM;
                            }

                            return 0;
                        }
                    } else if (block.isSolid()) {
                        return Level.BLOCK_UPDATE_RANDOM;
                    }
                }
            } else {
                for (int k = 0; k < 3; ++k) {
                    Vector3 v = this.add(random.nextInt(3) - 1, 0, random.nextInt(3) - 1);
                    Block block = this.getLevel().getBlock(v);

                    if (block.up().getId() == AIR && block.getBurnChance() > 0) {
                        BlockIgniteEvent e = new BlockIgniteEvent(block, this, null, BlockIgniteEvent.BlockIgniteCause.LAVA);
                        this.level.getServer().getPluginManager().callEvent(e);

                        if (!e.isCancelled()) {
                            BlockFire fire = new BlockFire();
                            this.getLevel().setBlock(v, fire, true);
                            this.getLevel().scheduleUpdate(fire, fire.tickRate());
                        }
                    }
                }
            }

        }

        return result;
    }

    protected boolean isSurroundingBlockFlammable(Block block) {
        for (BlockFace face : BlockFace.values()) {
            if (block.getSide(face).getBurnChance() > 0) {
                return true;
            }
        }

        return false;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.LAVA_BLOCK_COLOR;
    }

    @Override
    public BlockLiquid getBlock(int meta) {
        return new BlockLava(meta);
    }
}
