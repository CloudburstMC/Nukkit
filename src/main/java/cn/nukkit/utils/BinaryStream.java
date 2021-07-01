package cn.nukkit.utils;

import cn.nukkit.block.Block;
import cn.nukkit.entity.Attribute;
import cn.nukkit.entity.data.*;
import cn.nukkit.item.*;
import cn.nukkit.item.RuntimeItemMapping.LegacyEntry;
import cn.nukkit.item.RuntimeItemMapping.RuntimeEntry;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.GameRules;
import cn.nukkit.level.GlobalBlockPalette;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.network.LittleEndianByteBufInputStream;
import cn.nukkit.network.LittleEndianByteBufOutputStream;
import cn.nukkit.network.protocol.types.CommandOriginData;
import cn.nukkit.network.protocol.types.EntityLink;
import it.unimi.dsi.fastutil.io.FastByteArrayInputStream;
import io.netty.buffer.AbstractByteBufAllocator;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BinaryStream {

    public int offset;
    private byte[] buffer;
    private int count;

    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    public BinaryStream() {
        this.buffer = new byte[32];
        this.offset = 0;
        this.count = 0;
    }

    public BinaryStream(byte[] buffer) {
        this(buffer, 0);
    }

    public BinaryStream(byte[] buffer, int offset) {
        this.buffer = buffer;
        this.offset = offset;
        this.count = buffer.length;
    }

    public BinaryStream reset() {
        this.offset = 0;
        this.count = 0;
        return this;
    }

    public void setBuffer(byte[] buffer) {
        this.buffer = buffer;
        this.count = buffer == null ? -1 : buffer.length;
    }

    public void setBuffer(byte[] buffer, int offset) {
        this.setBuffer(buffer);
        this.setOffset(offset);
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public byte[] getBuffer() {
        return Arrays.copyOf(buffer, count);
    }

    public int getCount() {
        return count;
    }

    public byte[] get() {
        return this.get(this.count - this.offset);
    }

    public byte[] get(int len) {
        if (len < 0) {
            this.offset = this.count - 1;
            return new byte[0];
        }
        len = Math.min(len, this.getCount() - this.offset);
        this.offset += len;
        return Arrays.copyOfRange(this.buffer, this.offset - len, this.offset);
    }

    public void put(byte[] bytes) {
        if (bytes == null) {
            return;
        }

        this.ensureCapacity(this.count + bytes.length);

        System.arraycopy(bytes, 0, this.buffer, this.count, bytes.length);
        this.count += bytes.length;
    }

    public long getLong() {
        return Binary.readLong(this.get(8));
    }

    public void putLong(long l) {
        this.put(Binary.writeLong(l));
    }

    public int getInt() {
        return Binary.readInt(this.get(4));
    }

    public void putInt(int i) {
        this.put(Binary.writeInt(i));
    }

    public long getLLong() {
        return Binary.readLLong(this.get(8));
    }

    public void putLLong(long l) {
        this.put(Binary.writeLLong(l));
    }

    public int getLInt() {
        return Binary.readLInt(this.get(4));
    }

    public void putLInt(int i) {
        this.put(Binary.writeLInt(i));
    }

    public int getShort() {
        return Binary.readShort(this.get(2));
    }

    public void putShort(int s) {
        this.put(Binary.writeShort(s));
    }

    public int getLShort() {
        return Binary.readLShort(this.get(2));
    }

    public void putLShort(int s) {
        this.put(Binary.writeLShort(s));
    }

    public float getFloat() {
        return getFloat(-1);
    }

    public float getFloat(int accuracy) {
        return Binary.readFloat(this.get(4), accuracy);
    }

    public void putFloat(float v) {
        this.put(Binary.writeFloat(v));
    }

    public float getLFloat() {
        return getLFloat(-1);
    }

    public float getLFloat(int accuracy) {
        return Binary.readLFloat(this.get(4), accuracy);
    }

    public void putLFloat(float v) {
        this.put(Binary.writeLFloat(v));
    }

    public int getTriad() {
        return Binary.readTriad(this.get(3));
    }

    public void putTriad(int triad) {
        this.put(Binary.writeTriad(triad));
    }

    public int getLTriad() {
        return Binary.readLTriad(this.get(3));
    }

    public void putLTriad(int triad) {
        this.put(Binary.writeLTriad(triad));
    }

    public boolean getBoolean() {
        return this.getByte() == 0x01;
    }

    public void putBoolean(boolean bool) {
        this.putByte((byte) (bool ? 1 : 0));
    }

    public int getByte() {
        return this.buffer[this.offset++] & 0xff;
    }

    public void putByte(byte b) {
        this.put(new byte[]{b});
    }

    /**
     * Reads a list of Attributes from the stream.
     *
     * @return Attribute[]
     */
    public Attribute[] getAttributeList() throws Exception {
        List<Attribute> list = new ArrayList<>();
        for (int i = 0, count = this.getUnsignedVarInt(); i < count; i++) {
            float min = this.getLFloat();
            float max = this.getLFloat();
            float value = this.getLFloat();
            float defaultValue = this.getLFloat();
            Strimg name = this.getString();

            Attribute attribute = Attribute.getAttributeByName(name);
            if (attribute != null) {
                attribute.setMinValue(min);
                attribute.setMaxValue(max);
                attribute.setValue(value);
                attribute.setDefaultValue(defaultValue);
                list.add(attribute);
            } else {
                throw new Exception("Unknown attribute type \"" + name + "\"");
            }
        }
        return list.toArray(new Attribute[0]);
    }

    /**
     * Writes a list of Attributes to the packet buffer using the standard format.
     */
    public void putAttributeList(Attribute[] attributes) {
        this.putUnsignedVarInt(attributes.length);
        for (Attribute attribute : attributes) {
            this.putLFloat(attribute.getMinValue());
            this.putLFloat(attribute.getMaxValue());
            this.putLFloat(attribute.getValue());
            this.putLFloat(attribute.getDefaultValue());
            this.putString(attribute.getName());
        }
    }

    public void putUUID(UUID uuid) {
        this.put(Binary.writeUUID(uuid));
    }

    public UUID getUUID() {
        return Binary.readUUID(this.get(16));
    }

    public void putSkin(Skin skin) {
        this.putString(skin.getSkinId());
        this.putString(skin.getPlayFabId());
        this.putString(skin.getSkinResourcePatch());
        this.putImage(skin.getSkinData());

        List<SkinAnimation> animations = skin.getAnimations();
        this.putLInt(animations.size());
        for (SkinAnimation animation : animations) {
            this.putImage(animation.image);
            this.putLInt(animation.type);
            this.putLFloat(animation.frames);
            this.putLInt(animation.expression);
        }

        this.putImage(skin.getCapeData());
        this.putString(skin.getGeometryData());
        this.putString(skin.getAnimationData());
        this.putBoolean(skin.isPremium());
        this.putBoolean(skin.isPersona());
        this.putBoolean(skin.isCapeOnClassic());
        this.putString(skin.getCapeId());
        this.putString(skin.getFullSkinId());
        this.putString(skin.getArmSize());
        this.putString(skin.getSkinColor());
        List<PersonaPiece> pieces = skin.getPersonaPieces();
        this.putLInt(pieces.size());
        for (PersonaPiece piece : pieces) {
            this.putString(piece.id);
            this.putString(piece.type);
            this.putString(piece.packId);
            this.putBoolean(piece.isDefault);
            this.putString(piece.productId);
        }

        List<PersonaPieceTint> tints = skin.getTintColors();
        this.putLInt(tints.size());
        for (PersonaPieceTint tint : tints) {
            this.putString(tint.pieceType);
            List<String> colors = tint.colors;
            this.putLInt(colors.size());
            for (String color : colors) {
                this.putString(color);
            }
        }
    }

    public Skin getSkin() {
        Skin skin = new Skin();
        skin.setSkinId(this.getString());
        skin.setPlayFabId(this.getString());
        skin.setSkinResourcePatch(this.getString());
        skin.setSkinData(this.getImage());

        int animationCount = this.getLInt();
        for (int i = 0; i < animationCount; i++) {
            SerializedImage image = this.getImage();
            int type = this.getLInt();
            float frames = this.getLFloat();
            int expression = this.getLInt();
            skin.getAnimations().add(new SkinAnimation(image, type, frames, expression));
        }

        skin.setCapeData(this.getImage());
        skin.setGeometryData(this.getString());
        skin.setAnimationData(this.getString());
        skin.setPremium(this.getBoolean());
        skin.setPersona(this.getBoolean());
        skin.setCapeOnClassic(this.getBoolean());
        skin.setCapeId(this.getString());
        this.getString(); // TODO: Full skin id
        skin.setArmSize(this.getString());
        skin.setSkinColor(this.getString());

        int piecesLength = this.getLInt();
        for (int i = 0; i < piecesLength; i++) {
            String pieceId = this.getString();
            String pieceType = this.getString();
            String packId = this.getString();
            boolean isDefault = this.getBoolean();
            String productId = this.getString();
            skin.getPersonaPieces().add(new PersonaPiece(pieceId, pieceType, packId, isDefault, productId));
        }

        int tintsLength = this.getLInt();
        for (int i = 0; i < tintsLength; i++) {
            String pieceType = this.getString();
            List<String> colors = new ArrayList<>();
            int colorsLength = this.getLInt();
            for (int i2 = 0; i2 < colorsLength; i2++) {
                colors.add(this.getString());
            }
            skin.getTintColors().add(new PersonaPieceTint(pieceType, colors));
        }
        return skin;
    }

    public void putImage(SerializedImage image) {
        this.putLInt(image.width);
        this.putLInt(image.height);
        this.putByteArray(image.data);
    }

    public SerializedImage getImage() {
        int width = this.getLInt();
        int height = this.getLInt();
        byte[] data = this.getByteArray();
        return new SerializedImage(width, height, data);
    }

    public Item getSlot() {
        int runtimeId = this.getVarInt();
        if (runtimeId == 0) {
            return Item.get(0, 0, 0);
        }

        int count = this.getLShort();
        int damage = (int) this.getUnsignedVarInt();

        RuntimeItemMapping mapping = RuntimeItems.getMapping();
        LegacyEntry legacyEntry = mapping.fromRuntime(runtimeId);

        int id = legacyEntry.getLegacyId();
        if (legacyEntry.isHasDamage()) {
            damage = legacyEntry.getDamage();
        }

        if (this.getBoolean()) { // hasNetId
            this.getVarInt(); // netId
        }

        this.getVarInt(); // blockRuntimeId

        byte[] bytes = this.getByteArray();
        ByteBuf buf = AbstractByteBufAllocator.DEFAULT.ioBuffer(bytes.length);
        buf.writeBytes(bytes);

        byte[] nbt = new byte[0];
        String[] canPlace;
        String[] canBreak;

        try (LittleEndianByteBufInputStream stream = new LittleEndianByteBufInputStream(buf)) {
            int nbtSize = stream.readShort();

            CompoundTag compoundTag = null;
            if (nbtSize > 0) {
                compoundTag = NBTIO.read(stream, ByteOrder.LITTLE_ENDIAN);
            } else if (nbtSize == -1) {
                int tagCount = stream.readUnsignedByte();
                if (tagCount != 1) throw new IllegalArgumentException("Expected 1 tag but got " + tagCount);
                compoundTag = NBTIO.read(stream, ByteOrder.LITTLE_ENDIAN);
            }

            if (compoundTag != null && compoundTag.getAllTags().size() > 0) {
                if (compoundTag.contains("Damage")) {
                    damage = compoundTag.getInt("Damage");
                    compoundTag.remove("Damage");
                }
                if (compoundTag.contains("__DamageConflict__")) {
                    compoundTag.put("Damage", compoundTag.removeAndGet("__DamageConflict__"));
                }
                if (!compoundTag.isEmpty()) {
                    nbt = NBTIO.write(compoundTag, ByteOrder.LITTLE_ENDIAN);
                }
            }

            canPlace = new String[stream.readInt()];
            for (int i = 0; i < canPlace.length; i++) {
                canPlace[i] = stream.readUTF();
            }

            canBreak = new String[stream.readInt()];
            for (int i = 0; i < canBreak.length; i++) {
                canBreak[i] = stream.readUTF();
            }

            if (id == ItemID.SHIELD) {
                stream.readLong();
            }
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read item user data", e);
        } finally {
            buf.release();
        }

        Item item = Item.get(id, damage, count, nbt);

        if (canBreak.length > 0 || canPlace.length > 0) {
            CompoundTag namedTag = item.getNamedTag();
            if (namedTag == null) {
                namedTag = new CompoundTag();
            }

            if (canBreak.length > 0) {
                ListTag<StringTag> listTag = new ListTag<>("CanDestroy");
                for (String blockName : canBreak) {
                    listTag.add(new StringTag("", blockName));
                }
                namedTag.put("CanDestroy", listTag);
            }

            if (canPlace.length > 0) {
                ListTag<StringTag> listTag = new ListTag<>("CanPlaceOn");
                for (String blockName : canPlace) {
                    listTag.add(new StringTag("", blockName));
                }
                namedTag.put("CanPlaceOn", listTag);
            }

            item.setNamedTag(namedTag);
        }

        return item;
    }

    public void putSlot(Item item) {
        this.putSlot(item, false);
    }

    public void putSlot(Item item, boolean instanceItem) {
        if (item == null || item.getId() == 0) {
            this.putByte((byte) 0);
            return;
        }

        RuntimeItemMapping mapping = RuntimeItems.getMapping();
        RuntimeEntry runtimeEntry = mapping.toRuntime(item.getId(), item.getDamage());
        int runtimeId = runtimeEntry.getRuntimeId();
        int damage = runtimeEntry.isHasDamage() ? 0 : item.getDamage();

        this.putVarInt(runtimeId);
        this.putLShort(item.getCount());

        this.putUnsignedVarInt(damage);

        if (!instanceItem) {
            this.putBoolean(true);
            this.putVarInt(0); //TODO
        }

        Block block = item.getBlockUnsafe();
        int blockRuntimeId = block == null ? 0 : GlobalBlockPalette.getOrCreateRuntimeId(block.getId(), block.getDamage());
        this.putVarInt(blockRuntimeId);

        ByteBuf userDataBuf = ByteBufAllocator.DEFAULT.ioBuffer();
        try (LittleEndianByteBufOutputStream stream = new LittleEndianByteBufOutputStream(userDataBuf)) {
            if (item.getDamage() != 0) {
                byte[] nbt = item.getCompoundTag();
                CompoundTag tag;
                if (nbt == null || nbt.length == 0) {
                    tag = new CompoundTag();
                } else {
                    tag = NBTIO.read(nbt, ByteOrder.LITTLE_ENDIAN);
                }
                if (tag.contains("Damage")) {
                    tag.put("__DamageConflict__", tag.removeAndGet("Damage"));
                }
                tag.putInt("Damage", item.getDamage());
                stream.writeShort(-1);
                stream.writeByte(1); // Hardcoded in current version
                stream.write(NBTIO.write(tag, ByteOrder.LITTLE_ENDIAN));
            } else if (item.hasCompoundTag()) {
                stream.writeShort(-1);
                stream.writeByte(1); // Hardcoded in current version
                stream.write(item.getCompoundTag());
            } else {
                userDataBuf.writeShortLE(0);
            }

            List<String> canPlaceOn = extractStringList(item, "CanPlaceOn");
            stream.writeInt(canPlaceOn.size());
            for (String string : canPlaceOn) {
                stream.writeUTF(string);
            }

            List<String> canDestroy = extractStringList(item, "CanDestroy");
            stream.writeInt(canDestroy.size());
            for (String string : canDestroy) {
                stream.writeUTF(string);
            }

            if (item.getId() == ItemID.SHIELD) {
                stream.writeLong(0);
            }

            byte[] bytes = new byte[userDataBuf.readableBytes()];
            userDataBuf.readBytes(bytes);
            putByteArray(bytes);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to write item user data", e);
        } finally {
            userDataBuf.release();
        }
    }

    public Item getRecipeIngredient() {
        int runtimeId = this.getVarInt();
        if (runtimeId == 0) {
            return Item.get(0, 0, 0);
        }

        RuntimeItemMapping mapping = RuntimeItems.getMapping();
        LegacyEntry legacyEntry = mapping.fromRuntime(runtimeId);

        int id = legacyEntry.getLegacyId();
        int damage = this.getVarInt();

        if (legacyEntry.isHasDamage()) {
            damage = legacyEntry.getDamage();
        } else if (damage == 0x7fff) {
            damage = -1;
        }

        int count = this.getVarInt();
        return Item.get(id, damage, count);
    }

    public void putRecipeIngredient(Item item) {
        if (item == null || item.getId() == 0) {
            this.putVarInt(0);
            return;
        }

        RuntimeItemMapping mapping = RuntimeItems.getMapping();
        int runtimeId, damage;

        if (!item.hasMeta()) {
            RuntimeEntry runtimeEntry = mapping.toRuntime(item.getId(), 0);
            runtimeId = runtimeEntry.getRuntimeId();
            damage = 0x7fff;
        } else {
            RuntimeEntry runtimeEntry = mapping.toRuntime(item.getId(), item.getDamage());
            runtimeId = runtimeEntry.getRuntimeId();
            damage = runtimeEntry.isHasDamage() ? 0 : item.getDamage();
        }
        this.putVarInt(runtimeId);
        this.putVarInt(damage);
        this.putVarInt(item.getCount());
    }

    public EntityMetadata getMetadata() {
        EntityMetadata entityMetadata = new EntityMetadata();
        for (int i = 0, count = this.getUnsignedVarInt(); i < count; i++) {
            int key = (int) this.getUnsignedVarInt();
            int type = (int) this.getUnsignedVarInt();
            EntityData value = null;
            switch (type) {
                case Entity.DATA_TYPE_BYTE:
                    value = new ByteEntityData(key, this.getByte());
                    break;
                case Entity.DATA_TYPE_SHORT:
                    value = new ShortEntityData(key, this.getLShort());
                    break;
                case Entity.DATA_TYPE_INT:
                    value = new IntEntityData(key, this.getVarInt());
                    break;
                case Entity.DATA_TYPE_FLOAT:
                    value = new FloatEntityData(key, this.getLFloat());
                    break;
                case Entity.DATA_TYPE_STRING:
                    value = new StringEntityData(key, this.getString());
                    break;
                case Entity.DATA_TYPE_NBT:
                    int offset = this.getOffset();
                    FastByteArrayInputStream fbais = new FastByteArrayInputStream(this.get());
                    try {
                        CompoundTag namedTag = NBTIO.read(fbais, ByteOrder.LITTLE_ENDIAN, true);
                        value = new NBTEntityData(key, namedTag);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    this.setOffset(offset + (int) fbais.position());
                    break;
                case Entity.DATA_TYPE_POS:
                    BlockVector3 v3 = this.getSignedBlockPosition();
                    value = new IntPositionEntityData(key, v3.x, v3.y, v3.z);
                    break;
                case Entity.DATA_TYPE_LONG:
                    value = new LongEntityData(key, this.getVarLong());
                    break;
                case Entity.DATA_TYPE_VECTOR3F:
                    value = new Vector3fEntityData(key, this.getVector3f());
                    break;
            }
            if (value != null) entityMetadata.put(value);
        }
        return entityMetadata;
    }

    public void putMetadata(EntityMetadata metadata) {
        Map<Integer, EntityData> map = metadata.getMap();
        this.putUnsignedVarInt(map.size());
        for (int id : map.keySet()) {
            EntityData d = map.get(id);
            this.putUnsignedVarInt(id);
            this.putUnsignedVarInt(d.getType());
            switch (d.getType()) {
                case Entity.DATA_TYPE_BYTE:
                    this.putByte(((ByteEntityData) d).getData().byteValue());
                    break;
                case Entity.DATA_TYPE_SHORT:
                    this.putLShort(((ShortEntityData) d).getData());
                    break;
                case Entity.DATA_TYPE_INT:
                    this.putVarInt(((IntEntityData) d).getData());
                    break;
                case Entity.DATA_TYPE_FLOAT:
                    this.putLFloat(((FloatEntityData) d).getData());
                    break;
                case Entity.DATA_TYPE_STRING:
                    String s = ((StringEntityData) d).getData();
                    this.putUnsignedVarInt(s.getBytes(StandardCharsets.UTF_8).length);
                    this.put(s.getBytes(StandardCharsets.UTF_8));
                    break;
                case Entity.DATA_TYPE_NBT:
                    NBTEntityData slot = (NBTEntityData) d;
                    try {
                        this.put(NBTIO.write(slot.getData(), ByteOrder.LITTLE_ENDIAN, true));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case Entity.DATA_TYPE_POS:
                    IntPositionEntityData pos = (IntPositionEntityData) d;
                    this.putVarInt(pos.x);
                    this.putVarInt(pos.y);
                    this.putVarInt(pos.z);
                    break;
                case Entity.DATA_TYPE_LONG:
                    this.putVarLong(((LongEntityData) d).getData());
                    break;
                case Entity.DATA_TYPE_VECTOR3F:
                    Vector3fEntityData v3data = (Vector3fEntityData) d;
                    this.putLFloat(v3data.x);
                    this.putLFloat(v3data.y);
                    this.putLFloat(v3data.z);
                    break;
            }
        }
    }

    private List<String> extractStringList(Item item, String tagName) {
        CompoundTag namedTag = item.getNamedTag();
        if (namedTag == null) {
            return Collections.emptyList();
        }

        ListTag<StringTag> listTag = namedTag.getList(tagName, StringTag.class);
        if (listTag == null) {
            return Collections.emptyList();
        }

        int size = listTag.size();
        List<String> values = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            StringTag stringTag = listTag.get(i);
            if (stringTag != null) {
                values.add(stringTag.data);
            }
        }

        return values;
    }

    public byte[] getByteArray() {
        return this.get((int) this.getUnsignedVarInt());
    }

    public void putByteArray(byte[] b) {
        this.putUnsignedVarInt(b.length);
        this.put(b);
    }

    public String getString() {
        return new String(this.getByteArray(), StandardCharsets.UTF_8);
    }

    public void putString(String string) {
        byte[] b = string.getBytes(StandardCharsets.UTF_8);
        this.putByteArray(b);
    }

    public long getUnsignedVarInt() {
        return VarInt.readUnsignedVarInt(this);
    }

    public void putUnsignedVarInt(long v) {
        VarInt.writeUnsignedVarInt(this, v);
    }

    public int getVarInt() {
        return VarInt.readVarInt(this);
    }

    public void putVarInt(int v) {
        VarInt.writeVarInt(this, v);
    }

    public long getVarLong() {
        return VarInt.readVarLong(this);
    }

    public void putVarLong(long v) {
        VarInt.writeVarLong(this, v);
    }

    public long getUnsignedVarLong() {
        return VarInt.readUnsignedVarLong(this);
    }

    public void putUnsignedVarLong(long v) {
        VarInt.writeUnsignedVarLong(this, v);
    }

    public BlockVector3 getBlockVector3() {
        return new BlockVector3(this.getVarInt(), (int) this.getUnsignedVarInt(), this.getVarInt());
    }

    public BlockVector3 getBlockVector3(int x, int y, int z) {
        x = this.getVarInt();
        y = (int) this.getUnsignedVarInt();
        z = this.getVarInt();
    }

    public BlockVector3 getSignedBlockPosition() {
        return new BlockVector3(getVarInt(), getVarInt(), getVarInt());
    }

    public void putSignedBlockPosition(BlockVector3 v) {
        putVarInt(v.x);
        putVarInt(v.y);
        putVarInt(v.z);
    }

    public void putBlockVector3(BlockVector3 v) {
        this.putBlockVector3(v.x, v.y, v.z);
    }

    public void putBlockVector3(int x, int y, int z) {
        this.putVarInt(x);
        this.putUnsignedVarInt(y);
        this.putVarInt(z);
    }

    public Vector3f getVector3f() {
        return new Vector3f(this.getLFloat(4), this.getLFloat(4), this.getLFloat(4));
    }

    public void putVector3f(Vector3f v) {
        this.putVector3f(v.x, v.y, v.z);
    }

    public void putVector3f(float x, float y, float z) {
        this.putLFloat(x);
        this.putLFloat(y);
        this.putLFloat(z);
    }

    public void putGameRules(GameRules gameRules) {
        Map<GameRule, GameRules.Value> rules = gameRules.getGameRules();
        this.putUnsignedVarInt(rules.size());
        rules.forEach((gameRule, value) -> {
            this.putString(gameRule.getName().toLowerCase());
            value.write(this);
        });
    }

    public CompoundTag getCompoundTag() {
        try {
            return NBTIO.read(this.getByteArray(), ByteOrder.LITTLE_ENDIAN, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new CompoundTag();
    }

    public void putCompoundTag(CompoundTag compoundTag) {
        try {
            this.put(NBTIO.write(compoundTag, ByteOrder.LITTLE_ENDIAN, true));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public CommandOriginData getCommandOriginData() {
		CommandOriginData commandOriginData = new CommandOriginData();
		commandOriginData.type = (int) this.getUnsignedVarInt();
		commandOriginData.uuid = this.getUUID();
		commandOriginData.requestId = this.getString();
		if (commandOriginData.type == CommandOriginData.ORIGIN_DEV_CONSOLE || commandOriginData.type == CommandOriginData.ORIGIN_TEST) {
			commandOriginData.playerUniqueId = this.getEntityUniqueId();
		}
		return commandOriginData;
	}

    public void putCommandOriginData(CommandOriginData commandOriginData) {
		this.putUnsignedVarInt(commandOriginData.type);
		this.putUUID(commandOriginData.uuid);
		this.putString(commandOriginData.requestId);
		if (commandOriginData.type == CommandOriginData.ORIGIN_DEV_CONSOLE || commandOriginData.type == CommandOriginData.ORIGIN_TEST) {
			this.putEntityUniqueId(commandOriginData.playerUniqueId);
		}
	}

    /**
     * Reads and returns an EntityUniqueID
     *
     * @return int
     */
    public long getEntityUniqueId() {
        return this.getVarLong();
    }

    /**
     * Writes an EntityUniqueID
     */
    public void putEntityUniqueId(long eid) {
        this.putVarLong(eid);
    }

    /**
     * Reads and returns an EntityRuntimeID
     */
    public long getEntityRuntimeId() {
        return this.getUnsignedVarLong();
    }

    /**
     * Writes an EntityUniqueID
     */
    public void putEntityRuntimeId(long eid) {
        this.putUnsignedVarLong(eid);
    }

    public BlockFace getBlockFace() {
        return BlockFace.fromIndex(this.getVarInt());
    }

    public void putBlockFace(BlockFace face) {
        this.putVarInt(face.getIndex());
    }

    public void putEntityLink(EntityLink link) {
        putEntityUniqueId(link.fromEntityUniquieId);
        putEntityUniqueId(link.toEntityUniquieId);
        putByte(link.type);
        putBoolean(link.immediate);
        putBoolean(link.riderInitiated);
    }

    public EntityLink getEntityLink() {
        return new EntityLink(
                getEntityUniqueId(),
                getEntityUniqueId(),
                (byte) getByte(),
                getBoolean(),
                getBoolean()
        );
    }

    @SuppressWarnings("unchecked")
    public <T> T[] getArray(Class<T> clazz, Function<BinaryStream, T> function) {
        ArrayDeque<T> deque = new ArrayDeque<>();
        int count = (int) getUnsignedVarInt();
        for (int i = 0; i < count; i++) {
            deque.add(function.apply(this));
        }
        return deque.toArray((T[]) Array.newInstance(clazz, 0));
    }

    public boolean feof() {
        return this.offset < 0 || this.offset >= this.buffer.length;
    }

    private void ensureCapacity(int minCapacity) {
        // overflow-conscious code
        if (minCapacity - buffer.length > 0) {
            grow(minCapacity);
        }
    }

    private void grow(int minCapacity) {
        // overflow-conscious code
        int oldCapacity = buffer.length;
        int newCapacity = oldCapacity << 1;

        if (newCapacity - minCapacity < 0) {
            newCapacity = minCapacity;
        }

        if (newCapacity - MAX_ARRAY_SIZE > 0) {
            newCapacity = hugeCapacity(minCapacity);
        }
        this.buffer = Arrays.copyOf(buffer, newCapacity);
    }

    private static int hugeCapacity(int minCapacity) {
        if (minCapacity < 0) { // overflow
            throw new OutOfMemoryError();
        }
        return (minCapacity > MAX_ARRAY_SIZE) ?
                Integer.MAX_VALUE :
                MAX_ARRAY_SIZE;
    }
}
