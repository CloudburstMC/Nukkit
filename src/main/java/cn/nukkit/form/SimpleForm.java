package cn.nukkit.form;

import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.response.SimpleFormResponse;
import cn.nukkit.form.util.FormType;
import cn.nukkit.player.Player;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class SimpleForm extends Form {

    private final List<ElementButton> buttons;

    public SimpleForm(String title, List<ElementButton> buttons) {
        super(FormType.SIMPLE, title);
        this.buttons = buttons;
    }

    public ElementButton getButton(int index) {
        return buttons.size() > index ? buttons.get(index) : null;
    }

    @Override
    public void handleResponse(Player p, String data) {
        try {
            int clicked = Integer.parseInt(data);
            submit(p, new SimpleFormResponse(clicked, getButton(clicked)));
        } catch (NumberFormatException e) {
            error(p);
        }
    }

    public static class SimpleFormBuilder extends FormBuilder<SimpleForm, SimpleFormBuilder, SimpleFormResponse> {

        private final List<ElementButton> buttons = new ArrayList<>();

        @Override
        public SimpleForm build() {
            return new SimpleForm(title, buttons);
        }
    }
}
