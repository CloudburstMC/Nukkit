package cn.nukkit.potion;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.ThrownPotion;
import cn.nukkit.level.particle.Particle;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Potions;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Snake1999 on 2016/1/11.
 * Package cn.nukkit.potion in project nukkit
 */
public abstract class Potion {

    private static Map<Integer, Potion> registry = new LinkedHashMap<>();

    public static final Potion no_effects = registerPotion(0, new PotionNoEffect().setDisplayType(0));
    public static final Potion mundane = registerPotion(1, new PotionNoEffect().setDisplayType(1));
    public static final Potion mundane_II = registerPotion(2, new PotionNoEffect().setDisplayType(2).setLevelII());
    public static final Potion thick = registerPotion(3, new PotionNoEffect().setDisplayType(3));
    public static final Potion awkward = registerPotion(4, new PotionNoEffect().setDisplayType(4));
    public static final Potion night_vision = registerPotion(5, new PotionEffective(5));
    public static final Potion night_vision_long = registerPotion(6, new PotionEffective(6));
    public static final Potion invisible = registerPotion(7, new PotionEffective(7));
    public static final Potion invisible_long = registerPotion(8, new PotionEffective(8));
    public static final Potion leaping = registerPotion(9, new PotionEffective(9));
    public static final Potion leaping_long = registerPotion(10, new PotionEffective(10));
    public static final Potion leaping_II = registerPotion(11, new PotionEffective(11).setLevelII());
    public static final Potion fire_resistance = registerPotion(12, new PotionEffective(12));
    public static final Potion fire_resistance_long = registerPotion(13, new PotionEffective(13));
    public static final Potion speed = registerPotion(14, new PotionEffective(14));
    public static final Potion speed_long = registerPotion(15, new PotionEffective(15));
    public static final Potion speed_II = registerPotion(16, new PotionEffective(16).setLevelII());
    public static final Potion slowness = registerPotion(17, new PotionEffective(17));
    public static final Potion slowness_long = registerPotion(18, new PotionEffective(18));
    public static final Potion water_breathing = registerPotion(19, new PotionEffective(19));
    public static final Potion water_breathing_long = registerPotion(20, new PotionEffective(20));
    public static final Potion instant_health = registerPotion(21, new PotionEffective(21));
    public static final Potion instant_health_II = registerPotion(22, new PotionEffective(22).setLevelII());
    public static final Potion harming = registerPotion(23, new PotionEffective(23));
    public static final Potion harming_II = registerPotion(24, new PotionEffective(24).setLevelII());
    public static final Potion poison = registerPotion(25, new PotionEffective(25));
    public static final Potion poison_long = registerPotion(26, new PotionEffective(26));
    public static final Potion poison_II = registerPotion(27, new PotionEffective(27).setLevelII());
    public static final Potion regeneration = registerPotion(28, new PotionEffective(28));
    public static final Potion regeneration_long = registerPotion(29, new PotionEffective(29));
    public static final Potion regeneration_II = registerPotion(30, new PotionEffective(30).setLevelII());
    public static final Potion strength = registerPotion(31, new PotionEffective(31));
    public static final Potion strength_long = registerPotion(32, new PotionEffective(32));
    public static final Potion strength_II = registerPotion(33, new PotionEffective(33).setLevelII());
    public static final Potion weakness = registerPotion(34, new PotionEffective(34));
    public static final Potion weakness_long = registerPotion(35, new PotionEffective(35));

    public static Potion registerPotion(int id, Potion potion) {
        registry.put(id, potion);
        return potion;
    }

    public static Potion registerPotion(Potion potion) {
        return registerPotion(registry.size() + 1, potion);
    }

    public static Potion getPotion(int id) {
        return registry.getOrDefault(id, null);
    }

    protected int displayType = 0;//what this potion "looks like" in client
    protected boolean levelII = false;
    protected boolean splashPotion = false;

    public final void applyTo(Entity entity) {
        //todo events?
        onApplyTo(entity);
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
        return new Particle[]{Potions.getParticle(getDisplayType(), pos)};
    }

}
