import java.util.*;
import java.io.FileNotFoundException;

public class Hangman {

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

        String secretWord = WordRepository.selectRandomWord(words);
        String maskedWord = "_".repeat(secretWord.length());
        Set<Character> usedLetters = new HashSet<>();
        int wrongAttemptsCount = 0;
        boolean isRoundActive = true;
        boolean isGameWon = false;

        System.out.println("\nИгра началась! У вас " + HangmanGraphics.MAX_ATTEMPTS + " попыток");

        while (isRoundActive) {
            HangmanGraphics.displayGameState(maskedWord, usedLetters, wrongAttemptsCount);
            char letter = inputRussianLetter(scanner);

            GameStateUpdateResult result = processPlayerGuess(letter, secretWord, maskedWord, usedLetters, wrongAttemptsCount);

            maskedWord = result.maskedWord;
            wrongAttemptsCount = result.wrongAttemptsCount;
            isRoundActive = result.isRoundActive;
            isGameWon = result.isGameWon;

            if (isWordGuessed(maskedWord)) {
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
    private static class GameStateUpdateResult {
        String maskedWord;
        int wrongAttemptsCount;
        boolean isRoundActive;
        boolean isGameWon;

        GameStateUpdateResult(String maskedWord, int wrongAttemptsCount, boolean isRoundActive,
                              boolean isGameWon) {
            this.maskedWord = maskedWord;
            this.wrongAttemptsCount = wrongAttemptsCount;
            this.isRoundActive = isRoundActive;
            this.isGameWon = isGameWon;
        }
    }
    private static GameStateUpdateResult processPlayerGuess(char letter, String secretWord, String maskedWord, Set<Character> usedLetters, int wrongAttemptsCount) {
        boolean isRoundActive = true;
        boolean isGameWon = false;

        if (usedLetters.contains(letter)) {
            System.out.println("Вы уже вводили эту букву '" + letter + "'");
            return new GameStateUpdateResult(maskedWord, wrongAttemptsCount, isRoundActive, isGameWon);
        }
        usedLetters.add(letter);
        if (secretWord.contains(String.valueOf(letter))) {
            System.out.println("Правильно! Буква '" + letter + "' есть в слове");
            maskedWord = updateMaskedWord(letter, secretWord, maskedWord);
        } else {
            System.out.println("Буквы '" + letter + "' нет в этом слове");
            wrongAttemptsCount++;
            if (wrongAttemptsCount >= HangmanGraphics.MAX_ATTEMPTS) {
                isRoundActive = false;
            }
        }

        return new GameStateUpdateResult(maskedWord, wrongAttemptsCount, isRoundActive, isGameWon);
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

    private static String updateMaskedWord(char letter, String secretWord, String maskedWord) {
        StringBuilder newMaskedWord = new StringBuilder(maskedWord);
        for (int i = 0; i < secretWord.length(); i++) {
            if (secretWord.charAt(i) == letter) {
                newMaskedWord.setCharAt(i, letter);
            }
        }
        return newMaskedWord.toString();
    }

    private static boolean isWordGuessed(String maskedWord) {
        return !maskedWord.contains("_");
    }
}
