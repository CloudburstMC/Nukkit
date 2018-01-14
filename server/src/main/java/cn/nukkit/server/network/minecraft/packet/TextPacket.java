package cn.nukkit.server.network.minecraft.packet;

import cn.nukkit.api.message.ParameterMessage;
import cn.nukkit.server.network.NetworkPacketHandler;
import cn.nukkit.server.network.minecraft.MinecraftPacket;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.util.Collection;

import static cn.nukkit.server.nbt.util.VarInt.writeUnsignedInt;
import static cn.nukkit.server.network.minecraft.MinecraftUtil.readString;
import static cn.nukkit.server.network.minecraft.MinecraftUtil.writeString;

@Data
public class TextPacket implements MinecraftPacket {
    private Type type;
    private boolean translationNeeded;
    private String source;
    private String message;
    private ParameterMessage parameterMessage;
    private String xuid;

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeByte(type.ordinal());
        buffer.writeBoolean(translationNeeded);
        switch (type) {
            case CHAT:
            case WHISPER:
            case ANNOUNCEMENT:
                writeString(buffer, source);
            case RAW:
            case TIP:
            case SYSTEM:
                writeString(buffer, message);
                break;
            case TRANSLATION:
            case POPUP:
            case JUKEBOX_POPUP:
                writeString(buffer, message);
                Collection<String> parameters = parameterMessage.getParameters();
                writeUnsignedInt(buffer, parameters.size());
                parameters.forEach(param -> writeString(buffer, param));
        }
        writeString(buffer, xuid);
    }

    @Override
    public void decode(ByteBuf buffer) {
        type = Type.values()[buffer.readByte()];
        translationNeeded = buffer.readBoolean();
        switch (type) {
            case CHAT:
            case WHISPER:
            case ANNOUNCEMENT:
                source = readString(buffer);
            case RAW:
            case TIP:
            case SYSTEM:
                message = readString(buffer);
                break;
            case TRANSLATION:
            case POPUP:
            case JUKEBOX_POPUP:
                message = readString(buffer);
                // TODO:
        }
        writeString(buffer, xuid);
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
