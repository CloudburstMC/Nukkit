package cn.nukkit.network.protocol;

import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;
import cn.nukkit.utils.BinaryStream;
import lombok.ToString;

import java.util.UUID;

/**
 * @author Nukkit Project Team
 */
@ToString
public class CraftingEventPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.CRAFTING_EVENT_PACKET;

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "The name don't match the packet content")
    public static final int TYPE_SHAPELESS = 0;

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "The name don't match the packet content")
    public static final int TYPE_SHAPED = 1;

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "The name don't match the packet content")
    public static final int TYPE_FURNACE = 2;

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "The name don't match the packet content")
    public static final int TYPE_FURNACE_DATA = 3;

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "The name don't match the packet content")
    public static final int TYPE_MULTI = 4;

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "The name don't match the packet content")
    public static final int TYPE_SHULKER_BOX = 5;
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final int TYPE_INVENTORY = 0;

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final int TYPE_CRAFTING = 1;

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final int TYPE_WORKBENCH = 2;

    public int windowId;
    public int type;
    public UUID id;

    public Item[] input;
    public Item[] output;

    @Override
    public void decode() {
        this.windowId = this.getByte();
        this.type = (int) this.getUnsignedVarInt();
        this.id = this.getUUID();

        this.input = this.getArray(Item.class, BinaryStream::getSlot);
        this.output = this.getArray(Item.class, BinaryStream::getSlot);
    }

    @Override
    public void encode() {

    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

}
