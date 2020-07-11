package cn.nukkit.utils;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

import java.util.Comparator;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class HumanStringComparator implements Comparator<String> {
    private static final HumanStringComparator INSTANCE = new HumanStringComparator();
    
    public int compare(String o1, String o2) {

        String o1StringPart = o1.replaceAll("\\d", "");
        String o2StringPart = o2.replaceAll("\\d", "");


        if (o1StringPart.equalsIgnoreCase(o2StringPart)) {
            return extractInt(o1) - extractInt(o2);
        }
        return o1.compareTo(o2);
    }

    int extractInt(String s) {
        String num = s.replaceAll("\\D", "");
        // return 0 if no digits found
        try {
            return num.isEmpty() ? 0 : Integer.parseInt(num);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static HumanStringComparator getInstance() {
        return INSTANCE;
    }
}
