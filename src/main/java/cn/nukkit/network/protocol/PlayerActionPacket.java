package cn.nukkit.network.protocol;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAir;
import cn.nukkit.block.BlockNoteblock;
import cn.nukkit.entity.data.ShortEntityData;
import cn.nukkit.event.player.*;
import cn.nukkit.level.Position;
import cn.nukkit.level.particle.PunchBlockParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;

import static cn.nukkit.Player.CRAFTING_SMALL;
import static cn.nukkit.Player.DEFAULT_SPEED;

/**
 * @author Nukkit Project Team
 */
public class PlayerActionPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.PLAYER_ACTION_PACKET;

    public static final int ACTION_START_BREAK = 0;
    public static final int ACTION_ABORT_BREAK = 1;
    public static final int ACTION_STOP_BREAK = 2;
    public static final int ACTION_GET_UPDATED_BLOCK = 3;
    public static final int ACTION_DROP_ITEM = 4;
    public static final int ACTION_START_SLEEPING = 5;
    public static final int ACTION_STOP_SLEEPING = 6;
    public static final int ACTION_RESPAWN = 7;
    public static final int ACTION_JUMP = 8;
    public static final int ACTION_START_SPRINT = 9;
    public static final int ACTION_STOP_SPRINT = 10;
    public static final int ACTION_START_SNEAK = 11;
    public static final int ACTION_STOP_SNEAK = 12;
    public static final int ACTION_DIMENSION_CHANGE_REQUEST = 13; //sent when dying in different dimension
    public static final int ACTION_DIMENSION_CHANGE_ACK = 14; //sent when spawning in a different dimension to tell the server we spawned
    public static final int ACTION_START_GLIDE = 15;
    public static final int ACTION_STOP_GLIDE = 16;
    public static final int ACTION_BUILD_DENIED = 17;
    public static final int ACTION_CONTINUE_BREAK = 18;

    public long entityId;
    public int action;
    public int x;
    public int y;
    public int z;
    public int face;


    @Override
    public void decode() {
        this.entityId = this.getEntityRuntimeId();
        this.action = this.getVarInt();
        BlockVector3 v = this.getBlockVector3();
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.face = this.getVarInt();
    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityRuntimeId(this.entityId);
        this.putVarInt(this.action);
        this.putBlockVector3(this.x, this.y, this.z);
        this.putVarInt(this.face);
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void handle(Player player) {
        if (!player.spawned || (!player.isAlive() && this.action != PlayerActionPacket.ACTION_RESPAWN && this.action != PlayerActionPacket.ACTION_DIMENSION_CHANGE_REQUEST)) {
            return;
        }

        this.entityId = player.id;
        Vector3 pos = new Vector3(this.x, this.y, this.z);
        BlockFace face = BlockFace.fromIndex(this.face);

        switch (this.action) {
            case PlayerActionPacket.ACTION_START_BREAK:
                if (player.lastBreak != Long.MAX_VALUE || pos.distanceSquared(player) > 100) {
                    break;
                }
                Block target = player.level.getBlock(pos);
                PlayerInteractEvent playerInteractEvent = new PlayerInteractEvent(player, player.inventory.getItemInHand(), target, face, target.getId() == 0 ? PlayerInteractEvent.Action.LEFT_CLICK_AIR : PlayerInteractEvent.Action.LEFT_CLICK_BLOCK);
                player.getServer().getPluginManager().callEvent(playerInteractEvent);
                if (playerInteractEvent.isCancelled()) {
                    player.inventory.sendHeldItem(player);
                    break;
                }
                if (target.getId() == Block.NOTEBLOCK) {
                    ((BlockNoteblock) target).emitSound();
                    break;
                }
                Block block = target.getSide(face);
                if (block.getId() == Block.FIRE) {
                    player.level.setBlock(block, new BlockAir(), true);
                    break;
                }
                if (!player.isCreative()) {
                    //improved this to take stuff like swimming, ladders, enchanted tools into account, fix wrong tool break time calculations for bad tools (pmmp/PocketMine-MP#211)
                    //Done by lmlstarqaq
                    double breakTime = Math.ceil(target.getBreakTime(player.inventory.getItemInHand(), player) * 20);
                    if (breakTime > 0) {
                        LevelEventPacket pk = new LevelEventPacket();
                        pk.evid = LevelEventPacket.EVENT_BLOCK_START_BREAK;
                        pk.x = (float) pos.x;
                        pk.y = (float) pos.y;
                        pk.z = (float) pos.z;
                        pk.data = (int) (65535 / breakTime);
                        player.getLevel().addChunkPacket(pos.getFloorX() >> 4, pos.getFloorZ() >> 4, pk);
                    }
                }

                player.breakingBlock = target;
                player.lastBreak = System.currentTimeMillis();
                break;
            case PlayerActionPacket.ACTION_ABORT_BREAK:
                player.lastBreak = Long.MAX_VALUE;
                player.breakingBlock = null;
            case PlayerActionPacket.ACTION_STOP_BREAK:
                LevelEventPacket pk = new LevelEventPacket();
                pk.evid = LevelEventPacket.EVENT_BLOCK_STOP_BREAK;
                pk.x = (float) pos.x;
                pk.y = (float) pos.y;
                pk.z = (float) pos.z;
                pk.data = 0;
                player.level.addChunkPacket(pos.getFloorX() >> 4, pos.getFloorZ() >> 4, pk);
                player.breakingBlock = null;
                break;
            case PlayerActionPacket.ACTION_GET_UPDATED_BLOCK:
                break; //TODO
            case PlayerActionPacket.ACTION_DROP_ITEM:
                break; //TODO
            case PlayerActionPacket.ACTION_STOP_SLEEPING:
                player.stopSleep();
                break;
            case PlayerActionPacket.ACTION_RESPAWN:
                if (!player.spawned || player.isAlive() || !player.isOnline()) {
                    break;
                }

                if (player.server.isHardcore()) {
                    player.setBanned(true);
                    break;
                }

                player.craftingType = CRAFTING_SMALL;
                player.resetCraftingGridType();

                PlayerRespawnEvent playerRespawnEvent = new PlayerRespawnEvent(player, player.getSpawn());
                player.server.getPluginManager().callEvent(playerRespawnEvent);

                Position respawnPos = playerRespawnEvent.getRespawnPosition();

                player.teleport(respawnPos, null);

                RespawnPacket respawnPacket = new RespawnPacket();
                respawnPacket.x = (float) respawnPos.x;
                respawnPacket.y = (float) respawnPos.y;
                respawnPacket.z = (float) respawnPos.z;
                player.dataPacket(respawnPacket);

                player.setSprinting(false, true);
                player.setSneaking(false);

                player.extinguish();
                player.setDataProperty(new ShortEntityData(Player.DATA_AIR, 400), false);
                player.deadTicks = 0;
                player.noDamageTicks = 60;

                player.removeAllEffects();
                player.setHealth(player.getMaxHealth());
                player.getFoodData().setLevel(20, 20);

                player.sendData(player);

                player.setMovementSpeed(DEFAULT_SPEED);

                player.getAdventureSettings().update();
                player.inventory.sendContents(player);
                player.inventory.sendArmorContents(player);

                player.spawnToAll();
                player.scheduleUpdate();
                break;
            case PlayerActionPacket.ACTION_JUMP:
                return;
            case PlayerActionPacket.ACTION_START_SPRINT:
                PlayerToggleSprintEvent playerToggleSprintEvent = new PlayerToggleSprintEvent(player, true);
                player.server.getPluginManager().callEvent(playerToggleSprintEvent);
                if (playerToggleSprintEvent.isCancelled()) {
                    player.sendData(player);
                } else {
                    player.setSprinting(true);
                }
                return;
            case PlayerActionPacket.ACTION_STOP_SPRINT:
                playerToggleSprintEvent = new PlayerToggleSprintEvent(player, false);
                player.server.getPluginManager().callEvent(playerToggleSprintEvent);
                if (playerToggleSprintEvent.isCancelled()) {
                    player.sendData(player);
                } else {
                    player.setSprinting(false);
                }
                return;
            case PlayerActionPacket.ACTION_START_SNEAK:
                PlayerToggleSneakEvent playerToggleSneakEvent = new PlayerToggleSneakEvent(player, true);
                player.server.getPluginManager().callEvent(playerToggleSneakEvent);
                if (playerToggleSneakEvent.isCancelled()) {
                    player.sendData(player);
                } else {
                    player.setSneaking(true);
                }
                return;
            case PlayerActionPacket.ACTION_STOP_SNEAK:
                playerToggleSneakEvent = new PlayerToggleSneakEvent(player, false);
                player.server.getPluginManager().callEvent(playerToggleSneakEvent);
                if (playerToggleSneakEvent.isCancelled()) {
                    player.sendData(player);
                } else {
                    player.setSneaking(false);
                }
                return;
            case PlayerActionPacket.ACTION_DIMENSION_CHANGE_ACK:
                break; //TODO
            case PlayerActionPacket.ACTION_START_GLIDE:
                PlayerToggleGlideEvent playerToggleGlideEvent = new PlayerToggleGlideEvent(player, true);
                player.server.getPluginManager().callEvent(playerToggleGlideEvent);
                if (playerToggleGlideEvent.isCancelled()) {
                    player.sendData(player);
                } else {
                    player.setGliding(true);
                }
                return;
            case PlayerActionPacket.ACTION_STOP_GLIDE:
                playerToggleGlideEvent = new PlayerToggleGlideEvent(player, false);
                player.server.getPluginManager().callEvent(playerToggleGlideEvent);
                if (playerToggleGlideEvent.isCancelled()) {
                    player.sendData(player);
                } else {
                    player.setGliding(false);
                }
                return;
            case PlayerActionPacket.ACTION_CONTINUE_BREAK:
                if (player.isBreakingBlock()) {
                    block = player.level.getBlock(pos);
                    player.level.addParticle(new PunchBlockParticle(pos, block, face));
                }
                break;
        }

        player.startAction = -1;
        player.setDataFlag(Player.DATA_FLAGS, Player.DATA_FLAG_ACTION, false);
    }
}
