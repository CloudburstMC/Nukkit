package cn.nukkit.registry.impl;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityHuman;
import cn.nukkit.entity.item.EntityBoat;
import cn.nukkit.entity.item.EntityMinecartEmpty;
import cn.nukkit.entity.item.EntityPotion;
import cn.nukkit.entity.item.EntityXPBottle;
import cn.nukkit.entity.item.EntityFallingBlock;
import cn.nukkit.entity.item.EntityFirework;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.entity.item.EntityPainting;
import cn.nukkit.entity.item.EntityPrimedTNT;
import cn.nukkit.entity.item.EntityXPOrb;
import cn.nukkit.entity.mob.EntityBlaze;
import cn.nukkit.entity.mob.EntityCaveSpider;
import cn.nukkit.entity.mob.EntityCreeper;
import cn.nukkit.entity.mob.EntityElderGuardian;
import cn.nukkit.entity.mob.EntityEnderDragon;
import cn.nukkit.entity.mob.EntityEnderman;
import cn.nukkit.entity.mob.EntityEndermite;
import cn.nukkit.entity.mob.EntityEvoker;
import cn.nukkit.entity.mob.EntityGhast;
import cn.nukkit.entity.mob.EntityGuardian;
import cn.nukkit.entity.mob.EntityHusk;
import cn.nukkit.entity.mob.EntityMagmaCube;
import cn.nukkit.entity.mob.EntityShulker;
import cn.nukkit.entity.mob.EntitySilverfish;
import cn.nukkit.entity.mob.EntitySkeleton;
import cn.nukkit.entity.mob.EntitySlime;
import cn.nukkit.entity.mob.EntitySpider;
import cn.nukkit.entity.mob.EntityStray;
import cn.nukkit.entity.mob.EntityVex;
import cn.nukkit.entity.mob.EntityVindicator;
import cn.nukkit.entity.mob.EntityWitch;
import cn.nukkit.entity.mob.EntityWither;
import cn.nukkit.entity.mob.EntityWitherSkeleton;
import cn.nukkit.entity.mob.EntityZombie;
import cn.nukkit.entity.mob.EntityZombiePigman;
import cn.nukkit.entity.mob.EntityZombieVillager;
import cn.nukkit.entity.passive.EntityBat;
import cn.nukkit.entity.passive.EntityChicken;
import cn.nukkit.entity.passive.EntityCow;
import cn.nukkit.entity.passive.EntityDonkey;
import cn.nukkit.entity.passive.EntityHorse;
import cn.nukkit.entity.passive.EntityLlama;
import cn.nukkit.entity.passive.EntityMooshroom;
import cn.nukkit.entity.passive.EntityMule;
import cn.nukkit.entity.passive.EntityOcelot;
import cn.nukkit.entity.passive.EntityPig;
import cn.nukkit.entity.passive.EntityPolarBear;
import cn.nukkit.entity.passive.EntityRabbit;
import cn.nukkit.entity.passive.EntitySheep;
import cn.nukkit.entity.passive.EntitySkeletonHorse;
import cn.nukkit.entity.passive.EntitySquid;
import cn.nukkit.entity.passive.EntityVillager;
import cn.nukkit.entity.passive.EntityWolf;
import cn.nukkit.entity.projectile.EntityArrow;
import cn.nukkit.entity.projectile.EntityEgg;
import cn.nukkit.entity.projectile.EntitySnowball;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.registry.AbstractRegistry;
import cn.nukkit.registry.RegistryType;
import cn.nukkit.registry.function.BiObjectObjectFunction;

public final class EntityRegistry extends AbstractRegistry<Entity, BiObjectObjectFunction<FullChunk, CompoundTag, Entity>> {
    public static final EntityRegistry INSTANCE = new EntityRegistry();

    private EntityRegistry() {
        super(RegistryType.ENTITY);
    }

    @Override
    protected void init() {
        register("arrow", EntityArrow::new, EntityArrow.class);
        register("item", EntityItem::new, EntityItem.class);
        register("falling_block", EntityFallingBlock::new, EntityFallingBlock.class);
        register("primed_tnt", EntityPrimedTNT::new, EntityPrimedTNT.class);
        register("snowball", EntitySnowball::new, EntitySnowball.class);
        register("painting", EntityPainting::new, EntityPainting.class);
        //monsters
        register("creeper", EntityCreeper::new, EntityCreeper.class);
        register("blaze", EntityBlaze::new, EntityBlaze.class);
        register("cave_spider", EntityCaveSpider::new, EntityCaveSpider.class);
        register("elder_guardian", EntityElderGuardian::new, EntityElderGuardian.class);
        register("ender_dragon", EntityEnderDragon::new, EntityEnderDragon.class);
        register("enderman", EntityEnderman::new, EntityEnderman.class);
        register("endermite", EntityEndermite::new, EntityEndermite.class);
        register("evoker", EntityEvoker::new, EntityEvoker.class);
        register("firework", EntityFirework::new, EntityFirework.class);
        register("ghast", EntityGhast::new, EntityGhast.class);
        register("guardian", EntityGuardian::new, EntityGuardian.class);
        register("husk", EntityHusk::new, EntityHusk.class);
        register("magma_cube", EntityMagmaCube::new, EntityMagmaCube.class);
        register("shulker", EntityShulker::new, EntityShulker.class);
        register("silverfish", EntitySilverfish::new, EntitySilverfish.class);
        register("skeleton", EntitySkeleton::new, EntitySkeleton.class);
        register("skeleton_horse", EntitySkeletonHorse::new, EntitySkeletonHorse.class);
        register("slime", EntitySlime::new, EntitySlime.class);
        register("spider", EntitySpider::new, EntitySpider.class);
        register("stray", EntityStray::new, EntityStray.class);
        register("vindicator", EntityVindicator::new, EntityVindicator.class);
        register("vex", EntityVex::new, EntityVex.class);
        register("wither_skeleton", EntityWitherSkeleton::new, EntityWitherSkeleton.class);
        register("wither", EntityWither::new, EntityWither.class);
        register("witch", EntityWitch::new, EntityWitch.class);
        register("zombie_pigman", EntityZombiePigman::new, EntityZombiePigman.class);
        register("zombie_villager", EntityZombieVillager::new, EntityZombieVillager.class);
        register("zombie", EntityZombie::new, EntityZombie.class);
        //passive
        register("bat", EntityBat::new, EntityBat.class);
        register("chicken", EntityChicken::new, EntityChicken.class);
        register("cow", EntityCow::new, EntityCow.class);
        register("donkey", EntityDonkey::new, EntityDonkey.class);
        register("horse", EntityHorse::new, EntityHorse.class);
        register("llama", EntityLlama::new, EntityLlama.class);
        register("mooshroom", EntityMooshroom::new, EntityMooshroom.class);
        register("mule", EntityMule::new, EntityMule.class);
        register("polar_bear", EntityPolarBear::new, EntityPolarBear.class);
        register("pig", EntityPig::new, EntityPig.class);
        register("rabbit", EntityRabbit::new, EntityRabbit.class);
        register("sheep", EntitySheep::new, EntitySheep.class);
        register("squid", EntitySquid::new, EntitySquid.class);
        register("wolf", EntityWolf::new, EntityWolf.class);
        register("ocelot", EntityOcelot::new, EntityOcelot.class);
        register("villager", EntityVillager::new, EntityVillager.class);

        register("xp_bottle", EntityXPBottle::new, EntityXPBottle.class);
        register("xp_orb", EntityXPOrb::new, EntityXPOrb.class);
        register("potion", EntityPotion::new, EntityPotion.class);
        register("egg", EntityEgg::new, EntityEgg.class);

        register("human", EntityHuman::new, EntityHuman.class);

        register("minecart_rideable", EntityMinecartEmpty::new, EntityMinecartEmpty.class);

        register("boat", EntityBoat::new, EntityBoat.class);
    }

    @Override
    protected Entity accept(BiObjectObjectFunction<FullChunk, CompoundTag, Entity> func, int i, Object... args) {
        if (args.length != 2) {
            throw new IllegalArgumentException("Exactly two arguments must be passed!");
        }
        return func.accept((FullChunk) args[0], (CompoundTag) args[1]);
    }
}
