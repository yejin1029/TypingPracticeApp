package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;
import logic.TypingUtils;

public class DifficultySelectionPanel extends JPanel {
    public interface DifficultySelectionCallback {
        void onDifficultyChosen(List<String> sentences, boolean isContinuous);
    }

    public DifficultySelectionPanel(CardLayout cardLayout, JPanel mainPanel, boolean isContinuousMode, DifficultySelectionCallback callback) {
        setLayout(new BorderLayout());

        JLabel label = new JLabel("난이도를 선택하세요", SwingConstants.CENTER);
        label.setFont(new Font("Dialog", Font.BOLD, 24));
        add(label, BorderLayout.NORTH);

        JPanel buttons = new JPanel(new GridLayout(1, 3, 20, 20));
        JButton easyBtn = new JButton("쉬움");
        JButton normalBtn = new JButton("보통");
        JButton hardBtn = new JButton("어려움");

        ActionListener listener = e -> {
            String file = "sentences/sentences_easy.txt";
            if (e.getSource() == normalBtn) file = "sentences/sentences_medium.txt";
            else if (e.getSource() == hardBtn) file = "sentences/sentences_hard.txt";

            List<String> sentences = TypingUtils.loadSentencesFromFile(file);
            callback.onDifficultyChosen(sentences, isContinuousMode);
            cardLayout.show(mainPanel, "typing");
        };

        easyBtn.addActionListener(listener);
        normalBtn.addActionListener(listener);
        hardBtn.addActionListener(listener);

        buttons.add(easyBtn);
        buttons.add(normalBtn);
        buttons.add(hardBtn);

        add(buttons, BorderLayout.CENTER);
        setBorder(BorderFactory.createEmptyBorder(100, 100, 100, 100));
    }
}
