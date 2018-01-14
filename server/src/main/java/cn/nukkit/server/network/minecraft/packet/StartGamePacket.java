package cn.nukkit.server.network.minecraft.packet;

import cn.nukkit.api.GameMode;
import cn.nukkit.api.level.LevelSettings;
import cn.nukkit.server.network.NetworkPacketHandler;
import cn.nukkit.server.network.minecraft.MinecraftPacket;
import com.flowpowered.math.vector.Vector2f;
import com.flowpowered.math.vector.Vector3f;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static cn.nukkit.server.nbt.util.VarInt.writeSignedInt;
import static cn.nukkit.server.network.minecraft.MinecraftUtil.*;

@Data
public class StartGamePacket implements MinecraftPacket {
    private long uniqueEntityId;
    private long runtimeEntityId;
    private GameMode gamemode;
    private Vector3f playerPosition;
    private Vector2f rotation;
    private LevelSettings levelSettings;
    private String levelId;
    private String worldName;
    private String premiumWorldTemplateId;
    private boolean trial;
    private long currentTick;
    private int enchantmentSeed;

    @Override
    public void encode(ByteBuf buffer) {
        writeUniqueEntityId(buffer, uniqueEntityId);
        writeRuntimeEntityId(buffer, runtimeEntityId);
        writeSignedInt(buffer, gamemode.ordinal());
        writeVector3f(buffer, playerPosition);
        writeVector2f(buffer, rotation);
        writeLevelSettings(buffer, levelSettings);
        writeString(buffer, levelId);
        writeString(buffer, worldName);
        writeString(buffer, premiumWorldTemplateId);
        buffer.writeBoolean(trial);
        buffer.writeLongLE(currentTick);
        writeSignedInt(buffer, enchantmentSeed);
    }

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        // Only client bound.
    }
}
