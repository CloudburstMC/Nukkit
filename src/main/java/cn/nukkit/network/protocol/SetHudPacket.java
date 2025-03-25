package cn.nukkit.network.protocol;

import cn.nukkit.network.protocol.types.HudElement;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.ToString;

import java.util.List;

@ToString
public class SetHudPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.__INTERNAL__SET_HUD_PACKET;

    public final List<HudElement> elements = new ObjectArrayList<>();
    public boolean visible;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.decodeUnsupported();
    }

    @Override
    public void encode() {
        this.reset();
        this.putUnsignedVarInt(this.elements.size());
        for (HudElement element : this.elements) {
            this.putVarInt(element.ordinal());
        }
        this.putVarInt(this.visible ? 1 : 0);
    }
}
