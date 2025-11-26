import java.util.*;
import java.io.FileNotFoundException;

public class Hangman {
    private static String secretWord;
    private static String maskedWord;
    private static Set<Character> usedLetters;
    private static int wrongAttemptsCount;


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
        while (true) {
            String input = scanner.nextLine().trim();
            if (input.length() == 1) {
                char choice = input.charAt(0);
                if (choice == HangmanGraphics.START || choice == HangmanGraphics.QUIT) {
                    return choice;
                }
            }
            System.out.printf("Неверный выбор. Пожалуйста, введите ровно один символ: '%c' или '%c'%n",
                    HangmanGraphics.START, HangmanGraphics.QUIT);
            System.out.print("Попробуйте снова: ");
        }
    }

    private static void startNewGame(Scanner scanner) {
        try {
        WordRepository wordRepository = new WordRepository();
        List<String> words = wordRepository.loadWordsFromFile();

        secretWord = WordRepository.selectRandomWord(words);
        maskedWord = "_".repeat(secretWord.length());
        usedLetters = new HashSet<>();
        wrongAttemptsCount = 0;

        System.out.println("\nИгра началась! У вас " + HangmanGraphics.MAX_ATTEMPTS + " попыток");

        while (!isGameOver()) {
            HangmanGraphics.displayGameState(maskedWord, usedLetters, wrongAttemptsCount);
            char letter = inputRussianLetter(scanner);

            processPlayerGuess(letter);
        }

            if (isWin()) {
                HangmanGraphics.displayGameResult(secretWord, wrongAttemptsCount, true);
            } else {
                HangmanGraphics.displayGameResult(secretWord, wrongAttemptsCount, false);
            }

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
        }
    }
    private static boolean isWin() {
        return isWordGuessed();
    }
    private static boolean isLose() {
        return wrongAttemptsCount >= HangmanGraphics.MAX_ATTEMPTS;
    }

    private static boolean isGameOver() {
        return  isWin() || isLose();
    }


    private static char inputRussianLetter(Scanner scanner) {
        while (true) {
            System.out.print("Введите букву русского алфавита: ");
            String input = scanner.nextLine().trim();

            if (input.length() != 1) {
                System.out.println("Ошибка! Введите ровно одну букву!");
                continue;
            }

            char letter = input.charAt(0);
            if (isValidRussianLetter(letter)) {
                return letter;
            }

            System.out.println("Ошибка! Введите букву русского алфавита!");
        }
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
