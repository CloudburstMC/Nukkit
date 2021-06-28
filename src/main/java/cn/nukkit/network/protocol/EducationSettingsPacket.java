package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class EducationSettingsPacket extends DataPacket {

    public String codeBuilderDefaultUri;
    public String codeBuilderTitle;
    public boolean canResizeCodeBuilder;
    public String codeBuilderOverrideUri;
    public boolean hasQuiz;

    @Override
    public byte pid() {
        return ProtocolInfo.EDUCATION_SETTINGS_PACKET;
    }

    @Override
    public void decode() {
        this.codeBuilderDefaultUri = this.getString();
        this.codeBuilderTitle = this.getString();
        this.canResizeCodeBuilder = this.getBoolean();
        this.codeBuilderOverrideUri = this.getBoolean() ? this.getString : null;
        this.hasQuiz = this.getBoolean();
    }

    @Override
    public void encode() {
        this.reset();
        this.putString(this.codeBuilderDefaultUri);
        this.putString(this.codeBuilderTitle);
        this.putBoolean(this.canResizeCodeBuilder);
        this.putBoolean(this.codeBuilderOverrideUri != null);
        if (this.codeBuilderOverrideUri != null) {
            this.putString(this.codeBuilderOverrideUri);
        }
        this.putBoolean(this.hasQuiz);
    }
}
