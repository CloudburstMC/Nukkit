package cn.nukkit.dispenser;

import cn.nukkit.block.BlockID;
import cn.nukkit.item.ItemID;

import java.util.HashMap;
import java.util.Map;

/**
 * @author CreeperFace
 */
public final class DispenseBehaviorRegister {

    private static final Map<Integer, DispenseBehavior> behaviors = new HashMap<>();
    private static DispenseBehavior defaultBehavior = new DefaultDispenseBehavior();

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
        registerBehavior(BlockID.UNDYED_SHULKER_BOX, new ShulkerBoxDispenseBehavior());
        registerBehavior(ItemID.SPAWN_EGG, new SpawnEggDispenseBehavior());
        registerBehavior(BlockID.TNT, new TNTDispenseBehavior());
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
//        registerBehavior(ItemID.LINGERING_POTION, new ProjectileDispenseBehavior("LingeringPotion")); //TODO
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
    }
}
