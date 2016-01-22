package cn.nukkit.item.potion;

import cn.nukkit.Player;
import cn.nukkit.entity.Effect;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.Living;
import cn.nukkit.entity.ThrownPotion;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityRegainHealthEvent;
import cn.nukkit.event.potion.PotionApplyEvent;
import cn.nukkit.level.particle.InstantSpellParticle;
import cn.nukkit.level.particle.Particle;
import cn.nukkit.level.particle.SpellParticle;
import cn.nukkit.math.Vector3;
import cn.nukkit.plugin.Plugin;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Snake1999 on 2016/1/11.
 * Package cn.nukkit.item.potion in project nukkit
 */
public abstract class Potion {


    public static final int NO_EFFECTS = 0;
    public static final int MUNDANE = 1;
    public static final int MUNDANE_II = 2;
    public static final int THICK = 3;
    public static final int AWKWARD = 4;
    public static final int NIGHT_VISION = 5;
    public static final int NIGHT_VISION_LONG = 6;
    public static final int INVISIBLE = 7;
    public static final int INVISIBLE_LONG = 8;
    public static final int LEAPING = 9;
    public static final int LEAPING_LONG = 10;
    public static final int LEAPING_II = 11;
    public static final int FIRE_RESISTANCE = 12;
    public static final int FIRE_RESISTANCE_LONG = 13;
    public static final int SPEED = 14;
    public static final int SPEED_LONG = 15;
    public static final int SPEED_II = 16;
    public static final int SLOWNESS = 17;
    public static final int SLOWNESS_LONG = 18;
    public static final int WATER_BREATHING = 19;
    public static final int WATER_BREATHING_LONG = 20;
    public static final int INSTANT_HEALTH = 21;
    public static final int INSTANT_HEALTH_II = 22;
    public static final int HARMING = 23;
    public static final int HARMING_II = 24;
    public static final int POISON = 25;
    public static final int POISON_LONG = 26;
    public static final int POISON_II = 27;
    public static final int REGENERATION = 28;
    public static final int REGENERATION_LONG = 29;
    public static final int REGENERATION_II = 30;
    public static final int STRENGTH = 31;
    public static final int STRENGTH_LONG = 32;
    public static final int STRENGTH_II = 33;
    public static final int WEAKNESS = 34;
    public static final int WEAKNESS_LONG = 35;

    private static Map<Integer, Potion> registryDefault = new LinkedHashMap<>();
    private static Map<NodeIDPlugin, Potion> registryCustom = new LinkedHashMap<>();

    public static final Potion no_effects = registerDefaultPotion(0, new PotionNoEffect().setDisplayType(0));
    public static final Potion mundane = registerDefaultPotion(1, new PotionNoEffect().setDisplayType(1));
    public static final Potion mundane_II = registerDefaultPotion(2, new PotionNoEffect().setDisplayType(2).setLevelII());
    public static final Potion thick = registerDefaultPotion(3, new PotionNoEffect().setDisplayType(3));
    public static final Potion awkward = registerDefaultPotion(4, new PotionNoEffect().setDisplayType(4));
    public static final Potion night_vision = registerDefaultPotion(5, new PotionEffective(5));
    public static final Potion night_vision_long = registerDefaultPotion(6, new PotionEffective(6));
    public static final Potion invisible = registerDefaultPotion(7, new PotionEffective(7));
    public static final Potion invisible_long = registerDefaultPotion(8, new PotionEffective(8));
    public static final Potion leaping = registerDefaultPotion(9, new PotionEffective(9));
    public static final Potion leaping_long = registerDefaultPotion(10, new PotionEffective(10));
    public static final Potion leaping_II = registerDefaultPotion(11, new PotionEffective(11).setLevelII());
    public static final Potion fire_resistance = registerDefaultPotion(12, new PotionEffective(12));
    public static final Potion fire_resistance_long = registerDefaultPotion(13, new PotionEffective(13));
    public static final Potion speed = registerDefaultPotion(14, new PotionEffective(14));
    public static final Potion speed_long = registerDefaultPotion(15, new PotionEffective(15));
    public static final Potion speed_II = registerDefaultPotion(16, new PotionEffective(16).setLevelII());
    public static final Potion slowness = registerDefaultPotion(17, new PotionEffective(17));
    public static final Potion slowness_long = registerDefaultPotion(18, new PotionEffective(18));
    public static final Potion water_breathing = registerDefaultPotion(19, new PotionEffective(19));
    public static final Potion water_breathing_long = registerDefaultPotion(20, new PotionEffective(20));
    public static final Potion instant_health = registerDefaultPotion(21, new PotionEffective(21));
    public static final Potion instant_health_II = registerDefaultPotion(22, new PotionEffective(22).setLevelII());
    public static final Potion harming = registerDefaultPotion(23, new PotionEffective(23));
    public static final Potion harming_II = registerDefaultPotion(24, new PotionEffective(24).setLevelII());
    public static final Potion poison = registerDefaultPotion(25, new PotionEffective(25));
    public static final Potion poison_long = registerDefaultPotion(26, new PotionEffective(26));
    public static final Potion poison_II = registerDefaultPotion(27, new PotionEffective(27).setLevelII());
    public static final Potion regeneration = registerDefaultPotion(28, new PotionEffective(28));
    public static final Potion regeneration_long = registerDefaultPotion(29, new PotionEffective(29));
    public static final Potion regeneration_II = registerDefaultPotion(30, new PotionEffective(30).setLevelII());
    public static final Potion strength = registerDefaultPotion(31, new PotionEffective(31));
    public static final Potion strength_long = registerDefaultPotion(32, new PotionEffective(32));
    public static final Potion strength_II = registerDefaultPotion(33, new PotionEffective(33).setLevelII());
    public static final Potion weakness = registerDefaultPotion(34, new PotionEffective(34));
    public static final Potion weakness_long = registerDefaultPotion(35, new PotionEffective(35));

    public static Potion registerPotion(int id, Potion potion, Plugin plugin) {
        Objects.requireNonNull(potion);
        Objects.requireNonNull(plugin);
        registryCustom.put(new NodeIDPlugin(id, plugin), potion);
        return potion;
    }

    private static Potion registerDefaultPotion(int id, Potion potion) {
        registryDefault.put(id, potion);
        return potion;
    }

    public static Potion getPotion(int id) {
        final Potion[] result = {null};
        registryCustom.forEach((n, p) -> {
            if (n.id == id && n.plugin.isEnabled()) result[0] = p;
        });
        if (result[0] == null) result[0] = registryDefault.getOrDefault(id, null);
        return result[0];
    }

    protected int displayType = 0;//what this potion "looks like" in client
    protected boolean levelII = false;
    protected boolean splashPotion = false;

    public final void applyTo(Entity entity) {
        PotionApplyEvent event = new PotionApplyEvent(this, entity);
        entity.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) return;
        event.getPotion().onApplyTo(entity);
    }

    protected abstract void onApplyTo(Entity entity);

    public final void thrownPotionCollide(ThrownPotion potionEntity) {
        if (!this.isSplashPotion()) return;
        Potion potion = Potion.getPotion(potionEntity.getPotionType()).setSplashPotion();
        Particle[] particles = potion.getParticles(potionEntity);
        if (particles != null) {
            for (Particle particle : particles) {
                if (particle != null) potionEntity.getLevel().addParticle(particle);
            }
        }
        onThrownPotionCollide(potionEntity);
    }

    protected void onThrownPotionCollide(ThrownPotion potionEntity) {

    }

    public int getDisplayType() {
        return displayType;
    }

    public Potion setDisplayType(int displayType) {
        this.displayType = displayType;
        return this;
    }

    public boolean isLevelII() {
        return levelII;
    }

    public Potion setLevelII() {
        this.levelII = true;
        return this;
    }

    public boolean isSplashPotion() {
        return splashPotion;
    }

    public Potion setSplashPotion() {
        this.splashPotion = true;
        return this;
    }

    public Particle[] getParticles(Vector3 pos) {
        return new Particle[]{getParticle(getDisplayType(), pos)};
    }

    static class NodeIDPlugin {
        int id;
        Plugin plugin;

        public NodeIDPlugin(int id, Plugin plugin) {
            this.id = id;
            this.plugin = plugin;
        }
    }

    public static boolean isTypeValid(int potionType) {
        return potionType >= 0 && potionType <= 35;
    }

    public static int requireValid(int potionType) {
        if (!isTypeValid(potionType)) throw new IllegalArgumentException("Invalid potion type: " + potionType);
        return potionType;
    }

    public static int getApplySeconds(int potionType, boolean isSplash) {
        if (isSplash) {
            switch (potionType) {
                case NO_EFFECTS:
                    return 0;
                case MUNDANE:
                    return 0;
                case MUNDANE_II:
                    return 0;
                case THICK:
                    return 0;
                case AWKWARD:
                    return 0;
                case NIGHT_VISION:
                    return 135;
                case NIGHT_VISION_LONG:
                    return 360;
                case INVISIBLE:
                    return 135;
                case INVISIBLE_LONG:
                    return 360;
                case LEAPING:
                    return 135;
                case LEAPING_LONG:
                    return 360;
                case LEAPING_II:
                    return 67;
                case FIRE_RESISTANCE:
                    return 135;
                case FIRE_RESISTANCE_LONG:
                    return 360;
                case SPEED:
                    return 135;
                case SPEED_LONG:
                    return 360;
                case SPEED_II:
                    return 67;
                case SLOWNESS:
                    return 67;
                case SLOWNESS_LONG:
                    return 180;
                case WATER_BREATHING:
                    return 135;
                case WATER_BREATHING_LONG:
                    return 360;
                case INSTANT_HEALTH:
                    return 0;
                case INSTANT_HEALTH_II:
                    return 0;
                case HARMING:
                    return 0;
                case HARMING_II:
                    return 0;
                case POISON:
                    return 33;
                case POISON_LONG:
                    return 90;
                case POISON_II:
                    return 16;
                case REGENERATION:
                    return 33;
                case REGENERATION_LONG:
                    return 90;
                case REGENERATION_II:
                    return 16;
                case STRENGTH:
                    return 135;
                case STRENGTH_LONG:
                    return 360;
                case STRENGTH_II:
                    return 67;
                case WEAKNESS:
                    return 67;
                case WEAKNESS_LONG:
                    return 180;
                default:
                    return 0;
            }
        } else {
            switch (potionType) {
                case NO_EFFECTS:
                    return 0;
                case MUNDANE:
                    return 0;
                case MUNDANE_II:
                    return 0;
                case THICK:
                    return 0;
                case AWKWARD:
                    return 0;
                case NIGHT_VISION:
                    return 180;
                case NIGHT_VISION_LONG:
                    return 480;
                case INVISIBLE:
                    return 180;
                case INVISIBLE_LONG:
                    return 480;
                case LEAPING:
                    return 180;
                case LEAPING_LONG:
                    return 480;
                case LEAPING_II:
                    return 90;
                case FIRE_RESISTANCE:
                    return 180;
                case FIRE_RESISTANCE_LONG:
                    return 480;
                case SPEED:
                    return 180;
                case SPEED_LONG:
                    return 480;
                case SPEED_II:
                    return 480;
                case SLOWNESS:
                    return 90;
                case SLOWNESS_LONG:
                    return 240;
                case WATER_BREATHING:
                    return 180;
                case WATER_BREATHING_LONG:
                    return 480;
                case INSTANT_HEALTH:
                    return 0;
                case INSTANT_HEALTH_II:
                    return 0;
                case HARMING:
                    return 0;
                case HARMING_II:
                    return 0;
                case POISON:
                    return 45;
                case POISON_LONG:
                    return 120;
                case POISON_II:
                    return 22;
                case REGENERATION:
                    return 45;
                case REGENERATION_LONG:
                    return 120;
                case REGENERATION_II:
                    return 22;
                case STRENGTH:
                    return 180;
                case STRENGTH_LONG:
                    return 480;
                case STRENGTH_II:
                    return 90;
                case WEAKNESS:
                    return 90;
                case WEAKNESS_LONG:
                    return 240;
                default:
                    return 0;
            }
        }
    }

    public static boolean isIILevel(int potionType) {
        switch (potionType) {
            case MUNDANE_II:
            case LEAPING_II:
            case SPEED_II:
            case INSTANT_HEALTH_II:
            case HARMING_II:
            case POISON_II:
            case REGENERATION_II:
            case STRENGTH_II:
                return true;
            default:
                return false;
        }
    }

    public static boolean isEffectInstant(int potionType) {
        switch (potionType) {
            case INSTANT_HEALTH:
            case INSTANT_HEALTH_II:
            case HARMING:
            case HARMING_II:
                return true;
            default:
                return false;
        }
    }

    public static Effect getApplyEffect(int potionType, boolean isSplash) {
        Effect raw;
        switch (potionType) {
            case NO_EFFECTS:
            case MUNDANE:
            case MUNDANE_II:
            case THICK:
            case AWKWARD:
                return null;
            case NIGHT_VISION:
            case NIGHT_VISION_LONG:
                raw = Effect.getEffect(Effect.NIGHT_VISION);
                break;
            case INVISIBLE:
            case INVISIBLE_LONG:
                raw = Effect.getEffect(Effect.INVISIBILITY);
                break;
            case LEAPING:
            case LEAPING_LONG:
            case LEAPING_II:
                raw = Effect.getEffect(Effect.JUMP);
                break;
            case FIRE_RESISTANCE:
            case FIRE_RESISTANCE_LONG:
                raw = Effect.getEffect(Effect.FIRE_RESISTANCE);
                break;
            case SPEED:
            case SPEED_LONG:
            case SPEED_II:
                raw = Effect.getEffect(Effect.SPEED);
                break;
            case SLOWNESS:
            case SLOWNESS_LONG:
                raw = Effect.getEffect(Effect.SLOWNESS);
                break;
            case WATER_BREATHING:
            case WATER_BREATHING_LONG:
                raw = Effect.getEffect(Effect.WATER_BREATHING);
                break;
            case INSTANT_HEALTH:
            case INSTANT_HEALTH_II:
                return Effect.getEffect(Effect.HEALING);
            case HARMING:
            case HARMING_II:
                return Effect.getEffect(Effect.HARMING);
            case POISON:
            case POISON_LONG:
            case POISON_II:
                raw = Effect.getEffect(Effect.POISON);
                break;
            case REGENERATION:
            case REGENERATION_LONG:
            case REGENERATION_II:
                raw = Effect.getEffect(Effect.REGENERATION);
                break;
            case STRENGTH:
            case STRENGTH_LONG:
            case STRENGTH_II:
                raw = Effect.getEffect(Effect.STRENGTH);
                break;
            case WEAKNESS:
            case WEAKNESS_LONG:
                raw = Effect.getEffect(Effect.WEAKNESS);
                break;
            default:
                return null;
        }
        if (isIILevel(potionType)) {
            raw.setAmplifier(1);
        }
        if (!isEffectInstant(potionType)) {
            raw.setDuration(20 * getApplySeconds(potionType, isSplash));
        }
        return raw;
    }

    public static Particle getParticle(int potionType, Vector3 pos) {
        requireValid(potionType);
        Effect effect = getApplyEffect(potionType, true);
        if (effect == null) return null;
        int[] colors = effect.getColor();
        int r = colors[0];
        int g = colors[1];
        int b = colors[2];
        if (isEffectInstant(potionType)) {
            return new InstantSpellParticle(pos, r, g, b);
        } else {
            return new SpellParticle(pos, r, g, b);
        }
    }

    public static void applyPotion(int potionType, boolean isSplash, Entity entity) {
        if (!(entity instanceof Living)) return;
        Effect applyEffect = getApplyEffect(potionType, isSplash);
        if (applyEffect == null) return;
        if (entity instanceof Player) if (!((Player) entity).isSurvival() && applyEffect.isBad()) return;
        switch (potionType) {
            case INSTANT_HEALTH:
                entity.heal(4, new EntityRegainHealthEvent(entity, 4, EntityRegainHealthEvent.CAUSE_EATING));
                break;
            case INSTANT_HEALTH_II:
                entity.heal(8, new EntityRegainHealthEvent(entity, 8, EntityRegainHealthEvent.CAUSE_EATING));
                break;
            case HARMING:
                entity.attack(new EntityDamageEvent(entity, EntityDamageEvent.CAUSE_MAGIC, 6));
                break;
            case HARMING_II:
                entity.attack(new EntityDamageEvent(entity, EntityDamageEvent.CAUSE_MAGIC, 12));
                break;
            default:
                entity.addEffect(applyEffect);
        }
    }

}
