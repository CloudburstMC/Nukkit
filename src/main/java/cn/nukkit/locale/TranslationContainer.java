package cn.nukkit.locale;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class TranslationContainer extends TextContainer implements Cloneable {

    protected Object[] params;

    public TranslationContainer(String text) {
        this(text, new Object[0]);
    }

    public TranslationContainer(String text, Object params) {
        super(text);
        this.setParameters(new Object[]{params});
    }

    public TranslationContainer(String text, Object... params) {
        super(text);
        this.setParameters(params);
    }

    public Object[] getParameters() {
        return params;
    }

    public void setParameters(Object[] params) {
        this.params = params;
    }

    public Object getParameter(int i) {
        return (i >= 0 && i < this.params.length) ? this.params[i] : null;
    }

    public void setParameter(int i, Object obj) {
        if (i >= 0 && i < this.params.length) {
            this.params[i] = obj;
        }
    }

    @Override
    public TranslationContainer clone() {
        return new TranslationContainer(this.text, this.params.clone());
    }
}
