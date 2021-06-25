package cn.nukkit.network.protocol;

import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@ToString
public class PurchaseReceiptPacket extends DataPacket {

    public List<String> receipts = new ArrayList<>();

    @Override
    public byte pid() {
        return ProtocolInfo.PURCHASE_RECEIPT_PACKET;
    }

    @Override
    public void decode() {
    	for (int i = 0, count = this.getUnsignedVarInt(); i < count; i++) {
    	    this.receipts.add(this.getString());
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
