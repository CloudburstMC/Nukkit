package cn.nukkit.network.protocol.types;

import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

public class EntityLink {

    public static final byte TYPE_REMOVE = 0;
    public static final byte TYPE_RIDER = 1;
    public static final byte TYPE_PASSENGER = 2;

    public long fromEntityUniquieId;
    public long toEntityUniquieId;
    public byte type;
    public boolean immediate;
    
    @Since("1.3.0.0-PN")
    public boolean riderInitiated;

    @Since("1.3.0.0-PN")
    public EntityLink(long fromEntityUniquieId, long toEntityUniquieId, byte type, boolean immediate, boolean riderInitiated) {
        this.fromEntityUniquieId = fromEntityUniquieId;
        this.toEntityUniquieId = toEntityUniquieId;
        this.type = type;
        this.immediate = immediate;
        this.riderInitiated = riderInitiated;
    }

    @Since("1.3.0.0-PN")
    @PowerNukkitOnly("Backward compatibility")
    @Deprecated
    @DeprecationDetails(
            since = "1.3.0.0-PN", toBeRemovedAt = "1.4.0.0-PN",
            reason = "NukkitX added the immediate riderInitiated", 
            replaceWith = "EntityLink(long fromEntityUniquieId, long toEntityUniquieId, byte type, boolean immediate, boolean riderInitiated)")
    public EntityLink(long fromEntityUniquieId, long toEntityUniquieId, byte type, boolean immediate) {
        this(fromEntityUniquieId, toEntityUniquieId, type, immediate, false);
    }
}
