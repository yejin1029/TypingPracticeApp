package ui;

import javax.swing.*;
import java.awt.*;

public class SettingsPanel extends JPanel {
    public interface TimeSelectionCallback {
        void onTimeSelected(int seconds);
    }

    public SettingsPanel(CardLayout cardLayout, JPanel mainPanel, TimeSelectionCallback callback) {
        setLayout(new BorderLayout());

        JLabel label = new JLabel("연습 시간을 선택하세요", SwingConstants.CENTER);
        label.setFont(new Font("Dialog", Font.BOLD, 22));
        add(label, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 20, 20));
        int[] times = {30, 60, 90};

        for (int sec : times) {
            JButton btn = new JButton(sec + "초");
            btn.addActionListener(e -> {
                callback.onTimeSelected(sec);
                cardLayout.show(mainPanel, "difficulty"); // 난이도 선택 화면으로 이동
            });
            buttonPanel.add(btn);
        }

        add(buttonPanel, BorderLayout.CENTER);
        setBorder(BorderFactory.createEmptyBorder(80, 100, 80, 100));
    }
}
