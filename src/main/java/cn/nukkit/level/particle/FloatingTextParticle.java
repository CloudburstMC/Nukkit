package cn.nukkit.level.particle;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.EntityMetadata;
import cn.nukkit.item.Item;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.AddPlayerPacket;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.RemoveEntityPacket;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created on 2015/11/21 by xtypr.
 * Package cn.nukkit.level.particle in project Nukkit .
 */
public class FloatingTextParticle extends Particle {
    protected String text;
    protected String title;
    protected long entityId = -1;
    protected boolean invisible = false;
    protected EntityMetadata metadata = new EntityMetadata();

    public FloatingTextParticle(Vector3 pos, String text) {
        this(pos, text, "");
    }

    public FloatingTextParticle(Vector3 pos, String text, String title) {
        super(pos.x, pos.y, pos.z);
        this.text = text;
        this.title = title;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isInvisible() {
        return invisible;
    }

    public void setInvisible(boolean invisible) {
        this.invisible = invisible;
    }

    public void setInvisible() {
        this.setInvisible(true);
    }

    @Override
    public DataPacket[] encode() {
        ArrayList<DataPacket> packets = new ArrayList<>();

        if (this.entityId == -1) {
            this.entityId = 1095216660480L + ThreadLocalRandom.current().nextLong(0, 0x7fffffffL);
        } else {
            RemoveEntityPacket pk = new RemoveEntityPacket();
            pk.eid = this.entityId;

            packets.add(pk);
        }

        if (!this.invisible) {
            AddPlayerPacket pk = new AddPlayerPacket();
            pk.uuid = UUID.randomUUID();
            pk.username = "";
            pk.eid = this.entityId;
            pk.x = (float) this.x;
            pk.y = (float) (this.y - 1.62);
            pk.z = (float) this.z;
            pk.speedX = 0;
            pk.speedY = 0;
            pk.speedZ = 0;
            pk.yaw = 0;
            pk.pitch = 0;
            long flags = 0;
            flags |= 1 << Entity.DATA_FLAG_INVISIBLE;
            flags |= 1 << Entity.DATA_FLAG_CAN_SHOW_NAMETAG;
            flags |= 1 << Entity.DATA_FLAG_ALWAYS_SHOW_NAMETAG;
            flags |= 1 << Entity.DATA_FLAG_IMMOBILE;
            pk.metadata = new EntityMetadata()
                    .putLong(Entity.DATA_FLAGS, flags)
                    .putString(Entity.DATA_NAMETAG, this.title + (!"".equals(this.text) ? "\n" + this.text : ""))
                    .putLong(Entity.DATA_LEAD_HOLDER, -1)
                    .putByte(Entity.DATA_LEAD, 0);
            pk.item = Item.get(Item.AIR);
            packets.add(pk);
        }

        return packets.stream().toArray(DataPacket[]::new);
    }
}