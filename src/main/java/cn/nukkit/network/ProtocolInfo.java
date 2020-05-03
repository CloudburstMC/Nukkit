package cn.nukkit.network;

import com.nukkitx.protocol.bedrock.BedrockPacketCodec;
import com.nukkitx.protocol.bedrock.v390.Bedrock_v390;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Nonnegative;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.google.common.base.Preconditions.checkNotNull;

@Log4j2
@ParametersAreNonnullByDefault
public final class ProtocolInfo {
    private static final Set<BedrockPacketCodec> PACKET_CODECS = ConcurrentHashMap.newKeySet();
    private static final Set<BedrockPacketCodec> UNMODIFIABLE_PACKET_CODECS = Collections.unmodifiableSet(PACKET_CODECS);

    private static BedrockPacketCodec DEFAULT_PACKET_CODEC;

    static {
        setDefaultPacketCodec(Bedrock_v390.V390_CODEC);
    }

    public static BedrockPacketCodec getDefaultPacketCodec() {
        return DEFAULT_PACKET_CODEC;
    }

    public static void setDefaultPacketCodec(BedrockPacketCodec packetCodec) {
        DEFAULT_PACKET_CODEC = checkNotNull(packetCodec, "packetCodec");
        PACKET_CODECS.add(DEFAULT_PACKET_CODEC);
    }

    public static String getDefaultMinecraftVersion() {
        return DEFAULT_PACKET_CODEC.getMinecraftVersion();
    }

    public static int getDefaultProtocolVersion() {
        return DEFAULT_PACKET_CODEC.getProtocolVersion();
    }

    @Nullable
    public static BedrockPacketCodec getPacketCodec(@Nonnegative int protocolVersion) {
        for (BedrockPacketCodec packetCodec : PACKET_CODECS) {
            if (packetCodec.getProtocolVersion() == protocolVersion) {
                return packetCodec;
            }
        }
        return null;
    }

    public static void addPacketCodec(BedrockPacketCodec packetCodec) {
        PACKET_CODECS.add(checkNotNull(packetCodec, "packetCodec"));
    }

    public static Set<BedrockPacketCodec> getPacketCodecs() {
        return UNMODIFIABLE_PACKET_CODECS;
    }
}
