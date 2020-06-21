package cn.nukkit.level.particle;

import cn.nukkit.block.BlockID;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.EntityMetadata;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.*;
import cn.nukkit.utils.SerializedImage;
import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created on 2015/11/21 by xtypr.
 * Package cn.nukkit.level.particle in project Nukkit .
 */
public class FloatingTextParticle extends Particle {

    private static final Skin EMPTY_SKIN = new Skin();

    private static final SerializedImage SKIN_DATA = SerializedImage.fromLegacy(new byte[8192]);

    static {
        FloatingTextParticle.EMPTY_SKIN.setSkinData(FloatingTextParticle.SKIN_DATA);
        FloatingTextParticle.EMPTY_SKIN.generateSkinId("FloatingText");
    }

    protected final Level level;

    protected UUID uuid = UUID.randomUUID();

    protected long entityId = -1;

    protected boolean invisible = false;

    protected EntityMetadata metadata = new EntityMetadata();

    public FloatingTextParticle(final Location location, final String title) {
        this(location, title, null);
    }

    public FloatingTextParticle(final Location location, final String title, final String text) {
        this(location.getLevel(), location, title, text);
    }

    public FloatingTextParticle(final Vector3 pos, final String title) {
        this(pos, title, null);
    }

    public FloatingTextParticle(final Vector3 pos, final String title, final String text) {
        this(null, pos, title, text);
    }

    private FloatingTextParticle(final Level level, final Vector3 pos, final String title, final String text) {
        super(pos.x, pos.y, pos.z);
        this.level = level;

        final long flags = 1L << Entity.DATA_FLAG_NO_AI;
        this.metadata.putLong(Entity.DATA_FLAGS, flags)
            .putLong(Entity.DATA_LEAD_HOLDER_EID, -1)
            .putFloat(Entity.DATA_SCALE, 0.01f) //zero causes problems on debug builds?
            .putFloat(Entity.DATA_BOUNDING_BOX_HEIGHT, 0.01f)
            .putFloat(Entity.DATA_BOUNDING_BOX_WIDTH, 0.01f);
        if (!Strings.isNullOrEmpty(title)) {
            this.metadata.putString(Entity.DATA_NAMETAG, title);
        }
        if (!Strings.isNullOrEmpty(text)) {
            this.metadata.putString(Entity.DATA_SCORE_TAG, text);
        }
    }

    public String getText() {
        return this.metadata.getString(Entity.DATA_SCORE_TAG);
    }

    public void setText(final String text) {
        this.metadata.putString(Entity.DATA_SCORE_TAG, text);
        this.sendMetadata();
    }

    public String getTitle() {
        return this.metadata.getString(Entity.DATA_NAMETAG);
    }

    public void setTitle(final String title) {
        this.metadata.putString(Entity.DATA_NAMETAG, title);
        this.sendMetadata();
    }

    public boolean isInvisible() {
        return this.invisible;
    }

    public void setInvisible(final boolean invisible) {
        this.invisible = invisible;
    }

    public void setInvisible() {
        this.setInvisible(true);
    }

    public long getEntityId() {
        return this.entityId;
    }

    @Override
    public DataPacket[] encode() {
        final ArrayList<DataPacket> packets = new ArrayList<>();

        if (this.entityId == -1) {
            this.entityId = 1095216660480L + ThreadLocalRandom.current().nextLong(0, 0x7fffffffL);
        } else {
            final RemoveEntityPacket pk = new RemoveEntityPacket();
            pk.eid = this.entityId;

            packets.add(pk);
        }

        if (!this.invisible) {
            final PlayerListPacket.Entry[] entry = {new PlayerListPacket.Entry(this.uuid, this.entityId,
                this.metadata.getString(Entity.DATA_NAMETAG), FloatingTextParticle.EMPTY_SKIN)};
            final PlayerListPacket playerAdd = new PlayerListPacket();
            playerAdd.entries = entry;
            playerAdd.type = PlayerListPacket.TYPE_ADD;
            packets.add(playerAdd);

            final AddPlayerPacket pk = new AddPlayerPacket();
            pk.uuid = this.uuid;
            pk.username = "";
            pk.entityUniqueId = this.entityId;
            pk.entityRuntimeId = this.entityId;
            pk.x = (float) this.x;
            pk.y = (float) (this.y - 0.75);
            pk.z = (float) this.z;
            pk.speedX = 0;
            pk.speedY = 0;
            pk.speedZ = 0;
            pk.yaw = 0;
            pk.pitch = 0;
            pk.metadata = this.metadata;
            pk.item = Item.get(BlockID.AIR);
            packets.add(pk);

            final PlayerListPacket playerRemove = new PlayerListPacket();
            playerRemove.entries = entry;
            playerRemove.type = PlayerListPacket.TYPE_REMOVE;
            packets.add(playerRemove);
        }

        return packets.toArray(new DataPacket[0]);
    }

    private void sendMetadata() {
        if (this.level != null) {
            final SetEntityDataPacket packet = new SetEntityDataPacket();
            packet.eid = this.entityId;
            packet.metadata = this.metadata;
            this.level.addChunkPacket(this.getChunkX(), this.getChunkZ(), packet);
        }
    }

}
