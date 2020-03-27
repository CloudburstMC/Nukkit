package cn.nukkit.dispenser;

import cn.nukkit.entity.EntityTypes;
import cn.nukkit.utils.Identifier;

import java.util.HashMap;
import java.util.Map;

import static cn.nukkit.block.BlockIds.SHULKER_BOX;
import static cn.nukkit.block.BlockIds.TNT;
import static cn.nukkit.item.ItemIds.*;

/**
 * @author CreeperFace
 */
public final class DispenseBehaviorRegister {

    private static final Map<Identifier, DispenseBehavior> behaviors = new HashMap<>();
    private static DispenseBehavior defaultBehavior = new DefaultDispenseBehavior();

    public static void registerBehavior(Identifier itemId, DispenseBehavior behavior) {
        behaviors.put(itemId, behavior);
    }

    public static DispenseBehavior getBehavior(Identifier id) {
        return behaviors.getOrDefault(id, defaultBehavior);
    }

    public static void removeDispenseBehavior(Identifier id) {
        behaviors.remove(id);
    }

    public static void init() {
        registerBehavior(BOAT, new BoatDispenseBehavior());
        registerBehavior(BUCKET, new BucketDispenseBehavior());
        registerBehavior(DYE, new DyeDispenseBehavior());
        registerBehavior(FIREWORKS, new FireworksDispenseBehavior());
        registerBehavior(FLINT_AND_STEEL, new FlintAndSteelDispenseBehavior());
        registerBehavior(SHULKER_BOX, new ShulkerBoxDispenseBehavior());
        registerBehavior(SPAWN_EGG, new SpawnEggDispenseBehavior());
        registerBehavior(TNT, new TNTDispenseBehavior());
        registerBehavior(ARROW, new ProjectileDispenseBehavior(EntityTypes.ARROW) {
            @Override
            protected double getMotion() {
                return super.getMotion() * 1.5;
            }
        });
        //TODO: tipped arrow
        //TODO: spectral arrow
        registerBehavior(EGG, new ProjectileDispenseBehavior(EntityTypes.EGG));
        registerBehavior(SNOWBALL, new ProjectileDispenseBehavior(EntityTypes.SNOWBALL));
        registerBehavior(EXPERIENCE_BOTTLE, new ProjectileDispenseBehavior(EntityTypes.XP_BOTTLE) {
            @Override
            protected float getAccuracy() {
                return super.getAccuracy() * 0.5f;
            }

            @Override
            protected double getMotion() {
                return super.getMotion() * 1.25;
            }
        });
        registerBehavior(SPLASH_POTION, new ProjectileDispenseBehavior(EntityTypes.SPLASH_POTION) {
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
        registerBehavior(TRIDENT, new ProjectileDispenseBehavior(EntityTypes.THROWN_TRIDENT) {
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
