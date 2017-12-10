package cn.nukkit.server.utils.bugreport;

import cn.nukkit.server.Bootstrap;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.lang.BaseLang;
import cn.nukkit.server.utils.Utils;
import oshi.SystemInfo;
import oshi.hardware.HWDiskStore;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * Project nukkit
 */
public class BugReportGenerator extends Thread {

    private Throwable throwable;

    BugReportGenerator(Throwable throwable) {
        this.throwable = throwable;
    }

    @Override
    public void run() {
        BaseLang baseLang = NukkitServer.getInstance().getLanguage();
        try {
            log.info("[BugReport] " + baseLang.translateString("nukkit.bugreport.create"));
            String path = generate();
            log.info("[BugReport] " + baseLang.translateString("nukkit.bugreport.archive", path));
        } catch (Exception e) {
            StringWriter stringWriter = new StringWriter();
            e.printStackTrace(new PrintWriter(stringWriter));
            log.info("[BugReport] " + baseLang.translateString("nukkit.bugreport.error", stringWriter.toString()));
        }
    }

    private String generate() throws IOException {
        File reports = new File(Bootstrap.DATA_PATH, "logs/bug_reports");
        if (!reports.isDirectory()) {
            reports.mkdirs();
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmSS");
        String date = simpleDateFormat.format(new Date());

        SystemInfo systemInfo = new SystemInfo();
        long totalDiskSize = 0;
        StringBuilder model = new StringBuilder();
        for (HWDiskStore hwDiskStore : systemInfo.getHardware().getDiskStores()) {
            totalDiskSize += hwDiskStore.getSize();
            if (!model.toString().contains(hwDiskStore.getModel())) {
                model.append(hwDiskStore.getModel()).append(" ");
            }
        }

        StringWriter stringWriter = new StringWriter();
        throwable.printStackTrace(new PrintWriter(stringWriter));


        File mdReport = new File(reports, date + "_" + throwable.getClass().getSimpleName() + ".md");
        mdReport.createNewFile();
        String content = Utils.readFile(this.getClass().getClassLoader().getResourceAsStream("report_template.md"));

        Properties properties = getGitRepositoryState();
        System.out.println(properties.getProperty("git.commit.id.abbrev"));
        String abbrev = properties.getProperty("git.commit.id.abbrev");

        content = content.replace("${NUKKIT_VERSION}", Bootstrap.VERSION);
        content = content.replace("${GIT_COMMIT_ABBREV}", abbrev);
        content = content.replace("${JAVA_VERSION}", System.getProperty("java.vm.name") + " (" + System.getProperty("java.runtime.version") + ")");
        content = content.replace("${HOSTOS}", systemInfo.getOperatingSystem().getFamily() + " [" + systemInfo.getOperatingSystem().getVersion().getVersion() + "]");
        content = content.replace("${MEMORY}", getCount(systemInfo.getHardware().getMemory().getTotal(), true));
        content = content.replace("${STORAGE_SIZE}", getCount(totalDiskSize, true));
        content = content.replace("${CPU_TYPE}", systemInfo.getHardware().getProcessor().getName());
        content = content.replace("${PHYSICAL_CORE}", String.valueOf(systemInfo.getHardware().getProcessor().getPhysicalProcessorCount()));
        content = content.replace("${LOGICAL_CORE}", String.valueOf(systemInfo.getHardware().getProcessor().getLogicalProcessorCount()));
        content = content.replace("${STACKTRACE}", stringWriter.toString());
        content = content.replace("${PLUGIN_ERROR}", String.valueOf(!throwable.getStackTrace()[0].getClassName().startsWith("cn.nukkit")).toUpperCase());
        content = content.replace("${STORAGE_TYPE}", model.toString());

        Utils.writeFile(mdReport, content);

        return mdReport.getAbsolutePath();
    }

    public Properties getGitRepositoryState() throws IOException {
        Properties properties = new Properties();
        properties.load(getClass().getClassLoader().getResourceAsStream("git.properties"));
        return properties;
    }

    //Code section from SOF
    public static String getCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

}
