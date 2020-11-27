package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.mob.*;
import cn.nukkit.entity.passive.*;
import cn.nukkit.event.entity.CreatureSpawnEvent;
import cn.nukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;

import java.util.Random;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemSpawnEgg extends Item {
    
    private static Int2IntMap newIds = new Int2IntOpenHashMap();
    static {
        registerNewIds();
    }

    public ItemSpawnEgg() {
        this(0, 1);
    }

    public ItemSpawnEgg(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEgg(Integer meta, int count) {
        super(SPAWN_EGG, meta, count, "Spawn EntityEgg");
    }

    protected ItemSpawnEgg(int id, Integer meta, int count, String name) {
        super(id, meta, count, name);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Level level, Player player, Block block, Block target, BlockFace face, double fx, double fy, double fz) {
        FullChunk chunk = level.getChunk((int) block.getX() >> 4, (int) block.getZ() >> 4);

        if (chunk == null) {
            return false;
        }

        CompoundTag nbt = new CompoundTag()
                .putList(new ListTag<DoubleTag>("Pos")
                        .add(new DoubleTag("", block.getX() + 0.5))
                        .add(new DoubleTag("", target.getBoundingBox() == null ? block.getY() : target.getBoundingBox().getMaxY() + 0.0001f))
                        .add(new DoubleTag("", block.getZ() + 0.5)))
                .putList(new ListTag<DoubleTag>("Motion")
                        .add(new DoubleTag("", 0))
                        .add(new DoubleTag("", 0))
                        .add(new DoubleTag("", 0)))
                .putList(new ListTag<FloatTag>("Rotation")
                        .add(new FloatTag("", new Random().nextFloat() * 360))
                        .add(new FloatTag("", 0)));

        if (this.hasCustomName()) {
            nbt.putString("CustomName", this.getCustomName());
        }

        int networkId = getEntityNetworkId();
        CreatureSpawnEvent ev = new CreatureSpawnEvent(networkId, block, nbt, SpawnReason.SPAWN_EGG);
        level.getServer().getPluginManager().callEvent(ev);

        if (ev.isCancelled()) {
            return false;
        }

        Entity entity = Entity.createEntity(networkId, chunk, nbt);

        if (entity != null) {
            if (player.isSurvival()) {
                player.getInventory().decreaseCount(player.getInventory().getHeldItemIndex());
            }
            entity.spawnToAll();
            return true;
        }

        return false;
    }

    @Since("1.3.2.0-PN")
    @PowerNukkitOnly
    public Item getLegacySpawnEgg() {
        return Item.getItem(SPAWN_EGG, getEntityNetworkId(), getCount(), getCompoundTag(), false);
    }

    @Since("1.3.2.0-PN")
    @PowerNukkitOnly
    @Override
    public Item selfUpgrade() {
        if (getId() != SPAWN_EGG) {
            return this;
        }
        
        int newId = newIds.getOrDefault(super.getDamage(), Integer.MIN_VALUE);
        if (newId == Integer.MIN_VALUE) {
            return this;
        }
        
        return Item.get(newId, 0, getCount(), getCompoundTag());
    }

    @PowerNukkitOnly
    @Since("1.3.2.0-PN")
    public int getEntityNetworkId() {
        return this.meta;
    }
    
    private static void registerNewIds() {
        newIds.put(56, AGENT_SPAWN_EGG);
        newIds.put(EntityBat.NETWORK_ID, BAT_SPAWN_EGG);
        newIds.put(EntityBee.NETWORK_ID, BEE_SPAWN_EGG);
        newIds.put(EntityBlaze.NETWORK_ID, BLAZE_SPAWN_EGG);
        newIds.put(EntityCat.NETWORK_ID, CAT_SPAWN_EGG);
        newIds.put(EntityCaveSpider.NETWORK_ID, CAVE_SPIDER_SPAWN_EGG);
        newIds.put(EntityChicken.NETWORK_ID, CHICKEN_SPAWN_EGG);
        newIds.put(EntityCod.NETWORK_ID, COD_SPAWN_EGG);
        newIds.put(EntityCow.NETWORK_ID, COW_SPAWN_EGG);
        newIds.put(EntityCreeper.NETWORK_ID, CREEPER_SPAWN_EGG);
        newIds.put(EntityDolphin.NETWORK_ID, DOLPHIN_SPAWN_EGG);
        newIds.put(EntityDonkey.NETWORK_ID, DONKEY_SPAWN_EGG);
        newIds.put(EntityDrowned.NETWORK_ID, DROWNED_SPAWN_EGG);
        newIds.put(EntityElderGuardian.NETWORK_ID, ELDER_GUARDIAN_SPAWN_EGG);
        newIds.put(EntityEnderman.NETWORK_ID, ENDERMAN_SPAWN_EGG);
        newIds.put(EntityEndermite.NETWORK_ID, ENDERMITE_SPAWN_EGG);
        newIds.put(EntityEvoker.NETWORK_ID, EVOKER_SPAWN_EGG);
        newIds.put(EntityFox.NETWORK_ID, FOX_SPAWN_EGG);
        newIds.put(EntityGhast.NETWORK_ID, GHAST_SPAWN_EGG);
        newIds.put(EntityGuardian.NETWORK_ID, GUARDIAN_SPAWN_EGG);
        newIds.put(EntityHoglin.NETWORK_ID, HOGLIN_SPAWN_EGG);
        newIds.put(EntityHorse.NETWORK_ID, HORSE_SPAWN_EGG);
        newIds.put(EntityHusk.NETWORK_ID, HUSK_SPAWN_EGG);
        newIds.put(EntityLlama.NETWORK_ID, LLAMA_SPAWN_EGG);
        newIds.put(EntityMagmaCube.NETWORK_ID, MAGMA_CUBE_SPAWN_EGG);
        newIds.put(EntityMooshroom.NETWORK_ID, MOOSHROOM_SPAWN_EGG);
        newIds.put(EntityMule.NETWORK_ID, MULE_SPAWN_EGG);
        newIds.put(EntityNPCEntity.NETWORK_ID, NPC_SPAWN_EGG);
        newIds.put(EntityOcelot.NETWORK_ID, OCELOT_SPAWN_EGG);
        newIds.put(EntityPanda.NETWORK_ID, PANDA_SPAWN_EGG);
        newIds.put(EntityParrot.NETWORK_ID, PARROT_SPAWN_EGG);
        newIds.put(EntityPhantom.NETWORK_ID, PHANTOM_SPAWN_EGG);
        newIds.put(EntityPig.NETWORK_ID, PIG_SPAWN_EGG);
        newIds.put(EntityPiglin.NETWORK_ID, PIGLIN_SPAWN_EGG);
        newIds.put(EntityPiglinBrute.NETWORK_ID, PIGLIN_BRUTE_SPAWN_EGG);
        newIds.put(EntityPillager.NETWORK_ID, PILLAGER_SPAWN_EGG);
        newIds.put(EntityPolarBear.NETWORK_ID, POLAR_BEAR_SPAWN_EGG);
        newIds.put(EntityPufferfish.NETWORK_ID, PUFFERFISH_SPAWN_EGG);
        newIds.put(EntityRabbit.NETWORK_ID, RABBIT_SPAWN_EGG);
        newIds.put(EntityRavager.NETWORK_ID, RAVAGER_SPAWN_EGG);
        newIds.put(EntitySalmon.NETWORK_ID, SALMON_SPAWN_EGG);
        newIds.put(EntitySheep.NETWORK_ID, SHEEP_SPAWN_EGG);
        newIds.put(EntityShulker.NETWORK_ID, SHULKER_SPAWN_EGG);
        newIds.put(EntitySilverfish.NETWORK_ID, SILVERFISH_SPAWN_EGG);
        newIds.put(EntitySkeleton.NETWORK_ID, SKELETON_SPAWN_EGG);
        newIds.put(EntitySkeletonHorse.NETWORK_ID, SKELETON_HORSE_SPAWN_EGG);
        newIds.put(EntitySlime.NETWORK_ID, SLIME_SPAWN_EGG);
        newIds.put(EntitySpider.NETWORK_ID, SPIDER_SPAWN_EGG);
        newIds.put(EntitySquid.NETWORK_ID, SQUID_SPAWN_EGG);
        newIds.put(EntityStray.NETWORK_ID, STRAY_SPAWN_EGG);
        newIds.put(EntityStrider.NETWORK_ID, STRIDER_SPAWN_EGG);
        newIds.put(EntityTropicalFish.NETWORK_ID, TROPICAL_FISH_SPAWN_EGG);
        newIds.put(EntityTurtle.NETWORK_ID, TURTLE_SPAWN_EGG);
        newIds.put(EntityVex.NETWORK_ID, VEX_SPAWN_EGG);
        newIds.put(EntityVillagerV1.NETWORK_ID, VILLAGER_SPAWN_EGG);
        newIds.put(EntityVillager.NETWORK_ID, VILLAGER_SPAWN_EGG);
        newIds.put(EntityVindicator.NETWORK_ID, VINDICATOR_SPAWN_EGG);
        newIds.put(EntityWanderingTrader.NETWORK_ID, WANDERING_TRADER_SPAWN_EGG);
        newIds.put(EntityWitch.NETWORK_ID, WITCH_SPAWN_EGG);
        newIds.put(EntityWitherSkeleton.NETWORK_ID, WITHER_SKELETON_SPAWN_EGG);
        newIds.put(EntityWolf.NETWORK_ID, WOLF_SPAWN_EGG);
        newIds.put(EntityZoglin.NETWORK_ID, ZOGLIN_SPAWN_EGG);
        newIds.put(EntityZombie.NETWORK_ID, ZOMBIE_SPAWN_EGG);
        newIds.put(EntityZombieHorse.NETWORK_ID, ZOMBIE_HORSE_SPAWN_EGG);
        newIds.put(EntityZombiePigman.NETWORK_ID, ZOMBIE_PIGMAN_SPAWN_EGG);
        newIds.put(EntityZombieVillagerV1.NETWORK_ID, ZOMBIE_VILLAGER_SPAWN_EGG);
        newIds.put(EntityZombieVillager.NETWORK_ID, ZOMBIE_VILLAGER_SPAWN_EGG);
    }
}
