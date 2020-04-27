package cn.nukkit.form;

import cn.nukkit.form.util.FormType;
import cn.nukkit.player.Player;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.ToString;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Getter
@ToString
public class ModalForm extends Form<Boolean> {

    @JsonProperty("button1")
    private final String trueValue;
    @JsonProperty("button2")
    private final String falseValue;
    private final String content;

    public ModalForm(String title,
                     String content,
                     String trueValue,
                     String falseValue,
                     List<BiConsumer<Player, Boolean>> listeners,
                     List<Consumer<Player>> closeListeners,
                     List<Consumer<Player>> errorListeners
    ) {
        super(FormType.MODAL, title, listeners, closeListeners, errorListeners);
        this.content = content;
        this.trueValue = trueValue;
        this.falseValue = falseValue;
    }

    @Override
    public void handleResponse(Player p, JsonNode node) {
        if (!node.isBoolean()) {
            error(p);
            log.warn("Received invalid response for ModalForm {}", node);
            return;
        }

        submit(p, node.booleanValue());
    }

    public static class ModalFormBuilder extends FormBuilder<ModalForm, ModalFormBuilder, Boolean> {

        private String content = "";
        private String trueValue = "true";
        private String falseValue = "false";

        public ModalFormBuilder content(@Nonnull String content) {
            this.content = content;
            return this;
        }

        public ModalFormBuilder trueValue(@Nonnull String value) {
            this.trueValue = value;
            return this;
        }

        public ModalFormBuilder falseValue(@Nonnull String value) {
            this.falseValue = value;
            return this;
        }

        @Override
        public ModalForm build() {
            return new ModalForm(title, content, trueValue, falseValue, listeners, closeListeners, errorListeners);
        }

        @Override
        protected ModalFormBuilder self() {
            return this;
        }
    }
}
