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
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@RequiredArgsConstructor
@Getter
public abstract class Form {

    private final FormType type;
    private final String title;

    private final List<BiConsumer<Player, Object>> listeners = new LinkedList<>();
    private final List<Consumer<Player>> closeListeners = new LinkedList<>();
    private final List<Consumer<Player>> errorListeners = new LinkedList<>();

    public static SimpleFormBuilder simple() {
        return new SimpleFormBuilder();
    }

    public static ModalFormBuilder modal() {
        return new ModalFormBuilder();
    }

    public static CustomFormBuilder custom() {
        return new CustomFormBuilder();
    }

    public abstract void handleResponse(Player p, String data);

    public void close(Player p) {
        for (Consumer<Player> closeListener : closeListeners) {
            closeListener.accept(p);
        }
    }

    public void submit(Player p, Object response) {
        if (response == null) {
            close(p);
            return;
        }

        for (BiConsumer<Player, Object> listener : listeners) {
            listener.accept(p, response);
        }
    }

    public void error(Player p) {
        for (Consumer<Player> errorListener : errorListeners) {
            errorListener.accept(p);
        }
    }

    public static abstract class FormBuilder<F extends Form, T extends FormBuilder<F, T, R>, R> {

        private final T self;

        protected String title = "";

        protected final List<BiConsumer<Player, R>> listeners = new LinkedList<>();
        protected final List<Consumer<Player>> closeListeners = new LinkedList<>();
        protected final List<Consumer<Player>> errorListeners = new LinkedList<>();

        @SuppressWarnings("unchecked")
        public FormBuilder() {
            self = (T) this;
        }

        public T title(@Nonnull String title) {
            this.title = title;
            return self;
        }

        public T onSubmit(BiConsumer<Player, R> listener) {
            this.listeners.add(listener);
            return self;
        }

        public T onClose(Consumer<Player> listener) {
            this.closeListeners.add(listener);
            return self;
        }

        public T onError(Consumer<Player> listener) {
            this.errorListeners.add(listener);
            return self;
        }

        public abstract F build();
    }
}
