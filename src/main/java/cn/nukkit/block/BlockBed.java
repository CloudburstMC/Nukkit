package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityBed;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.mob.*;
import cn.nukkit.event.player.PlayerBedEnterEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Explosion;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.DestroyBlockParticle;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;
import cn.nukkit.utils.Faceable;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public class BlockBed extends BlockTransparentMeta implements Faceable {

    public BlockBed() {
        this(0);
    }

    public BlockBed(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return BED_BLOCK;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public double getHardness() {
        return 0.2;
    }

    @Override
    public String getName() {
        return this.getDyeColor().getName() + " Bed Block";
    }

    @Override
    public double getMaxY() {
        return this.y + 0.5625;
    }

    /**
     * List of mob network IDs which make players unable to sleep when nearby the bed.
     */
    private static final IntSet MOB_IDS = new IntOpenHashSet(new int[]{EntityBlaze.NETWORK_ID, EntityCaveSpider.NETWORK_ID, EntityCreeper.NETWORK_ID, EntityDrowned.NETWORK_ID, EntityElderGuardian.NETWORK_ID, EntityEnderman.NETWORK_ID, EntityEndermite.NETWORK_ID, EntityEvoker.NETWORK_ID, EntityGhast.NETWORK_ID, EntityGuardian.NETWORK_ID, EntityHoglin.NETWORK_ID, EntityHusk.NETWORK_ID, EntityPiglinBrute.NETWORK_ID, EntityPillager.NETWORK_ID, EntityRavager.NETWORK_ID, EntityShulker.NETWORK_ID, EntitySilverfish.NETWORK_ID, EntitySkeleton.NETWORK_ID, EntitySlime.NETWORK_ID, EntitySpider.NETWORK_ID, EntityStray.NETWORK_ID, EntityVex.NETWORK_ID, EntityVindicator.NETWORK_ID, EntityWitch.NETWORK_ID, EntityWither.NETWORK_ID, EntityWitherSkeleton.NETWORK_ID, EntityZoglin.NETWORK_ID, EntityZombie.NETWORK_ID, EntityZombiePigman.NETWORK_ID, EntityZombieVillagerV1.NETWORK_ID, EntityZombieVillager.NETWORK_ID});

    @Override
    public boolean onActivate(Item item, Player player) {
        if (this.level.getDimension() != Level.DIMENSION_OVERWORLD) {
            if (this.level.getGameRules().getBoolean(GameRule.RESPAWN_BLOCKS_EXPLODE)) {
                if (this.getLevel().setBlock(this, Block.get(BlockID.AIR), true, true)) {
                    this.level.addParticle(new DestroyBlockParticle(this.add(0.5, 0.5, 0.5), this));
                }

                Explosion explosion = new Explosion(this.add(0.5, 0, 0.5), 5, this);
                explosion.setFireSpawnChance(0.3333);
                explosion.explodeA();
                explosion.explodeB();
            }
            return true;
        }

        Block blockNorth = this.north();
        Block blockSouth = this.south();
        Block blockEast = this.east();
        Block blockWest = this.west();

        Block b;
        if ((this.getDamage() & 0x08) == 0x08) {
            b = this;
        } else {
            if (blockNorth.getId() == BED_BLOCK && (blockNorth.getDamage() & 0x08) == 0x08) {
                b = blockNorth;
            } else if (blockSouth.getId() == BED_BLOCK && (blockSouth.getDamage() & 0x08) == 0x08) {
                b = blockSouth;
            } else if (blockEast.getId() == BED_BLOCK && (blockEast.getDamage() & 0x08) == 0x08) {
                b = blockEast;
            } else if (blockWest.getId() == BED_BLOCK && (blockWest.getDamage() & 0x08) == 0x08) {
                b = blockWest;
            } else {
                if (player != null) {
                    player.sendMessage("§7%tile.bed.notValid", true);
                }

                return true;
            }
        }

        if (player != null) {
            if (player.distanceSquared(this) > 36) {
                player.sendMessage("§7%tile.bed.tooFar", true);
                return true;
            }

            if (!player.isCreative()) {
                BlockFace secondPart = this.getBlockFace().getOpposite();
                AxisAlignedBB checkArea = new SimpleAxisAlignedBB(b.x - 8, b.y - 6.5, b.z - 8, b.x + 9, b.y + 5.5, b.z + 9).addCoord(secondPart.getXOffset(), 0, secondPart.getZOffset());

                for (Entity entity : this.getLevel().getCollidingEntities(checkArea)) {
                    if (!entity.isClosed() && MOB_IDS.contains(entity.getNetworkId())) {
                        player.sendMessage("§7%tile.bed.notSafe", true);
                        return true;
                    }
                }
            }

            int time = this.getLevel().getTime() % Level.TIME_FULL;
            boolean isNight = time >= Level.TIME_NIGHT && time < Level.TIME_SUNRISE;
            if (!isNight && !this.getLevel().isThundering()) {
                if (!b.equals(player.getSpawnPosition())) {
                    PlayerBedEnterEvent ev = new PlayerBedEnterEvent(player, this, true); // TODO: Event for setting player respawn point?
                    player.getServer().getPluginManager().callEvent(ev);
                    if (!ev.isCancelled()) {
                        player.setSpawn(b);
                        player.sendMessage("§7%tile.bed.respawnSet", true);
                    }
                }
                player.sendMessage("§7%tile.bed.noSleep", true);
            } else if (!player.sleepOn(b)) {
                player.sendMessage("§7%tile.bed.occupied", true);
            }
        }

        return true;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (canStayOnFullNonSolid(this.down())) {
            Block next = this.getSide(player.getHorizontalFacing());

            if (next.canBeReplaced() && canStayOnFullNonSolid(next.down())) {
                int meta = player.getDirection().getHorizontalIndex();

                this.getLevel().setBlock(block, Block.get(BED_BLOCK, meta), true, true);

                this.getLevel().setBlock(next, Block.get(BED_BLOCK, meta | 0x08), true, true);

                createBlockEntity(this, item.getDamage());
                createBlockEntity(next, item.getDamage());
                return true;
            }
        }

        return false;
    }

    /**
     * Internal: Can drop item when broken
     */
    public boolean canDropItem = true;

    @Override
    public boolean onBreak(Item item) {
        Block blockNorth = this.north();
        Block blockSouth = this.south();
        Block blockEast = this.east();
        Block blockWest = this.west();

        Block secondPart = null;
        if ((this.getDamage() & 0x08) == 0x08) { // Top part of the bed
            if (blockNorth.getId() == BED_BLOCK && (blockNorth.getDamage() & 0x08) != 0x08) { // Check if the block ID & meta are right
                secondPart = blockNorth;
            } else if (blockSouth.getId() == BED_BLOCK && (blockSouth.getDamage() & 0x08) != 0x08) {
                secondPart = blockSouth;
            } else if (blockEast.getId() == BED_BLOCK && (blockEast.getDamage() & 0x08) != 0x08) {
                secondPart = blockEast;
            } else if (blockWest.getId() == BED_BLOCK && (blockWest.getDamage() & 0x08) != 0x08) {
                secondPart = blockWest;
            }
        } else { // Bottom part of the bed
            if (blockNorth.getId() == BED_BLOCK && (blockNorth.getDamage() & 0x08) == 0x08) {
                secondPart = blockNorth;
            } else if (blockSouth.getId() == BED_BLOCK && (blockSouth.getDamage() & 0x08) == 0x08) {
                secondPart = blockSouth;
            } else if (blockEast.getId() == BED_BLOCK && (blockEast.getDamage() & 0x08) == 0x08) {
                secondPart = blockEast;
            } else if (blockWest.getId() == BED_BLOCK && (blockWest.getDamage() & 0x08) == 0x08) {
                secondPart = blockWest;
            }
        }

        if (secondPart != null) {
            Item secondPartDrop = (secondPart.getDamage() & 0x08) == 0x08 ? secondPart.toItem() : null; // Get drops before block entity is destroyed to keep the color
            if (this.getLevel().setBlock(secondPart, Block.get(BlockID.AIR), true, true)) {
                if (secondPartDrop != null && this.canDropItem && this.getLevel().gameRules.getBoolean(GameRule.DO_TILE_DROPS)) {
                    this.getLevel().dropItem(this.add(0.5, 0.5, 0.5), secondPartDrop); // Drops only from the top part, prevent a dupe
                }
            }
        }

        this.getLevel().setBlock(this, Block.get(BlockID.AIR), true, secondPart == null); // Don't update both parts to prevent duplication bug if there are two fallable blocks on top of the bed

        for (Entity entity : this.level.getNearbyEntities(new SimpleAxisAlignedBB(this, this).grow(2, 1, 2))) {
            if (!(entity instanceof Player)) continue;
            Player player = (Player) entity;

            if (player.getSleepingPos() == null) continue;
            if (!player.getSleepingPos().equals(this) && !player.getSleepingPos().equals(secondPart)) continue;
            player.stopSleep();
        }

        if (level.getDimension() == Level.DIMENSION_OVERWORLD) {
            Vector3 safeSpawn = null;
            for (Player player : level.getServer().getOnlinePlayers().values()) {
                if (player.getSpawnPosition() != null && (player.getSpawnPosition().equals(this) || player.getSpawnPosition().equals(secondPart))) {
                    player.setSpawn(safeSpawn == null ? (safeSpawn = level.getServer().getDefaultLevel().getSafeSpawn()) : safeSpawn);
                }
            }
        }

        return true;
    }

    private void createBlockEntity(Block pos, int color) {
        CompoundTag nbt = BlockEntity.getDefaultCompound(pos, BlockEntity.BED);
        nbt.putByte("color", color);

        BlockEntityBed be = (BlockEntityBed) BlockEntity.createBlockEntity(BlockEntity.BED, pos.getChunk(), nbt);
        be.spawnToAll();
    }

    @Override
    public Item toItem() {
        return Item.get(Item.BED, this.getDyeColor().getWoolData());
    }

    @Override
    public BlockColor getColor() {
        return this.getDyeColor().getColor();
    }

    public DyeColor getDyeColor() {
        if (this.level != null) {
            BlockEntity blockEntity = this.level.getBlockEntity(this);

            if (blockEntity instanceof BlockEntityBed) {
                return ((BlockEntityBed) blockEntity).getDyeColor();
            }
        }

        return DyeColor.WHITE;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(this.getDamage() & 0x7);
    }

    /*@Override
    public boolean breakWhenPushed() {
        return true;
    }*/

    @Override
    public boolean canBePushed() {
        return false; // Temporary dupe patch
    }
}
