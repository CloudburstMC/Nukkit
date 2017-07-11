package cn.nukkit.test;

import cn.nukkit.utils.BinaryStream;
import cn.nukkit.utils.VarInt;
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

	@DisplayName("Writing")
	@Test
	void testWrite() throws IOException {
		BinaryStream bs = new BinaryStream();
		VarInt.writeUnsignedVarInt(bs, 237356812);
		VarInt.writeVarInt(bs, 0xea3eca71);
		VarInt.writeUnsignedVarLong(bs, 0x1234567812345678L);
		VarInt.writeVarLong(bs, 0xea3eca710becececL);
		assertAll(
				() -> assertEquals(237356812, VarInt.readUnsignedVarInt(bs)),
				() -> assertEquals(0xea3eca71, VarInt.readVarInt(bs)),
				() -> assertEquals(0x1234567812345678L, VarInt.readUnsignedVarLong(bs)),
				() -> assertEquals(0xea3eca710becececL, VarInt.readVarLong(bs))
		);
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		VarInt.writeUnsignedVarInt(os, 237356812);
		VarInt.writeVarInt(os, 0xea3eca71);
		VarInt.writeUnsignedVarLong(os, 0x1234567812345678L);
		VarInt.writeVarLong(os, 0xea3eca710becececL);
		ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
		assertAll(
				() -> assertEquals(237356812, VarInt.readUnsignedVarInt(is)),
				() -> assertEquals(0xea3eca71, VarInt.readVarInt(is)),
				() -> assertEquals(0x1234567812345678L, VarInt.readUnsignedVarLong(is)),
				() -> assertEquals(0xea3eca710becececL, VarInt.readVarLong(is))
		);
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

	private static BinaryStream wrapBinaryStream(String hex) {
		return new BinaryStream(hexStringToByte(hex));
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
}
