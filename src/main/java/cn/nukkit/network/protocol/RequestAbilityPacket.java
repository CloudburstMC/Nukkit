package cn.nukkit.network.protocol;

import cn.nukkit.network.protocol.types.PlayerAbility;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class RequestAbilityPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.REQUEST_ABILITY_PACKET;

    protected static final PlayerAbility[] ABILITIES = PlayerAbility.values();
    protected static final AbilityType[] ABILITY_TYPES = AbilityType.values();

    private PlayerAbility ability;
    private AbilityType type;
    private boolean boolValue;
    private float floatValue;

    @Override
    public void decode() {
        this.setAbility(ABILITIES[this.getVarInt()]);
        this.setType(ABILITY_TYPES[this.getByte()]);
        this.setBoolValue(this.getBoolean());
        this.setFloatValue(this.getLFloat());
    }

    @Override
    public void encode() {
        this.encodeUnsupported();
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    public enum AbilityType {
        NONE,
        BOOLEAN,
        FLOAT
    }
}
