package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class PurchaseReceiptPacket extends DataPacket {

    public String[] receipts = new String[0]

    @Override
    public byte pid() {
        return ProtocolInfo.PURCHASE_RECEIPT_PACKET;
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
        this.putUnsignedVarInt(this.receipts.size());
        for (String receipt : this.receipts) {
            this.putString(receipt);
        }
    }
}
