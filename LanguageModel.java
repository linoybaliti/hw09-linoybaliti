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
        // אתחול החלון הראשון
        for (int i = 0; i < windowLength; i++) {
            if (!in.isEmpty()) {
                window += in.readChar();
            }
        }
        // קריאת שאר הקובץ ועדכון המפה
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

        // חישוב הסתברויות לכל הרשימות במפה
        for (List list : CharDataMap.values()) {
            calculateProbabilities(list);
        }
    }

    void calculateProbabilities(List probs) {               
        int count = 0;
        ListIterator it = probs.listIterator(0);
        while (it.hasNext()) {
            count += it.next().count;
        }
        
        double cumulativeProb = 0.0;
        it = probs.listIterator(0);
        while (it.hasNext()) {
            CharData cd = it.next();
            cd.p = (double) cd.count / count;
            cumulativeProb += cd.p;
            cd.cp = cumulativeProb;
        }
    }

    char getRandomChar(List probs) {
        double r = randomGenerator.nextDouble();
        ListIterator it = probs.listIterator(0);
        CharData cd = null;
        while (it.hasNext()) {
            cd = it.next();
            if (r < cd.cp) {
                return cd.chr;
            }
        }
        return cd.chr;
    }

    public String generate(String initialText, int textLength) {
        if (initialText.length() < windowLength) {
            return initialText;
        }
        
        StringBuilder result = new StringBuilder(initialText);
        // הלולאה רצה עד שהאורך הכולל מגיע ל-textLength
        while (result.length() < textLength) {
            String currentWindow = result.substring(result.length() - windowLength);
            List probs = CharDataMap.get(currentWindow);
            
            if (probs != null) {
                char nextChar = getRandomChar(probs);
                result.append(nextChar);
            } else {
                // אם החלון לא נמצא במפה, אין לנו איך להמשיך
                break;
            }
        }
        return result.toString();
    }

    public String toString() {
        StringBuilder str = new StringBuilder();
        for (String key : CharDataMap.keySet()) {
            List keyProbs = CharDataMap.get(key);
            str.append(key + " : " + keyProbs + "\n");
        }
        return str.toString();
    }

    public static void main(String[] args) {
        int windowLength = Integer.parseInt(args[0]); 
        String initialText = args[1]; 
        int generatedTextLength = Integer.parseInt(args[2]); 
        boolean randomGeneration = args[3].equals("random"); 
        String fileName = args[4]; 

        LanguageModel lm; 
        if (randomGeneration) {
            lm = new LanguageModel(windowLength);
        } else {
            lm = new LanguageModel(windowLength, 20); 
        }

        lm.train(fileName); 
        System.out.println(lm.generate(initialText, generatedTextLength)); 
    }
}
