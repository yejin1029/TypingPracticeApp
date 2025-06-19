package ui;
import javax.swing.*;
import java.awt.*;

public class ResultPanel extends JPanel {
    private final JTextArea resultText;

    public interface ResultPanelCallback {
        void onRetry();
        void onGoHome();
    }

    public ResultPanel(ResultPanelCallback callback) {
        setLayout(new BorderLayout());

        resultText = new JTextArea();
        resultText.setEditable(false);
        resultText.setFont(new Font("Monospaced", Font.PLAIN, 14));
        add(new JScrollPane(resultText), BorderLayout.CENTER);

        JPanel btns = new JPanel();
        JButton retryBtn = new JButton("다시하기");
        JButton homeBtn = new JButton("처음으로");

        retryBtn.addActionListener(e -> callback.onRetry());
        homeBtn.addActionListener(e -> callback.onGoHome());

        btns.add(retryBtn);
        btns.add(homeBtn);
        add(btns, BorderLayout.SOUTH);
    }

    public void setResultText(String text) {
        resultText.setText(text);
    }
}
