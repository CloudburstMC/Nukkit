package cn.nukkit.potion;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.ThrownPotion;
import cn.nukkit.event.potion.PotionApplyEvent;
import cn.nukkit.level.particle.Particle;
import cn.nukkit.math.Vector3;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.utils.Potions;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Snake1999 on 2016/1/11.
 * Package cn.nukkit.potion in project nukkit
 */
public abstract class Potion {

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
        return new Particle[]{Potions.getParticle(getDisplayType(), pos)};
    }

    static class NodeIDPlugin {
        int id;
        Plugin plugin;

        public NodeIDPlugin(int id, Plugin plugin) {
            this.id = id;
            this.plugin = plugin;
        }
    }

}
