package cn.nukkit.network.protocol;

import cn.nukkit.entity.Attribute;
import cn.nukkit.entity.data.EntityDataMap;
import cn.nukkit.network.protocol.types.EntityLink;
import cn.nukkit.utils.Binary;
import cn.nukkit.utils.Identifier;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString
public class AddEntityPacket extends DataPacket {
    public static final short NETWORK_ID = ProtocolInfo.ADD_ENTITY_PACKET;

//    public static ImmutableMap<Integer, String> LEGACY_IDS = ImmutableMap.<Integer, String>builder()
//            .put(51, "minecraft:npc")
//            .put(Blaze.NETWORK_ID, "minecraft:blaze")
//            .put(CaveSpider.NETWORK_ID, "minecraft:cave_spider")
//            .put(Creeper.NETWORK_ID, "minecraft:creeper")
//            .put(Wolf.NETWORK_ID, "minecraft:wolf")
//            .put(Drowned.NETWORK_ID, "minecraft:drowned")
//            .put(Mooshroom.NETWORK_ID, "minecraft:mooshroom")
//            .put(Cow.NETWORK_ID, "minecraft:cow")
//            .put(Chicken.NETWORK_ID, "minecraft:chicken")
//            .put(107, "minecraft:balloon")
//            .put(Llama.NETWORK_ID, "minecraft:llama")
//            .put(20, "minecraft:iron_golem")
//            .put(Rabbit.NETWORK_ID, "minecraft:rabbit")
//            .put(21, "minecraft:snow_golem")
//            .put(100, "minecraft:command_block_minecart")
//            .put(61, "minecraft:armor_stand")
//            .put(DroppedItem.NETWORK_ID, "minecraft:item")
//            .put(Tnt.NETWORK_ID, "minecraft:tnt")
//            .put(FallingBlock.NETWORK_ID, "minecraft:falling_block")
//            .put(XpBottle.NETWORK_ID, "minecraft:xp_bottle")
//            .put(XpOrb.NETWORK_ID, "minecraft:xp_orb")
//            .put(70, "minecraft:eye_of_ender_signal")
//            .put(EnderCrystal.NETWORK_ID, "minecraft:ender_crystal")
//            .put(76, "minecraft:shulker_bullet")
//            .put(FishingHook.NETWORK_ID, "minecraft:fishing_hook")
//            .put(79, "minecraft:dragon_fireball")
//            .put(Arrow.NETWORK_ID, "minecraft:arrow")
//            .put(Snowball.NETWORK_ID, "minecraft:snowball")
//            .put(Egg.NETWORK_ID, "minecraft:egg")
//            .put(Painting.NETWORK_ID, "minecraft:painting")
//            .put(ThrownTrident.NETWORK_ID, "minecraft:thrown_trident")
//            .put(85, "minecraft:fireball")
//            .put(SplashPotion.NETWORK_ID, "minecraft:splash_potion")
//            .put(EnderPearl.NETWORK_ID, "minecraft:ender_pearl")
//            .put(88, "minecraft:leash_knot")
//            .put(89, "minecraft:wither_skull")
//            .put(91, "minecraft:wither_skull_dangerous")
//            .put(Boat.NETWORK_ID, "minecraft:boat")
//            .put(LightningBolt.NETWORK_ID, "minecraft:lightning_bolt")
//            .put(94, "minecraft:small_fireball")
//            .put(102, "minecraft:llama_spit")
//            .put(95, "minecraft:area_effect_cloud")
//            .put(101, "minecraft:lingering_potion")
//            .put(FireworksRocket.NETWORK_ID, "minecraft:fireworks_rocket")
//            .put(103, "minecraft:evocation_fang")
//            .put(104, "minecraft:evocation_illager")
//            .put(Vex.NETWORK_ID, "minecraft:vex")
//            .put(56, "minecraft:agent")
//            .put(106, "minecraft:ice_bomb")
//            .put(Phantom.NETWORK_ID, "minecraft:phantom")
//            .put(62, "minecraft:tripod_camera")
//            .put(Pillager.NETWORK_ID, "minecraft:pillager")
//            .put(WanderingTrader.NETWORK_ID, "minecraft:wandering_trader")
//            .put(Ravager.NETWORK_ID, "minecraft:ravager")
//            .put(DeprecatedVillager.NETWORK_ID, "minecraft:villager_v2")
//            .put(DeprecatedZombieVillager.NETWORK_ID, "minecraft:zombie_villager_v2")
//            .put(121, "minecraft:fox")
//            .put(122, "minecraft:bee")
//            .build();

    @Override
    public short pid() {
        return NETWORK_ID;
    }

    public long entityUniqueId;
    public long entityRuntimeId;
    public Identifier type;
    public float x;
    public float y;
    public float z;
    public float speedX = 0f;
    public float speedY = 0f;
    public float speedZ = 0f;
    public float yaw;
    public float pitch;
    public float headYaw;
    public EntityDataMap dataMap = new EntityDataMap();
    public Attribute[] attributes = new Attribute[0];
    public EntityLink[] links = new EntityLink[0];

    @Override
    protected void decode(ByteBuf buffer) {

    }

    @Override
    protected void encode(ByteBuf buffer) {
        Binary.writeEntityUniqueId(buffer, this.entityUniqueId);
        Binary.writeEntityRuntimeId(buffer, this.entityRuntimeId);
        Binary.writeString(buffer, this.type.toString());
        Binary.writeVector3f(buffer, this.x, this.y, this.z);
        Binary.writeVector3f(buffer, this.speedX, this.speedY, this.speedZ);
        buffer.writeFloatLE(this.pitch);
        buffer.writeFloatLE(this.yaw);
        buffer.writeFloatLE(this.headYaw);
        Binary.writeAttributes(buffer, this.attributes);
        Binary.writeEntityData(buffer, this.dataMap);
        Binary.writeUnsignedVarInt(buffer, this.links.length);
        for (EntityLink link : links) {
            Binary.writeEntityLink(buffer, link);
        }
    }
}
