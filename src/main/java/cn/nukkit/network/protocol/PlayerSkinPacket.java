package cn.nukkit.network.protocol;

import cn.nukkit.entity.data.Skin;

import java.util.UUID;

public class PlayerSkinPacket extends DataPacket {

    public UUID uuid;
    public Skin skin;
    public String skinName;
    public String oldSkinName;
    public String geometryModel;
    public String geometryData;

    @Override
    public byte pid(PlayerProtocol protocol) {
        return protocol.getPacketId("PLAYER_SKIN_PACKET");
    }

    @Override
    public void decode(PlayerProtocol protocol) {
        this.uuid = this.getUUID(protocol);
        String skinId = this.getString();
        this.skinName = this.getString();
        this.oldSkinName = this.getString();
        byte[] data = this.getByteArray();
        byte[] cape = this.getByteArray();
        this.skin = new Skin(data, skinId);
        this.skin.setCape(this.skin.new Cape(cape));
        this.geometryModel = this.getString();
        this.geometryData = this.getString();
    }

    @Override
    public void encode(PlayerProtocol protocol) {
        this.reset(protocol);
        this.putUUID(this.uuid, protocol);
        this.putString(this.skin.getModel());
        this.putString(this.skinName);
        this.putString(this.oldSkinName);
        this.putByteArray(this.skin.getData());
        this.putByteArray(this.skin.getCape().getData());
        this.putString(this.geometryModel);
        this.putString(this.geometryData);
    }
}
