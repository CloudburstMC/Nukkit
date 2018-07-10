package cn.nukkit.utils;

import cn.nukkit.Player;
import cn.nukkit.entity.Attribute;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.EntityMetadata;
import cn.nukkit.entity.mob.EntityCreeper;
import cn.nukkit.network.protocol.*;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * DummyBossBar
 * ===============
 * author: boybook
 * Nukkit Project
 * ===============
 */
public class DummyBossBar {

    private final Player player;
    private final long bossBarId;

    private String text;
    private float length;
    private Color color;

    private DummyBossBar(Builder builder) {
        this.player = builder.player;
        this.bossBarId = builder.bossBarId;
        this.text = builder.text;
        this.length = builder.length;
        this.color = builder.color;
    }

    public static class Builder {
        private final Player player;
        private final long bossBarId;

        private String text = "";
        private float length = 100;
        private Color color = null;

        public Builder(Player player) {
            this.player = player;
            this.bossBarId = 1095216660480L + ThreadLocalRandom.current().nextLong(0, 0x7fffffffL);
        }

        public Builder text(String text) {
            this.text = text;
            return this;
        }

        public Builder length(float length) {
            if (length >= 0 && length <= 100) this.length = length;
            return this;
        }

        public Builder color(Color color) {
            this.color = color;
            return this;
        }

        public Builder color(int red, int green, int blue) {
            return color(new Color(red, green, blue));
        }

        public DummyBossBar build() {
            return new DummyBossBar(this);
        }
    }

    public Player getPlayer() {
        return player;
    }

    public long getBossBarId() {
        return bossBarId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        if (!this.text.equals(text)) {
            this.text = text;
            this.updateBossEntityNameTag();
            this.sendSetBossBarTitle();
        }
    }

    public float getLength() {
        return length;
    }

    public void setLength(float length) {
        if (this.length != length) {
            this.length = length;
            this.sendAttributes();
        }
    }

    /**
     * Color is not working in the current version. We are keep waiting for client support.
     * @param color the boss bar color
     */
    public void setColor(Color color) {
        if (this.color == null || !this.color.equals(color)) {
            this.color = color;
            this.sendSetBossBarTexture();
        }
    }

    public void setColor(int red, int green, int blue) {
        this.setColor(new Color(red, green, blue));
    }

    public int getMixedColor() {
        return this.color.getRGB();//(this.color.getRed() << 16 | this.color.getGreen() << 8 | this.color.getBlue()) & 0xffffff;
    }

    public Color getColor() {
        return this.color;
    }

    private void createBossEntity() {
        AddEntityPacket pkAdd = new AddEntityPacket();
        pkAdd.type = EntityCreeper.NETWORK_ID;
        pkAdd.entityUniqueId = bossBarId;
        pkAdd.entityRuntimeId = bossBarId;
        pkAdd.x = (float) player.x;
        pkAdd.y = (float) -10; // Below the bedrock
        pkAdd.z = (float) player.z;
        pkAdd.speedX = 0;
        pkAdd.speedY = 0;
        pkAdd.speedZ = 0;
        pkAdd.metadata = new EntityMetadata()
                // Default Metadata tags
                .putLong(Entity.DATA_FLAGS, 0)
                .putShort(Entity.DATA_AIR, 400)
                .putShort(Entity.DATA_MAX_AIR, 400)
                .putLong(Entity.DATA_LEAD_HOLDER_EID, -1)
                .putString(Entity.DATA_NAMETAG, text) // Set the entity name
                .putFloat(Entity.DATA_SCALE, 0); // And make it invisible

        player.dataPacket(pkAdd);
    }

    private void sendAttributes() {
        UpdateAttributesPacket pkAttributes = new UpdateAttributesPacket();
        pkAttributes.entityId = bossBarId;
        Attribute attr = Attribute.getAttribute(Attribute.MAX_HEALTH);
        attr.setMaxValue(100); // Max value - We need to change the max value first, or else the "setValue" will return a IllegalArgumentException
        attr.setValue(length); // Entity health
        pkAttributes.entries = new Attribute[]{attr};
        player.dataPacket(pkAttributes);
    }

    private void sendShowBossBar() {
        BossEventPacket pkBoss = new BossEventPacket();
        pkBoss.bossEid = bossBarId;
        pkBoss.type = BossEventPacket.TYPE_SHOW;
        pkBoss.title = text;
        pkBoss.healthPercent = this.length;
        player.dataPacket(pkBoss);
    }

    private void sendHideBossBar() {
        BossEventPacket pkBoss = new BossEventPacket();
        pkBoss.bossEid = bossBarId;
        pkBoss.type = BossEventPacket.TYPE_HIDE;
        player.dataPacket(pkBoss);
    }

    private void sendSetBossBarTexture() {
        BossEventPacket pk = new BossEventPacket();
        pk.bossEid = this.bossBarId;
        pk.type = BossEventPacket.TYPE_TEXTURE;
        pk.color = this.getMixedColor();
        player.dataPacket(pk);
    }

    private void sendSetBossBarTitle() {
        BossEventPacket pkBoss = new BossEventPacket();
        pkBoss.bossEid = bossBarId;
        pkBoss.type = BossEventPacket.TYPE_TITLE;
        pkBoss.title = text;
        pkBoss.healthPercent = this.length;
        player.dataPacket(pkBoss);
    }

    /**
     * Don't let the entity go too far from the player, or the BossBar will disappear.
     * Update boss entity's position when teleport and each 5s.
     */
    public void updateBossEntityPosition() {
        MoveEntityAbsolutePacket pk = new MoveEntityAbsolutePacket();
        pk.eid = this.bossBarId;
        pk.x = this.player.x;
        pk.y = -10;
        pk.z = this.player.z;
        pk.headYaw = 0;
        pk.yaw = 0;
        pk.pitch = 0;
        player.dataPacket(pk);
    }

    private void updateBossEntityNameTag() {
        SetEntityDataPacket pk = new SetEntityDataPacket();
        pk.eid = this.bossBarId;
        pk.metadata = new EntityMetadata().putString(Entity.DATA_NAMETAG, this.text);
        player.dataPacket(pk);
    }

    private void removeBossEntity() {
        RemoveEntityPacket pkRemove = new RemoveEntityPacket();
        pkRemove.eid = bossBarId;
        player.dataPacket(pkRemove);
    }

    public void create() {
        createBossEntity();
        sendAttributes();
        sendShowBossBar();
        if (color != null) this.sendSetBossBarTexture();
    }

    /**
     * Once the player has teleported, resend Show BossBar
     */
    public void reshow() {
        updateBossEntityPosition();
        sendShowBossBar();
    }

    public void destroy() {
        sendHideBossBar();
        removeBossEntity();
    }

}
