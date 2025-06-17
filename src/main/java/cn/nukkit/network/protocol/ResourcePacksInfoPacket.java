package cn.nukkit.network.protocol;

import cn.nukkit.resourcepacks.ResourcePack;
import lombok.ToString;

import java.util.UUID;

@ToString
public class ResourcePacksInfoPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.RESOURCE_PACKS_INFO_PACKET;

    public boolean mustAccept;
    public boolean scripting;
    public boolean hasAddonPacks;
    public boolean forceDisableVibrantVisuals;
    public UUID worldTemplateId = new UUID(0, 0);
    public String worldTemplateVersion = "";
    public ResourcePack[] behaviourPackEntries = ResourcePack.EMPTY_ARRAY;
    public ResourcePack[] resourcePackEntries = ResourcePack.EMPTY_ARRAY;

    @Override
    public void decode() {
        this.decodeUnsupported();
    }

    @Override
    public void encode() {
        this.reset();
        this.putBoolean(this.mustAccept);
        this.putBoolean(this.hasAddonPacks);
        this.putBoolean(this.scripting);
        this.putBoolean(this.forceDisableVibrantVisuals);
        this.putUUID(this.worldTemplateId);
        this.putString(this.worldTemplateVersion);

        this.encodeResourcePacks(this.resourcePackEntries);
    }

    private void encodeResourcePacks(ResourcePack[] packs) {
        this.putLShort(packs.length);
        for (ResourcePack entry : packs) {
            this.putUUID(entry.getPackId());
            this.putString(entry.getPackVersion());
            this.putLLong(entry.getPackSize());
            this.putString(entry.getEncryptionKey());
            this.putString(entry.getSubPackName());
            this.putString(!entry.getEncryptionKey().isEmpty() ? entry.getPackId().toString() : "");
            this.putBoolean(entry.usesScripting());
            this.putBoolean(entry.isAddonPack());
            this.putBoolean(entry.isRaytracingCapable());
            this.putString(entry.getCDNUrl());
        }
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }
}
