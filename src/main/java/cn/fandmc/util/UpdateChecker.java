package cn.fandmc.util;

import org.bukkit.plugin.java.JavaPlugin;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
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
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()))) {
                    JSONObject response = (JSONObject) new JSONParser().parse(reader);
                    return (String) response.get("tag_name");
                }
            }
        } catch (Exception e) {
            logger.warning("更新检查异常: " + e.getMessage());
        }
        return null;
    }
}
