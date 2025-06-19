package logic;

import java.util.*;

public class SentenceManager {
    private final List<String> allSentences;  // 전체 문장 목록
    private List<String> pool;                // 현재 순환 중인 문장 풀
    private String lastSentence = null;       // 직전 문장

    public SentenceManager(List<String> sentenceList) {
        this.allSentences = new ArrayList<>(sentenceList);
        resetPool();
    }

    private void resetPool() {
        int attempts = 0;
        do {
            pool = new ArrayList<>(allSentences);
            Collections.shuffle(pool);
            attempts++;
        } while (!pool.isEmpty() && pool.get(0).equals(lastSentence) && attempts < 10);
    }

    public String getNextSentence() {
        if (pool.isEmpty()) {
            resetPool();
        }

        String next = pool.remove(0);
        lastSentence = next;
        return next;
    }

    public void reset() {
        lastSentence = null;
        resetPool();
    }
}