package cn.nukkit.network.protocol;

import cn.nukkit.network.protocol.types.AbilityLayer;
import cn.nukkit.network.protocol.types.PlayerAbility;
import cn.nukkit.utils.BinaryStream;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.EnumMap;
import java.util.List;
import java.util.Set;

@ToString
@Getter
@Setter
public class UpdateAbilitiesPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.UPDATE_ABILITIES_PACKET;

    private static final EnumMap<PlayerAbility, Integer> FLAGS_TO_BITS = new EnumMap<>(PlayerAbility.class);

    static {
        PlayerAbility[] VALID_FLAGS = PlayerAbility.values();
        for (int i = 0; i < VALID_FLAGS.length; i++) {
            FLAGS_TO_BITS.put(VALID_FLAGS[i], (1 << i));
        }
    }

    private long entityId;
    private PlayerPermission playerPermission;
    private CommandPermission commandPermission;
    private final List<AbilityLayer> abilityLayers = new ObjectArrayList<>(2);

    @Override
    public void decode() {
        this.decodeUnsupported();
    }

    @Override
    public void encode() {
        this.reset();
        this.putLLong(this.entityId);
        this.putUnsignedVarInt(this.playerPermission.ordinal());
        this.putUnsignedVarInt(this.commandPermission.ordinal());
        this.putArray(this.abilityLayers, this::writeAbilityLayer);
    }

    private void writeAbilityLayer(BinaryStream buffer, AbilityLayer abilityLayer) {
        buffer.putLShort(abilityLayer.getLayerType().ordinal());
        buffer.putLInt(getAbilitiesNumber(abilityLayer.getAbilitiesSet()));
        buffer.putLInt(getAbilitiesNumber(abilityLayer.getAbilityValues()));
        buffer.putLFloat(abilityLayer.getFlySpeed());
        buffer.putLFloat(abilityLayer.getVerticalFlySpeed());
        buffer.putLFloat(abilityLayer.getWalkSpeed());
    }

    private static int getAbilitiesNumber(Set<PlayerAbility> abilities) {
        int number = 0;
        for (PlayerAbility ability : abilities) {
            number |= FLAGS_TO_BITS.getOrDefault(ability, 0);
        }
        return number;
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    public enum PlayerPermission {
        VISITOR,
        MEMBER,
        OPERATOR,
        CUSTOM
    }

    public enum CommandPermission {
        NORMAL,
        OPERATOR,
        HOST,
        AUTOMATION,
        ADMIN
    }
}
