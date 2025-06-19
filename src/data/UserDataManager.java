package data;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class UserDataManager {
    private static final String DIR_NAME = "user_data";

    public UserDataManager() {
        File dir = new File(DIR_NAME);
        if (!dir.exists()) dir.mkdirs();
    }

    private File getUserFile(String username) {
        return new File(DIR_NAME + File.separator + username + ".json");
    }

    private JSONObject loadUserData(String username) {
        File file = getUserFile(username);
        if (!file.exists()) return new JSONObject();

        try {
            String content = new String(Files.readAllBytes(file.toPath()));
            return new JSONObject(content);
        } catch (IOException e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }

    private void saveUserData(String user, JSONObject obj) {
        try (FileWriter fw = new FileWriter(getUserFile(user))) {
            fw.write(obj.toString(4)); // 들여쓰기 포함
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 최고기록 저장
    public void saveIfHighScore(String username, int count, double acc, double wpm, double kpm) {
        JSONObject obj = loadUserData(username);

        obj.put("maxCount", Math.max(obj.optInt("maxCount", 0), count));
        obj.put("maxAccuracy", Math.max(obj.optDouble("maxAccuracy", 0), acc));
        obj.put("maxWPM", Math.max(obj.optDouble("maxWPM", 0), wpm));
        obj.put("maxKPM", Math.max(obj.optDouble("maxKPM", 0), kpm));

        saveUserData(username, obj);
    }

    // 기록 추가
    public void addHistory(String username, String mode, double acc, double wpm, double kpm) {
        JSONObject obj = loadUserData(username);

        JSONArray history = obj.optJSONArray("history");
        if (history == null) history = new JSONArray();

        JSONObject record = new JSONObject();
        record.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        record.put("mode", mode);
        record.put("accuracy", acc);
        record.put("wpm", wpm);
        record.put("kpm", kpm);

        history.put(record);
        obj.put("history", history);

        saveUserData(username, obj);
    }

    // 기록 불러오기
    public List<String> getHistory(String username) {
        List<String> result = new ArrayList<>();
        JSONObject obj = loadUserData(username);
        JSONArray history = obj.optJSONArray("history");
        if (history == null) return result;

        for (int i = 0; i < history.length(); i++) {
            JSONObject r = history.getJSONObject(i);
            String line = String.format("📅 %s | %s | 정확도: %.1f%% | WPM: %.1f | 타수: %.0f",
                    r.getString("timestamp"), r.getString("mode"), r.getDouble("accuracy"), r.getDouble("wpm"), r.getDouble("kpm"));
            result.add(line);
        }

        return result;
    }

    // 전체 사용자 최고기록 정렬
    public List<String[]> getAllHighScoresSortedByKPM() {
        List<String[]> list = new ArrayList<>();
        File folder = new File(DIR_NAME);

        for (File file : Objects.requireNonNull(folder.listFiles())) {
            if (file.getName().endsWith(".json")) {
                try {
                    String name = file.getName().replace(".json", "");
                    JSONObject obj = new JSONObject(new String(Files.readAllBytes(file.toPath())));
                    double kpm = obj.optDouble("maxKPM", 0);
                    list.add(new String[]{name, String.valueOf((int) kpm)});
                } catch (Exception ignored) {}
            }
        }

        list.sort((a, b) -> Double.compare(Double.parseDouble(b[1]), Double.parseDouble(a[1])));
        return list;
    }

    public String getHighScoreSummary(String user) {
        JSONObject obj = loadUserData(user);

        return String.format(
            "\n\n[%s]의 최고 기록\n문장 수: %d  정확도: %.1f%%  WPM: %.1f  타수: %.0f",
            user,
            obj.optInt("maxCount", 0),
            obj.optDouble("maxAccuracy", 0),
            obj.optDouble("maxWPM", 0),
            obj.optDouble("maxKPM", 0)
        );
    }
}
