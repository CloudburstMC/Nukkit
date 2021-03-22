package cn.nukkit.network.protocol;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.MainLogger;

import java.io.IOException;
import java.nio.ByteOrder;

import lombok.ToString;

/**
 * @author GoodLucky777
 */
@ToString
@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class ItemComponentPacket extends DataPacket {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final byte NETWORK_ID = ProtocolInfo.ITEM_COMPONENT_PACKET;
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    private Entry[] entries = Entry.EMPTY_ARRAY;
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setEntries(Entry[] entries) {
        this.entries = entries == null? null : entries.length == 0? Entry.EMPTY_ARRAY : entries.clone();
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public Entry[] getEntries() {
        return entries == null? null : entries.length == 0? Entry.EMPTY_ARRAY : entries.clone();
    }
    
    @Override
    public byte pid() {
        return NETWORK_ID;
    }
    
    @Override
    public void decode() {
        
    }
    
    @Override
    public void encode() {
        this.reset();
        this.putUnsignedVarInt(this.entries.length);
        try {
            for (Entry entry : this.entries) {
                this.putString(entry.getName());
                this.put(NBTIO.write(entry.getData(), ByteOrder.LITTLE_ENDIAN, true));
            }
        } catch (IOException e) {
            MainLogger.getLogger().error("Error while encoding NBT data of ItemComponentPacket", e);
        }
    }
    
    @ToString
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static class Entry {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        public static final Entry[] EMPTY_ARRAY = new Entry[0];
        
        private final String name;
        private final CompoundTag data;
        
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        public Entry(String name, CompoundTag data) {
            this.name = name;
            this.data = data;
        }
        
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        public String getName() {
            return name;
        }
        
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        public CompoundTag getData() {
            return data;
        }
    }
}
