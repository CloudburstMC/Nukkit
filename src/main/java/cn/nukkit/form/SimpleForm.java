package cn.nukkit.form;

import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.util.FormType;
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

    public static class SimpleFormBuilder extends FormBuilder<SimpleForm, SimpleFormBuilder> {

        private final List<ElementButton> buttons = new ArrayList<>();

        @Override
        public SimpleForm build() {
            return new SimpleForm(title, buttons);
        }
    }
}
