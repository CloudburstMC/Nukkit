package cn.nukkit.level.particle;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.EntityMetadata;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.*;
import com.google.common.base.Strings;

import java.util.ArrayList;

/**
 * Created on 2015/11/21 by xtypr.
 * Package cn.nukkit.level.particle in project Nukkit .
 */
public class FloatingTextParticle extends Particle {

    protected final Level level;
    protected long entityId = -1;
    protected boolean invisible = false;
    protected String title;
    protected String text;
    protected EntityMetadata metadata = new EntityMetadata();

    public FloatingTextParticle(Location location, String title) {
        this(location, title, null);
    }

    public FloatingTextParticle(Location location, String title, String text) {
        this(location.getLevel(), location, title, text);
    }

    public FloatingTextParticle(Vector3 pos, String title) {
        this(pos, title, null);
    }

    public FloatingTextParticle(Vector3 pos, String title, String text) {
        this(null, pos, title, text);
    }

    private FloatingTextParticle(Level level, Vector3 pos, String title, String text) {
        super(pos.x, pos.y, pos.z);
        this.level = level;
        this.title = title;
        this.text = text;

        long flags = 0;
        flags ^= 1 << Entity.DATA_FLAG_NO_AI;
        flags ^= 1 << Entity.DATA_FLAG_CAN_SHOW_NAMETAG;

        this.metadata.putLong(Entity.DATA_FLAGS, flags)
                .putLong(Entity.DATA_LEAD_HOLDER_EID, -1)
                .putByte(Entity.DATA_ALWAYS_SHOW_NAMETAG, 1)
                .putFloat(Entity.DATA_SCALE, 0.01f) // Zero causes problems on debug builds?
                .putFloat(Entity.DATA_BOUNDING_BOX_HEIGHT, 0.01f)
                .putFloat(Entity.DATA_BOUNDING_BOX_WIDTH, 0.01f);

        updateNameTag();
    }

    public String getText() {
        return this.text == null ? "" : this.text;
    }

    public void setText(String text) {
        this.text = text;
        updateNameTag();
        sendMetadata();
    }

    public String getTitle() {
        return this.title == null ? "" : this.title;
    }

    public void setTitle(String title) {
        this.title = title;
        updateNameTag();
        sendMetadata();
    }

    private void updateNameTag() {
        // Score tag only works on player
        boolean hasTitle = !Strings.isNullOrEmpty(this.title);
        boolean hasText = !Strings.isNullOrEmpty(this.text);
        String tag = "";
        if (hasTitle) {
            tag += this.title;
            if (hasText) {
                tag += "\n";
            }
        }
        if (hasText) {
            tag += this.text;
        }
        this.metadata.putString(Entity.DATA_NAMETAG, tag);
    }

    private void sendMetadata() {
        if (this.level != null) {
            SetEntityDataPacket packet = new SetEntityDataPacket();
            packet.eid = entityId;
            packet.metadata = this.metadata;

            this.level.addChunkPacket(getChunkX(), getChunkZ(), packet);
        }
    }

    public boolean isInvisible() {
        return this.invisible;
    }

    public void setInvisible(boolean invisible) {
        this.invisible = invisible;

        if (this.level != null) {
            if (invisible) {
                this.level.addChunkPacket(getChunkX(), getChunkZ(), getRemovePacket());
            } else {
                this.level.addChunkPacket(getChunkX(), getChunkZ(), getAddPacket());
            }
        }
    }

    public void setInvisible() {
        this.setInvisible(true);
    }

    public long getEntityId() {
        return this.entityId;
    }

    @Override
    public DataPacket[] encode() {
        ArrayList<DataPacket> packets = new ArrayList<>();

        if (this.entityId == -1) {
            this.entityId = Entity.entityCount++;
        } else {
            packets.add(getRemovePacket());
        }

        if (!this.invisible) {
            packets.add(getAddPacket());
        }

        return packets.toArray(new DataPacket[0]);
    }

    private AddEntityPacket getAddPacket() {
        AddEntityPacket pk = new AddEntityPacket();
        pk.id = "minecraft:armor_stand";
        pk.entityUniqueId = this.entityId;
        pk.entityRuntimeId = this.entityId;
        pk.x = (float) this.x;
        pk.y = (float) this.y - 0.75f;
        pk.z = (float) this.z;
        pk.metadata = this.metadata;
        return pk;
    }

    private RemoveEntityPacket getRemovePacket() {
        RemoveEntityPacket pk = new RemoveEntityPacket();
        pk.eid = this.entityId;
        return pk;
    }
}
