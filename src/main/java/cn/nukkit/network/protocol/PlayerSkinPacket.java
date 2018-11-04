package cn.nukkit.network.protocol;

import cn.nukkit.entity.data.Skin;

import java.util.UUID;

public class PlayerSkinPacket extends DataPacket {

    public UUID uuid;
    public Skin skin;
    public String newSkinName;
    public String oldSkinName;
    public boolean premium;

    @Override
    public byte pid() {
        return ProtocolInfo.PLAYER_SKIN_PACKET;
    }

    @Override
    public void decode() {
        uuid = getUUID();
        skin = new Skin();
        skin.setSkinId(getString());
        newSkinName = getString();
        oldSkinName = getString();
        skin.setEncodedSkinData(getByteArray());
        skin.setEncodedCapeData(getByteArray());
        skin.setGeometryName(getString());
        skin.setEncodedGeometryData(getByteArray());
        premium = getBoolean();
    }

    @Override
    public void encode() {
        reset();
        putUUID(uuid);
        putString(skin.getGeometryName());
        putString(newSkinName);
        putString(oldSkinName);
        putByteArray(skin.getEncodedSkinData());
        putByteArray(skin.getEncodedCapeData());
        putString(skin.getGeometryName());
        putByteArray(skin.getEncodedGeometryData());
        putBoolean(premium);
    }
}
