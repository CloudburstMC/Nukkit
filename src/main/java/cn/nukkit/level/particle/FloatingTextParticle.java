package cn.nukkit.level.particle;

import cn.nukkit.entity.DroppedItem;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.ByteEntityData;
import cn.nukkit.entity.data.StringEntityData;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.AddEntityPacket;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.RemoveEntityPacket;

import java.util.ArrayList;
import java.util.HashMap;
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

    public void setInvisible() {
        this.setInvisible(true);
    }

    public void setInvisible(boolean invisible) {
        this.invisible = invisible;
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
            AddEntityPacket pk = new AddEntityPacket();
            pk.eid = this.entityId;
            pk.type = DroppedItem.NETWORK_ID;
            pk.x = (float) this.x;
            pk.y = (float) (this.y - 0.75);
            pk.z = (float) this.z;
            pk.speedX = 0;
            pk.speedY = 0;
            pk.speedZ = 0;
            pk.yaw = 0;
            pk.pitch = 0;
            pk.metadata = new HashMap<>();
            pk.metadata.put(Entity.DATA_FLAGS, new ByteEntityData((byte) (1 << Entity.DATA_FLAG_INVISIBLE)));
            pk.metadata.put(Entity.DATA_NAMETAG, new StringEntityData(this.title + (!"".equals(this.text) ? "\n" + this.text : "")));
            pk.metadata.put(Entity.DATA_SHOW_NAMETAG, new ByteEntityData((byte) 1));
            pk.metadata.put(Entity.DATA_NO_AI, new ByteEntityData((byte) 1));

            packets.add(pk);
        }

        return packets.stream().toArray(DataPacket[]::new);
    }
}
