package cn.nukkit.form;

import cn.nukkit.form.util.FormType;
import cn.nukkit.player.Player;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;

import javax.annotation.Nonnull;

@Getter
public class ModalForm extends Form<Boolean> {

    private final String trueValue;
    private final String falseValue;
    private final String content;

    public ModalForm(String title, String content, String trueValue, String falseValue) {
        super(FormType.MODAL, title);
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
            return new ModalForm(title, content, trueValue, falseValue);
        }

        @Override
        protected ModalFormBuilder self() {
            return this;
        }
    }
}
