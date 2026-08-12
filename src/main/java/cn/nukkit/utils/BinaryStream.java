package cn.nukkit.utils;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.entity.Attribute;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.item.*;
import cn.nukkit.item.RuntimeItemMapping.LegacyEntry;
import cn.nukkit.item.RuntimeItemMapping.RuntimeEntry;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.GameRules;
import cn.nukkit.level.GlobalBlockPalette;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector2f;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.network.LittleEndianByteBufInputStream;
import cn.nukkit.network.LittleEndianByteBufOutputStream;
import cn.nukkit.network.protocol.types.EntityLink;
import cn.nukkit.network.protocol.types.ExperimentData;
import org.cloudburstmc.nbt.NBTOutputStream;
import org.cloudburstmc.nbt.NbtUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * BinaryStream
 *
 * @author MagicDroidX
 * Nukkit Project
 */
public class BinaryStream {

    public int offset;
    private byte[] buffer;
    protected int count;

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

    public byte[] getRawBuffer() {
        return buffer;
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
        len = Math.min(len, this.count - this.offset);
        this.offset += len;
        return Arrays.copyOfRange(this.buffer, this.offset - len, this.offset);
    }

    public void put(byte[] bytes) {
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
        long count = this.getUnsignedVarInt();

        for (int i = 0; i < count; ++i) {
            String name = this.getString();
            Attribute attr = Attribute.getAttributeByName(name);
            if (attr != null) {
                attr.setMinValue(this.getLFloat());
                attr.setValue(this.getLFloat());
                attr.setMaxValue(this.getLFloat());
                list.add(attr);
            } else {
                throw new Exception("Unknown attribute type \"" + name + '"');
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
            this.putString(attribute.getName());
            this.putLFloat(attribute.getMinValue());
            this.putLFloat(attribute.getValue());
            this.putLFloat(attribute.getMaxValue());
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
        this.putUnsignedVarInt(animations.size());

        for (SkinAnimation animation : animations) {
            this.putImage(animation.image);

            this.putUnsignedVarInt(animation.type);

            this.putLFloat(animation.frames);

            this.putUnsignedVarInt(animation.expression);
        }

        this.putImage(skin.getCapeData());
        this.putString(skin.getGeometryData());

        this.putString(skin.getGeometryDataEngineVersion());

        this.putString(skin.getAnimationData());

        this.putString(skin.getCapeId());
        this.putString(skin.getFullSkinId());

        this.putByte((byte) ("slim".equalsIgnoreCase(skin.getArmSize()) ? 0 : 1));
        this.putLInt(skin.getColor().getRGB());

        List<PersonaPiece> pieces = skin.getPersonaPieces();
        this.putUnsignedVarInt(pieces.size());

        for (PersonaPiece piece : pieces) {
            this.putString(piece.id);

            this.putLInt(piece.type.ordinal());
            this.putUUID(piece.packId);

            this.putBoolean(piece.isDefault);
            this.putString(piece.productId);
        }

        List<PersonaPieceTint> tints = skin.getTintColors();
        this.putUnsignedVarInt(tints.size());

        for (PersonaPieceTint tint : tints) {
            this.putString(tint.pieceType.getSerializeName());

            List<Color> colorsNew = tint.getColorsNew();
            for (int i = 0; i < 4; i++) {
                if (i >= colorsNew.size()) {
                    this.putLInt(0);
                } else {
                    this.putLInt(colorsNew.get(i).getRGB());
                }
            }
        }

        this.putBoolean(skin.isPremium());
        this.putBoolean(skin.isPersona());
        this.putBoolean(skin.isCapeOnClassic());
        this.putBoolean(skin.isPrimaryUser());

        this.putBoolean(skin.isOverridingPlayerAppearance());

        this.putString(Boolean.toString(skin.isTrusted()));
        this.putString(skin.getProfileHash());
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

    public Skin getSkin() {
        Skin skin = new Skin();
        skin.setSkinId(this.getString());

        skin.setPlayFabId(this.getString());

        skin.setSkinResourcePatch(this.getString());
        skin.setSkinData(this.getImage());

        int animationCount = (int) this.getUnsignedVarInt();

        for (int i = 0; i < Math.min(animationCount, 1024); i++) {
            SerializedImage image = this.getImage();
            int type = (int) this.getUnsignedVarInt();
            float frames = this.getLFloat();
            int expression = (int) this.getUnsignedVarInt();
            skin.getAnimations().add(new SkinAnimation(image, type, frames, expression));
        }

        skin.setCapeData(this.getImage());
        skin.setGeometryData(this.getString());

        skin.setGeometryDataEngineVersion(this.getString());

        skin.setAnimationData(this.getString());

        skin.setCapeId(this.getString());
        skin.setFullSkinId(this.getString());

        skin.setArmSize(this.getByte() == 1 ? "wide" : "slim");
        skin.setColor(new Color(this.getLInt(), true));

        int piecesLength = (int) this.getUnsignedVarInt();
        for (int i = 0; i < Math.min(piecesLength, 1024); i++) {
            String pieceId = this.getString();

            PersonaPieceType pieceType;
            UUID packId;
            pieceType = PersonaPieceType.values()[this.getLInt()];
            packId = this.getUUID();

            boolean isDefault = this.getBoolean();
            String productId = this.getString();

            if (pieceType != PersonaPieceType.UNKNOWN && pieceType != PersonaPieceType.UNSUPPORTED) {
                skin.getPersonaPieces().add(new PersonaPiece(pieceId, pieceType, packId, isDefault, productId));
            }
        }

        int tintsLength = (int) this.getUnsignedVarInt();
        for (int i = 0; i < Math.min(tintsLength, 1024); i++) {
            PersonaPieceType pieceType = PersonaPieceType.fromName(this.getString());

            List<Color> colors = new ArrayList<>(4);
            for (int i2 = 0; i2 < 4; i2++) {
                colors.add(new Color(this.getLInt(), true));
            }

            if (pieceType != PersonaPieceType.UNKNOWN && pieceType != PersonaPieceType.UNSUPPORTED) {
                skin.getTintColors().add(new PersonaPieceTint(pieceType, colors));
            }
        }

        skin.setPremium(this.getBoolean());
        skin.setPersona(this.getBoolean());
        skin.setCapeOnClassic(this.getBoolean());
        skin.setPrimaryUser(this.getBoolean());

        this.getBoolean(); //skin.setOverridingPlayerAppearance(this.getBoolean());

        this.getString(); //skin.setTrusted("true".equalsIgnoreCase(this.getString()));
        this.getString(); //skin.setProfileHash(this.getString());
        return skin;
    }

    public Item getSlot() {
        return getNetworkItemStackDescriptor();
    }

    public void putSlot(Item item) {
        this.putSlot(item, false);
    }

    public void putSlot(Item item, boolean instanceItem) {
        putNetworkItemStackDescriptor(item, instanceItem);
    }

    public void putNetworkItemStackDescriptor(Item item) {
        putNetworkItemStackDescriptor(item, false);
    }

    public void putNetworkItemStackDescriptor(Item item, boolean instanceItem) {
        if (item == null) {
            item = Item.get(Item.AIR);
        }

        int id = item.getId();
        int meta = item.getDamage();
        boolean isBlock = item instanceof ItemBlock;
        boolean isDurable = item instanceof ItemDurable;

        RuntimeEntry runtimeEntry = null;
        if (id != Item.AIR) {
            runtimeEntry = RuntimeItems.getMapping().toRuntime(id, meta);
        }

        int runtimeId = runtimeEntry == null ? 0 : runtimeEntry.getRuntimeId();
        int damage = isBlock || isDurable || runtimeEntry == null || runtimeEntry.isHasDamage() ? 0 : meta;

        if (instanceItem) {
            this.putVarInt(runtimeId);
        } else {
            this.putLShort(runtimeId);
        }
        this.putLShort(item.getCount());
        this.putUnsignedVarInt(damage);

        if (!instanceItem) {
            boolean hasNetId = id != Item.AIR;
            this.putBoolean(hasNetId); // hasNetId
            if (hasNetId) {
                this.putVarInt(1); // netId 1 = Item is present
            }
        }

        Block block = isBlock && id != Item.AIR ? item.getBlockUnsafe() : null;
        int blockRuntimeId = block == null ? 0 : GlobalBlockPalette.getOrCreateRuntimeId(block.getId(), block.getDamage());
        this.putUnsignedVarInt(blockRuntimeId);

        if (id == Item.AIR) {
            this.putUnsignedVarInt(0); // No user date
            return;
        }

        ByteBuf userDataBuf = ByteBufAllocator.DEFAULT.ioBuffer();
        try (LittleEndianByteBufOutputStream stream = new LittleEndianByteBufOutputStream(userDataBuf)) {
            if (!instanceItem && isDurable && runtimeEntry != null && !runtimeEntry.isHasDamage()) {
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
                tag.putInt("Damage", meta);
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

            if (id == ItemID.SHIELD) {
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

    public Item getNetworkItemStackDescriptor() {
        int id = 0;
        short runtimeId = (short) this.getLShort(); // signed short
        int count = this.getLShort();
        int damage = (int) this.getUnsignedVarInt();

        LegacyEntry legacyEntry = null;

        if (runtimeId != 0) {
            legacyEntry = RuntimeItems.getMapping().fromRuntime(runtimeId);

            id = legacyEntry.getLegacyId();

            if (legacyEntry.isHasDamage()) {
                damage = legacyEntry.getDamage();
            }
        }

        if (this.getBoolean()) { // hasNetId
            this.getVarInt(); // netId
        }

        int blockRuntimeId = (int) this.getUnsignedVarInt();

        if (id != Item.AIR && id < 256 && id != 166 && !(id == -212 && legacyEntry.getDamage() == 0) && !legacyEntry.isHasDamage() && (id == BlockID.RED_MUSHROOM_BLOCK || id == BlockID.BROWN_MUSHROOM_BLOCK)) { // ItemBlock
            int fullId = GlobalBlockPalette.getLegacyFullId(blockRuntimeId);
            if (fullId != -1) {
                damage = fullId & Block.DATA_MASK;
            }
        }

        byte[] nbt = new byte[0];
        String[] canPlace = null;
        String[] canBreak = null;

        byte[] bytes = this.getByteArray();

        if (count <= 0) {
            return Item.get(Item.AIR, 0, 0);
        }

        if (bytes.length != 0) {
            ByteBuf buf = ByteBufAllocator.DEFAULT.ioBuffer(bytes.length);
            buf.writeBytes(bytes);

            try (LittleEndianByteBufInputStream stream = new LittleEndianByteBufInputStream(buf)) {
                int nbtSize = stream.readShort();

                CompoundTag compoundTag = null;
                if (nbtSize > 0) {
                    compoundTag = NBTIO.readSafely(stream, ByteOrder.LITTLE_ENDIAN, false);
                } else if (nbtSize == -1) {
                    int tagCount = stream.readUnsignedByte();
                    if (tagCount != 1) throw new IllegalArgumentException("Expected 1 tag but got " + tagCount);
                    compoundTag = NBTIO.readSafely(stream, ByteOrder.LITTLE_ENDIAN, false);
                }

                if (compoundTag != null && !compoundTag.getAllTags().isEmpty()) {
                    if (!legacyEntry.isHasDamage() && compoundTag.contains("Damage")) {
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

                int canPlaceCount = stream.readInt();
                if (canPlaceCount > 4096) {
                    throw new RuntimeException("Too many CanPlaceOn blocks: " + canPlaceCount);
                }

                canPlace = new String[canPlaceCount];
                for (int i = 0; i < canPlace.length; i++) {
                    canPlace[i] = stream.readUTF();
                }

                int canBreakCount = stream.readInt();
                if (canBreakCount > 4096) {
                    throw new RuntimeException("Too many CanDestroy blocks: " + canBreakCount);
                }

                canBreak = new String[canBreakCount];
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
        }

        Item item = Item.get(id, damage, count, nbt);

        if ((canBreak != null && canBreak.length > 0) || (canPlace != null && canPlace.length > 0)) {
            CompoundTag namedTag = item.getNamedTag();
            if (namedTag == null) {
                namedTag = new CompoundTag();
            }

            if (canBreak != null && canBreak.length > 0) {
                ListTag<StringTag> listTag = new ListTag<>("CanDestroy");
                for (String blockName : canBreak) {
                    listTag.add(new StringTag("", blockName));
                }
                namedTag.put("CanDestroy", listTag);
            }

            if (canPlace != null && canPlace.length > 0) {
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

    public void putRecipeIngredient(Item item) {
        if (item.isNull()) {
            this.putUnsignedVarInt(0); // type
            this.putVarInt(0); // meta
            this.putVarInt(0); // count
        } else {
            this.putUnsignedVarInt(1); // type
            this.putString("name"); // type
            RuntimeEntry runtime = RuntimeItems.getMapping().toRuntime(item.getId(), item.getDamage());
            this.putString(runtime.getIdentifier());
            this.putVarInt(runtime.isHasDamage() ? 0 : item.getDamage());
            this.putVarInt(item.getCount());
        }
    }

    private static List<String> extractStringList(Item item, String tagName) {
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
        return new BlockVector3(this.getVarInt(), this.getVarInt(), this.getVarInt());
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
        this.putVarInt(y);
        this.putVarInt(z);
    }

    public Vector3f getVector3f() {
        return new Vector3f(this.getLFloat(), this.getLFloat(), this.getLFloat());
    }

    public void putVector3f(Vector3f v) {
        this.putVector3f(v.x, v.y, v.z);
    }

    public void putVector3f(float x, float y, float z) {
        this.putLFloat(x);
        this.putLFloat(y);
        this.putLFloat(z);
    }

    public Vector2f getVector2f() {
        return new Vector2f(this.getLFloat(), this.getLFloat());
    }

    public void putGameRules(GameRules gameRules) {
        Map<GameRule, GameRules.Value> rulesToSend = gameRules.getGameRules();
        this.putUnsignedVarInt(rulesToSend.size());
        rulesToSend.forEach((gameRule, value) -> {
            putString(gameRule.getName().toLowerCase(Locale.ROOT));
            value.write(this);
        });
    }

    public void putGameRulesMap(Map<GameRule, GameRules.Value> allGameRules) {
        Map<GameRule, GameRules.Value> rulesToSend = new HashMap<>(allGameRules.size(), 1);
        allGameRules.forEach((gameRule, value) -> {
            if (gameRule == GameRule.NATURAL_REGENERATION) {
                rulesToSend.put(gameRule, new GameRules.Value<>(GameRules.Type.BOOLEAN, false)); // Fix client-side desync?
            } else {
                rulesToSend.put(gameRule, value);
            }
        });
        this.putUnsignedVarInt(rulesToSend.size());
        rulesToSend.forEach((gameRule, value) -> {
            putString(gameRule.getName().toLowerCase(Locale.ROOT));
            value.write(this);
        });
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
        putLFloat(link.vehicleAngularVelocity);
    }

    public EntityLink getEntityLink() {
        return new EntityLink(
                getEntityUniqueId(),
                getEntityUniqueId(),
                (byte) getByte(),
                getBoolean(),
                getBoolean(),
                getLFloat()
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

    public <T> void putArray(Collection<T> array, BiConsumer<BinaryStream, T> biConsumer) {
        this.putUnsignedVarInt(array.size());
        for (T val : array) {
            biConsumer.accept(this, val);
        }
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

    public <T> void putNbtTag(T tag) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try (NBTOutputStream writer = NbtUtils.createNetworkWriter(stream)) {
            writer.writeTag(tag);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.put(stream.toByteArray());
    }

    public void putExperiments(Collection<ExperimentData> experiments) {
        this.putLInt(experiments.size());
        for (ExperimentData experimentData : experiments) {
            this.putString(experimentData.getName());
            this.putBoolean(experimentData.isEnabled());
        }
        this.putBoolean(!experiments.isEmpty());
    }

    public <T> void putOptionalNull(T object, BiConsumer<BinaryStream, T> consumer) {
        if (object != null) {
            this.putBoolean(true);
            consumer.accept(this, object);
        } else {
            this.putBoolean(false);
        }
    }
}
