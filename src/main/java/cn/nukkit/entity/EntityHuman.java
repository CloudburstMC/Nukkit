package cn.nukkit.entity;

import cn.nukkit.Player;
import cn.nukkit.entity.data.IntPositionEntityData;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.AddPlayerPacket;
import cn.nukkit.network.protocol.RemoveEntityPacket;
import cn.nukkit.network.protocol.SetEntityLinkPacket;
import cn.nukkit.utils.Utils;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EntityHuman extends EntityHumanType {

    public static final int DATA_PLAYER_FLAG_SLEEP = 1;
    public static final int DATA_PLAYER_FLAG_DEAD = 2;

    public static final int DATA_PLAYER_FLAGS = 26;

    public static final int DATA_PLAYER_BED_POSITION = 28;
    public static final int DATA_PLAYER_BUTTON_TEXT = 40;

    protected UUID uuid;
    protected byte[] rawUUID;

    @Override
    public float getWidth() {
        return 0.6f;
    }

    @Override
    public float getLength() {
        return 0.6f;
    }

    @Override
    public float getHeight() {
        return 1.8f;
    }

    @Override
    public float getEyeHeight() {
        return 1.62f;
    }

    @Override
    protected float getBaseOffset() {
        return this.getEyeHeight();
    }

    protected Skin skin;

    @Override
    public int getNetworkId() {
        return -1;
    }

    public EntityHuman(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    public Skin getSkin() {
        return skin;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public byte[] getRawUniqueId() {
        return rawUUID;
    }

    public void setSkin(Skin skin) {
        this.skin = skin;
    }

    @Override
    protected void initEntity() {
        this.setDataFlag(DATA_PLAYER_FLAGS, DATA_PLAYER_FLAG_SLEEP, false);
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_GRAVITY);

        this.setDataProperty(new IntPositionEntityData(DATA_PLAYER_BED_POSITION, 0, 0, 0), false);

        if (!(this instanceof Player)) {
            if (this.namedTag.contains("NameTag")) {
                this.setNameTag(this.namedTag.getString("NameTag"));
            }

            if (this.namedTag.contains("Skin") && this.namedTag.get("Skin") instanceof CompoundTag) {
                CompoundTag skinTag = this.namedTag.getCompound("Skin");
                if (!skinTag.contains("Transparent")) {
                    skinTag.putBoolean("Transparent", false);
                }
                Skin newSkin = new Skin();
                if (skinTag.contains("ModelId")) {
                    newSkin.setSkinId(skinTag.getString("ModelId"));
                }
                if (skinTag.contains("Data")) {
                    newSkin.setSkinData(skinTag.getByteArray("Data"));
                }
                if (skinTag.contains("CapeData")) {
                    newSkin.setCapeData(skinTag.getByteArray("CapeData"));
                }
                if (skinTag.contains("GeometryName")) {
                    newSkin.setGeometryName(skinTag.getString("GeometryName"));
                }
                if (skinTag.contains("GeometryData")) {
                    newSkin.setGeometryData(new String(skinTag.getByteArray("GeometryData"), StandardCharsets.UTF_8));
                }
                this.setSkin(newSkin);
            }

            this.uuid = Utils.dataToUUID(String.valueOf(this.getId()).getBytes(StandardCharsets.UTF_8), this.getSkin()
                    .getSkinData(), this.getNameTag().getBytes(StandardCharsets.UTF_8));
        }

        super.initEntity();
    }

    @Override
    public String getName() {
        return this.getNameTag();
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        if (skin != null) {
            this.namedTag.putCompound("Skin", new CompoundTag()
                    .putByteArray("Data", this.getSkin().getSkinData())
                    .putString("ModelId", this.getSkin().getSkinId())
                    .putByteArray("CapeData", this.getSkin().getCapeData())
                    .putString("GeometryName", this.getSkin().getGeometryName())
                    .putByteArray("GeometryData", this.getSkin().getGeometryData().getBytes(StandardCharsets.UTF_8))
            );
        }
    }

    @Override
    public void spawnTo(Player player) {
        if (this != player && !this.hasSpawned.containsKey(player.getLoaderId())) {
            this.hasSpawned.put(player.getLoaderId(), player);

            if (!this.skin.isValid()) {
                throw new IllegalStateException(this.getClass().getSimpleName() + " must have a valid skin set");
            }

            if (this instanceof Player)
                this.server.updatePlayerListData(this.getUniqueId(), this.getId(), this.getName(), this.skin, ((Player) this).getLoginChainData().getXUID(), new Player[]{player});
            else
                this.server.updatePlayerListData(this.getUniqueId(), this.getId(), this.getName(), this.skin, new Player[]{player});

            AddPlayerPacket pk = new AddPlayerPacket();
            pk.uuid = this.getUniqueId();
            pk.username = this.getName();
            pk.entityUniqueId = this.getId();
            pk.entityRuntimeId = this.getId();
            pk.x = (float) this.x;
            pk.y = (float) this.y;
            pk.z = (float) this.z;
            pk.speedX = (float) this.motionX;
            pk.speedY = (float) this.motionY;
            pk.speedZ = (float) this.motionZ;
            pk.yaw = (float) this.yaw;
            pk.pitch = (float) this.pitch;
            pk.item = this.getInventory().getItemInHand();
            pk.metadata = this.dataProperties;
            player.dataPacket(pk);

            this.inventory.sendArmorContents(player);

            if (this.riding != null) {
                SetEntityLinkPacket pkk = new SetEntityLinkPacket();
                pkk.rider = this.riding.getId();
                pkk.riding = this.getId();
                pkk.type = 1;
                pkk.unknownByte = 1;

                player.dataPacket(pkk);
            }

            if (!(this instanceof Player)) {
                this.server.removePlayerListData(this.getUniqueId(), new Player[]{player});
            }
        }
    }

    @Override
    public void despawnFrom(Player player) {
        if (this.hasSpawned.containsKey(player.getLoaderId())) {

            RemoveEntityPacket pk = new RemoveEntityPacket();
            pk.eid = this.getId();
            player.dataPacket(pk);
            this.hasSpawned.remove(player.getLoaderId());
        }
    }

    @Override
    public void close() {
        if (!this.closed) {
            if (inventory != null && (!(this instanceof Player) || ((Player) this).loggedIn)) {
                for (Player viewer : this.inventory.getViewers()) {
                    viewer.removeWindow(this.inventory);
                }
            }

            super.close();
        }
    }

}
