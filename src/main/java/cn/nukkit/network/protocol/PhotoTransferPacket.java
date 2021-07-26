package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class PhotoTransferPacket extends DataPacket {

	public static final byte NETWORK_ID = ProtocolInfo.PHOTO_TRANSFER_PACKET;

	public String photoName;
	public String photoData;
	public String bookId; //Photos are stored in a sibling directory to the games folder (screenshots/(some UUID)/bookID/example.png)

	@Override
	public byte pid() {
		return NETWORK_ID;
	}

	@Override
	public void decode() {
		this.photoName = this.getString();
		this.photoData = this.getString();
		this.bookId = this.getString();
	}

	@Override
	public void encode() {
		this.reset();
		this.putString(this.photoName);
		this.putString(this.photoData);
		this.putString(this.bookId);
	}
}
