package cn.nukkit.dispenser;

import cn.nukkit.block.BlockID;
import cn.nukkit.item.ItemID;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

/**
 * @author CreeperFace
 */
public final class DispenseBehaviorRegister {

    private static final Int2ObjectMap<DispenseBehavior> behaviors = new Int2ObjectOpenHashMap<>();
    private static final DispenseBehavior defaultBehavior = new DefaultDispenseBehavior();

    public static void registerBehavior(int itemId, DispenseBehavior behavior) {
        behaviors.put(itemId, behavior);
    }

    public static DispenseBehavior getBehavior(int id) {
        return behaviors.getOrDefault(id, defaultBehavior);
    }

    public static void removeDispenseBehavior(int id) {
        behaviors.remove(id);
    }

    public static void init() {
        registerBehavior(ItemID.BOAT, new BoatDispenseBehavior());
        registerBehavior(ItemID.BUCKET, new BucketDispenseBehavior());
        registerBehavior(ItemID.DYE, new DyeDispenseBehavior());
        registerBehavior(ItemID.FIREWORKS, new FireworksDispenseBehavior());
        registerBehavior(ItemID.FLINT_AND_STEEL, new FlintAndSteelDispenseBehavior());
        registerBehavior(BlockID.SHULKER_BOX, new ShulkerBoxDispenseBehavior());
        registerBehavior(BlockID.UNDYED_SHULKER_BOX, new UndyedShulkerBoxDispenseBehavior());
        registerBehavior(ItemID.SPAWN_EGG, new SpawnEggDispenseBehavior());
        registerBehavior(BlockID.TNT, new TNTDispenseBehavior());
        registerBehavior(ItemID.FIRE_CHARGE, new FireChargeDispenseBehavior());
        registerBehavior(ItemID.SHEARS, new ShearsDispenseBehaviour());
        registerBehavior(ItemID.POTION, new PotionDispenseBehaviour());
        registerBehavior(ItemID.ARROW, new ProjectileDispenseBehavior("Arrow") {
            @Override
            protected double getMotion() {
                return super.getMotion() * 1.5;
            }
        });
        //TODO: tipped arrow
        //TODO: spectral arrow
        registerBehavior(ItemID.EGG, new ProjectileDispenseBehavior("Egg"));
        registerBehavior(ItemID.SNOWBALL, new ProjectileDispenseBehavior("Snowball"));
        registerBehavior(ItemID.EXPERIENCE_BOTTLE, new ProjectileDispenseBehavior("ThrownExpBottle") {
            @Override
            protected float getAccuracy() {
                return super.getAccuracy() * 0.5f;
            }

            @Override
            protected double getMotion() {
                return super.getMotion() * 1.25;
            }
        });
        registerBehavior(ItemID.SPLASH_POTION, new ProjectileDispenseBehavior("ThrownPotion") {
            @Override
            protected float getAccuracy() {
                return super.getAccuracy() * 0.5f;
            }

            @Override
            protected double getMotion() {
                return super.getMotion() * 1.25;
            }
        });
        registerBehavior(ItemID.LINGERING_POTION, new ProjectileDispenseBehavior("LingeringPotion") {
            @Override
            protected float getAccuracy() {
                return super.getAccuracy() * 0.5f;
            }

            @Override
            protected double getMotion() {
                return super.getMotion() * 1.25;
            }
        });
        registerBehavior(ItemID.TRIDENT, new ProjectileDispenseBehavior("ThrownTrident") {
            @Override
            protected float getAccuracy() {
                return super.getAccuracy() * 0.5f;
            }

            @Override
            protected double getMotion() {
                return super.getMotion() * 1.25;
            }
        });

        registerBehavior(ItemID.ACACIA_CHEST_BOAT, new ChestBoatDispenseBehavior());
        registerBehavior(ItemID.DARK_OAK_CHEST_BOAT, new ChestBoatDispenseBehavior());
        registerBehavior(ItemID.BIRCH_CHEST_BOAT, new ChestBoatDispenseBehavior());
        registerBehavior(ItemID.JUNGLE_CHEST_BOAT, new ChestBoatDispenseBehavior());
        registerBehavior(ItemID.MANGROVE_CHEST_BOAT, new ChestBoatDispenseBehavior());
        registerBehavior(ItemID.SPRUCE_CHEST_BOAT, new ChestBoatDispenseBehavior());
        registerBehavior(ItemID.OAK_CHEST_BOAT, new ChestBoatDispenseBehavior());

        registerBehavior(ItemID.MINECART, new MinecartDispenseBehavior());
        registerBehavior(ItemID.MINECART_WITH_CHEST, new MinecartDispenseBehavior());
        registerBehavior(ItemID.MINECART_WITH_HOPPER, new MinecartDispenseBehavior());
        registerBehavior(ItemID.MINECART_WITH_TNT, new MinecartDispenseBehavior());
    }
}
