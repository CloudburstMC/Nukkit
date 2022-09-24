package cn.nukkit.network.protocol;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class UpdateAdventureSettingsPacket extends DataPacket {
    private boolean noPvM;
    private boolean noMvP;
    private boolean immutableWorld;
    private boolean showNameTags;
    private boolean autoJump;

    @Override
    public void decode() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void encode() {
        this.reset();
        this.putBoolean(this.isNoPvM());
        this.putBoolean(this.isNoMvP());
        this.putBoolean(this.isImmutableWorld());
        this.putBoolean(this.isShowNameTags());
        this.putBoolean(this.isAutoJump());
    }

    @Override
    public byte pid() {
        return ProtocolInfo.UPDATE_ADVENTURE_SETTINGS_PACKET;
    }
}
