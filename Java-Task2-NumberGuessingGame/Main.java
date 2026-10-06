import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;

/**
 * Number Guessing Game
 * - Player name, 3 difficulty levels, hints (including "Very close!")
 * - Time taken per round, scoring and session statistics
 * - Top 5 leaderboard saved in a file (leaderboard.txt)
 */
public class Main {

    private static final Scanner sc = new Scanner(System.in);
    private static final Random random = new Random();
    private static final Leaderboard leaderboard = new Leaderboard("leaderboard.txt");

    private static String playerName;

    // Session statistics
    private static int gamesPlayed = 0;
    private static int gamesWon = 0;
    private static int totalScore = 0;
    private static int bestScore = 0;

    public static void main(String[] args) {

        System.out.println("================================");
        System.out.println("     NUMBER GUESSING GAME");
        System.out.println("================================");

        try {
            playerName = readName();
            System.out.println("\nWelcome, " + playerName + "! Good luck!");
            runMenu();
        } catch (NoSuchElementException e) {
            // Input stream closed (for example Ctrl+D / Ctrl+Z)
            System.out.println("\nInput ended. Exiting game.");
        }
    }

    // Main menu loop
    private static void runMenu() {

        while (true) {

            System.out.println("\n========== MAIN MENU ==========");
            System.out.println("1. Easy   (1-50,  10 attempts)");
            System.out.println("2. Medium (1-100,  7 attempts)");
            System.out.println("3. Hard   (1-200,  5 attempts)");
            System.out.println("4. View Statistics");
            System.out.println("5. Leaderboard (Top 5)");
            System.out.println("6. Exit");

            int choice = readInt("Enter your choice: ", 1, 6);

            switch (choice) {
                case 1:
                    playLevel("Easy", 50, 10, 10);
                    break;
                case 2:
                    playLevel("Medium", 100, 7, 20);
                    break;
                case 3:
                    playLevel("Hard", 200, 5, 30);
                    break;
                case 4:
                    showStatistics();
                    break;
                case 5:
                    leaderboard.display();
                    break;
                case 6:
                    System.out.println("\nThanks for playing, " + playerName + "!");
                    System.out.println("Final score: " + totalScore);
                    return;
            }
        }
    }

    // Plays the same level again and again until the player says no
    private static void playLevel(String level, int max, int maxAttempts, int multiplier) {

        do {
            playRound(level, max, maxAttempts, multiplier);
        } while (readYesNo("\nPlay " + level + " level again? (yes/no): "));
    }

    // Plays one round and updates statistics and leaderboard
    private static void playRound(String level, int max, int maxAttempts, int multiplier) {

        int secret = random.nextInt(max) + 1;
        gamesPlayed++;

        System.out.println("\n----- " + level + " Level -----");
        System.out.println("I am thinking of a number between 1 and " + max + ".");
        System.out.println("You have " + maxAttempts + " attempts.");

        long startTime = System.currentTimeMillis();

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {

            int guess = readInt("\nAttempt " + attempt + " - Enter your guess: ", 1, max);

            if (guess == secret) {

                long seconds = (System.currentTimeMillis() - startTime) / 1000;
                int score = (maxAttempts - attempt + 1) * multiplier;

                gamesWon++;
                totalScore += score;
                if (score > bestScore) {
                    bestScore = score;
                }
                leaderboard.add(playerName, score);

                System.out.println("\nCorrect! You guessed it in " + attempt
                        + " attempt(s) and " + seconds + " second(s).");
                System.out.println("Round Score: " + score);
                return;
            }

            if (guess < secret) {
                System.out.println("Too low! Try a higher number.");
            } else {
                System.out.println("Too high! Try a lower number.");
            }

            // Extra hint when the guess is very near the secret number
            if (Math.abs(guess - secret) <= max / 10) {
                System.out.println("Very close!");
            }

            System.out.println("Attempts left: " + (maxAttempts - attempt));
        }

        System.out.println("\nYou are out of attempts. The number was " + secret + ".");
        System.out.println("Round Score: 0");
    }

    // Shows session statistics
    private static void showStatistics() {

        System.out.println("\n========== STATISTICS (" + playerName + ") ==========");
        System.out.println("Games Played : " + gamesPlayed);
        System.out.println("Games Won    : " + gamesWon);
        System.out.println("Games Lost   : " + (gamesPlayed - gamesWon));
        System.out.println("Total Score  : " + totalScore);
        System.out.println("Best Score   : " + bestScore);
    }

    // Asks for the player name (not empty, max 15 characters, no commas)
    private static String readName() {

        while (true) {

            System.out.print("\nEnter your name: ");
            String name = sc.nextLine().trim().replace(",", "");

            if (name.isEmpty()) {
                System.out.println("Name cannot be empty.");
            } else if (name.length() > 15) {
                System.out.println("Name can have at most 15 characters.");
            } else {
                return name;
            }
        }
    }

    // Keeps asking until the user enters a whole number in [min, max]
    private static int readInt(String prompt, int min, int max) {

        while (true) {

            System.out.print(prompt);
            String input = sc.nextLine().trim();

            try {
                int value = Integer.parseInt(input);

                if (value < min || value > max) {
                    System.out.println("Please enter a number between " + min + " and " + max + ".");
                } else {
                    return value;
                }

            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a whole number.");
            }
        }
    }

    // Keeps asking until the user enters yes/no
    private static boolean readYesNo(String prompt) {

        while (true) {

            System.out.print(prompt);
            String input = sc.nextLine().trim().toLowerCase();

            if (input.equals("yes") || input.equals("y")) {
                return true;
            }
            if (input.equals("no") || input.equals("n")) {
                return false;
            }
            System.out.println("Please type yes or no.");
        }
    }
}

// ===================== LEADERBOARD =====================
// Keeps the top 5 scores and saves them in a text file
class Leaderboard {

    private static final int MAX_ENTRIES = 5;

    private final String fileName;
    private final ArrayList<Entry> entries = new ArrayList<Entry>();

    public Leaderboard(String fileName) {
        this.fileName = fileName;
        load();
    }

    // Adds a score, keeps only the top 5 and saves to file
    public void add(String name, int score) {

        entries.add(new Entry(name, score));
        sortEntries();

        while (entries.size() > MAX_ENTRIES) {
            entries.remove(entries.size() - 1);
        }
        save();
    }

    public void display() {

        System.out.println("\n===== LEADERBOARD (TOP 5) =====");

        if (entries.isEmpty()) {
            System.out.println("No scores yet. Win a round to get on the board!");
            return;
        }

        for (int i = 0; i < entries.size(); i++) {
            Entry e = entries.get(i);
            System.out.println((i + 1) + ". " + e.name + " - " + e.score);
        }
    }

    private void sortEntries() {
        Collections.sort(entries, new Comparator<Entry>() {
            public int compare(Entry a, Entry b) {
                return b.score - a.score;   // highest score first
            }
        });
    }

    // Reads the saved scores (format: name,score)
    private void load() {

        File file = new File(fileName);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            String line;
            while ((line = reader.readLine()) != null) {

                String[] parts = line.split(",");
                if (parts.length != 2) {
                    continue;   // skip broken lines
                }

                try {
                    entries.add(new Entry(parts[0], Integer.parseInt(parts[1].trim())));
                } catch (NumberFormatException e) {
                    // skip lines with a wrong score
                }
            }

        } catch (IOException e) {
            System.out.println("Could not read the leaderboard file.");
        }

        sortEntries();

        while (entries.size() > MAX_ENTRIES) {
            entries.remove(entries.size() - 1);
        }
    }

    private void save() {

        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {

            for (Entry e : entries) {
                writer.println(e.name + "," + e.score);
            }

        } catch (IOException e) {
            System.out.println("Could not save the leaderboard file.");
        }
    }

    // One leaderboard row
    private static class Entry {

        final String name;
        final int score;

        Entry(String name, int score) {
            this.name = name;
            this.score = score;
        }
    }
}