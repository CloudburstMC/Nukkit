package cn.nukkit.form.handler;

import cn.nukkit.Player;

import java.util.function.IntConsumer;

public interface FormResponseHandler {

    static FormResponseHandler withoutPlayer(IntConsumer formIDConsumer) {
        return (player, formID) -> formIDConsumer.accept(formID);
    }

    void handle(Player player, int formID);

}
