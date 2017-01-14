package cn.nukkit.network.protocol;


import cn.nukkit.math.Vector3f;

public class LevelSoundEventPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.LEVEL_SOUND_EVENT_PACKET;

    public static final int SOUND_ITEM_USE_ON = 0;
    public static final int SOUND_HIT = 1;
    public static final int SOUND_STEP = 2;
    public static final int SOUND_JUMP = 3;
    public static final int SOUND_BREAK = 4;
    public static final int SOUND_PLACE = 5;
    public static final int SOUND_HEAVY_STEP = 6;
    public static final int SOUND_GALLOP = 7;
    public static final int SOUND_FALL = 8;
    public static final int SOUND_AMBIENT = 9;
    public static final int SOUND_AMBIENT_BABY = 10;
    public static final int SOUND_AMBIENT_IN_WATER = 11;
    public static final int SOUND_BREATHE = 12;
    public static final int SOUND_DEATH = 13;
    public static final int SOUND_DEATH_IN_WATER = 14;
    public static final int SOUND_DEATH_TO_ZOMBIE = 15;
    public static final int SOUND_HURT = 16;
    public static final int SOUND_HURT_IN_WATER = 17;
    public static final int SOUND_MAD = 18;
    public static final int SOUND_BOOST = 19;
    public static final int SOUND_BOW = 20;
    public static final int SOUND_SQUISH_BIG = 21;
    public static final int SOUND_SQUISH_SMALL = 22;
    public static final int SOUND_FALL_BIG = 23;
    public static final int SOUND_FALL_SMALL = 24;
    public static final int SOUND_SPLASH = 25;
    public static final int SOUND_FIZZ = 26;
    public static final int SOUND_FLAP = 27;
    public static final int SOUND_SWIM = 28;
    public static final int SOUND_DRINK = 29;
    public static final int SOUND_EAT = 30;
    public static final int SOUND_TAKEOFF = 31;
    public static final int SOUND_SHAKE = 32;
    public static final int SOUND_PLOP = 33;
    public static final int SOUND_LAND = 34;
    public static final int SOUND_SADDLE = 35;
    public static final int SOUND_ARMOR = 36;
    public static final int SOUND_ADD_CHEST = 37;
    public static final int SOUND_THROW = 38;
    public static final int SOUND_ATTACK = 39;
    public static final int SOUND_ATTACK_NODAMAGE = 40;
    public static final int SOUND_WARN = 41;
    public static final int SOUND_SHEAR = 42;
    public static final int SOUND_MILK = 43;
    public static final int SOUND_THUNDER = 44;
    public static final int SOUND_EXPLODE = 45;
    public static final int SOUND_FIRE = 46;
    public static final int SOUND_IGNITE = 47;
    public static final int SOUND_FUSE = 48;
    public static final int SOUND_STARE = 49;
    public static final int SOUND_SPAWN = 50;
    public static final int SOUND_SHOOT = 51;
    public static final int SOUND_BREAK_BLOCK = 52;
    public static final int SOUND_REMEDY = 53;
    public static final int SOUND_UNFECT = 54;
    public static final int SOUND_LEVELUP = 55;
    public static final int SOUND_BOW_HIT = 56;
    public static final int SOUND_BULLET_HIT = 57;
    public static final int SOUND_EXTINGUISH_FIRE = 58;
    public static final int SOUND_ITEM_FIZZ = 59;
    public static final int SOUND_CHEST_OPEN = 60;
    public static final int SOUND_CHEST_CLOSED = 61;
    public static final int SOUND_POWER_ON = 62;
    public static final int SOUND_POWER_OFF = 63;
    public static final int SOUND_ATTACH = 64;
    public static final int SOUND_DETACH = 65;
    public static final int SOUND_DENY = 66;
    public static final int SOUND_TRIPOD = 67;
    public static final int SOUND_POP = 68;
    public static final int SOUND_DROP_SLOT = 69;
    public static final int SOUND_NOTE = 70;
    public static final int SOUND_THORNS = 71;
    public static final int SOUND_PISTON_IN = 72;
    public static final int SOUND_PISTON_OUT = 73;
    public static final int SOUND_PORTAL = 74;
    public static final int SOUND_WATER = 75;
    public static final int SOUND_LAVA_POP = 76;
    public static final int SOUND_LAVA = 77;
    public static final int SOUND_BURP = 78;
    public static final int SOUND_BUCKET_FILL_WATER = 79;
    public static final int SOUND_BUCKET_FILL_LAVA = 80;
    public static final int SOUND_BUCKET_EMPTY_WATER = 81;
    public static final int SOUND_BUCKET_EMPTY_LAVA = 82;
    public static final int SOUND_GUARDIAN_FLOP = 83;
    public static final int SOUND_ELDERGUARDIAN_CURSE = 84;
    public static final int SOUND_MOB_WARNING = 85;
    public static final int SOUND_MOB_WARNING_BABY = 86;
    public static final int SOUND_TELEPORT = 87;
    public static final int SOUND_SHULKER_OPEN = 88;
    public static final int SOUND_SHULKER_CLOSE = 89;
    public static final int SOUND_DEFAULT = 90;
    public static final int SOUND_UNDEFINED = 91;

    public int type;
    public float x;
    public float y;
    public float z;
    public int case1;
    public int case2;
    public boolean unknownBool;
    public boolean unknownBool2;

    @Override
    public void decode() {
        this.type = this.getVarInt();
        Vector3f v = this.getVector3f();
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.case1 = this.getVarInt();
        this.case2 = this.getVarInt();
        this.unknownBool = this.getBoolean();
        this.unknownBool2 = this.getBoolean();
    }

    @Override
    public void encode() {
        this.reset();
        this.putVarInt(this.type);
        this.putVector3f(this.x, this.y, this.z);
        this.putVarInt(this.case1);
        this.putVarInt(this.case2);
        this.putBoolean(this.unknownBool);
        this.putBoolean(this.unknownBool2);
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }
}
