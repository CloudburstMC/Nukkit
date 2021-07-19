package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class PurchaseReceiptPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.PURCHASE_RECEIPT_PACKET;

    public String[] receipts = new String[0];

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        int count = (int) this.getUnsignedVarInt();
        this.receipts = new String[count];
        for (int i = 0; i < count; i++) {
            this.receipts[i] = this.getString();
        }
    }

    @Override
    public void encode() {
        this.reset();
        this.putUnsignedVarInt(this.receipts.length);
        for (String receipt : this.receipts) {
            this.putString(receipt);
        }
    }
}
