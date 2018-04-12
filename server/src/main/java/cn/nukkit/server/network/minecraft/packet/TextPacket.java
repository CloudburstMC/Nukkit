/*
 * GNU GENERAL PUBLIC LICENSE
 * Copyright (C) 2018 NukkitX Project
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * verion 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * Contact info: info@nukkitx.com
 */

package cn.nukkit.server.network.minecraft.packet;

import cn.nukkit.api.message.*;
import cn.nukkit.server.network.minecraft.MinecraftPacket;
import cn.nukkit.server.network.minecraft.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static cn.nukkit.server.nbt.util.VarInt.readUnsignedInt;
import static cn.nukkit.server.network.minecraft.MinecraftUtil.readString;
import static cn.nukkit.server.network.minecraft.MinecraftUtil.writeString;

@Data
public class TextPacket implements MinecraftPacket {
    private Message message;
    private String xuid;
    private String platformChatId = "";

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeByte(message.getType().ordinal());
        buffer.writeBoolean(message.needsTranslating());
        if (message instanceof SourceMessage) {
            writeString(buffer, ((SourceMessage) message).getSender());
        }
        writeString(buffer, message.getMessage());

        if (message instanceof ParameterMessage) {
            for (String param : ((ParameterMessage) message).getParameters()) {
                writeString(buffer, param);
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
        String sourceThirdPartyName = "";
        int platformId; //TODO: Implement these into message api
        String message = null;
        String[] parameters = null;
        switch (type) {
            case CHAT:
            case WHISPER:
            case ANNOUNCEMENT:
                source = readString(buffer);
                sourceThirdPartyName = readString(buffer);
                platformId = readUnsignedInt(buffer);
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
