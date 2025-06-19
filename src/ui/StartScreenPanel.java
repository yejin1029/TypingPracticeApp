package ui;
import javax.swing.*;

import data.UserDataManager;

import java.awt.*;
import java.util.List;

public class StartScreenPanel extends JPanel {
    private JTextArea rankingArea;

    public StartScreenPanel(CardLayout cardLayout, JPanel mainPanel, UserDataManager userDataManager) {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Typing Practice App", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        add(title, BorderLayout.NORTH);

        rankingArea = new JTextArea();
        rankingArea.setEditable(false);
        rankingArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        List<String[]> scores = userDataManager.getAllHighScoresSortedByKPM();
        StringBuilder sb = new StringBuilder("\uD83C\uDFC6 최고 타수 랭킹\n\n");
        int rank = 1;
        for (String[] entry : scores) {
            sb.append(String.format("%2d위. %-10s : %s 타수\n", rank++, entry[0], entry[1]));
        }
        rankingArea.setText(sb.toString());
        add(new JScrollPane(rankingArea), BorderLayout.CENTER);

        JButton startBtn = new JButton("START");
        startBtn.setFont(new Font("Arial", Font.BOLD, 18));
        startBtn.addActionListener(e -> cardLayout.show(mainPanel, "login"));

        JPanel btnPanel = new JPanel();
        btnPanel.add(startBtn);
        add(btnPanel, BorderLayout.SOUTH);

        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    public void updateRanking(UserDataManager userDataManager) {
        List<String[]> scores = userDataManager.getAllHighScoresSortedByKPM();
        StringBuilder sb = new StringBuilder("\uD83C\uDFC6 최고 타수 랭킹\n\n");
        int rank = 1;
        for (String[] entry : scores) {
            sb.append(String.format("%2d위. %-10s : %s 타수\n", rank++, entry[0], entry[1]));
        }
        rankingArea.setText(sb.toString());
    }
}
