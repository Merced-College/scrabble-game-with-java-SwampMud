/*
 * Ashton
 * 11/17/25
 * Scrabble Game - Read words from file into Word objects
 * New ROUND IMPROVEMENT!!
 * I also had the AI add points for extra flair but that is not my achievement unfortunately
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;

public class ScrabbleGame {
    /**
     * Global list that stores all Word objects read from the dictionary file.
     */
    public static final ArrayList<Word> wordsList = new ArrayList<>();

    /**
     * Standard Scrabble letter point values.
     */
    public static final Map<Character, Integer> LETTER_POINTS = new HashMap<>();

    // Static initializer block to populate the letter points map.
    static {
        // 1 point
        for (char c : "AEIOULNSTR".toCharArray()) LETTER_POINTS.put(c, 1);
        // 2 points
        for (char c : "DG".toCharArray()) LETTER_POINTS.put(c, 2);
        // 3 points
        for (char c : "BCMP".toCharArray()) LETTER_POINTS.put(c, 3);
        // 4 points
        for (char c : "FHVWY".toCharArray()) LETTER_POINTS.put(c, 4);
        // 5 points
        LETTER_POINTS.put('K', 5);
        // 8 points
        for (char c : "JX".toCharArray()) LETTER_POINTS.put(c, 8);
        // 10 points
        for (char c : "QZ".toCharArray()) LETTER_POINTS.put(c, 10);
    }

    /**
     * Reads words from a text file and stores them into the global `wordsList`.
     * The method skips the first two lines of the file (header) and then
     * expects each subsequent line to contain a word followed by its definition
     * on the same line.
     *
     * @param filename path to the input file
     * @throws IOException if an I/O error occurs while reading the file
     */
    public static void readWordsFromFile(String filename) throws IOException {
        Path path = Path.of(filename);
        if (!Files.exists(path)) {
            throw new IOException("File not found: " + filename);
        }

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            // skip first two lines
            br.readLine();
            br.readLine();

            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                // Split into two parts: the first token is the word, the rest is the definition
                String[] parts = line.split("\\s+", 2);
                String wordText = parts.length > 0 ? parts[0] : "";
                String definition = parts.length > 1 ? parts[1] : "";

                Word w = new Word(wordText, definition);
                wordsList.add(w);
            }
        }
    }

    /**
     * Calculates the Scrabble-style point value for a given word.
     * The word is converted to uppercase and points are summed based on LETTER_POINTS.
     * Letters not found (e.g., punctuation) are treated as 0 points.
     *
     * @param word The word to score.
     * @return The total score for the word.
     */
    public static int getWordScore(String word) {
        if (word == null || word.isEmpty()) return 0;
        int score = 0;
        // Convert to uppercase to match the keys in LETTER_POINTS
        for (char c : word.toUpperCase().toCharArray()) {
            // Use getOrDefault to assign 0 points to non-letter characters
            score += LETTER_POINTS.getOrDefault(c, 0);
        }
        return score;
    }

    //New ROUND!!!!
    public static boolean NewRound(Scanner scanner) {
        
        System.out.print("Play another round? (yes or no): ");
        String response = scanner.nextLine(); 
        
        if (response.equalsIgnoreCase("yes") ) {
            System.out.println("------------------------------------");
            return true;
        } else {
            System.out.println("------------------------------------");
            return false;
        }
    }

    public static void main(String[] args) {
        // Default filename - update if your file has a different name
        String filename = "Collins Scrabble Words (2019) with definitions.txt";
        if (args.length > 0) filename = args[0];

        try {
            readWordsFromFile(filename);
            
            // Ensure list is sorted for binary search (case-insensitive by Word.compareTo)
            Collections.sort(wordsList);

            Scanner scanner = new Scanner(System.in);
            boolean playAgain = true;
            int totalScore = 0; // Initialize total score

            // Main game loop
            do {
                // Generate 4 random uppercase letters for the new round
                ArrayList<Character> letters = generateRandomLetters(4);
                System.out.println("\n--- NEW ROUND ---");
                System.out.println("Current Total Score: " + totalScore); // Display total score
                System.out.println("Your available letters: " + letters);

                // Ask user for a word made from those letters
                System.out.print("Enter a word made from those letters (or 'exit' to quit): ");
                String input = scanner.nextLine().trim();

                if ("exit".equalsIgnoreCase(input) || input.isEmpty()) {
                    System.out.println("Final Score: " + totalScore);
                    System.out.println("Exiting game. Thanks for playing!");
                    playAgain = false;
                } else {
                    String candidate = input;
                    if (!canFormWord(candidate, letters)) {
                        System.out.println("The word '" + candidate + "' cannot be formed from the given letters.");
                    } else {
                        int idx = binarySearchWord(candidate);
                        if (idx >= 0) {
                            int roundScore = getWordScore(candidate); // Calculate the score
                            totalScore += roundScore; // Add to total score
                            System.out.println("------------------------------------");
                            System.out.println("Success! '" + candidate + "' is a valid word.");
                            System.out.println("Round Score: " + roundScore + " points!");
                            System.out.println("New Total Score: " + totalScore);
                            System.out.println("Definition: " + wordsList.get(idx).getDefinition());
                        } else {
                            System.out.println("'" + candidate + "' is NOT found in the dictionary.");
                        }
                    }
                    // Ask if the user wants to play again
                    if (playAgain) { // Only ask if they didn't already type 'exit'
                       playAgain = NewRound(scanner);
                    }
                }
            } while (playAgain);
            
            // close scanner to avoid resource leak
            scanner.close();

        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }

    /**
     * Generate n random uppercase letters and return as an ArrayList<Character>.
     */
    public static ArrayList<Character> generateRandomLetters(int n) {
        Random rnd = new Random();
        ArrayList<Character> out = new ArrayList<>(n);
        for (int i = 0; i < n; i++) { // Fixed loop condition from previous version
            char c = (char) ('A' + rnd.nextInt(26));
            out.add(c);
        }
        return out;
    }

    /**
     * Returns true if `word` can be formed from the letters in `available` (case-insensitive).
     * Each letter in `available` can only be used once.
     */
    public static boolean canFormWord(String word, List<Character> available) {
        if (word == null) return false;
        word = word.toUpperCase();
        ArrayList<Character> pool = new ArrayList<>();
        // Create a copy of the available letters, converted to uppercase
        for (Character c : available) pool.add(Character.toUpperCase(c));

        for (char ch : word.toCharArray()) {
            int pos = pool.indexOf(ch);
            if (pos < 0) return false;
            // Remove the letter from the pool so it can't be reused
            pool.remove(pos);
        }
        return true;
    }

    /**
     * Binary search for a word string in the sorted `wordsList`. Comparison is case-insensitive.
     * Returns index if found, otherwise -1.
     */
    public static int binarySearchWord(String target) {
        if (target == null) return -1;
        target = target.trim();
        int low = 0, high = wordsList.size() - 1;
        while (low <= high) {
            // Safe way to calculate mid index
            int mid = low + ((high - low) / 2);
            Word midWordObject = wordsList.get(mid);
            String midWord = (midWordObject != null) ? midWordObject.getWord() : "";
            
            // Perform case-insensitive comparison using String's built-in method
            int cmp = midWord.compareToIgnoreCase(target);
            
            if (cmp < 0) {
                low = mid + 1; // Target is in the upper half
            } else if (cmp > 0) {
                high = mid - 1; // Target is in the lower half
            } else {
                return mid; // Found
            }
        }
        return -1; // Not found
    }
}