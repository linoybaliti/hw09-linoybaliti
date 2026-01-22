import java.util.HashMap;
import java.util.Random;

public class LanguageModel {

    // The map of this model.
    // Maps windows to lists of character data objects.
    HashMap<String, List> CharDataMap;
    
    // The window length used in this model.
    int windowLength;
    
    // The random number generator used by this model. 
    private Random randomGenerator;

    /** Constructs a language model with the given window length and a given
     * seed value. Generating texts from this model multiple times with the 
     * same seed value will produce the same random texts. Good for debugging. */
    public LanguageModel(int windowLength, int seed) {
        this.windowLength = windowLength;
        randomGenerator = new Random(seed);
        CharDataMap = new HashMap<String, List>();
    }

    /** Constructs a language model with the given window length.
     * Generating texts from this model multiple times will produce
     * different random texts. Good for production. */
    public LanguageModel(int windowLength) {
        this.windowLength = windowLength;
        randomGenerator = new Random();
        CharDataMap = new HashMap<String, List>();
    }

    /** Builds a language model from the text in the given file (the corpus). */
    public void train(String fileName) {
        In in = new In(fileName);
        String window = "";
        for (int i = 0; i < windowLength; i++) {
            if (!in.isEmpty()) {
                window += in.readChar();
            }
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

    // Computes and sets the probabilities (p and cp fields) of all the
    // characters in the given list. */
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

    // Returns a random character from the given probabilities list.
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
        return (cd != null) ? cd.chr : ' ';
    }

    /**
     * Generates a random text, based on the probabilities that were learned during training. 
     */
    public String generate(String initialText, int textLength) {
        // אם הטקסט ההתחלתי קצר מאורך החלון, אי אפשר להמשיך לפי המודל
        if (initialText.length() < windowLength) {
            return initialText;
        }
        
        // אם הטקסט הקיים כבר הגיע לאורך המבוקש או עבר אותו
        if (initialText.length() >= textLength) {
            return initialText;
        }

        StringBuilder result = new StringBuilder(initialText);
        // יצירת החלון הנוכחי מתוך סוף ה-initialText
        String window = initialText.substring(initialText.length() - windowLength);

        // הלולאה רצה עד שהטקסט ב-result מגיע לאורך textLength הכולל
        while (result.length() < textLength) {
            List probs = CharDataMap.get(window);
            
            if (probs != null) {
                char nextChar = getRandomChar(probs);
                result.append(nextChar);
                // הזזת החלון: מורידים תו מההתחלה ומוסיפים את התו החדש
                window = window.substring(1) + nextChar;
            } else {
                // אם הגענו לקומבינציה שלא קיימת במודל, עוצרים
                break;
            }
        }
        return result.toString();
    }

    /** Returns a string representing the map of this language model. */
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (String key : CharDataMap.keySet()) {
            List keyProbs = CharDataMap.get(key);
            str.append(key + " : " + keyProbs + "\n");
        }
        return str.toString();
    }

    public static void main(String[] args) {
        if (args.length < 5) return;
        
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
