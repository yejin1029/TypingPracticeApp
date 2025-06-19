package ui;
import javax.swing.*;

import data.UserManager;

import java.awt.*;

public class LoginPanel extends JPanel {
    private JTextField loginNameField;
    private JPasswordField passwordField;
    private JLabel loginStatusLabel;

    public interface LoginCallback {
        void onLoginSuccess(String userName);
    }

    public LoginPanel(CardLayout cardLayout, JPanel mainPanel, UserManager userManager, LoginCallback callback) {
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(40, 100, 10, 100));

        loginNameField = new JTextField(15);
        passwordField = new JPasswordField(15);
        loginStatusLabel = new JLabel("", SwingConstants.CENTER);
        loginStatusLabel.setForeground(Color.RED);

        inputPanel.add(new JLabel("사용자 이름:"));
        inputPanel.add(loginNameField);
        inputPanel.add(new JLabel("비밀번호:"));
        inputPanel.add(passwordField);

        add(inputPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 100, 40, 100));

        JButton loginBtn = new JButton("Login");
        loginBtn.setPreferredSize(new Dimension(100, 40));
        loginBtn.addActionListener(e -> {
            String name = loginNameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            if (name.isEmpty() || password.isEmpty()) {
                loginStatusLabel.setText("이름과 비밀번호를 모두 입력하세요.");
                return;
            }

            if (userManager.isExistingUser(name)) {
                if (userManager.isPasswordCorrect(name, password)) {
                    loginStatusLabel.setText("");
                    callback.onLoginSuccess(name);
                    cardLayout.show(mainPanel, "modeSelection");
                } else {
                    loginStatusLabel.setText("비밀번호가 틀렸습니다.");
                }
            } else {
                userManager.registerNewUser(name, password);
                loginStatusLabel.setText("");
                callback.onLoginSuccess(name);
                cardLayout.show(mainPanel, "modeSelection");
            }
        });

        JPanel loginBtnWrapper = new JPanel();
        loginBtnWrapper.add(loginBtn);
        bottomPanel.add(loginBtnWrapper, BorderLayout.NORTH);

        JLabel guide = new JLabel("※ 새로운 사용자 등록 시 password가 저장됩니다.", SwingConstants.CENTER);
        bottomPanel.add(guide, BorderLayout.CENTER);
        bottomPanel.add(loginStatusLabel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);
    }
}
