package logic;

public class ResultStats {
    private int sessionCount = 0;
    private double totalAccuracy = 0.0;
    private double totalWPM = 0.0;
    private double totalKPM = 0.0;
    public double getTotalAccuracy() { return totalAccuracy; }
    public double getTotalWPM() { return totalWPM; }
    public double getTotalKPM() { return totalKPM; }

    public void addSession(double accuracy, double wpm, double kpm) {
        totalAccuracy += accuracy;
        totalWPM += wpm;
        totalKPM += kpm;
        sessionCount++;
    }

    public int getSessionCount() {
        return sessionCount;
    }

    public double getAverageAccuracy() {
        return sessionCount == 0 ? 0 : totalAccuracy / sessionCount;
    }

    public double getAverageWPM() {
        return sessionCount == 0 ? 0 : totalWPM / sessionCount;
    }

    public double getAverageKPM() {
        return sessionCount == 0 ? 0 : totalKPM / sessionCount;
    }

    public void reset() {
        sessionCount = 0;
        totalAccuracy = totalWPM = totalKPM = 0.0;
    }

    public String getSummaryText() {
        return String.format(
            "\uD83C\uDFC6 최종 결과\n\n" +
            "✅ 입력한 문장 수: %d\n" +
            "🎯 평균 정확도: %.1f%%\n" +
            "📈 평균 WPM: %.1f\n" +
            "⌨ 평균 타수: %.0f",
            sessionCount,
            getAverageAccuracy(),
            getAverageWPM(),
            getAverageKPM()
        );
    }
}
