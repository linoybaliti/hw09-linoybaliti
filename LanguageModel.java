import java.util.HashMap;
import java.util.Random;

public class LanguageModel {
    HashMap<String, List> CharDataMap;
    int windowLength;
    private Random randomGenerator;

    public LanguageModel(int windowLength, int seed) {
        this.windowLength = windowLength;
        randomGenerator = new Random(seed);
        CharDataMap = new HashMap<String, List>();
    }

    public LanguageModel(int windowLength) {
        this.windowLength = windowLength;
        randomGenerator = new Random();
        CharDataMap = new HashMap<String, List>();
    }

    public void train(String fileName) {
        In in = new In(fileName);
        String window = "";
        for (int i = 0; i < windowLength; i++) {
            if (!in.isEmpty()) window += in.readChar();
        }
        while (!in.isEmpty()) {
            char nextChar = in.readChar();
            List probabilities = CharDataMap.get(window);
            if (probabilities == null) {
                probabilities = new List();
                CharDataMap.put(window, probabilities);
            }
            probabilities.update(nextChar);
            window = window.substring(1) + nextChar; 
        }
        for (List list : CharDataMap.values()) calculateProbabilities(list);
    }

    void calculateProbabilities(List probs) {               
        int count = 0;
        for (int i = 0; i < probs.getSize(); i++) {
            count += probs.get(i).count;
        }
        double cumulativeProb = 0.0;
        // חשוב: עוברים מהסוף להתחלה כי השתמשנו ב-addFirst
        for (int i = probs.getSize() - 1; i >= 0; i--) {
            CharData cd = probs.get(i);
            cd.p = (double) cd.count / count;
            cumulativeProb += cd.p;
            cd.cp = cumulativeProb;
        }
    }

    char getRandomChar(List probs) {
        double r = randomGenerator.nextDouble();
        // עוברים מהסוף להתחלה כדי להתאים ל-calculateProbabilities
        for (int i = probs.getSize() - 1; i >= 0; i--) {
            CharData cd = probs.get(i);
            if (r < cd.cp) return cd.chr;
        }
        return probs.get(0).chr;
    }

    public String generate(String initialText, int textLength) {
        if (initialText.length() < windowLength) return initialText;
        StringBuilder result = new StringBuilder(initialText);
        String window = initialText.substring(initialText.length() - windowLength);

        while (result.length() < textLength) {
            List probs = CharDataMap.get(window);
            if (probs != null) {
                char nextChar = getRandomChar(probs);
                result.append(nextChar);
                window = window.substring(1) + nextChar;
            } else break;
        }
        return result.toString();
    }
}
