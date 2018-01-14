package cn.nukkit.server.network.minecraft.packet;

import cn.nukkit.server.nbt.NBTEncodingType;
import cn.nukkit.server.nbt.stream.NBTReader;
import cn.nukkit.server.nbt.stream.NBTWriter;
import cn.nukkit.server.nbt.tag.Tag;
import cn.nukkit.server.network.NetworkPacketHandler;
import cn.nukkit.server.network.minecraft.MinecraftPacket;
import cn.nukkit.server.network.util.LittleEndianByteBufInputStream;
import cn.nukkit.server.network.util.LittleEndianByteBufOutputStream;
import com.flowpowered.math.vector.Vector3i;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.io.IOException;

import static cn.nukkit.server.network.minecraft.MinecraftUtil.readBlockPosition;
import static cn.nukkit.server.network.minecraft.MinecraftUtil.writeBlockPosition;

@Data
public class BlockEntityDataPacket implements MinecraftPacket {
    private Vector3i blockPostion;
    private Tag<?> data;

    @Override
    public void encode(ByteBuf buffer) {
        writeBlockPosition(buffer, blockPostion);
        try (NBTWriter writer = new NBTWriter(new LittleEndianByteBufOutputStream(buffer), NBTEncodingType.MCPE)) {
            writer.write(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void decode(ByteBuf buffer) {
        blockPostion = readBlockPosition(buffer);
        try (NBTReader reader = new NBTReader(new LittleEndianByteBufInputStream(buffer), NBTEncodingType.MCPE)) {
            data = reader.readTag();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        handler.handle(this);
    }
}
