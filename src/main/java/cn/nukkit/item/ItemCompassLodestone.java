package cn.nukkit.item;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.positiontracking.NamedPosition;

import javax.annotation.Nullable;
import java.io.IOException;

/**
 * @author joserobjr
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class ItemCompassLodestone extends Item {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public ItemCompassLodestone() {
        this(0, 1);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public ItemCompassLodestone(Integer meta) {
        this(meta, 1);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public ItemCompassLodestone(Integer meta, int count) {
        super(LODESTONE_COMPASS, meta, count, "Lodestone Compass");
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setTrackingPosition(@Nullable NamedPosition position) throws IOException {
        if (position == null) {
            setTrackingHandle(0);
            return;
        }
        setTrackingHandle(Server.getInstance().getPositionTrackingService().addOrReusePosition(position));
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nullable
    public NamedPosition getTrackingPosition() throws IOException {
        int trackingHandle = getTrackingHandle();
        if (trackingHandle == 0) {
            return null;
        }
        return Server.getInstance().getPositionTrackingService().getPosition(trackingHandle);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getTrackingHandle() {
        return hasCompoundTag()? getNamedTag().getInt("trackingHandle") : 0;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setTrackingHandle(int trackingHandle) {
        CompoundTag tag = getNamedTag();
        if (tag == null) {
            tag = new CompoundTag();
        }
        tag.putInt("trackingHandle", trackingHandle);
        setNamedTag(tag);
    }
}
