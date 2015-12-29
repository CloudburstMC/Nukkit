package cn.nukkit.plugin.certification;

import java.io.Serializable;

/**
 * Created by iNevet.
 */
public abstract class PluginCertificate extends Thread implements Serializable {

    protected transient boolean localCertificated = false;

    protected String certificate;
    protected boolean certificated;
    protected String encryptType;

}
