import java.util.*;
import java.io.FileNotFoundException;

public class Hangman {
    private static String secretWord;
    private static String maskedWord;
    private static Set<Character> usedLetters;
    private static int wrongAttemptsCount;
    private static boolean isGameWon;
    private static boolean isRoundActive;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        startGameLoop(scanner);
        scanner.close();
    }

    private static void startGameLoop(Scanner scanner) {

        while (true) {
            HangmanGraphics.displayMainMenu();
            char choice = inputSymbol(scanner);

            switch (choice) {
                case HangmanGraphics.START -> startNewGame(scanner);
                case HangmanGraphics.QUIT -> {
                    System.out.println("До свидания!");
                    return;
                }
                default -> System.out.printf("Неверный выбор. Пожалуйста, введите '%c' или '%c'%n", HangmanGraphics.START, HangmanGraphics.QUIT);

            }
        }
    }

    private static char inputSymbol(Scanner scanner) {
        String input = scanner.next().toLowerCase();
        return input.charAt(0);
    }

    private static void startNewGame(Scanner scanner) {
        try{
        WordRepository wordRepository = new WordRepository();
        List<String> words = wordRepository.loadWordsFromFile();

        secretWord = WordRepository.selectRandomWord(words);
        maskedWord = "_".repeat(secretWord.length());
        usedLetters = new HashSet<>();
        wrongAttemptsCount = 0;
        isRoundActive = true;
        isGameWon = false;

        System.out.println("\nИгра началась! У вас " + HangmanGraphics.MAX_ATTEMPTS + " попыток");

        while (isRoundActive) {
            HangmanGraphics.displayGameState(maskedWord, usedLetters, wrongAttemptsCount);
            char letter = inputRussianLetter(scanner);

             processPlayerGuess(letter);

            if (isWordGuessed()) {
                isRoundActive = false;
                isGameWon = true;
            }
        }

        HangmanGraphics.displayGameResult(secretWord, wrongAttemptsCount, isGameWon);
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            System.out.println("Работа программы будет завершена.");
            System.exit(1);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            System.out.println("Работа программы будет завершена.");
            System.exit(1);

        }
    }

    private static void processPlayerGuess(char letter) {

        if (usedLetters.contains(letter)) {
            System.out.println("Вы уже вводили эту букву '" + letter + "'");
            return;
        }
        usedLetters.add(letter);
        if (secretWord.contains(String.valueOf(letter))) {
            System.out.println("Правильно! Буква '" + letter + "' есть в слове");
            updateMaskedWord(letter);
        } else {
            System.out.println("Буквы '" + letter + "' нет в этом слове");
            wrongAttemptsCount++;
            if (wrongAttemptsCount >= HangmanGraphics.MAX_ATTEMPTS) {
                isRoundActive = false;
            }
        }
    }



    private static char inputRussianLetter(Scanner scanner) {
        char letter;
        do {
            System.out.print("Введите букву русского алфавита: ");
            String input = scanner.next().toLowerCase();
            letter = input.charAt(0);

            if (!isValidRussianLetter(letter)) {
                System.out.println("Ошибка! Введите букву русского алфавита!");
            }
        } while (!isValidRussianLetter(letter));
        return letter;
    }

    private static boolean isValidRussianLetter(char letter) {
        return Character.toString(letter).matches("[а-яё]");
    }

    private static void updateMaskedWord(char letter) {
        StringBuilder newMaskedWord = new StringBuilder(maskedWord);
        for (int i = 0; i < secretWord.length(); i++) {
            if (secretWord.charAt(i) == letter) {
                newMaskedWord.setCharAt(i, letter);
            }
        }
        maskedWord=newMaskedWord.toString();
    }

    private static boolean isWordGuessed() {
        return !maskedWord.contains("_");
    }
}
