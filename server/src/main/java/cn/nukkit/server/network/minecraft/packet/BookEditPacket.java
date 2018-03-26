package cn.nukkit.server.network.minecraft.packet;

import cn.nukkit.server.network.minecraft.MinecraftPacket;
import cn.nukkit.server.network.minecraft.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static cn.nukkit.server.network.minecraft.MinecraftUtil.readString;
import static cn.nukkit.server.network.minecraft.MinecraftUtil.writeString;

@Data
public class BookEditPacket implements MinecraftPacket {
    private Type type;
    private byte inventorySlot;
    private byte pageNumber;
    private byte secondaryPageNumber;
    private String text;
    private String photoName;
    private String title;
    private String author;
    private String xuid;

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeByte(type.ordinal());
        buffer.writeByte(inventorySlot);
        switch (type) {
            case REPLACE_PAGE:
            case ADD_PAGE:
                buffer.writeByte(pageNumber);
                writeString(buffer, text);
                writeString(buffer, photoName);
                break;
            case DELETE_PAGE:
                buffer.writeByte(pageNumber);
                break;
            case SWAP_PAGES:
                buffer.writeByte(pageNumber);
                buffer.writeByte(secondaryPageNumber);
                break;
            case SIGN_BOOK:
                writeString(buffer, title);
                writeString(buffer, author);
                writeString(buffer, xuid);
                break;
            default:
                throw new RuntimeException("BookEdit type was null or unknown!");
        }
    }

    @Override
    public void decode(ByteBuf buffer) {
        type = Type.values()[buffer.readByte()];
        inventorySlot = buffer.readByte();
        switch (type) {
            case REPLACE_PAGE:
            case ADD_PAGE:
                pageNumber = buffer.readByte();
                text = readString(buffer);
                photoName = readString(buffer);
                break;
            case DELETE_PAGE:
                pageNumber = buffer.readByte();
                break;
            case SWAP_PAGES:
                pageNumber = buffer.readByte();
                secondaryPageNumber = buffer.readByte();
                break;
            case SIGN_BOOK:
                title = readString(buffer);
                author = readString(buffer);
                xuid = readString(buffer);
                break;
            default:
                throw new RuntimeException("BookEdit type was null or unknown!");
        }
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        handler.handle(this);
    }

    public enum Type {
        REPLACE_PAGE,
        ADD_PAGE,
        DELETE_PAGE,
        SWAP_PAGES,
        SIGN_BOOK
    }
}
