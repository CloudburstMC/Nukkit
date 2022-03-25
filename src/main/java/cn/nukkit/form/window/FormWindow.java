package cn.nukkit.form.window;

import cn.nukkit.form.handler.FormResponseHandler;
import cn.nukkit.form.response.FormResponse;
import com.google.gson.Gson;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;

public abstract class FormWindow {

    protected static final Gson GSON = new Gson();

    protected transient boolean closed = false;
    protected final transient List<FormResponseHandler> handlers = new ObjectArrayList<>();

    public String getJSONData() {
        return FormWindow.GSON.toJson(this);
    }

    public abstract void setResponse(String data);

    public abstract FormResponse getResponse();

    public boolean wasClosed() {
        return closed;
    }

    public void addHandler(FormResponseHandler handler) {
        this.handlers.add(handler);
    }

    public List<FormResponseHandler> getHandlers() {
        return handlers;
    }

}
