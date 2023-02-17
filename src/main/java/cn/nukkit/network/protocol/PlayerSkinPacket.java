package cn.nukkit.network.protocol;

import cn.nukkit.Server;
import cn.nukkit.entity.data.Skin;
import lombok.ToString;

import java.util.UUID;

@ToString
public class PlayerSkinPacket extends DataPacket {

    public UUID uuid;
    public Skin skin;
    public String newSkinName;

    @Override
    public byte pid() {
        return ProtocolInfo.PLAYER_SKIN_PACKET;
    }

    @Override
    public void decode() {
        uuid = getUUID();
        skin = getSkin();
        newSkinName = getString();
    }

    @Override
    public void encode() {
        reset();
        putUUID(uuid);
        putSkin(skin);
        putString(newSkinName);
        putBoolean(skin.isTrusted());
    }
}
