package com.nukkitx.server.metadata.serializer.block;

import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.metadata.block.NetherWart;
import com.nukkitx.server.metadata.serializer.Serializer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author CreeperFace
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NetherWartSerializer implements Serializer<NetherWart> {

    public static final Serializer INSTANCE = new NetherWartSerializer();

    @Override
    public short readMetadata(NetherWart metadata) {
        return metadata.getAge();
    }

    @Override
    public NetherWart writeMetadata(ItemType type, short metadata) {
        return NetherWart.of(Math.min(3, metadata));
    }
}
