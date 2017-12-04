package cn.nukkit.server.test;

import cn.nukkit.server.utils.ClientChainData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * An example to show how to use ClientChainData
 * This is also a test for client chain data.
 *
 * By lmlstarqaq http://snake1999.com/
 * Creation time: 2017/6/7 15:07.
 */
@DisplayName("ClientChainData")
class ClientChainDataTest {

	@DisplayName("Getters")
	@Test
	void testGetter() throws Exception {
		InputStream is = ClientChainDataTest.class.getResourceAsStream("chain.dat");
		ClientChainData data = ClientChainData.of(readStream(is));
		String got = String.format("userName=%s, clientUUID=%s, " +
						"identityPublicKey=%s, clientId=%d, " +
						"serverAddress=%s, deviceModel=%s, " +
						"deviceOS=%d, gameVersion=%s, " +
						"guiScale=%d, languageCode=%s, " +
						"xuid=%s, currentInputMode=%d, " +
						"defaultInputMode=%d, UIProfile=%d"
				,
				data.getUsername(), data.getClientUUID(),
				data.getIdentityPublicKey(), data.getClientId(),
				data.getServerAddress(), data.getDeviceModel(),
				data.getDeviceOS(), data.getGameVersion(),
				data.getGuiScale(), data.getLanguageCode(),
				data.getXUID(), data.getCurrentInputMode(),
				data.getDefaultInputMode(), data.getUIProfile()
		);
		String expecting = "userName=lmlstarqaq, clientUUID=8323afe1-641e-3b61-9a92-d5d20b279065, " +
				"identityPublicKey=MHYwEAYHKoZIzj0CAQYFK4EEACIDYgAE4lyvA1iVhV2u3pLQqJAjJnJZSlSjib8mM1uB5h5yqOBSvCHW+nZxDmkOAW6MS1GA7yGHitGmfS4jW/yUISUdWvLzEWJYOzphb3GNh5J1oLJRwESc5278i4MEDk1y21/q, " +
				"clientId=-6315607246631494544, " +
				"serverAddress=192.168.1.108:19132, deviceModel=iPhone6,2, " +
				"deviceOS=2, gameVersion=1.1.0, " +
				"guiScale=0, languageCode=zh_CN, " +
				"xuid=2535465134455915, currentInputMode=2, " +
				"defaultInputMode=2, UIProfile=1";
		assertEquals(got, expecting);
	}

	private static byte[] readStream(InputStream inStream) throws Exception {
		ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
		byte[] buffer = new byte[65536];
		int len;
		while ((len = inStream.read(buffer)) != -1) {
			outSteam.write(buffer, 0, len);
		}
		outSteam.close();
		inStream.close();
		return outSteam.toByteArray();
	}
}
