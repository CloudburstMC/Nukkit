package cn.nukkit.form;

import cn.nukkit.form.CustomForm.CustomFormBuilder;
import cn.nukkit.form.ModalForm.ModalFormBuilder;
import cn.nukkit.form.SimpleForm.SimpleFormBuilder;
import cn.nukkit.form.util.FormType;
import cn.nukkit.player.Player;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

@RequiredArgsConstructor
@Getter
public class Form {

    private final FormType type;
    private final String title;

    private final List<Consumer<Player>> listeners = new LinkedList<>();

    public static SimpleFormBuilder simple() {
        return new SimpleFormBuilder();
    }

    public static ModalFormBuilder modal() {
        return new ModalFormBuilder();
    }

    public static CustomFormBuilder custom() {
        return new CustomFormBuilder();
    }

    public static abstract class FormBuilder<F extends Form, T extends FormBuilder<F, T>> {

        private final T self;

        protected String title = "";

        private final List<Consumer<Player>> listeners = new LinkedList<>();

        @SuppressWarnings("unchecked")
        public FormBuilder() {
            self = (T) this;
        }

        public T title(@Nonnull String title) {
            this.title = title;
            return self;
        }

        public T onSubmit(Consumer<Player> listener) {
            this.listeners.add(listener);
            return self;
        }

        public abstract F build();
    }
}
