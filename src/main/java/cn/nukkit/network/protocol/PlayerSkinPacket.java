package cn.nukkit.network.protocol;

import cn.nukkit.entity.data.Skin;
import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

import java.util.UUID;

@ToString
public class PlayerSkinPacket extends DataPacket {

    public UUID uuid;
    public Skin skin;
    public String newSkinName;
    public String oldSkinName;

    @Override
    public short pid() {
        return ProtocolInfo.PLAYER_SKIN_PACKET;
    }

    @Override
    protected void decode(ByteBuf buffer) {
        uuid = Binary.readUuid(buffer);
        skin = Binary.readSkin(buffer);
        newSkinName = Binary.readString(buffer);
        oldSkinName = Binary.readString(buffer);
    }

    @Override
    protected void encode(ByteBuf buffer) {
        Binary.writeUuid(buffer, uuid);
        Binary.writeSkin(buffer, skin);
        Binary.writeString(buffer, newSkinName);
        Binary.writeString(buffer, oldSkinName);
    }
}
