package logic;

public class TypingSession {
    private final String target;
    private String userInput;
    private final long startTime;
    private long endTime;

    public TypingSession(String target) {
        this.target = target.trim();
        this.startTime = System.currentTimeMillis();
    }

    public void endSession(String userInput) {
        this.userInput = userInput.trim();
        this.endTime = System.currentTimeMillis();
    }

    public double getTimeSeconds() {
        return (endTime - startTime) / 1000.0;
    }

    public double getAccuracy() {
        int correct = 0;
        int len = Math.min(userInput.length(), target.length());
        for (int i = 0; i < len; i++) {
            if (userInput.charAt(i) == target.charAt(i)) {
                correct++;
            }
        }
        return (correct / (double) target.length()) * 100.0;
    }

    public double getWPM() {
        int wordCount = target.split("\\s+").length;
        return (wordCount / getTimeSeconds()) * 60.0;
    }

    public double getKPM() {
        return (userInput.length() / getTimeSeconds()) * 60.0;
    }

    public String getTarget() {
        return target;
    }

    public String getUserInput() {
        return userInput;
    }
}
