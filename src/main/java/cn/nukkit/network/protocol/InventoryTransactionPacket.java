package cn.nukkit.network.protocol;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAir;
import cn.nukkit.block.BlockDoor;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySpawnable;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.player.PlayerInteractEntityEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerItemConsumeEvent;
import cn.nukkit.inventory.transaction.CraftingTransaction;
import cn.nukkit.inventory.transaction.InventoryTransaction;
import cn.nukkit.inventory.transaction.action.InventoryAction;
import cn.nukkit.inventory.transaction.data.ReleaseItemData;
import cn.nukkit.inventory.transaction.data.TransactionData;
import cn.nukkit.inventory.transaction.data.UseItemData;
import cn.nukkit.inventory.transaction.data.UseItemOnEntityData;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemBucket;
import cn.nukkit.item.ItemGlassBottle;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.item.food.Food;
import cn.nukkit.level.GameRule;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.types.NetworkInventoryAction;
import cn.nukkit.potion.Potion;

import java.util.*;

import static cn.nukkit.Player.SURVIVAL;
import static cn.nukkit.entity.Entity.DATA_FLAGS;
import static cn.nukkit.entity.Entity.DATA_FLAG_ACTION;

public class InventoryTransactionPacket extends DataPacket {

    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_MISMATCH = 1;
    public static final int TYPE_USE_ITEM = 2;
    public static final int TYPE_USE_ITEM_ON_ENTITY = 3;
    public static final int TYPE_RELEASE_ITEM = 4;

    public static final int USE_ITEM_ACTION_CLICK_BLOCK = 0;
    public static final int USE_ITEM_ACTION_CLICK_AIR = 1;
    public static final int USE_ITEM_ACTION_BREAK_BLOCK = 2;

    public static final int RELEASE_ITEM_ACTION_RELEASE = 0; //bow shoot
    public static final int RELEASE_ITEM_ACTION_CONSUME = 1; //eat food, drink potion

    public static final int USE_ITEM_ON_ENTITY_ACTION_INTERACT = 0;
    public static final int USE_ITEM_ON_ENTITY_ACTION_ATTACK = 1;


    public static final int ACTION_MAGIC_SLOT_DROP_ITEM = 0;
    public static final int ACTION_MAGIC_SLOT_PICKUP_ITEM = 1;

    public static final int ACTION_MAGIC_SLOT_CREATIVE_DELETE_ITEM = 0;
    public static final int ACTION_MAGIC_SLOT_CREATIVE_CREATE_ITEM = 1;

    public int transactionType;
    public NetworkInventoryAction[] actions;
    public TransactionData transactionData;

    /**
     * NOTE: THIS FIELD DOES NOT EXIST IN THE PROTOCOL, it's merely used for convenience for PocketMine-MP to easily
     * determine whether we're doing a crafting transaction.
     */
    public boolean isCraftingPart = false;

    @Override
    public byte pid() {
        return ProtocolInfo.INVENTORY_TRANSACTION_PACKET;
    }

    @Override
    public void encode() {
        this.reset();
        this.putUnsignedVarInt(this.transactionType);

        this.putUnsignedVarInt(this.actions.length);
        for (NetworkInventoryAction action : this.actions) {
            action.write(this);
        }

        switch (this.transactionType) {
            case TYPE_NORMAL:
            case TYPE_MISMATCH:
                break;
            case TYPE_USE_ITEM:
                UseItemData useItemData = (UseItemData) this.transactionData;

                this.putUnsignedVarInt(useItemData.actionType);
                this.putBlockVector3(useItemData.blockPos);
                this.putBlockFace(useItemData.face);
                this.putVarInt(useItemData.hotbarSlot);
                this.putSlot(useItemData.itemInHand);
                this.putVector3f(useItemData.playerPos.asVector3f());
                this.putVector3f(useItemData.clickPos);
                break;
            case TYPE_USE_ITEM_ON_ENTITY:
                UseItemOnEntityData useItemOnEntityData = (UseItemOnEntityData) this.transactionData;

                this.putEntityRuntimeId(useItemOnEntityData.entityRuntimeId);
                this.putUnsignedVarInt(useItemOnEntityData.actionType);
                this.putVarInt(useItemOnEntityData.hotbarSlot);
                this.putSlot(useItemOnEntityData.itemInHand);
                this.putVector3f(useItemOnEntityData.vector1.asVector3f());
                this.putVector3f(useItemOnEntityData.vector2.asVector3f());
                break;
            case TYPE_RELEASE_ITEM:
                ReleaseItemData releaseItemData = (ReleaseItemData) this.transactionData;

                this.putUnsignedVarInt(releaseItemData.actionType);
                this.putVarInt(releaseItemData.hotbarSlot);
                this.putSlot(releaseItemData.itemInHand);
                this.putVector3f(releaseItemData.headRot.asVector3f());
                break;
            default:
                throw new RuntimeException("Unknown transaction type " + this.transactionType);
        }
    }

    @Override
    public void decode() {
        this.transactionType = (int) this.getUnsignedVarInt();

        this.actions = new NetworkInventoryAction[(int) this.getUnsignedVarInt()];
        for (int i = 0; i < this.actions.length; i++) {
            this.actions[i] = new NetworkInventoryAction().read(this);
        }

        switch (this.transactionType) {
            case TYPE_NORMAL:
            case TYPE_MISMATCH:
                //Regular ComplexInventoryTransaction doesn't read any extra data
                break;
            case TYPE_USE_ITEM:
                UseItemData itemData = new UseItemData();

                itemData.actionType = (int) this.getUnsignedVarInt();
                itemData.blockPos = this.getBlockVector3();
                itemData.face = this.getBlockFace();
                itemData.hotbarSlot = this.getVarInt();
                itemData.itemInHand = this.getSlot();
                itemData.playerPos = this.getVector3f().asVector3();
                itemData.clickPos = this.getVector3f();

                this.transactionData = itemData;
                break;
            case TYPE_USE_ITEM_ON_ENTITY:
                UseItemOnEntityData useItemOnEntityData = new UseItemOnEntityData();

                useItemOnEntityData.entityRuntimeId = this.getEntityRuntimeId();
                useItemOnEntityData.actionType = (int) this.getUnsignedVarInt();
                useItemOnEntityData.hotbarSlot = this.getVarInt();
                useItemOnEntityData.itemInHand = this.getSlot();
                useItemOnEntityData.vector1 = this.getVector3f().asVector3();
                useItemOnEntityData.vector2 = this.getVector3f().asVector3();

                this.transactionData = useItemOnEntityData;
                break;
            case TYPE_RELEASE_ITEM:
                ReleaseItemData releaseItemData = new ReleaseItemData();

                releaseItemData.actionType = (int) getUnsignedVarInt();
                releaseItemData.hotbarSlot = getVarInt();
                releaseItemData.itemInHand = getSlot();
                releaseItemData.headRot = this.getVector3f().asVector3();

                this.transactionData = releaseItemData;
                break;
            default:
                throw new RuntimeException("Unknown transaction type " + this.transactionType);
        }
    }

    @Override
    public void handle(Player player) {
        if (player.isSpectator()) {
            player.sendAllInventories();
            return;
        }
        
        List<InventoryAction> actions = new ArrayList<>();
        for (NetworkInventoryAction networkInventoryAction : this.actions) {
            InventoryAction a = networkInventoryAction.createInventoryAction(player);

            if (a == null) {
                player.getServer().getLogger().debug("Unmatched inventory action from " + player.getName() + ": " + networkInventoryAction);
                player.sendAllInventories();
                return;
            }

            actions.add(a);
        }

        if (this.isCraftingPart) {
            if (player.craftingTransaction == null) {
                player.craftingTransaction = new CraftingTransaction(player, actions);
            } else {
                for (InventoryAction action : actions) {
                    player.craftingTransaction.addAction(action);
                }
            }

            if (player.craftingTransaction.getPrimaryOutput() != null) {
                //we get the actions for this in several packets, so we can't execute it until we get the result

                player.craftingTransaction.execute();
                player.craftingTransaction = null;
            }

            return;
        } else if (player.craftingTransaction != null) {
            player.server.getLogger().debug("Got unexpected normal inventory action with incomplete crafting transaction from " + player.getName() + ", refusing to execute crafting");
            player.craftingTransaction = null;
        }

        switch (this.transactionType) {
            case InventoryTransactionPacket.TYPE_NORMAL:
                InventoryTransaction transaction = new InventoryTransaction(player, actions);

                if (!transaction.execute()) {
                    player.server.getLogger().debug("Failed to execute inventory transaction from " + player.getName() + " with actions: " + Arrays.toString(this.actions));
                    return; //oops!
                }

                //TODO: fix achievement for getting iron from furnace

                return;
            case InventoryTransactionPacket.TYPE_MISMATCH:
                if (this.actions.length > 0) {
                    player.server.getLogger().debug("Expected 0 actions for mismatch, got " + this.actions.length + ", " + Arrays.toString(this.actions));
                }
                player.sendAllInventories();

                return;
            case InventoryTransactionPacket.TYPE_USE_ITEM:
                UseItemData useItemData = (UseItemData) this.transactionData;

                BlockVector3 blockVector = useItemData.blockPos;
                BlockFace face = useItemData.face;

                int type = useItemData.actionType;
                switch (type) {
                    case InventoryTransactionPacket.USE_ITEM_ACTION_CLICK_BLOCK:
                        player.setDataFlag(DATA_FLAGS, DATA_FLAG_ACTION, false);

                        if (player.canInteract(blockVector.add(0.5, 0.5, 0.5), player.isCreative() ? 13 : 7)) {
                            if (player.isCreative()) {
                                Item i = player.inventory.getItemInHand();
                                if (player.level.useItemOn(blockVector.asVector3(), i, face, useItemData.clickPos.x, useItemData.clickPos.y, useItemData.clickPos.z, player) != null) {
                                    return;
                                }
                            } else if (player.inventory.getItemInHand().equals(useItemData.itemInHand)) {
                                Item i = player.inventory.getItemInHand();
                                Item oldItem = i.clone();
                                //TODO: Implement adventure mode checks
                                if ((i = player.level.useItemOn(blockVector.asVector3(), i, face, useItemData.clickPos.x, useItemData.clickPos.y, useItemData.clickPos.z, player)) != null) {
                                    if (!i.equals(oldItem) || i.getCount() != oldItem.getCount()) {
                                        player.inventory.setItemInHand(i);
                                        player.inventory.sendHeldItem(player.getViewers().values());
                                    }
                                    return;
                                }
                            }
                        }

                        player.inventory.sendHeldItem(player);

                        if (blockVector.distanceSquared(player) > 10000) {
                            return;
                        }

                        Block target = player.level.getBlock(blockVector.asVector3());
                        Block block = target.getSide(face);

                        player.level.sendBlocks(new Player[]{player}, new Block[]{target, block}, UpdateBlockPacket.FLAG_ALL_PRIORITY);

                        if (target instanceof BlockDoor) {
                            BlockDoor door = (BlockDoor) target;

                            Block part;

                            if ((door.getDamage() & 0x08) > 0) { //up
                                part = target.down();

                                if (part.getId() == target.getId()) {
                                    target = part;

                                    player.level.sendBlocks(new Player[]{player}, new Block[]{target}, UpdateBlockPacket.FLAG_ALL_PRIORITY);
                                }
                            }
                        }
                        return;
                    case InventoryTransactionPacket.USE_ITEM_ACTION_BREAK_BLOCK:
                        if (!player.spawned || !player.isAlive()) {
                            return;
                        }

                        player.resetCraftingGridType();

                        Item i = player.getInventory().getItemInHand();

                        Item oldItem = i.clone();

                        if (player.canInteract(blockVector.add(0.5, 0.5, 0.5), player.isCreative() ? 13 : 7) && (i = player.level.useBreakOn(blockVector.asVector3(), i, player, true)) != null) {
                            if (player.isSurvival()) {
                                player.getFoodData().updateFoodExpLevel(0.025);
                                if (!i.equals(oldItem) || i.getCount() != oldItem.getCount()) {
                                    player.inventory.setItemInHand(i);
                                    player.inventory.sendHeldItem(player.getViewers().values());
                                }
                            }
                            return;
                        }

                        player.inventory.sendContents(player);
                        target = player.level.getBlock(blockVector.asVector3());
                        BlockEntity blockEntity = player.level.getBlockEntity(blockVector.asVector3());

                        player.level.sendBlocks(new Player[]{player}, new Block[]{target}, UpdateBlockPacket.FLAG_ALL_PRIORITY);

                        player.inventory.sendHeldItem(player);

                        if (blockEntity instanceof BlockEntitySpawnable) {
                            ((BlockEntitySpawnable) blockEntity).spawnTo(player);
                        }

                        return;
                    case InventoryTransactionPacket.USE_ITEM_ACTION_CLICK_AIR:
                        Vector3 directionVector = player.getDirectionVector();

                        Item item;
                        if (player.isCreative()) {
                            item = player.inventory.getItemInHand();
                        } else if (!player.inventory.getItemInHand().equals(useItemData.itemInHand)) {
                            player.inventory.sendHeldItem(player);
                            return;
                        } else {
                            item = player.inventory.getItemInHand();
                        }

                        PlayerInteractEvent interactEvent = new PlayerInteractEvent(player, item, directionVector, face, PlayerInteractEvent.Action.RIGHT_CLICK_AIR);

                        player.server.getPluginManager().callEvent(interactEvent);

                        if (interactEvent.isCancelled()) {
                            player.inventory.sendHeldItem(player);
                            return;
                        }

                        if (item.onClickAir(player, directionVector) && player.isSurvival()) {
                            player.inventory.setItemInHand(item);
                        }

                        player.setDataFlag(DATA_FLAGS, DATA_FLAG_ACTION, true);
                        player.startAction = player.server.getTick();

                        return;
                    default:
                        //unknown
                        break;
                }
                break;
            case InventoryTransactionPacket.TYPE_USE_ITEM_ON_ENTITY:
                UseItemOnEntityData useItemOnEntityData = (UseItemOnEntityData) this.transactionData;

                Entity target = player.level.getEntity(useItemOnEntityData.entityRuntimeId);
                if (target == null) {
                    return;
                }

                type = useItemOnEntityData.actionType;

                if (!useItemOnEntityData.itemInHand.equalsExact(player.inventory.getItemInHand())) {
                    player.inventory.sendHeldItem(player);
                }

                Item item = player.inventory.getItemInHand();

                switch (type) {
                    case InventoryTransactionPacket.USE_ITEM_ON_ENTITY_ACTION_INTERACT:
                        PlayerInteractEntityEvent playerInteractEntityEvent = new PlayerInteractEntityEvent(player, target, item);
                        if (player.isSpectator()) playerInteractEntityEvent.setCancelled();
                        player.getServer().getPluginManager().callEvent(playerInteractEntityEvent);

                        if (playerInteractEntityEvent.isCancelled()) {
                            break;
                        }

                        if (target.onInteract(player, item) && player.isSurvival()) {
                            if (item.isTool()) {
                                if (item.useOn(target) && item.getDamage() >= item.getMaxDurability()) {
                                    item = new ItemBlock(new BlockAir());
                                }
                            } else {
                                if (item.count > 1) {
                                    item.count--;
                                } else {
                                    item = new ItemBlock(new BlockAir());
                                }
                            }

                            player.inventory.setItemInHand(item);
                        }
                        break;
                    case InventoryTransactionPacket.USE_ITEM_ON_ENTITY_ACTION_ATTACK:
                        float itemDamage = item.getAttackDamage();

                        for (Enchantment enchantment : item.getEnchantments()) {
                            itemDamage += enchantment.getDamageBonus(target);
                        }

                        Map<EntityDamageEvent.DamageModifier, Float> damage = new EnumMap<>(EntityDamageEvent.DamageModifier.class);
                        damage.put(EntityDamageEvent.DamageModifier.BASE, itemDamage);

                        if (!player.canInteract(target, player.isCreative() ? 8 : 5)) {
                            break;
                        } else if (target instanceof Player) {
                            if ((((Player) target).getGamemode() & 0x01) > 0) {
                                break;
                            } else if (!player.server.getPropertyBoolean("pvp") || player.server.getDifficulty() == 0) {
                                break;
                            }
                        }

                        EntityDamageByEntityEvent entityDamageByEntityEvent = new EntityDamageByEntityEvent(player, target, EntityDamageEvent.DamageCause.ENTITY_ATTACK, damage);
                        if (player.isSpectator()) entityDamageByEntityEvent.setCancelled();
                        if ((target instanceof Player) && !player.level.getGameRules().getBoolean(GameRule.PVP)) {
                            entityDamageByEntityEvent.setCancelled();
                        }

                        if (!target.attack(entityDamageByEntityEvent)) {
                            if (item.isTool() && player.isSurvival()) {
                                player.inventory.sendContents(player);
                            }
                            break;
                        }

                        for (Enchantment enchantment : item.getEnchantments()) {
                            enchantment.doPostAttack(player, target);
                        }

                        if (item.isTool() && player.isSurvival()) {
                            if (item.useOn(target) && item.getDamage() >= item.getMaxDurability()) {
                                player.inventory.setItemInHand(new ItemBlock(new BlockAir()));
                            } else {
                                player.inventory.setItemInHand(item);
                            }
                        }
                        return;
                    default:
                        break; //unknown
                }

                break;
            case InventoryTransactionPacket.TYPE_RELEASE_ITEM:
                if (player.isSpectator()) {
                    player.sendAllInventories();
                    return;
                }
                ReleaseItemData releaseItemData = (ReleaseItemData) this.transactionData;

                try {
                    type = releaseItemData.actionType;
                    switch (type) {
                        case InventoryTransactionPacket.RELEASE_ITEM_ACTION_RELEASE:
                            if (player.isUsingItem()) {
                                item = player.inventory.getItemInHand();
                                if (item.onReleaseUsing(player)) {
                                    player.inventory.setItemInHand(item);
                                }
                            } else {
                                player.inventory.sendContents(player);
                            }
                            return;
                        case InventoryTransactionPacket.RELEASE_ITEM_ACTION_CONSUME:
                            Item itemInHand = player.inventory.getItemInHand();
                            PlayerItemConsumeEvent consumeEvent = new PlayerItemConsumeEvent(player, itemInHand);

                            if (itemInHand.getId() == Item.POTION) {
                                player.server.getPluginManager().callEvent(consumeEvent);
                                if (consumeEvent.isCancelled()) {
                                    player.inventory.sendContents(player);
                                    break;
                                }
                                Potion potion = Potion.getPotion(itemInHand.getDamage()).setSplash(false);

                                if (player.getGamemode() == SURVIVAL) {
                                    --itemInHand.count;
                                    player.inventory.setItemInHand(itemInHand);
                                    player.inventory.addItem(new ItemGlassBottle());
                                }

                                if (potion != null) {
                                    potion.applyPotion(player);
                                }

                            } else if (itemInHand.getId() == Item.BUCKET && itemInHand.getDamage() == 1) { //milk
                                player.server.getPluginManager().callEvent(consumeEvent);
                                if (consumeEvent.isCancelled()) {
                                    player.inventory.sendContents(player);
                                    break;
                                }

                                EntityEventPacket eventPacket = new EntityEventPacket();
                                eventPacket.eid = player.getId();
                                eventPacket.event = EntityEventPacket.USE_ITEM;
                                player.dataPacket(eventPacket);
                                Server.broadcastPacket(player.getViewers().values(), eventPacket);

                                if (player.isSurvival()) {
                                    itemInHand.count--;
                                    player.inventory.setItemInHand(itemInHand);
                                    player.inventory.addItem(new ItemBucket());
                                }

                                player.removeAllEffects();
                            } else {
                                player.server.getPluginManager().callEvent(consumeEvent);
                                if (consumeEvent.isCancelled()) {
                                    player.inventory.sendContents(player);
                                    break;
                                }

                                Food food = Food.getByRelative(itemInHand);
                                if (food != null && food.eatenBy(player)) --itemInHand.count;
                                player.inventory.setItemInHand(itemInHand);
                            }
                            return;
                        default:
                            break;
                    }
                } finally {
                    player.setUsingItem(false);
                }
                break;
            default:
                player.inventory.sendContents(player);
                break;
        }
    }
}
