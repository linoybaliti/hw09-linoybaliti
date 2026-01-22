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
        for (List list : CharDataMap.values()) {
            calculateProbabilities(list);
        }
    }

    void calculateProbabilities(List probs) {               
        int totalCount = 0;
        // חישוב סך המופעים בעזרת איטרטור
        ListIterator it = probs.listIterator(0);
        while (it.hasNext()) {
            totalCount += it.next().count;
        }

        double cumulativeProb = 0.0;
        // חישוב הסתברות והסתברות מצטברת
        it = probs.listIterator(0);
        while (it.hasNext()) {
            CharData cd = it.next();
            cd.p = (double) cd.count / totalCount;
            cumulativeProb += cd.p;
            cd.cp = cumulativeProb;
        }
    }

    char getRandomChar(List probs) {
        double r = randomGenerator.nextDouble();
        ListIterator it = probs.listIterator(0);
        CharData last = null;
        while (it.hasNext()) {
            last = it.next();
            if (r < last.cp) {
                return last.chr;
            }
        }
        return last.chr;
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
            } else {
                break;
            }
        }
        return result.toString();
    }
}
