package cn.nukkit.entity.item;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemArmorStand;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.particle.DestroyBlockParticle;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.LevelEventPacket;

public class EntityArmorStand extends EntityLiving {

    public static final int NETWORK_ID = 61;

    public static final String TAG_POSE_INDEX = "PoseIndex";

    @Override
    public int getNetworkId() {
        return 61;
    }

    public EntityArmorStand( FullChunk chunk, CompoundTag nbt ) {
        super( chunk, nbt );

        if( nbt.contains( TAG_POSE_INDEX ) ){
            this.setPose( nbt.getInt( TAG_POSE_INDEX ) );
        }
    }

    @Override
    protected void initEntity() {
        this.setMaxHealth( 6 );
        this.setHealth( 6 );
        this.setImmobile( true );

        super.initEntity();
    }

    @Override
    public float getHeight() {
        return 1.975f;
    }

    @Override
    public float getWidth() {
        return 0.5f;
    }

    @Override
    protected float getGravity() {
        return 0.04f;
    }

    @Override
    public boolean onInteract( Player player, Item item ) {
        if(player.isSneaking()){
            if(this.getPose() >= 12){
                this.setPose( 0 );
            }else{
                this.setPose( this.getPose() + 1 );
            }
            return true;
        }
        return false;
    }

    public int getPose(){
        return this.dataProperties.getInt( Entity.DATA_ARMOR_STAND_POSE_INDEX );
    }

    public void setPose( int pose ) {
        this.dataProperties.putInt( Entity.DATA_ARMOR_STAND_POSE_INDEX, pose );
    }

    @Override
    public void fall( float fallDistance ) {
        super.fall( fallDistance );

        this.level.addLevelSoundEvent( this, LevelEventPacket.EVENT_SOUND_ARMOR_STAND_FALL );
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.namedTag.putInt( TAG_POSE_INDEX, this.getPose() );
    }

    @Override
    public boolean attack( EntityDamageEvent source ) {
        if(source instanceof EntityDamageByEntityEvent){
            EntityDamageByEntityEvent entityDamageByEntityEvent = (EntityDamageByEntityEvent) source;
            Entity damager = entityDamageByEntityEvent.getDamager();
            if(damager instanceof Player){
                Player damagerPlayer = (Player) damager;
                if(damagerPlayer.isCreative()){
                    this.startDeathAnimation();
                    this.kill();
                    return true;
                }else{
                    if (level.getGameRules().getBoolean( GameRule.DO_ENTITY_DROPS)) {
                        this.level.dropItem(this, new ItemArmorStand());
                    }
                }
            }

        }

        if(source.getCause() == EntityDamageEvent.DamageCause.CONTACT){
            source.setCancelled( true );
        }

        super.attack( source );

        if(!source.isCancelled()){
            this.startDeathAnimation();
            this.kill();
        }

        return false;
    }

    public void startDeathAnimation(){
        this.level.addParticle( new DestroyBlockParticle( this, Block.get( Block.WOODEN_PLANKS ) ) );
    }

    @Override
    public String getName() {
        return "ArmorStand";
    }

    public boolean canBePushed(){
        return false;
    }

}
