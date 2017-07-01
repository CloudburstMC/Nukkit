package cn.nukkit.utils.bugreport;

import cn.nukkit.Nukkit;
import cn.nukkit.Server;
import cn.nukkit.lang.BaseLang;
import cn.nukkit.utils.Utils;
import oshi.SystemInfo;
import oshi.hardware.HWDiskStore;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

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
        BaseLang baseLang = Server.getInstance().getLanguage();
        try {
            Server.getInstance().getLogger().info("[BugReport] " + baseLang.translateString("nukkit.bugreport.create"));
            String path = generate();
            Server.getInstance().getLogger().info("[BugReport] " + baseLang.translateString("nukkit.bugreport.archive", path));
        } catch (Exception e) {
            StringWriter stringWriter = new StringWriter();
            e.printStackTrace(new PrintWriter(stringWriter));
            Server.getInstance().getLogger().info("[BugReport] " + baseLang.translateString("nukkit.bugreport.error", stringWriter.toString()));
        }
    }

    private String generate() throws IOException {
        File reports = new File(Nukkit.DATA_PATH, "logs/bug_reports");
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
        content = content.replace("${NUKKIT_VERSION}", Nukkit.VERSION);
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

    //Code section from SOF
    public static String getCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

}
