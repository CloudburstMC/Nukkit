package cn.nukkit.network.protocol;

import cn.nukkit.entity.data.Skin;
import lombok.ToString;

import java.util.UUID;

@ToString
public class PlayerSkinPacket extends DataPacket {

    public UUID uuid;
    public Skin skin;
    public String newSkinName;
    public String oldSkinName;

    @Override
    public byte pid() {
        return ProtocolInfo.PLAYER_SKIN_PACKET;
    }

    @Override
    public void decode() {
        this.uuid = this.getUUID();
        this.skin = this.getSkin();
        this.newSkinName = this.getString();
        this.oldSkinName = this.getString();
        if (!this.feof()) { //Facepalm
            this.skin.setTrusted(this.getBoolean());
        }
    }

    @Override
    public void encode() {
        this.reset();
        this.putUUID(this.uuid);
        this.putSkin(this.skin);
        this.putString(this.newSkinName);
        this.putString(this.oldSkinName);
        this.putBoolean(this.skin.isTrusted());
    }
}
