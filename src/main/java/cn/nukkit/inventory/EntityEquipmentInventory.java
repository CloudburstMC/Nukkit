package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.MobEquipmentPacket;
import cn.nukkit.entity.item.EntityArmorStand;

import java.util.HashSet;
import java.util.Set;

public class EntityEquipmentInventory extends BaseInventory {

    private EntityLiving entityLiving;
    private final Set<Player> viewers = new HashSet<>();

    private int MAINHAND = 0;
    private int  OFFHAND = 1;

    public EntityEquipmentInventory( EntityArmorStand entityArmorStand ) {
        super( entityArmorStand, InventoryType.PLAYER );
        this.entityLiving = entityArmorStand;
    }


    @Override
    public String getName() {
        return "Entity Equipment";
    }

    @Override
    public int getSize() {
        return 2;
    }

    @Override
    public InventoryHolder getHolder() {
        return this.holder;
    }

    @Override
    public void sendSlot( int index, Player... players ) {
        for ( Player player : players ) {
            this.sendSlot( index, player );
        }
    }

    @Override
    public void sendSlot( int index, Player player ) {
        MobEquipmentPacket mobEquipmentPacket = new MobEquipmentPacket();
        mobEquipmentPacket.eid = this.entityLiving.getId();
        mobEquipmentPacket.inventorySlot = mobEquipmentPacket.hotbarSlot = index;
        mobEquipmentPacket.item = this.getItem( index );
        player.dataPacket( mobEquipmentPacket );
    }

    @Override
    public Set<Player> getViewers() {
        return this.viewers;
    }

    @Override
    public boolean open( Player who ) {
        return this.viewers.add( who );
    }

    @Override
    public void onClose( Player who ) {
        this.viewers.remove( who );
    }

    public Item getItemInHand() {
        return this.getItem( this.MAINHAND );
    }

    public Item getOffHandItem() {
        return this.getItem( this.OFFHAND );
    }

    public boolean setItemInHand( Item item, boolean send ) {
        return this.setItem( this.MAINHAND, item, send );
    }

    public boolean setOffhandItem( Item item, boolean send ) {
        return this.setItem( this.OFFHAND, item, send );
    }

    @Override
    public void sendContents( Player target ) {
        this.sendSlot( this.MAINHAND, target );
        this.sendSlot( this.OFFHAND, target );
    }

    @Override
    public void sendContents( Player... target ) {
        for ( Player player : target ) {
            this.sendContents( player );
        }
    }
}
