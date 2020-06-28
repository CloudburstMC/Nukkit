package cn.nukkit.network.protocol;

import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.Since;
import lombok.ToString;

/**
 * @author Nukkit Project Team
 */
@ToString
public class HurtArmorPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.HURT_ARMOR_PACKET;

    /**
     * @deprecated Renamed to damage by NukkitX, will be removed on 1.3.0.0-PN 
     */
    @Deprecated @DeprecationDetails(
            since = "1.3.0.0-PN", replaceWith = "damage", 
            toBeRemovedAt = "1.4.0.0-PN", reason = "Renamed to damage by NukkitX")
    public int health;

    @Since("1.3.0.0-PN")
    public int cause;

    @Since("1.3.0.0-PN")
    public int damage;

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putVarInt(this.cause);
        this.putVarInt(health == 0? damage : health);
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

}
