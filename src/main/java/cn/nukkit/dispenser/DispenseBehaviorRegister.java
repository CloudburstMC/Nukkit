package cn.nukkit.dispenser;

import java.util.HashMap;
import java.util.Map;

/**
 * @author CreeperFace
 */
public class DispenseBehaviorRegister {

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
}
