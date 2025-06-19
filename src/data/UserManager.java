package data;
import java.io.*;
import java.util.*;

public class UserManager {
    private static final String FILE_NAME = "user_data/users.properties";
    private final Properties props = new Properties();

    public UserManager() {
        load();
    }

    private void load() {
        try (FileReader reader = new FileReader(FILE_NAME)) {
            props.load(reader);
        } catch (IOException ignored) {
            // 처음 실행 시 파일이 없을 수도 있음
        }
    }

    private void save() {
        try (FileWriter writer = new FileWriter(FILE_NAME)) {
            props.store(writer, "User Passwords");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isExistingUser(String username) {
        return props.containsKey(username);
    }

    public boolean isPasswordCorrect(String username, String inputPassword) {
        return props.getProperty(username, "").equals(inputPassword);
    }

    public void registerNewUser(String username, String password) {
        props.setProperty(username, password);
        save();
    }
}
