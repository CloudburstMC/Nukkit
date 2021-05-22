package cn.nukkit.network.protocol;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.math.BlockVector3;
import lombok.ToString;

@Since("1.4.0.0-PN")
@ToString
public class AnvilDamagePacket extends DataPacket {
    @Since("1.4.0.0-PN") @PowerNukkitOnly public static final byte NETWORK_ID = ProtocolInfo.ANVIL_DAMAGE_PACKET;

    @Since("1.4.0.0-PN") public int damage;
    @Since("1.4.0.0-PN") public int x;
    @Since("1.4.0.0-PN") public int y;
    @Since("1.4.0.0-PN") public int z;

    @Override
    public byte pid() {
        return ProtocolInfo.ANVIL_DAMAGE_PACKET;
    }

    @Override
    public void decode() {
        this.damage = this.getByte();
        BlockVector3 vec = this.getBlockVector3();
        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;
    }

    @Override
    public void encode() {

    }
}
