package cn.nukkit;

import cn.nukkit.utils.VarInt;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.buffer.UnpooledByteBufAllocator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * By lmlstarqaq http://snake1999.com/
 * Creation time: 2017/7/5 23:22.
 */
@DisplayName("VarInt")
class VarIntTest {

	@DisplayName("ZigZag")
	@Test
	void testZigZag() {
		assertAll(
				() -> assertEquals(0x2468acf0, VarInt.encodeZigZag32(0x12345678)),
				() -> assertEquals(0x2b826b1d, VarInt.encodeZigZag32(0xea3eca71)),
				() -> assertEquals(0x12345678, VarInt.decodeZigZag32(0x2468acf0)),
				() -> assertEquals(0xea3eca71, VarInt.decodeZigZag32(0x2b826b1d)),
				() -> assertEquals(2623536930346282224L, VarInt.encodeZigZag64(0x1234567812345678L)),
				() -> assertEquals(3135186066796324391L, VarInt.encodeZigZag64(0xea3eca710becececL)),
				() -> assertEquals(0x1234567812345678L, VarInt.decodeZigZag64(2623536930346282224L)),
				() -> assertEquals(0xea3eca710becececL, VarInt.decodeZigZag64(3135186066796324391L))
		);
	}

    private static ByteBuf wrapBinaryStream(String hex) {
        return Unpooled.wrappedBuffer(ByteBufUtil.decodeHexDump(hex));
	}

	@DisplayName("Write Sizes")
	@Test
	void testSizes() throws IOException {
		sizeTest(w -> VarInt.writeVarLong(w, 0x7FFFFFFF /* -1 >>> 1 */), 5);
		sizeTest(w -> VarInt.writeVarInt(w, 0x7FFFFFFF /* -1 >>> 1 */), 5);
		sizeTest(w -> VarInt.writeVarLong(w, -1), 1);
		sizeTest(w -> VarInt.writeVarInt(w, -1), 1);
	}

	@DisplayName("Reading")
	@Test
	void testRead() {
		assertAll(
				() -> assertEquals(2412, VarInt.readUnsignedVarInt(wrapBinaryStream("EC123EC456"))),
				() -> assertEquals(583868, VarInt.readUnsignedVarInt(wrapBinaryStream("BCD123EFA0"))),
				() -> assertEquals(1206, VarInt.readVarInt(wrapBinaryStream("EC123EC456"))),
				() -> assertEquals(291934, VarInt.readVarInt(wrapBinaryStream("BCD123EFA0"))),
				() -> assertEquals(6015, VarInt.readUnsignedVarLong(wrapBinaryStream("FF2EC456EC789EC012EC"))),
				() -> assertEquals(3694, VarInt.readUnsignedVarLong(wrapBinaryStream("EE1CD34BCD56BCD78BCD"))),
				() -> assertEquals(-3008, VarInt.readVarLong(wrapBinaryStream("FF2EC456EC789EC012EC"))),
				() -> assertEquals(1847, VarInt.readVarLong(wrapBinaryStream("EE1CD34BCD56BCD78BCD")))
		);
	}

    @DisplayName("Writing")
    @Test
    void testWrite() throws IOException {
        ByteBuf buffer = UnpooledByteBufAllocator.DEFAULT.buffer();
        VarInt.writeUnsignedVarInt(buffer, 237356812);
        VarInt.writeVarInt(buffer, 0xea3eca71);
        VarInt.writeUnsignedVarLong(buffer, 0x1234567812345678L);
        VarInt.writeVarLong(buffer, 0xea3eca710becececL);
        assertAll(
                () -> assertEquals(237356812, VarInt.readUnsignedVarInt(buffer)),
                () -> assertEquals(0xea3eca71, VarInt.readVarInt(buffer)),
                () -> assertEquals(0x1234567812345678L, VarInt.readUnsignedVarLong(buffer)),
                () -> assertEquals(0xea3eca710becececL, VarInt.readVarLong(buffer))
        );
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        VarInt.writeUnsignedVarInt(os, 237356812);
        VarInt.writeVarInt(os, 0xea3eca71);
        VarInt.writeUnsignedVarLong(os, 0x1234567812345678L);
        VarInt.writeVarLong(os, 0xea3eca710becececL);
        VarInt.writeVarInt(os, 0x7FFFFFFF);
        VarInt.writeVarInt(os, -1);
        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
        assertAll(
                () -> assertEquals(237356812, VarInt.readUnsignedVarInt(is)),
                () -> assertEquals(0xea3eca71, VarInt.readVarInt(is)),
                () -> assertEquals(0x1234567812345678L, VarInt.readUnsignedVarLong(is)),
                () -> assertEquals(0xea3eca710becececL, VarInt.readVarLong(is)),
                () -> assertEquals(0x7FFFFFFF, VarInt.readVarInt(is)),
                () -> assertEquals(-1, VarInt.readVarInt(is))
        );
	}

	private static byte[] hexStringToByte(String hex) {
		int len = (hex.length() / 2);
		byte[] result = new byte[len];
		char[] aChar = hex.toCharArray();
		for (int i = 0; i < len; i++) {
			int pos = i * 2;
			result[i] = (byte) (toByte(aChar[pos]) << 4 | toByte(aChar[pos + 1]));
		}
		return result;
	}

	private static byte toByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	private interface TestConsumer<T> {
		void accept(T t) throws IOException;
	}

	private void sizeTest(TestConsumer<ByteArrayOutputStream> write, int size) throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		write.accept(os);
		ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
		assertEquals(size, is.available());
	};
}
