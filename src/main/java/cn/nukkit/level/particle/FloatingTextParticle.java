package cn.nukkit.level.particle;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.EntityMetadata;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.AddEntityPacket;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.RemoveEntityPacket;
import cn.nukkit.network.protocol.SetEntityDataPacket;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created on 2015/11/21 by xtypr.
 * Package cn.nukkit.level.particle in project Nukkit .
 */
public class FloatingTextParticle extends Particle {
    protected String text;
    protected String title;
    protected long entityId = 1095216660480L + ThreadLocalRandom.current().nextLong(0, 0x7fffffffL);
    protected boolean invisible = false;
    protected EntityMetadata metadata = new EntityMetadata();

    protected boolean updated = false;
    protected boolean spawned = false;

    public FloatingTextParticle(Vector3 pos, String text) {
        this(pos, text, "");
    }

    public FloatingTextParticle(Vector3 pos, String text, String title) {
        super(pos.x, pos.y, pos.z);
        this.text = text;
        this.title = title;

        this.metadata.putByte(Entity.DATA_FLAGS, (byte) (1 << Entity.DATA_FLAG_INVISIBLE));
        this.metadata.putBoolean(Entity.DATA_SHOW_NAMETAG, true);
        this.metadata.putBoolean(Entity.DATA_NO_AI, true);
        updateNameTag();
    }

    public void setText(String text) {
        this.text = text;
        updateNameTag();
    }

    public void setTitle(String title) {
        this.title = title;
        updateNameTag();
    }

    private void updateNameTag() {
        this.metadata.putString(Entity.DATA_NAMETAG, this.title + (!"".equals(this.text) ? "\n" + this.text : ""));
        this.updated = true;
    }

    public boolean isInvisible() {
        return invisible;
    }

    public void setInvisible() {
        this.setInvisible(true);
    }

    public void setInvisible(boolean invisible) {
        this.invisible = invisible;
    }

    @Override
    public DataPacket[] encode() {
        ArrayList<DataPacket> packets = new ArrayList<>();

        if (!this.invisible) {
            if (!this.spawned) {
                AddEntityPacket pk = new AddEntityPacket();
                pk.eid = this.entityId;
                pk.type = EntityItem.NETWORK_ID;
                pk.x = (float) this.x;
                pk.y = (float) (this.y - 0.75);
                pk.z = (float) this.z;
                pk.speedX = 0;
                pk.speedY = 0;
                pk.speedZ = 0;
                pk.yaw = 0;
                pk.pitch = 0;
                pk.metadata = this.metadata;
                packets.add(pk);

                this.spawned = true;
            } else if (this.updated) {
                SetEntityDataPacket pk = new SetEntityDataPacket();
                pk.eid = this.entityId;
                pk.metadata = this.metadata;

                packets.add(pk);
                this.updated = false;
            }
        } else if (this.spawned) {
            RemoveEntityPacket pk = new RemoveEntityPacket();
            pk.eid = this.entityId;

            packets.add(pk);
            this.spawned = false;
        }

        return packets.stream().toArray(DataPacket[]::new);
    }
}
