package cn.nukkit.utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HastebinUtility {

    public static final String BIN_URL = "https://hastebin.com/documents", USER_AGENT = "Mozilla/5.0";
    public static final Pattern PATTERN = Pattern.compile("\\{\"key\":\"([\\S\\s]*)\"}");

    public static String upload(final String string) throws IOException {
        final URL url = new URL(BIN_URL);
        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("User-Agent", USER_AGENT);
        connection.setDoOutput(true);

        try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {
            outputStream.write(string.getBytes());
            outputStream.flush();
        }

        StringBuilder response;
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            response = new StringBuilder();

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
        }

        Matcher matcher = PATTERN.matcher(response.toString());
        if (matcher.matches()) {
            return "https://hastebin.com/" + matcher.group(1);
        } else {
            throw new RuntimeException("Couldn't read response!");
        }
    }

    public static String upload(final File file) throws IOException {
        final StringBuilder content = new StringBuilder();
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int i = 0;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }
        for (int i = Math.max(0, lines.size() - 1000); i < lines.size(); i++) {
            content.append(lines.get(i)).append("\n");
        }
        return upload(content.toString());
    }

}