package com.nukkitx.server.network.bedrock.packet;

import com.nukkitx.server.network.bedrock.BedrockPacket;
import com.nukkitx.server.network.bedrock.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static com.nukkitx.server.network.bedrock.BedrockUtil.writeString;
import static com.nukkitx.server.network.bedrock.BedrockUtil.writeUniqueEntityId;
import static com.nukkitx.server.network.util.VarInts.writeUnsignedInt;

@Data
public class BossEventPacket implements BedrockPacket {
    private long bossUniqueEntityId;
    private Type type;
    private long playerUniqueEntityId;
    private String title;
    private float healthPercentage;
    private short unknown0;
    private int color;
    private int overlay;

    @Override
    public void encode(ByteBuf buffer) {
        writeUniqueEntityId(buffer, bossUniqueEntityId);
        writeUnsignedInt(buffer, type.ordinal());

        switch (type) {
            case REGISTER_PLAYER:
            case UNREGISTER_PLAYER:
                writeUniqueEntityId(buffer, playerUniqueEntityId);
                break;
            case SHOW:
                writeString(buffer, title);
                buffer.writeFloatLE(healthPercentage);
            case UNKNOWN:
                buffer.writeShortLE(unknown0);
            case TEXTURE:
                writeUnsignedInt(buffer, color);
                writeUnsignedInt(buffer, overlay);
                break;
            case HEALTH_PERCENTAGE:
                buffer.writeFloatLE(healthPercentage);
                break;
            case TITLE:
                writeString(buffer, title);
                break;
            default:
                throw new RuntimeException("BossEvent type was unknown!");
        }
    }

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        // This packet isn't handled
    }

    public enum Type {
        /**
         * Shows the bossbar to the player.
         */
        SHOW,
        /**
         * Registers a player to a boss fight.
         */
        REGISTER_PLAYER,
        /**
         * Removes the bossbar from the client.
         */
        HIDE,
        /**
         * Unregisters a player from a boss fight.
         */
        UNREGISTER_PLAYER,
        /**
         * Appears not to be implemented. Currently bar percentage only appears to change in response to the target entity's health.
         */
        HEALTH_PERCENTAGE,
        /**
         * Also appears to not be implemented. Title clientside sticks as the target entity's nametag, or their entity type name if not set.
         */
        TITLE,
        /**
         * Not sure on this. Includes color and overlay fields, plus an unknown short. TODO: check this
         */
        UNKNOWN,
        /**
         * Not implemented :( Intended to alter bar appearance, but these currently produce no effect on clientside whatsoever.
         */
        TEXTURE
    }
}
