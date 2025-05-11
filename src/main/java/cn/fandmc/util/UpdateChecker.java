package cn.fandmc.util;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;

public class UpdateChecker {
    private static final String GITHUB_API = "https://api.github.com/repos/%s/releases/latest";

    private final String repo;
    private final String currentVersion;
    private final Logger logger;
    public UpdateChecker(String repo, String currentVersion, Logger logger) {
        this.repo = repo;
        this.currentVersion = currentVersion;
        this.logger = logger;
    }

    public String getLatestVersion() {
        try {
            URL url = new URL(String.format(GITHUB_API, repo));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "FlameTech-Plugin/" + currentVersion);
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            int responseCode = conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line);
                }
                reader.close();
                JSONObject json = (JSONObject) new JSONParser().parse(content.toString());
                Object tagNameObj = json.get("tag_name");
                if (tagNameObj instanceof String) {
                    String tag = (String) tagNameObj;
                    return tag;
                } else {
                    logger.warning("tag_name Error! " +
                            (tagNameObj != null ? tagNameObj.getClass().getName() : "null"));
                    return null;
                }
            } else {
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                StringBuilder errorContent = new StringBuilder();
                String errorLine;
                while ((errorLine = errorReader.readLine()) != null) {
                    errorContent.append(errorLine);
                }
                errorReader.close();
                logger.warning("GitHub API return: " + errorContent);
                return null;
            }
        } catch (Exception e) {
            logger.warning("Error! " + e);
            e.printStackTrace();
            return null;
        }
    }
}
