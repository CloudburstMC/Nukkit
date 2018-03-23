package cn.nukkit.registry.impl;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityHuman;
import cn.nukkit.entity.item.*;
import cn.nukkit.entity.mob.*;
import cn.nukkit.entity.passive.*;
import cn.nukkit.entity.projectile.EntityArrow;
import cn.nukkit.entity.projectile.EntityEgg;
import cn.nukkit.entity.projectile.EntitySnowball;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.registry.AbstractRegistry;
import cn.nukkit.registry.RegistryName;
import cn.nukkit.registry.RegistryType;
import cn.nukkit.registry.function.BiObjectObjectFunction;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

public final class EntityRegistry extends AbstractRegistry<Entity, BiObjectObjectFunction<FullChunk, CompoundTag, Entity>> {
    public static final EntityRegistry INSTANCE = new EntityRegistry();

    private final Int2ObjectMap<RegistryName> networkIds = new Int2ObjectOpenHashMap<>();

    private EntityRegistry() {
        super(RegistryType.ENTITY);
    }

    @Override
    protected void init() {
        register("arrow", EntityArrow::new, EntityArrow.class, EntityArrow.NETWORK_ID);
        register("item", EntityItem::new, EntityItem.class, EntityItem.NETWORK_ID);
        register("falling_block", EntityFallingBlock::new, EntityFallingBlock.class, EntityFallingBlock.NETWORK_ID);
        register("firework", EntityFirework::new, EntityFirework.class, EntityFirework.NETWORK_ID);
        register("primed_tnt", EntityPrimedTNT::new, EntityPrimedTNT.class, EntityPrimedTNT.NETWORK_ID);
        register("snowball", EntitySnowball::new, EntitySnowball.class, EntitySnowball.NETWORK_ID);
        register("painting", EntityPainting::new, EntityPainting.class, EntityPainting.NETWORK_ID);
        //monsters
        register("creeper", EntityCreeper::new, EntityCreeper.class, EntityCreeper.NETWORK_ID);
        register("blaze", EntityBlaze::new, EntityBlaze.class, EntityBlaze.NETWORK_ID);
        register("cave_spider", EntityCaveSpider::new, EntityCaveSpider.class, EntityCaveSpider.NETWORK_ID);
        register("elder_guardian", EntityElderGuardian::new, EntityElderGuardian.class, EntityElderGuardian.NETWORK_ID);
        register("ender_dragon", EntityEnderDragon::new, EntityEnderDragon.class, EntityEnderDragon.NETWORK_ID);
        register("enderman", EntityEnderman::new, EntityEnderman.class, EntityEnderman.NETWORK_ID);
        register("endermite", EntityEndermite::new, EntityEndermite.class, EntityEndermite.NETWORK_ID);
        register("evoker", EntityEvoker::new, EntityEvoker.class, EntityEvoker.NETWORK_ID);
        register("ghast", EntityGhast::new, EntityGhast.class, EntityGhast.NETWORK_ID);
        register("guardian", EntityGuardian::new, EntityGuardian.class, EntityGuardian.NETWORK_ID);
        register("husk", EntityHusk::new, EntityHusk.class, EntityHusk.NETWORK_ID);
        register("magma_cube", EntityMagmaCube::new, EntityMagmaCube.class, EntityMagmaCube.NETWORK_ID);
        register("shulker", EntityShulker::new, EntityShulker.class, EntityShulker.NETWORK_ID);
        register("silverfish", EntitySilverfish::new, EntitySilverfish.class, EntitySilverfish.NETWORK_ID);
        register("skeleton", EntitySkeleton::new, EntitySkeleton.class, EntitySkeleton.NETWORK_ID);
        register("skeleton_horse", EntitySkeletonHorse::new, EntitySkeletonHorse.class, EntitySkeletonHorse.NETWORK_ID);
        register("slime", EntitySlime::new, EntitySlime.class, EntitySlime.NETWORK_ID);
        register("spider", EntitySpider::new, EntitySpider.class, EntitySpider.NETWORK_ID);
        register("stray", EntityStray::new, EntityStray.class, EntityStray.NETWORK_ID);
        register("vindicator", EntityVindicator::new, EntityVindicator.class, EntityVindicator.NETWORK_ID);
        register("vex", EntityVex::new, EntityVex.class, EntityVex.NETWORK_ID);
        register("wither_skeleton", EntityWitherSkeleton::new, EntityWitherSkeleton.class, EntityWitherSkeleton.NETWORK_ID);
        register("wither", EntityWither::new, EntityWither.class, EntityWither.NETWORK_ID);
        register("witch", EntityWitch::new, EntityWitch.class, EntityWitch.NETWORK_ID);
        register("zombie_pigman", EntityZombiePigman::new, EntityZombiePigman.class, EntityZombiePigman.NETWORK_ID);
        register("zombie_villager", EntityZombieVillager::new, EntityZombieVillager.class, EntityZombieVillager.NETWORK_ID);
        register("zombie", EntityZombie::new, EntityZombie.class, EntityZombie.NETWORK_ID);
        //passive
        register("bat", EntityBat::new, EntityBat.class, EntityBat.NETWORK_ID);
        register("chicken", EntityChicken::new, EntityChicken.class, EntityChicken.NETWORK_ID);
        register("cow", EntityCow::new, EntityCow.class, EntityCow.NETWORK_ID);
        register("donkey", EntityDonkey::new, EntityDonkey.class, EntityDonkey.NETWORK_ID);
        register("horse", EntityHorse::new, EntityHorse.class, EntityHorse.NETWORK_ID);
        register("llama", EntityLlama::new, EntityLlama.class, EntityLlama.NETWORK_ID);
        register("mooshroom", EntityMooshroom::new, EntityMooshroom.class, EntityMooshroom.NETWORK_ID);
        register("mule", EntityMule::new, EntityMule.class, EntityMule.NETWORK_ID);
        register("polar_bear", EntityPolarBear::new, EntityPolarBear.class, EntityPolarBear.NETWORK_ID);
        register("pig", EntityPig::new, EntityPig.class, EntityPig.NETWORK_ID);
        register("rabbit", EntityRabbit::new, EntityRabbit.class, EntityRabbit.NETWORK_ID);
        register("sheep", EntitySheep::new, EntitySheep.class, EntitySheep.NETWORK_ID);
        register("squid", EntitySquid::new, EntitySquid.class, EntitySquid.NETWORK_ID);
        register("wolf", EntityWolf::new, EntityWolf.class, EntityWolf.NETWORK_ID);
        register("ocelot", EntityOcelot::new, EntityOcelot.class, EntityOcelot.NETWORK_ID);
        register("villager", EntityVillager::new, EntityVillager.class, EntityVillager.NETWORK_ID);

        register("xp_bottle", EntityXPBottle::new, EntityXPBottle.class, EntityXPBottle.NETWORK_ID);
        register("xp_orb", EntityXPOrb::new, EntityXPOrb.class, EntityXPOrb.NETWORK_ID);
        register("potion", EntityPotion::new, EntityPotion.class, EntityPotion.NETWORK_ID);
        register("egg", EntityEgg::new, EntityEgg.class, EntityEgg.NETWORK_ID);

        register("human", EntityHuman::new, EntityHuman.class, EntityHuman.NETWORK_ID);

        register("minecart_rideable", EntityMinecartEmpty::new, EntityMinecartEmpty.class, EntityMinecartEmpty.NETWORK_ID);

        register("boat", EntityBoat::new, EntityBoat.class, EntityBoat.NETWORK_ID);
    }

    protected void register(String name, BiObjectObjectFunction<FullChunk, CompoundTag, Entity> func, Class<? extends Entity> clazz, int networkId) {
        super.register(name, func, clazz);
        networkIds.put(networkId, getName(clazz));
    }

    public RegistryName getName(int networkId) {
        return networkIds.get(networkId);
    }

    @Override
    protected Entity accept(BiObjectObjectFunction<FullChunk, CompoundTag, Entity> func, int i, Object... args) {
        if (args.length != 2) {
            throw new IllegalArgumentException("Exactly two arguments must be passed!");
        }
        return func.accept((FullChunk) args[0], (CompoundTag) args[1]);
    }
}
