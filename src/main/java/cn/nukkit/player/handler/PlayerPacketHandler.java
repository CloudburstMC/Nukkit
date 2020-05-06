package cn.nukkit.player.handler;

import cn.nukkit.AdventureSettings;
import cn.nukkit.Server;
import cn.nukkit.block.*;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.ItemFrame;
import cn.nukkit.blockentity.Lectern;
import cn.nukkit.command.CommandUtils;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.impl.projectile.EntityArrow;
import cn.nukkit.entity.impl.vehicle.EntityAbstractMinecart;
import cn.nukkit.entity.impl.vehicle.EntityBoat;
import cn.nukkit.entity.misc.DroppedItem;
import cn.nukkit.entity.misc.ExperienceOrb;
import cn.nukkit.event.block.ItemFrameDropItemEvent;
import cn.nukkit.event.block.LecternPageChangeEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.inventory.InventoryCloseEvent;
import cn.nukkit.event.player.*;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.form.CustomForm;
import cn.nukkit.form.Form;
import cn.nukkit.inventory.transaction.CraftingTransaction;
import cn.nukkit.inventory.transaction.InventoryTransaction;
import cn.nukkit.inventory.transaction.action.InventoryAction;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemMap;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Location;
import cn.nukkit.level.Sound;
import cn.nukkit.level.gamerule.GameRules;
import cn.nukkit.level.particle.PunchBlockParticle;
import cn.nukkit.locale.TranslationContainer;
import cn.nukkit.math.BlockFace;
import cn.nukkit.network.protocol.types.InventoryTransactionUtils;
import cn.nukkit.player.Player;
import cn.nukkit.utils.TextFormat;
import co.aikar.timings.Timing;
import co.aikar.timings.Timings;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.nbt.tag.CompoundTag;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.data.*;
import com.nukkitx.protocol.bedrock.handler.BedrockPacketHandler;
import com.nukkitx.protocol.bedrock.packet.*;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static cn.nukkit.block.BlockIds.AIR;
import static cn.nukkit.player.Player.CRAFTING_SMALL;
import static cn.nukkit.player.Player.DEFAULT_SPEED;

/**
 * @author Extollite
 */
public class PlayerPacketHandler implements BedrockPacketHandler {
    private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(PlayerPacketHandler.class);
    private final Player player;

    protected Vector3i lastRightClickPos = null;
    protected double lastRightClickTime = 0.0;

    private Vector3i lastBreakPosition = Vector3i.ZERO;

    public PlayerPacketHandler(Player player) {
        this.player = player;
    }

    public boolean handle(BedrockPacket packet) {
        if (!player.isConnected()) {
            return true;
        }

        try (Timing ignored = Timings.getReceiveDataPacketTiming(packet).startTiming()) {
            if (log.isTraceEnabled() && !player.getServer().isIgnoredPacket(packet.getClass())) {
                log.trace("Inbound {}: {}", player.getName(), packet);
            }

            DataPacketReceiveEvent receiveEvent = new DataPacketReceiveEvent(player, packet);
            player.getServer().getPluginManager().callEvent(receiveEvent);
            if (receiveEvent.isCancelled()) {
                return true;
            }

            switch (packet.getPacketType()) {
                case PLAYER_SKIN:
                    return handle((PlayerSkinPacket) packet);
                case PLAYER_INPUT:
                    return handle((PlayerInputPacket) packet);
                case MOVE_PLAYER:
                    return handle((MovePlayerPacket) packet);
                case ADVENTURE_SETTINGS:
                    //TODO: player abilities, check for other changes
                    return handle((AdventureSettingsPacket) packet);
                case MOB_EQUIPMENT:
                    return handle((MobEquipmentPacket) packet);
                case PLAYER_ACTION:
                    return handle((PlayerActionPacket) packet);
                case MOB_ARMOR_EQUIPMENT:
                    return handle((MobArmorEquipmentPacket) packet);
                case MODAL_FORM_RESPONSE:
                    return handle((ModalFormResponsePacket) packet);
                case INTERACT:
                    return handle((InteractPacket) packet);
                case BLOCK_PICK_REQUEST:
                    return handle((BlockPickRequestPacket) packet);
                case ANIMATE:
                    return handle((AnimatePacket) packet);
                case SET_HEALTH:
                    // Cannot be trusted. Use UpdateAttributePacket instead
                    break;
                case ENTITY_EVENT:
                    return handle((EntityEventPacket) packet);
                case COMMAND_REQUEST:
                    return handle((CommandRequestPacket) packet);
                case TEXT:
                    return handle((TextPacket) packet);
                case CONTAINER_CLOSE:
                    return handle((ContainerClosePacket) packet);
                case CRAFTING_EVENT:
                    break;
                case BLOCK_ENTITY_DATA:
                    return handle((BlockEntityDataPacket) packet);
                case REQUEST_CHUNK_RADIUS:
                    return handle((RequestChunkRadiusPacket) packet);
                case SET_PLAYER_GAME_TYPE:
                    return handle((SetPlayerGameTypePacket) packet);
                case ITEM_FRAME_DROP_ITEM:
                    return handle((ItemFrameDropItemPacket) packet);
                case MAP_INFO_REQUEST:
                    return handle((MapInfoRequestPacket) packet);
                case LEVEL_SOUND_EVENT_2:
                case LEVEL_SOUND_EVENT_3:
                    return handle((LevelSoundEvent2Packet) packet);
                case INVENTORY_TRANSACTION:
                    return handle((InventoryTransactionPacket) packet);
                case PLAYER_HOTBAR:
                    return handle((PlayerHotbarPacket) packet);
                case SERVER_SETTINGS_REQUEST:
                    return handle((ServerSettingsRequestPacket) packet);
                case RESPAWN:
                    return handle((RespawnPacket) packet);
                case LECTERN_UPDATE:
                    return handle((LecternUpdatePacket) packet);
                case SET_LOCAL_PLAYER_AS_INITIALIZED:
                    return handle((SetLocalPlayerAsInitializedPacket) packet);
                default:
                    break;
            }
            return true;
        }
    }

    @Override
    public boolean handle(PlayerSkinPacket packet) {
        SerializedSkin skin = packet.getSkin();

        if (!skin.isValid()) {
            return true;
        }

        PlayerChangeSkinEvent playerChangeSkinEvent = new PlayerChangeSkinEvent(player, skin);
        playerChangeSkinEvent.setCancelled(TimeUnit.SECONDS.toMillis(player.getServer().getPlayerSkinChangeCooldown()) > System.currentTimeMillis() - player.lastSkinChange);
        player.getServer().getPluginManager().callEvent(playerChangeSkinEvent);
        if (!playerChangeSkinEvent.isCancelled()) {
            player.lastSkinChange = System.currentTimeMillis();
            player.setSkin(skin);
        }
        return true;
    }

    @Override
    public boolean handle(PlayerInputPacket packet) {
        if (!player.isAlive() || !player.spawned) {
            return true;
        }
        if (player.getVehicle() instanceof EntityAbstractMinecart) {
            ((EntityAbstractMinecart) player.getVehicle()).setCurrentSpeed(packet.getInputMotion().getY());
        }
        return true;
    }

    @Override
    public boolean handle(MovePlayerPacket packet) {
        if (player.getTeleportPosition() != null) {
            return true;
        }

        Vector3f newPos = packet.getPosition().sub(0, player.getEyeHeight(), 0);
        Vector3f currentPos = player.getPosition();

        float yaw = packet.getRotation().getY() % 360;
        float pitch = packet.getRotation().getX() % 360;

        if (yaw < 0) {
            yaw += 360;
        }

        if (newPos.distanceSquared(currentPos) < 0.01 && yaw == player.getYaw() && pitch == player.getPitch()) {
            return true;
        }

        if (currentPos.distance(newPos) > 50) {
            log.debug("packet too far REVERTING");
            player.sendPosition(currentPos, yaw, pitch, MovePlayerPacket.Mode.RESET);
            return true;
        }

        boolean revert = false;
        if (!player.isAlive() || !player.spawned) {
            revert = true;
            player.setForceMovement(currentPos);
        }


        if (player.getForceMovement() != null && (newPos.distanceSquared(player.getForceMovement()) > 0.1 || revert)) {
            log.debug("packet forceMovement {} REVERTING {}", player.getForceMovement(), newPos);
            player.sendPosition(player.getForceMovement(), yaw, pitch, MovePlayerPacket.Mode.RESET);
        } else {
            player.setRotation(yaw, pitch);
            player.setNewPosition(newPos);
            player.setForceMovement(null);
        }


        if (player.getVehicle() != null) {
            if (player.getVehicle() instanceof EntityBoat) {
                player.getVehicle().setPositionAndRotation(newPos.sub(0, 1, 0), (yaw + 90) % 360, 0);
            }
        }
        return true;
    }

    @Override
    public boolean handle(AdventureSettingsPacket packet) {
        Set<AdventureSettingsPacket.Flag> flags = packet.getFlags();
        if (!player.getServer().getAllowFlight() && flags.contains(AdventureSettingsPacket.Flag.FLYING) && !player.getAdventureSettings().get(AdventureSettings.Type.ALLOW_FLIGHT)) {
            player.kick(PlayerKickEvent.Reason.FLYING_DISABLED, "Flying is not enabled on player server");
            return true;
        }
        PlayerToggleFlightEvent playerToggleFlightEvent = new PlayerToggleFlightEvent(player, flags.contains(AdventureSettingsPacket.Flag.FLYING));
        player.getServer().getPluginManager().callEvent(playerToggleFlightEvent);
        if (playerToggleFlightEvent.isCancelled()) {
            player.getAdventureSettings().update();
        } else {
            player.getAdventureSettings().set(AdventureSettings.Type.FLYING, playerToggleFlightEvent.isFlying());
        }
        return true;
    }

    @Override
    public boolean handle(MobEquipmentPacket packet) {
        if (!player.spawned || !player.isAlive()) {
            return true;
        }

        boolean offhand = packet.getContainerId() == ContainerId.OFFHAND;
        Item serverItem;
        if (offhand) {
            serverItem = player.getInventory().getOffHand();
        } else {
            serverItem = player.getInventory().getItem(packet.getHotbarSlot());
        }
        Item clientItem = Item.fromNetwork(packet.getItem());

        if (!serverItem.equals(clientItem)) {
            log.debug("Tried to equip " + clientItem + " but have " + serverItem + " in target slot");
            player.getInventory().sendContents(player);
            return true;
        }
        if (offhand) {
            player.getInventory().setOffHandContents(clientItem);
        } else {
            player.getInventory().equipItem(packet.getHotbarSlot());
        }
        player.setUsingItem(false);

        return true;
    }

    @Override
    public boolean handle(PlayerActionPacket packet) {
        if (!player.spawned || (!player.isAlive() &&
                packet.getAction() != PlayerActionPacket.Action.RESPAWN &&
                packet.getAction() != PlayerActionPacket.Action.DIMENSION_CHANGE_REQUEST)) {
            return true;
        }

        packet.setRuntimeEntityId(player.getRuntimeId());
        Vector3f currentPos = player.getPosition();
        Vector3i blockPos = packet.getBlockPosition();
        BlockFace face = BlockFace.fromIndex(packet.getFace());

        switch (packet.getAction()) {
            case START_BREAK:
                long currentBreak = System.currentTimeMillis();
                Vector3i currentBreakPosition = packet.getBlockPosition();
                // HACK: Client spams multiple left clicks so we need to skip them.
                if ((lastBreakPosition.equals(currentBreakPosition) && (currentBreak - player.lastBreak) < 10) || currentPos.distanceSquared(blockPos.toFloat()) > 100) {
                    break;
                }
                Block target = player.getLevel().getBlock(blockPos);
                PlayerInteractEvent playerInteractEvent = new PlayerInteractEvent(player, player.getInventory().getItemInHand(), target, face, target.getId() == AIR ? PlayerInteractEvent.Action.LEFT_CLICK_AIR : PlayerInteractEvent.Action.LEFT_CLICK_BLOCK);
                player.getServer().getPluginManager().callEvent(playerInteractEvent);
                if (playerInteractEvent.isCancelled()) {
                    player.getInventory().sendHeldItem(player);
                    break;
                }
                if (target.getId() == BlockIds.NOTEBLOCK) {
                    ((BlockNoteblock) target).emitSound();
                    break;
                } else if (target.getId() == BlockIds.DRAGON_EGG) {
                    ((BlockDragonEgg) target).teleport();
                    break;
                }
                Block block = target.getSide(face);
                if (block.getId() == BlockIds.FIRE) {
                    player.getLevel().setBlock(block.getPosition(), Block.get(AIR), true);
                    player.getLevel().addLevelSoundEvent(block.getPosition(), SoundEvent.EXTINGUISH_FIRE);
                    break;
                }
                if (!player.isCreative()) {
                    //improved player to take stuff like swimming, ladders, enchanted tools into account, fix wrong tool break time calculations for bad tools (pmmp/PocketMine-MP#211)
                    //Done by lmlstarqaq
                    double breakTime = Math.ceil(target.getBreakTime(player.getInventory().getItemInHand(), player) * 20);
                    if (breakTime > 0) {
                        LevelEventPacket levelEvent = new LevelEventPacket();
                        levelEvent.setType(LevelEventType.BLOCK_START_BREAK);
                        levelEvent.setPosition(blockPos.toFloat());
                        levelEvent.setData((int) (65535 / breakTime));
                        player.getLevel().addChunkPacket(blockPos, levelEvent);
                    }
                }

                player.breakingBlock = target;
                player.lastBreak = currentBreak;
                lastBreakPosition = currentBreakPosition;
                break;
            case ABORT_BREAK:
            case STOP_BREAK:
                LevelEventPacket levelEvent = new LevelEventPacket();
                levelEvent.setType(LevelEventType.BLOCK_STOP_BREAK);
                levelEvent.setPosition(blockPos.toFloat());
                levelEvent.setData(0);
                player.getLevel().addChunkPacket(blockPos, levelEvent);
                player.breakingBlock = null;
                break;
            case GET_UPDATED_BLOCK:
                break; //TODO
            case DROP_ITEM:
                break; //TODO
            case STOP_SLEEP:
                player.stopSleep();
                break;
            case RESPAWN:
                if (!player.spawned || player.isAlive() || !player.isOnline()) {
                    break;
                }

                if (player.getServer().isHardcore()) {
                    player.setBanned(true);
                    break;
                }

                player.craftingType = CRAFTING_SMALL;
                player.resetCraftingGridType();

                PlayerRespawnEvent playerRespawnEvent = new PlayerRespawnEvent(player, player.getSpawn());
                player.getServer().getPluginManager().callEvent(playerRespawnEvent);

                Location respawnLoc = playerRespawnEvent.getRespawnLocation();

                player.teleport(respawnLoc, null);

                player.setSprinting(false);
                player.setSneaking(false);

                player.getData().setShort(EntityData.AIR, 400);
                player.deadTicks = 0;
                player.noDamageTicks = 60;

                player.removeAllEffects();
                player.setHealth(player.getMaxHealth());
                player.getFoodData().setLevel(20, 20);

                player.sendData(player);

                player.setMovementSpeed(DEFAULT_SPEED);

                player.getAdventureSettings().update();
                player.getInventory().sendContents(player);
                player.getInventory().sendArmorContents(player);

                player.spawnToAll();
                player.scheduleUpdate();
                break;
            case JUMP:
                player.getServer().getPluginManager().callEvent(new PlayerJumpEvent(player));
                break;
            case START_SPRINT:
                PlayerToggleSprintEvent playerToggleSprintEvent = new PlayerToggleSprintEvent(player, true);
                player.getServer().getPluginManager().callEvent(playerToggleSprintEvent);
                if (playerToggleSprintEvent.isCancelled()) {
                    player.sendFlags(player);
                } else {
                    player.setSprinting(true);
                }
                break;
            case STOP_SPRINT:
                playerToggleSprintEvent = new PlayerToggleSprintEvent(player, false);
                player.getServer().getPluginManager().callEvent(playerToggleSprintEvent);
                if (playerToggleSprintEvent.isCancelled()) {
                    player.sendFlags(player);
                } else {
                    player.setSprinting(false);
                }
                if (player.isSwimming()) {
                    PlayerToggleSwimEvent ptse = new PlayerToggleSwimEvent(player, false);
                    player.getServer().getPluginManager().callEvent(ptse);

                    if (ptse.isCancelled()) {
                        player.sendFlags(player);
                    } else {
                        player.setSwimming(false);
                    }
                }
                break;
            case START_SNEAK:
                PlayerToggleSneakEvent playerToggleSneakEvent = new PlayerToggleSneakEvent(player, true);
                player.getServer().getPluginManager().callEvent(playerToggleSneakEvent);
                if (playerToggleSneakEvent.isCancelled()) {
                    player.sendFlags(player);
                } else {
                    player.setSneaking(true);
                }
                break;
            case STOP_SNEAK:
                playerToggleSneakEvent = new PlayerToggleSneakEvent(player, false);
                player.getServer().getPluginManager().callEvent(playerToggleSneakEvent);
                if (playerToggleSneakEvent.isCancelled()) {
                    player.sendFlags(player);
                } else {
                    player.setSneaking(false);
                }
                break;
            case DIMENSION_CHANGE_REQUEST:
                player.sendPosition(player.getPosition(), player.getYaw(), player.getPitch(), MovePlayerPacket.Mode.NORMAL);
                break; //TODO
            case START_GLIDE:
                PlayerToggleGlideEvent playerToggleGlideEvent = new PlayerToggleGlideEvent(player, true);
                player.getServer().getPluginManager().callEvent(playerToggleGlideEvent);
                if (playerToggleGlideEvent.isCancelled()) {
                    player.sendFlags(player);
                } else {
                    player.setGliding(true);
                }
                break;
            case STOP_GLIDE:
                playerToggleGlideEvent = new PlayerToggleGlideEvent(player, false);
                player.getServer().getPluginManager().callEvent(playerToggleGlideEvent);
                if (playerToggleGlideEvent.isCancelled()) {
                    player.sendFlags(player);
                } else {
                    player.setGliding(false);
                }
                break;
            case CONTINUE_BREAK:
                if (player.isBreakingBlock()) {
                    block = player.getLevel().getBlock(blockPos);
                    player.getLevel().addParticle(new PunchBlockParticle(blockPos.toFloat(), block, face));
                }
                break;
            case START_SWIMMING:
                PlayerToggleSwimEvent ptse = new PlayerToggleSwimEvent(player, true);
                player.getServer().getPluginManager().callEvent(ptse);

                if (ptse.isCancelled()) {
                    player.sendFlags(player);
                } else {
                    player.setSwimming(true);
                }
                break;
            case STOP_SWIMMING:
                ptse = new PlayerToggleSwimEvent(player, false);
                player.getServer().getPluginManager().callEvent(ptse);

                if (ptse.isCancelled()) {
                    player.sendFlags(player);
                } else {
                    player.setSwimming(false);
                }
                break;
        }
        player.getData().update();

        player.setUsingItem(false);
        return true;
    }

    @Override
    public boolean handle(ModalFormResponsePacket packet) {
        if (!player.spawned || !player.isAlive()) {
            return true;
        }

        Form<?> window = player.removeFormWindow(packet.getFormId());

        if (window == null) {
            if (player.getServerSettings() != null && player.getServerSettingsId() == packet.getFormId()) {
                window = player.getServerSettings();
            } else {
                return true;
            }
        }

        try {
            JsonNode response = new JsonMapper().readTree(packet.getFormData());

            if ("null".equals(response.asText())) {
                window.close(player);
            } else {
                try {
                    window.handleResponse(player, response);
                } catch (Exception e) {
                    log.error("Error while handling form response", e);
                    window.error(player);
                }
            }
        } catch (JsonProcessingException e) {
            log.debug("Received corrupted form json data");
        }
        return true;
    }

    @Override
    public boolean handle(InteractPacket packet) {
        if (!player.spawned || !player.isAlive()) {
            return true;
        }

        player.craftingType = CRAFTING_SMALL;
        //this.resetCraftingGridType();

        Entity targetEntity = player.getLevel().getEntity(packet.getRuntimeEntityId());

        if (targetEntity == null || !player.isAlive() || !targetEntity.isAlive()) {
            return true;
        }

        if (targetEntity instanceof DroppedItem || targetEntity instanceof EntityArrow || targetEntity instanceof ExperienceOrb) {
            player.kick(PlayerKickEvent.Reason.INVALID_PVE, "Attempting to interact with an invalid entity");
            log.warn(player.getServer().getLanguage().translate("nukkit.player.invalidEntity", player.getName()));
            return true;
        }

        switch (packet.getAction()) {
            case MOUSEOVER:
                if (packet.getRuntimeEntityId() == 0) {
                    break;
                }
                player.getServer().getPluginManager().callEvent(new PlayerMouseOverEntityEvent(player, targetEntity));
                break;
            case LEAVE_VEHICLE:
                if (player.getVehicle() == null) {
                    break;
                }
                player.dismount(player.getVehicle());
                break;
        }
        return true;
    }

    @Override
    public boolean handle(BlockPickRequestPacket packet) {
        Vector3i pickPos = packet.getBlockPosition();
        Block block = player.getLevel().getBlock(pickPos.getX(), pickPos.getY(), pickPos.getZ());
        Item serverItem = block.toItem();

        if (packet.isAddUserData()) {
            BlockEntity blockEntity = player.getLevel().getLoadedBlockEntity(
                    Vector3i.from(pickPos.getX(), pickPos.getY(), pickPos.getZ()));
            if (blockEntity != null) {
                CompoundTag nbt = blockEntity.getItemTag();
                if (nbt != null) {
                    serverItem.addTag(nbt);
                    serverItem.setLore("+(DATA)");
                }
            }
        }

        PlayerBlockPickEvent pickEvent = new PlayerBlockPickEvent(player, block, serverItem);
        if (player.isSpectator()) {
            log.debug("Got block-pick request from " + player.getName() + " when in spectator mode");
            pickEvent.setCancelled();
        }

        player.getServer().getPluginManager().callEvent(pickEvent);

        if (!pickEvent.isCancelled()) {
            boolean itemExists = false;
            int itemSlot = -1;
            for (int slot = 0; slot < player.getInventory().getSize(); slot++) {
                if (player.getInventory().getItem(slot).equals(pickEvent.getItem())) {
                    if (slot < player.getInventory().getHotbarSize()) {
                        player.getInventory().setHeldItemSlot(slot);
                    } else {
                        itemSlot = slot;
                    }
                    itemExists = true;
                    break;
                }
            }

            for (int slot = 0; slot < player.getInventory().getHotbarSize(); slot++) {
                if (player.getInventory().getItem(slot).isNull()) {
                    if (!itemExists && player.isCreative()) {
                        player.getInventory().setHeldItemSlot(slot);
                        player.getInventory().setItemInHand(pickEvent.getItem());
                        return true;
                    } else if (itemSlot > -1) {
                        player.getInventory().setHeldItemSlot(slot);
                        player.getInventory().setItemInHand(player.getInventory().getItem(itemSlot));
                        player.getInventory().clear(itemSlot, true);
                        return true;
                    }
                }
            }

            if (!itemExists && player.isCreative()) {
                Item itemInHand = player.getInventory().getItemInHand();
                player.getInventory().setItemInHand(pickEvent.getItem());
                if (!player.getInventory().isFull()) {
                    for (int slot = 0; slot < player.getInventory().getSize(); slot++) {
                        if (player.getInventory().getItem(slot).isNull()) {
                            player.getInventory().setItem(slot, itemInHand);
                            break;
                        }
                    }
                }
            } else if (itemSlot > -1) {
                Item itemInHand = player.getInventory().getItemInHand();
                player.getInventory().setItemInHand(player.getInventory().getItem(itemSlot));
                player.getInventory().setItem(itemSlot, itemInHand);
            }
        }
        return true;
    }

    @Override
    public boolean handle(AnimatePacket packet) {
        if (!player.spawned || !player.isAlive()) {
            return true;
        }

        PlayerAnimationEvent animationEvent = new PlayerAnimationEvent(player, packet.getAction());
        player.getServer().getPluginManager().callEvent(animationEvent);
        if (animationEvent.isCancelled()) {
            return true;
        }

        AnimatePacket.Action animation = animationEvent.getAnimationType();

        switch (animation) {
            case ROW_RIGHT:
            case ROW_LEFT:
                if (player.getVehicle() instanceof EntityBoat) {
                    ((EntityBoat) player.getVehicle()).onPaddle(animation, packet.getRowingTime());
                }
                break;
        }

        AnimatePacket animatePacket = new AnimatePacket();
        animatePacket.setRuntimeEntityId(player.getRuntimeId());
        animatePacket.setAction(animationEvent.getAnimationType());
        Server.broadcastPacket(player.getViewers(), animatePacket);
        return true;
    }

    @Override
    public boolean handle(EntityEventPacket packet) {
        if (!player.spawned || !player.isAlive()) {
            return true;
        }
        player.craftingType = CRAFTING_SMALL;
        //player.resetCraftingGridType();

        if (packet.getType() == EntityEventType.EATING_ITEM) {
            if (packet.getData() == 0 || packet.getRuntimeEntityId() != player.getRuntimeId()) {
                return true;
            }

            packet.setRuntimeEntityId(player.getRuntimeId());

            player.sendPacket(packet);
            Server.broadcastPacket(player.getViewers(), packet);
        }
        return true;
    }

    @Override
    public boolean handle(CommandRequestPacket packet) {
        if (!player.spawned || !player.isAlive()) {
            return true;
        }
        player.craftingType = CRAFTING_SMALL;
        PlayerCommandPreprocessEvent playerCommandPreprocessEvent = new PlayerCommandPreprocessEvent(player, packet.getCommand());
        player.getServer().getPluginManager().callEvent(playerCommandPreprocessEvent);
        if (playerCommandPreprocessEvent.isCancelled()) {
            return true;
        }

        try (Timing ignored2 = Timings.playerCommandTimer.startTiming()) {
            player.getServer().dispatchCommand(playerCommandPreprocessEvent.getPlayer(), playerCommandPreprocessEvent.getMessage().substring(1));
        }
        return true;
    }

    @Override
    public boolean handle(TextPacket packet) {
        if (!player.spawned || !player.isAlive()) {
            return true;
        }

        if (packet.getType() == TextPacket.Type.CHAT) {
            player.chat(packet.getMessage());
        }
        return true;
    }

    @Override
    public boolean handle(ContainerClosePacket packet) {
        if (!player.spawned) {
            return true;
        }

        if (player.getWindowById(packet.getWindowId()) != null) {
            player.getServer().getPluginManager().callEvent(new InventoryCloseEvent(player.getWindowById(packet.getWindowId()), player));
            player.removeWindow(player.getWindowById(packet.getWindowId()));
        } else {
            player.removeWindowById(packet.getWindowId());
        }

        if (packet.getWindowId() == -1) {
            player.craftingType = CRAFTING_SMALL;
            player.resetCraftingGridType();
            player.addWindow(player.getCraftingGrid(), (byte) ContainerId.NONE);
        }
        return true;
    }

    @Override
    public boolean handle(BlockEntityDataPacket packet) {
        if (!player.spawned || !player.isAlive()) {
            return true;
        }

        player.craftingType = CRAFTING_SMALL;
        player.resetCraftingGridType();

        Vector3i blockPos = packet.getBlockPosition();
        if (blockPos.distanceSquared(player.getPosition().toInt()) > 10000) {
            return true;
        }

        BlockEntity blockEntity = player.getLevel().getLoadedBlockEntity(blockPos);
        if (blockEntity != null && blockEntity.isSpawnable()) {
            if (!blockEntity.updateFromClient((CompoundTag) packet.getData(), player)) {
                blockEntity.spawnTo(player);
            }
        }
        return true;
    }

    @Override
    public boolean handle(RequestChunkRadiusPacket packet) {
        player.getChunkManager().setChunkRadius(packet.getRadius());
        return true;
    }

    @Override
    public boolean handle(SetPlayerGameTypePacket packet) {
        if (packet.getGamemode() != player.getGamemode()) {
            if (!player.hasPermission("nukkit.command.gamemode")) {
                SetPlayerGameTypePacket packet1 = new SetPlayerGameTypePacket();
                packet1.setGamemode(player.getGamemode() & 0x01);
                player.sendPacket(packet1);
                player.getAdventureSettings().update();
                return true;
            }
            player.setGamemode(packet.getGamemode(), true);
            CommandUtils.broadcastCommandMessage(player, new TranslationContainer("%commands.gamemode.success.self", Server.getGamemodeString(player.getGamemode())));
        }
        return true;
    }

    @Override
    public boolean handle(ItemFrameDropItemPacket packet) {
        Vector3i vector3 = packet.getBlockPosition();
        BlockEntity blockEntity = player.getLevel().getLoadedBlockEntity(vector3);
        if (!(blockEntity instanceof ItemFrame)) {
            return true;
        }
        ItemFrame itemFrame = (ItemFrame) blockEntity;
        Block block = itemFrame.getBlock();
        Item itemDrop = itemFrame.getItem();
        ItemFrameDropItemEvent itemFrameDropItemEvent = new ItemFrameDropItemEvent(player, block, itemFrame, itemDrop);
        player.getServer().getPluginManager().callEvent(itemFrameDropItemEvent);
        if (!itemFrameDropItemEvent.isCancelled()) {
            if (itemDrop.getId() != AIR) {
                player.getLevel().dropItem(block.getPosition(), itemDrop);
                itemFrame.setItem(Item.get(AIR, 0, 0));
                itemFrame.setItemRotation(0);
                player.getLevel().addSound(player.getPosition(), Sound.BLOCK_ITEMFRAME_REMOVE_ITEM);
            }
        } else {
            itemFrame.spawnTo(player);
        }
        return true;
    }

    @Override
    public boolean handle(MapInfoRequestPacket packet) {
        Item mapItem = null;

        for (Item item1 : player.getInventory().getContents().values()) {
            if (item1 instanceof ItemMap && ((ItemMap) item1).getMapId() == packet.getUniqueMapId()) {
                mapItem = item1;
            }
        }

        if (mapItem == null) {
            for (BlockEntity be : player.getLevel().getBlockEntities()) {
                if (be instanceof ItemFrame) {
                    ItemFrame itemFrame1 = (ItemFrame) be;

                    if (itemFrame1.getItem() instanceof ItemMap && ((ItemMap) itemFrame1.getItem()).getMapId() == packet.getUniqueMapId()) {
                        ((ItemMap) itemFrame1.getItem()).sendImage(player);
                        break;
                    }
                }
            }
        }

        if (mapItem != null) {
            PlayerMapInfoRequestEvent event;
            player.getServer().getPluginManager().callEvent(event = new PlayerMapInfoRequestEvent(player, mapItem));

            if (!event.isCancelled()) {
                ((ItemMap) mapItem).sendImage(player);
            }
        }
        return true;
    }

    @Override
    public boolean handle(LevelSoundEvent2Packet packet) {
        if (!player.isSpectator() || (packet.getSound() != SoundEvent.HIT &&
                packet.getSound() != SoundEvent.ATTACK_NODAMAGE)) {
            player.getLevel().addChunkPacket(player.getPosition(), packet);
        }
        return true;
    }

    @Override
    public boolean handle(InventoryTransactionPacket packet) {
        if (player.isSpectator()) {
            player.sendAllInventories();
            return true;
        }

        List<InventoryAction> actions = new ArrayList<>();
        for (InventoryActionData inventoryActionData : packet.getActions()) {
            InventoryAction a = InventoryTransactionUtils.createInventoryAction(player, inventoryActionData);

            if (a == null) {
                log.debug("Unmatched inventory action from " + player.getName() + ": " + inventoryActionData);
                player.sendAllInventories();
                return true;
            }

            actions.add(a);
        }

        if (InventoryTransactionUtils.containsCraftingPart(packet)) {
            if (player.getCraftingTransaction() == null) {
                player.setCraftingTransaction(new CraftingTransaction(player, actions));
            } else {
                for (InventoryAction action : actions) {
                    player.getCraftingTransaction().addAction(action);
                }
            }

            if (player.getCraftingTransaction().getPrimaryOutput() != null) {
                //we get the actions for player in several packets, so we can't execute it until we get the result

                player.getCraftingTransaction().execute();
                player.setCraftingTransaction(null);
            }

            return true;
        } else if (player.getCraftingTransaction() != null) {
            log.debug("Got unexpected normal inventory action with incomplete crafting transaction from " + player.getName() + ", refusing to execute crafting");
            player.setCraftingTransaction(null);
        }

        switch (packet.getTransactionType()) {
            case NORMAL:
                InventoryTransaction transaction = new InventoryTransaction(player, actions);

                if (!transaction.execute()) {
                    log.debug("Failed to execute inventory transaction from " + player.getName() + " with actions: " + packet.getActions());
                    return true;
                }
                //TODO: fix achievement for getting iron from furnace
                return true;
            case INVENTORY_MISMATCH:
                if (packet.getActions().size() > 0) {
                    log.debug("Expected 0 actions for mismatch, got " + packet.getActions().size() + ", " + packet.getActions());
                }
                player.sendAllInventories();

                return true;
            case ITEM_USE:

                Vector3i blockVector = packet.getBlockPosition();
                BlockFace face = BlockFace.fromIndex(packet.getFace());

                switch (packet.getActionType()) {
                    case InventoryTransactionUtils.USE_ITEM_ACTION_CLICK_BLOCK:
                        // Remove if client bug is ever fixed
                        boolean spamBug = (lastRightClickPos != null && System.currentTimeMillis() - lastRightClickTime < 100.0 && blockVector.distanceSquared(lastRightClickPos) < 0.00001);
                        lastRightClickPos = blockVector;
                        lastRightClickTime = System.currentTimeMillis();
                        if (spamBug) {
                            return true;
                        }

                        player.setUsingItem(false);

                        if (player.canInteract(blockVector.toFloat().add(0.5, 0.5, 0.5), player.isCreative() ? 13 : 7)) {
                            Item clientHand = Item.fromNetwork(packet.getItemInHand());
                            if (player.isCreative()) {
                                Item i = player.getInventory().getItemInHand();
                                if (player.getLevel().useItemOn(blockVector, i, face,
                                        packet.getClickPosition(), player) != null) {
                                    return true;
                                }
                            } else if (player.getInventory().getItemInHand().equals(clientHand)) {
                                Item serverHand = player.getInventory().getItemInHand();
                                Item oldItem = serverHand.clone();
                                //TODO: Implement adventure mode checks
                                if ((serverHand = player.getLevel().useItemOn(blockVector, serverHand, face,
                                        packet.getClickPosition(), player)) != null) {
                                    if (!serverHand.equals(oldItem) ||
                                            serverHand.getCount() != oldItem.getCount()) {
                                        player.getInventory().setItemInHand(serverHand);
                                        player.getInventory().sendHeldItem(player.getViewers());
                                    }
                                    return true;
                                }
                            }
                        }

                        player.getInventory().sendHeldItem(player);

                        if (blockVector.distanceSquared(player.getPosition().toInt()) > 10000) {
                            return true;
                        }

                        Block target = player.getLevel().getBlock(blockVector);
                        Block block = target.getSide(face);

                        player.getLevel().sendBlocks(new Player[]{player}, new Block[]{target, block}, UpdateBlockPacket.FLAG_ALL_PRIORITY);
                        return true;
                    case InventoryTransactionUtils.USE_ITEM_ACTION_BREAK_BLOCK:
                        if (!player.spawned || !player.isAlive()) {
                            return true;
                        }

                        player.resetCraftingGridType();

                        Item i = player.getInventory().getItemInHand();

                        Item oldItem = i.clone();

                        if (player.canInteract(blockVector.toFloat().add(0.5, 0.5, 0.5), player.isCreative() ? 13 : 7) &&
                                (i = player.getLevel().useBreakOn(blockVector, face, i, player, true)) != null) {
                            if (player.isSurvival()) {
                                player.getFoodData().updateFoodExpLevel(0.025);
                                if (!i.equals(oldItem) || i.getCount() != oldItem.getCount()) {
                                    player.getInventory().setItemInHand(i);
                                    player.getInventory().sendHeldItem(player.getViewers());
                                }
                            }
                            return true;
                        }

                        player.getInventory().sendContents(player);
                        target = player.getLevel().getBlock(blockVector);
                        BlockEntity blockEntity = player.getLevel().getLoadedBlockEntity(blockVector);

                        player.getLevel().sendBlocks(new Player[]{player}, new Block[]{target}, UpdateBlockPacket.FLAG_ALL_PRIORITY);

                        player.getInventory().sendHeldItem(player);

                        if (blockEntity != null && blockEntity.isSpawnable()) {
                            blockEntity.spawnTo(player);
                        }

                        return true;
                    case InventoryTransactionUtils.USE_ITEM_ACTION_CLICK_AIR:
                        Vector3f directionVector = player.getDirectionVector();

                        Item clientHand = Item.fromNetwork(packet.getItemInHand());
                        Item serverItem;

                        if (player.isCreative()) {
                            serverItem = player.getInventory().getItemInHand();
                        } else if (!player.getInventory().getItemInHand().equals(clientHand)) {
                            player.getInventory().sendHeldItem(player);
                            return true;
                        } else {
                            serverItem = player.getInventory().getItemInHand();
                        }

                        PlayerInteractEvent interactEvent = new PlayerInteractEvent(player, serverItem, directionVector, face, PlayerInteractEvent.Action.RIGHT_CLICK_AIR);

                        player.getServer().getPluginManager().callEvent(interactEvent);

                        if (interactEvent.isCancelled()) {
                            player.getInventory().sendHeldItem(player);
                            return true;
                        }

                        if (serverItem.onClickAir(player, directionVector)) {
                            if (player.isSurvival()) {
                                player.getInventory().setItemInHand(serverItem);
                            }

                            if (!player.isUsingItem()) {
                                player.setUsingItem(true);
                                return true;
                            }

                            // Used item
                            int ticksUsed = player.getServer().getTick() - player.getStartActionTick();
                            player.setUsingItem(false);

                            if (!serverItem.onUse(player, ticksUsed)) {
                                player.getInventory().sendContents(player);
                            }
                        }

                        return true;
                    default:
                        //unknown
                        break;
                }
                break;
            case ITEM_USE_ON_ENTITY:

                Entity target = player.getLevel().getEntity(packet.getRuntimeEntityId());
                if (target == null) {
                    return true;
                }

                Item clientHand = Item.fromNetwork(packet.getItemInHand());

                if (!clientHand.equalsExact(player.getInventory().getItemInHand())) {
                    player.getInventory().sendHeldItem(player);
                }

                Item serverItem = player.getInventory().getItemInHand();

                switch (packet.getActionType()) {
                    case InventoryTransactionUtils.USE_ITEM_ON_ENTITY_ACTION_INTERACT:
                        PlayerInteractEntityEvent playerInteractEntityEvent = new PlayerInteractEntityEvent(player, target, serverItem, packet.getClickPosition());
                        if (player.isSpectator()) playerInteractEntityEvent.setCancelled();
                        player.getServer().getPluginManager().callEvent(playerInteractEntityEvent);

                        if (playerInteractEntityEvent.isCancelled()) {
                            break;
                        }
                        if (target.onInteract(player, serverItem, packet.getClickPosition()) && player.isSurvival()) {
                            if (serverItem.isTool()) {
                                if (serverItem.useOn(target) && serverItem.getMeta() >= serverItem.getMaxDurability()) {
                                    serverItem = Item.get(AIR, 0, 0);
                                }
                            } else {
                                if (serverItem.getCount() > 1) {
                                    serverItem.decrementCount();
                                } else {
                                    serverItem = Item.get(AIR, 0, 0);
                                }
                            }

                            player.getInventory().setItemInHand(serverItem);
                        }
                        break;
                    case InventoryTransactionUtils.USE_ITEM_ON_ENTITY_ACTION_ATTACK:
                        float itemDamage = serverItem.getAttackDamage();

                        for (Enchantment enchantment : serverItem.getEnchantments()) {
                            itemDamage += enchantment.getDamageBonus(target);
                        }

                        Map<EntityDamageEvent.DamageModifier, Float> damage = new EnumMap<>(EntityDamageEvent.DamageModifier.class);
                        damage.put(EntityDamageEvent.DamageModifier.BASE, itemDamage);

                        if (!player.canInteract(target.getPosition(), player.isCreative() ? 8 : 5)) {
                            break;
                        } else if (target instanceof Player) {
                            if ((((Player) target).getGamemode() & 0x01) > 0) {
                                break;
                            } else if (!player.getServer().getPropertyBoolean("pvp")) {
                                break;
                            }
                        }

                        EntityDamageByEntityEvent entityDamageByEntityEvent = new EntityDamageByEntityEvent(player, target, EntityDamageEvent.DamageCause.ENTITY_ATTACK, damage);
                        if (player.isSpectator()) entityDamageByEntityEvent.setCancelled();
                        if ((target instanceof Player) && !player.getLevel().getGameRules().get(GameRules.PVP)) {
                            entityDamageByEntityEvent.setCancelled();
                        }

                        if (!target.attack(entityDamageByEntityEvent)) {
                            if (serverItem.isTool() && player.isSurvival()) {
                                player.getInventory().sendContents(player);
                            }
                            break;
                        }

                        for (Enchantment enchantment : serverItem.getEnchantments()) {
                            enchantment.doPostAttack(player, target);
                        }

                        if (serverItem.isTool() && player.isSurvival()) {
                            if (serverItem.useOn(target) && serverItem.getMeta() >= serverItem.getMaxDurability()) {
                                player.getInventory().setItemInHand(Item.get(AIR, 0, 0));
                            } else {
                                player.getInventory().setItemInHand(serverItem);
                            }
                        }
                        return true;
                    default:
                        break; //unknown
                }

                break;
            case ITEM_RELEASE:
                if (player.isSpectator()) {
                    player.sendAllInventories();
                    return true;
                }

                try {
                    switch (packet.getActionType()) {
                        case InventoryTransactionUtils.RELEASE_ITEM_ACTION_RELEASE:
                            if (player.isUsingItem()) {
                                serverItem = player.getInventory().getItemInHand();

                                int ticksUsed = player.getServer().getTick() - player.getStartActionTick();
                                if (!serverItem.onRelease(player, ticksUsed)) {
                                    player.getInventory().sendContents(player);
                                }

                                player.setUsingItem(false);
                            } else {
                                player.getInventory().sendContents(player);
                            }
                            return true;
                        case InventoryTransactionUtils.RELEASE_ITEM_ACTION_CONSUME:
                            log.debug("Unexpected release item action consume from {}", player::getName);
                            return true;
                        default:
                            break;
                    }
                } finally {
                    player.setUsingItem(false);
                }
                break;
            default:
                player.getInventory().sendContents(player);
                break;
        }
        return true;
    }

    @Override
    public boolean handle(PlayerHotbarPacket packet) {
        if (packet.getContainerId() != ContainerId.INVENTORY) {
            return true; // This should never happen
        }

        player.getInventory().equipItem(packet.getSelectedHotbarSlot());
        return true;
    }

    @Override
    public boolean handle(ServerSettingsRequestPacket packet) {
        CustomForm settings = player.getServerSettings();

        if (settings == null) {
            return true;
        }

        try {
            ServerSettingsResponsePacket re = new ServerSettingsResponsePacket();
            re.setFormId(player.getServerSettingsId());
            re.setFormData(new JsonMapper().writeValueAsString(settings));
            player.sendPacket(re);
        } catch (JsonProcessingException e) {
            log.error("Error while writing form data", e);
        }
        return true;
    }

    @Override
    public boolean handle(RespawnPacket packet) {
        if (player.isAlive()) {
            return true;
        }
        if (packet.getState() == RespawnPacket.State.CLIENT_READY) {
            RespawnPacket respawn1 = new RespawnPacket();
            respawn1.setPosition(player.getSpawn().getPosition());
            respawn1.setState(RespawnPacket.State.SERVER_READY);
            player.sendPacket(respawn1);
        }
        return true;
    }

    @Override
    public boolean handle(LecternUpdatePacket packet) {
        Vector3i blockPosition = packet.getBlockPosition();

        if (packet.isDroppingBook()) {
            Block blockLectern = player.getLevel().getBlock(blockPosition);
            if (blockLectern instanceof BlockLectern) {
                ((BlockLectern) blockLectern).dropBook(player);
            }
        } else {
            BlockEntity blockEntity = player.getLevel().getBlockEntity(blockPosition);
            if (blockEntity instanceof Lectern) {
                Lectern lectern = (Lectern) blockEntity;
                LecternPageChangeEvent lecternPageChangeEvent = new LecternPageChangeEvent(player, lectern, packet.getPage());
                player.getServer().getPluginManager().callEvent(lecternPageChangeEvent);
                if (!lecternPageChangeEvent.isCancelled()) {
                    lectern.setPage(lecternPageChangeEvent.getNewRawPage());
                    lectern.spawnToAll();
                    Block blockLectern = lectern.getBlock();
                    if (blockLectern instanceof BlockLectern) {
                        ((BlockLectern) blockLectern).executeRedstonePulse();
                    }
                }
            }
        }
        return true;
    }

    @Override
    public boolean handle(SetLocalPlayerAsInitializedPacket packet) {
        if (player.isInitialized()) {
            return true;
        }
        player.setInitialized(true);
        PlayerJoinEvent playerJoinEvent = new PlayerJoinEvent(player,
                new TranslationContainer(TextFormat.YELLOW + "%multiplayer.player.joined", player.getDisplayName())
        );

        player.getServer().getPluginManager().callEvent(playerJoinEvent);

        if (playerJoinEvent.getJoinMessage().toString().trim().length() > 0) {
            player.getServer().broadcastMessage(playerJoinEvent.getJoinMessage());
        }
        return true;
    }
}
