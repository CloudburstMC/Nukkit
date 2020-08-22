package cn.nukkit.item;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.level.Location;
import cn.nukkit.nbt.tag.CompoundTag;

import javax.annotation.Nullable;

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
    public void setTrackingPosition(@Nullable Location location) {
        // TODO
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nullable
    public Location getTrackingPosition() {
        // TODO
        return null;
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
