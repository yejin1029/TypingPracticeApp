package ui;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ModeSelectionPanel extends JPanel {
    public interface ModeSelectionCallback {
        void onSingleModeSelected(List<String> sentences);
        void onContinuousModeSelected(List<String> sentences);
        void onViewHistoryRequested();
    }

    public ModeSelectionPanel(CardLayout cardLayout, JPanel mainPanel, ModeSelectionCallback callback) {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("연습 모드를 선택하세요", SwingConstants.CENTER);
        title.setFont(new Font("Dialog", Font.BOLD, 24));
        add(title, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 40, 10));

        JButton singleBtn = new JButton("단일 문장 연습");
        JButton contBtn = new JButton("연속 문장 연습");
        JButton historyBtn = new JButton("지난 기록 보기");

        singleBtn.addActionListener(e -> {
            mainPanel.add(new DifficultySelectionPanel(cardLayout, mainPanel, false, (sentences, isCont) -> {
                callback.onSingleModeSelected(sentences);
            }), "difficulty");
            cardLayout.show(mainPanel, "difficulty");
        });

        contBtn.addActionListener(e -> {
            cardLayout.show(mainPanel, "settings"); // 시간 선택 화면으로 이동
        });
        historyBtn.addActionListener(e -> callback.onViewHistoryRequested());

        buttonPanel.add(singleBtn);
        buttonPanel.add(contBtn);

        JPanel southPanel = new JPanel();
        southPanel.add(historyBtn);

        add(buttonPanel, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);
        setBorder(BorderFactory.createEmptyBorder(100, 100, 100, 100));
    }
}
