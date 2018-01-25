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
    public byte pid() {
        return ProtocolInfo.PLAYER_SKIN_PACKET;
    }

    @Override
    public void decode() {
        this.uuid = this.getUUID();
        String skinId = this.getString();
        this.skinName = this.getString();
        this.oldSkinName = this.getString();
        this.getInt(); // Always 1
        this.getInt(); // No point in storing this.
        byte[] data = this.getByteArray();
        this.getInt(); // Always 1
        this.getInt(); // Same with this one.
        byte[] cape = this.getByteArray();
        this.skin = new Skin(data, skinId);
        this.skin.setCape(this.skin.new Cape(cape));

        this.geometryModel = this.getString();
        this.geometryData = this.getString();
    }

    @Override
    public void encode() {
        this.reset();
        this.putUUID(this.uuid);
        this.putString(this.skin.getModel());
        this.putString(this.skinName);
        this.putString(this.oldSkinName);
        this.putInt(1); // Pointless at the moment
        this.putInt(this.skin.getData().length);
        this.putByteArray(this.skin.getData());
        this.putInt(1); // Another useless number
        this.putInt(this.skin.getCape().getData().length);
        this.putByteArray(this.skin.getCape().getData());
        this.putString(this.geometryModel);
        this.putString(this.geometryData);
    }
}
