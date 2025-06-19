package app;
import javax.swing.*;

import ui.*;
import logic.*;
import data.*;

import java.awt.*;
import java.util.List;

public class TypingPracticeApp {
    private JFrame frame;
    private JPanel mainPanel;
    private CardLayout cardLayout;

    private boolean isContinuousMode = false;
    private SentenceManager sentenceManager;
    private ResultStats resultStats = new ResultStats();
    private TypingSession currentSession;
    private String userName = "Guest";
    private StartScreenPanel startScreenPanel;

    private final UserManager userManager = new UserManager();
    private final UserDataManager userDataManager = new UserDataManager();

    private TypingPanel typingPanel;
    private ResultPanel resultPanel;
    private int practiceTimeSeconds = 60;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TypingPracticeApp().initUI());
    }

    public void initUI() {
        frame = new JFrame("타자 연습 프로그램");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(720, 500);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(new SettingsPanel(cardLayout, mainPanel, selectedTime -> {
            practiceTimeSeconds = selectedTime;

            // 연속 모드용 DifficultySelectionPanel 새로 생성
            DifficultySelectionPanel difficultyPanel = new DifficultySelectionPanel(cardLayout, mainPanel, true, (sentences, isCont) -> {
                isContinuousMode = isCont;
                sentenceManager = new SentenceManager(sentences);
                resultStats.reset();
                typingPanel.setMode(true);
                nextSentence();
                typingPanel.startTimer(
                    practiceTimeSeconds,
                    () -> {
                        showFinalResult();
                        cardLayout.show(mainPanel, "result");
                    },
                    (session, isFinal) -> handleSessionEnd(session, isFinal)
                );
                cardLayout.show(mainPanel, "typing");
            });

            mainPanel.add(difficultyPanel, "difficulty");
            cardLayout.show(mainPanel, "difficulty");
        }), "settings");

        mainPanel.add(new DifficultySelectionPanel(cardLayout, mainPanel, true, (sentences, isCont) -> {
            isContinuousMode = isCont;
            sentenceManager = new SentenceManager(sentences);
            resultStats.reset();
            typingPanel.setMode(true);
            nextSentence();
            typingPanel.startTimer(
                practiceTimeSeconds, // 사용자가 고른 시간
                () -> {
                    showFinalResult();
                    cardLayout.show(mainPanel, "result");
                },
                (session, isFinal) -> handleSessionEnd(session, isFinal)
            );
            cardLayout.show(mainPanel, "typing");
        }), "difficulty");

        startScreenPanel = new StartScreenPanel(cardLayout, mainPanel, userDataManager);
        mainPanel.add(startScreenPanel, "startScreen");

        mainPanel.add(new LoginPanel(cardLayout, mainPanel, userManager, name -> {
            userName = name;
        }), "login");

        mainPanel.add(new ModeSelectionPanel(cardLayout, mainPanel, new ModeSelectionPanel.ModeSelectionCallback() {
            @Override
            public void onSingleModeSelected(List<String> sentences) {
                isContinuousMode = false;
                sentenceManager = new SentenceManager(sentences);
                resultStats.reset();
                typingPanel.setMode(false);
                nextSentence();
                cardLayout.show(mainPanel, "typing");
            }

            @Override
            public void onContinuousModeSelected(List<String> sentences) {
                isContinuousMode = true;

                sentenceManager = new SentenceManager(sentences);
                resultStats.reset();

                typingPanel.setMode(true);
                nextSentence();

                typingPanel.startTimer(
                    practiceTimeSeconds, // 사용자가 고른 시간
                    () -> {
                        showFinalResult();
                        cardLayout.show(mainPanel, "result");
                    },
                    (session, isFinal) -> handleSessionEnd(session, isFinal)
                );
            }

            @Override
            public void onViewHistoryRequested() {
                showUserHistoryPopup();
            }
        }), "modeSelection");

        typingPanel = new TypingPanel((session, isFinal) -> handleSessionEnd(session, isFinal));
        mainPanel.add(typingPanel, "typing");

        resultPanel = new ResultPanel(new ResultPanel.ResultPanelCallback() {
            @Override
            public void onRetry() {
                cardLayout.show(mainPanel, "modeSelection");
            }

            @Override
            public void onGoHome() {
                cardLayout.show(mainPanel, "startScreen");
            }
        });
        mainPanel.add(resultPanel, "result");

        frame.setContentPane(mainPanel);
        cardLayout.show(mainPanel, "startScreen");
        frame.setVisible(true);
    }

    private void nextSentence() {
        String next = sentenceManager.getNextSentence();
        if (next == null || next.isBlank()) {
            JOptionPane.showMessageDialog(frame, "문장을 불러올 수 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        currentSession = new TypingSession(next);
        typingPanel.setNextSentence(next);
    }

    private void handleSessionEnd(TypingSession session, boolean isFinal) {
        currentSession = session;
        resultStats.addSession(session.getAccuracy(), session.getWPM(), session.getKPM());
        typingPanel.updateSessionStats(session);

        if (isFinal) {
            // 단일 모드에서도 종료 소리 재생
            SoundPlayer.play("sounds/ding.wav");

            showFinalResult();
            cardLayout.show(mainPanel, "result");
        } else {
            nextSentence();
        }
    }

    private void showFinalResult() {
        userDataManager.saveIfHighScore(
            userName,
            resultStats.getSessionCount(),
            resultStats.getAverageAccuracy(),
            resultStats.getAverageWPM(),
            resultStats.getAverageKPM()
        );

        userDataManager.addHistory(
            userName,
            isContinuousMode ? "연속 모드" : "단일 모드",
            currentSession.getAccuracy(),
            currentSession.getWPM(),
            currentSession.getKPM()
        );

        String summary = resultStats.getSummaryText() + userDataManager.getHighScoreSummary(userName);
        resultPanel.setResultText(summary);
        startScreenPanel.updateRanking(userDataManager);
    }

    private void showUserHistoryPopup() {
        List<String> history = userDataManager.getHistory(userName);
        if (history.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "저장된 기록이 없습니다.", "기록 없음", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JTextArea textArea = new JTextArea();
            textArea.setEditable(false);
            for (String line : history) {
                textArea.append(line + "\n");
            }

            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(500, 300));
            JOptionPane.showMessageDialog(frame, scrollPane, userName + "님의 기록", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
