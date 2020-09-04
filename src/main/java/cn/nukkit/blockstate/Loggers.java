package cn.nukkit.blockstate;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author joserobjr
 */
@PowerNukkitOnly("Only for internal use")
@Since("1.4.0.0-PN")
final class Loggers {
    private Loggers(){ throw new UnsupportedOperationException(); }
    
    static final Logger logIBlockState = LogManager.getLogger(IBlockState.class);
    static final Logger logIMutableBlockState = LogManager.getLogger(IMutableBlockState.class);
}
