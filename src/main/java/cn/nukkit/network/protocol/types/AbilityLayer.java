package cn.nukkit.network.protocol.types;

import lombok.Data;

import java.util.EnumSet;
import java.util.Set;

@Data
public class AbilityLayer {
    private Type layerType;
    private final Set<PlayerAbility> abilitiesSet = EnumSet.noneOf(PlayerAbility.class);
    private final Set<PlayerAbility> abilityValues = EnumSet.noneOf(PlayerAbility.class);
    private float flySpeed;
    private float walkSpeed;

    public enum Type {
        CACHE,
        BASE,
        SPECTATOR,
        COMMANDS
    }
}
