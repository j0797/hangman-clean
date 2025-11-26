import java.io.*;
import java.util.*;

public class Hangman {

    private static final int MAX_ATTEMPTS = 6;
    private static final String WORDS_FILE_PATH = "resources/words.txt";
    private final static char START = '1';
    private final static char QUIT = '2';

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        startGameLoop(scanner);
        scanner.close();
    }

    private static void startGameLoop(Scanner scanner) {

        while (true) {
            displayMainMenu();
            char choice = inputSymbol(scanner);

            switch (choice) {
                case START -> startNewGame(scanner);
                case QUIT -> {
                    System.out.println("До свидания!");
                    return;
                }
                default ->
                    System.out.printf("Неверный выбор. Пожалуйста, введите '%c' или '%c'  \n", START, QUIT);

            }
        }
    }

    private static void displayMainMenu() {
        System.out.println("\n=== ИГРА ВИСЕЛИЦА ===");
        System.out.println(START + " - начать новую игру");
        System.out.println(QUIT + " - выйти из игры");
        System.out.println("Выберите действие: ");
    }

    private static char inputSymbol(Scanner scanner) {
        String input = scanner.next().toLowerCase();
        return input.charAt(0);
    }

    private static void startNewGame(Scanner scanner) {
        List<String> words = loadWordsFromFile(WORDS_FILE_PATH);

        if (words.isEmpty()) {
            System.out.println("Не удалось загрузить слова для игры");
            return;
        }


        String secretWord = selectRandomWord(words);
        String maskedWord = "_".repeat(secretWord.length());
        Set<Character> usedLetters = new HashSet<>();
        int wrongAttemptsCount = 0;
        boolean isRoundActive = true;
        boolean isGameWon = false;

        System.out.println("\nИгра началась! У вас " + MAX_ATTEMPTS + " попыток");

        while (isRoundActive) {
            displayGameState(maskedWord, usedLetters, wrongAttemptsCount);
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

        displayGameResult(secretWord, wrongAttemptsCount, isGameWon);
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
            if (wrongAttemptsCount >= MAX_ATTEMPTS) {
                isRoundActive = false;
            }
        }

        return new GameStateUpdateResult(maskedWord, wrongAttemptsCount, isRoundActive, isGameWon);
    }

    private static List<String> loadWordsFromFile(String filePath) {
        List<String> words = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim().toLowerCase();

                if (line.isEmpty()) {
                    continue;
                }

                if (isValidRussianWord(line)) {
                    words.add(line);
                } else {
                    System.out.printf("Предупреждение: Строка %d содержит некорректные символы: %s %n", lineNumber, line);
                }
            }
            System.out.printf("Загружено %d слов из файла%n", words.size());

        } catch (FileNotFoundException e) {
            System.out.println("Ошибка: Файл со словами не найден: " + filePath);
        } catch (IOException e) {
            System.out.println("Ошибка чтения файла: " + e.getMessage());
        }

        return words;
    }

    private static boolean isValidRussianWord(String word) {
        return word.matches("[а-яё]+");
    }

    private static String selectRandomWord(List<String> words) {
        Random random = new Random();
        return words.get(random.nextInt(words.size()));
    }

    private static void displayGameState(String maskedWord, Set<Character> usedLetters, int wrongAttemptsCount) {
        System.out.println("\n=== ВИСЕЛИЦА ===");
        displayHangman(wrongAttemptsCount);
        System.out.println("================\n");
        System.out.printf("Осталось попыток: %d%n", MAX_ATTEMPTS - wrongAttemptsCount);
        System.out.println("Слово: " + maskedWord);

        if (!usedLetters.isEmpty()) {
            System.out.print("Использованные буквы: ");
            usedLetters.forEach(letter -> System.out.print(letter + " "));
            System.out.println();
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

    private static void displayGameResult(String secretWord, int wrongAttemptsCount, boolean isGameWon) {
        if (isGameWon) {
            System.out.println("Поздравляем! Вы отгадали слово: " + secretWord);
        } else {
            System.out.println("Вы проиграли! Загаданное слово: " + secretWord);
        }

        displayHangman(wrongAttemptsCount);

    }

    private static void displayHangman(int wrongAttempts) {
        switch (wrongAttempts) {
            case 0:
                System.out.println("  _______");
                System.out.println("  |     |");
                System.out.println("  |");
                System.out.println("  |");
                System.out.println("  |");
                System.out.println("  |");
                System.out.println("__|________");
                break;
            case 1:
                System.out.println("  _______");
                System.out.println("  |     |");
                System.out.println("  |     O");
                System.out.println("  |");
                System.out.println("  |");
                System.out.println("  |");
                System.out.println("__|________");
                break;
            case 2:
                System.out.println("  _______");
                System.out.println("  |     |");
                System.out.println("  |     O");
                System.out.println("  |     |");
                System.out.println("  |");
                System.out.println("  |");
                System.out.println("__|________");
                break;
            case 3:
                System.out.println("  _______");
                System.out.println("  |     |");
                System.out.println("  |     O");
                System.out.println("  |    /|");
                System.out.println("  |");
                System.out.println("  |");
                System.out.println("__|________");
                break;
            case 4:
                System.out.println("  _______");
                System.out.println("  |     |");
                System.out.println("  |     O");
                System.out.println("  |    /|\\");
                System.out.println("  |");
                System.out.println("  |");
                System.out.println("__|________");
                break;
            case 5:
                System.out.println("  _______");
                System.out.println("  |     |");
                System.out.println("  |     O");
                System.out.println("  |    /|\\");
                System.out.println("  |    /");
                System.out.println("  |");
                System.out.println("__|________");
                break;
            case 6:
                System.out.println("  _______");
                System.out.println("  |     |");
                System.out.println("  |     O");
                System.out.println("  |    /|\\");
                System.out.println("  |    / \\");
                System.out.println("  |");
                System.out.println("__|________");
                break;
        }
    }
}