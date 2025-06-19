package ui;
import javax.swing.*;
import javax.swing.text.*;

import logic.TypingSession;

import java.awt.*;
import java.awt.event.*;

public class TypingPanel extends JPanel {
    private final JTextPane inputPane;
    private final JTextArea sentenceArea;
    private final JLabel timeLabel, accLabel, wpmLabel, kpmLabel;
    private final JPanel infoPanel;
    private Timer timer;
    private int remainingSeconds = 60;

    private TypingSession currentSession;
    private boolean isContinuousMode = false;

    public interface TypingCallback {
        void onSessionEnd(TypingSession session, boolean isFinal);
    }

    public TypingPanel(TypingCallback callback) {
        setLayout(new BorderLayout());

        // 문장 출력 영역
        sentenceArea = new JTextArea();
        sentenceArea.setFont(new Font("Arial", Font.BOLD, 20));
        sentenceArea.setLineWrap(true); // 자동 줄바꿈
        sentenceArea.setWrapStyleWord(true); // 단어 단위 줄바꿈
        sentenceArea.setEditable(false);
        sentenceArea.setOpaque(false); // 배경 투명하게 해서 JLabel처럼
        sentenceArea.setFocusable(false);
        sentenceArea.setMargin(new Insets(10, 20, 10, 20));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setPreferredSize(new Dimension(720, 100)); // 필요에 따라 조정 가능
        topPanel.add(sentenceArea, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // 입력 영역
        inputPane = new JTextPane();
        inputPane.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setPreferredSize(new Dimension(720, 100));
        inputPanel.add(new JScrollPane(inputPane), BorderLayout.CENTER);
        add(inputPanel, BorderLayout.CENTER);

        // 정보 표시 영역
        infoPanel = new JPanel(new GridLayout(1, 4));
        timeLabel = new JLabel("시간: 60초");
        accLabel = new JLabel("정확도: 0%");
        wpmLabel = new JLabel("WPM: 0");
        kpmLabel = new JLabel("타수: 0");
        infoPanel.add(timeLabel);
        infoPanel.add(accLabel);
        infoPanel.add(wpmLabel);
        infoPanel.add(kpmLabel);
        add(infoPanel, BorderLayout.SOUTH);

        inputPane.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "submit");
        inputPane.getActionMap().put("submit", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!inputPane.isEditable()) return;

                // 타자 성공 소리
                logic.SoundPlayer.play("sounds/type.wav");

                currentSession.endSession(inputPane.getText());
                callback.onSessionEnd(currentSession, !isContinuousMode);
                if (isContinuousMode) {
                    setNextSentence(currentSession.getTarget());
                } else {
                    inputPane.setEditable(false);
                }
            }
        });

        inputPane.addKeyListener(new KeyAdapter() {
            private long lastSoundTime = 0;

            @Override
            public void keyReleased(KeyEvent e) {
                highlightMistakes();

                // 일정 간격(예: 100ms)마다만 소리 재생
                long now = System.currentTimeMillis();
                if (now - lastSoundTime > 100) {
                    logic.SoundPlayer.play("sounds/type.wav");
                    lastSoundTime = now;
                }
            }
        });
    }

    public void setNextSentence(String sentence) {
        inputPane.setText("");
        sentenceArea.setText(sentence);
        currentSession = new TypingSession(sentence);
        inputPane.setEditable(true);
        inputPane.requestFocus();
        if (infoPanel != null) infoPanel.setVisible(isContinuousMode);
    }

    public void setMode(boolean continuousMode) {
        this.isContinuousMode = continuousMode;
        infoPanel.setVisible(continuousMode);
        if (!continuousMode) {
            timeLabel.setText("시간: 60초");
            accLabel.setText("정확도: 0%");
            wpmLabel.setText("WPM: 0");
            kpmLabel.setText("타수: 0");
        }
    }

    public void startTimer(int seconds, Runnable onTimeOver, TypingCallback callback) {
        remainingSeconds = seconds;
        timeLabel.setText("시간: " + seconds + "초");

        timer = new Timer(1000, e -> {
            remainingSeconds--;
            timeLabel.setText("시간: " + remainingSeconds + "초");
            if (remainingSeconds <= 0) {
                timer.stop();
                // 종료 소리
                logic.SoundPlayer.play("sounds/ding.wav");

                if (inputPane.isEditable()) {
                    inputPane.setEditable(false);
                    currentSession.endSession(inputPane.getText());
                    callback.onSessionEnd(currentSession, true);
                }
                onTimeOver.run();
            }
        });
    timer.start();
    }

    public void updateSessionStats(TypingSession session) {
        accLabel.setText(String.format("정확도: %.1f%%", session.getAccuracy()));
        wpmLabel.setText(String.format("WPM: %.1f", session.getWPM()));
        kpmLabel.setText(String.format("타수: %.0f", session.getKPM()));
    }

    private void highlightMistakes() {
        String userInput = inputPane.getText();
        StyledDocument doc = inputPane.getStyledDocument();
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet good = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.BLACK);
        AttributeSet bad = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.RED);

        doc.setCharacterAttributes(0, userInput.length(), good, true);
        for (int i = 0; i < userInput.length(); i++) {
            if (i >= currentSession.getTarget().length() ||
                userInput.charAt(i) != currentSession.getTarget().charAt(i)) {
        
                doc.setCharacterAttributes(i, 1, bad, false);

                // 오타 소리
                logic.SoundPlayer.play("sounds/error.wav");
            }
        }
    }
}
