package com.nukkitx.server.network.bedrock.packet;

import com.nukkitx.api.message.*;
import com.nukkitx.server.network.bedrock.BedrockPacket;
import com.nukkitx.server.network.bedrock.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static com.nukkitx.server.network.bedrock.BedrockUtil.readString;
import static com.nukkitx.server.network.bedrock.BedrockUtil.writeString;
import static com.nukkitx.server.network.util.VarInts.*;

@Data
public class TextPacket implements BedrockPacket {
    private Message message;
    private String xuid;
    private String platformChatId = "";

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeByte(message.getType().ordinal());
        buffer.writeBoolean(message.needsTranslating());

        if (message instanceof SourceMessage) {
            writeString(buffer, ((SourceMessage) message).getSender());
            writeString(buffer, ""); //sourceThirdPartyName
            writeInt(buffer, 0); //PlatformID
        }

        writeString(buffer, message.getMessage());

        if (message instanceof ParameterMessage) {
            String[] parameters = ((ParameterMessage) message).getParameters();
            writeUnsignedInt(buffer, parameters.length);
            for (String parameter: parameters) {
                writeString(buffer, parameter);
            }
        }
        writeString(buffer, xuid);
        writeString(buffer, platformChatId);
    }

    @Override
    public void decode(ByteBuf buffer) {
        Type type = Type.values()[buffer.readByte()];
        boolean needsTranslating = buffer.readBoolean();
        String source = "";
        String message = null;
        String[] parameters = null;
        switch (type) {
            case CHAT:
            case WHISPER:
            case ANNOUNCEMENT:
                source = readString(buffer);
                readString(buffer);//TODO
                readInt(buffer);//TODO
            case RAW:
            case TIP:
            case SYSTEM:
                message = readString(buffer);
                break;
            case TRANSLATION:
            case POPUP:
            case JUKEBOX_POPUP:
                message = readString(buffer);
                int parameterSize = readUnsignedInt(buffer);
                parameters = new String[parameterSize];
                for (int i = 0; i < parameterSize; i++) {
                    parameters[i] = readString(buffer);
                }
        }
        xuid = readString(buffer);
        platformChatId = readString(buffer);

        switch (type) {
            case CHAT:
                this.message = new ChatMessage(source, message, needsTranslating);
                break;
            case WHISPER:
                this.message = new WhisperMessage(source, message, needsTranslating);
                break;
            case ANNOUNCEMENT:
                this.message = new AnnouncementMessage(source, message, needsTranslating);
                break;
            case RAW:
                this.message = new RawMessage(message, needsTranslating);
                break;
            case TIP:
                this.message = new TipMessage(message, needsTranslating);
                break;
            case SYSTEM:
                this.message = new SystemMessage(message, needsTranslating);
                break;
            case TRANSLATION:
                this.message = new TranslationMessage(message, parameters);
                break;
            case POPUP:
                this.message = new PopupMessage(message, needsTranslating, parameters);
                break;
            case JUKEBOX_POPUP:
                this.message = new JukeboxPopupMessage(message, needsTranslating, parameters);
        }
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        handler.handle(this);
    }

    public enum Type {
        RAW,
        CHAT,
        TRANSLATION,
        POPUP,
        JUKEBOX_POPUP,
        TIP,
        SYSTEM,
        WHISPER,
        ANNOUNCEMENT
    }
}
