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
    public boolean premium;

    @Override
    public short pid() {
        return ProtocolInfo.PLAYER_SKIN_PACKET;
    }

    @Override
    protected void decode(ByteBuf buffer) {
        uuid = Binary.readUuid(buffer);
        skin = new Skin();
        skin.setSkinId(Binary.readString(buffer));
        newSkinName = Binary.readString(buffer);
        oldSkinName = Binary.readString(buffer);
        skin.setSkinData(Binary.readByteArray(buffer));
        skin.setCapeData(Binary.readByteArray(buffer));
        skin.setGeometryName(Binary.readString(buffer));
        skin.setGeometryData(Binary.readString(buffer));
        premium = buffer.readBoolean();
    }

    @Override
    protected void encode(ByteBuf buffer) {
        Binary.writeUuid(buffer, uuid);
        Binary.writeString(buffer, skin.getGeometryName());
        Binary.writeString(buffer, newSkinName);
        Binary.writeString(buffer, oldSkinName);
        Binary.writeByteArray(buffer, skin.getSkinData());
        Binary.writeByteArray(buffer, skin.getCapeData());
        Binary.writeString(buffer, skin.getGeometryName());
        Binary.writeString(buffer, skin.getGeometryData());
        buffer.writeBoolean(premium);
    }
}
