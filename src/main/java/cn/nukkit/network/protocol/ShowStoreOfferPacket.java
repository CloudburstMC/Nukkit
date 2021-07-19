package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class ShowStoreOfferPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.SHOW_STORE_OFFER_PACKET;

    public String offerId;
    public boolean showAll;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.offerId = this.getString();
        this.showAll = this.getBoolean();
    }

    @Override
    public void encode() {
        this.reset();
        this.putString(this.offerId);
        this.putBoolean(this.showAll);
    }
}
