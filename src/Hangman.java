import java.io.*;
import java.util.*;

public class Hangman {

    private static final int MAX_ATTEMPTS = 6;
    private static final String WORDS_FILE_PATH = "resources/words.txt";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        startGameLoop(scanner);
        scanner.close();
    }

    private static void startGameLoop(Scanner scanner) {
        boolean continuePlaying = true;

        while (continuePlaying) {
            displayMainMenu();
            char choice = readMenuChoice(scanner);

            switch (choice) {
                case '1':
                    startNewGame(scanner);
                    break;
                case '2':
                    continuePlaying = false;
                    System.out.println("Всего доброго!");
                    break;
                default:
                    System.out.println("Неверный выбор. Пожалуйста, введите '1' или '2'");

            }
        }
    }

    private static void displayMainMenu() {
        System.out.println("\n=== ИГРА ВИСЕЛИЦА ===");
        System.out.println("1 - начать новую игру");
        System.out.println("2 - выйти из игры");
        System.out.println("Выберите действие: ");
    }

    private static char readMenuChoice(Scanner scanner) {
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
            char letter = readPlayerGuess(scanner);

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
        List<String> validWords = new ArrayList<>();
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
                    validWords.add(line);
                } else {
                    System.out.println("Предупреждение: Строка " + lineNumber + " содержит некорректные символы: " + line);
                }
            }
            System.out.println("Загружено " + validWords.size() + " слов из файла");

        } catch (FileNotFoundException e) {
            System.out.println("Ошибка: Файл со словами не найден: " + filePath);
        } catch (IOException e) {
            System.out.println("Ошибка чтения файла: " + e.getMessage());
        }

        return validWords;
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
        drawHangman(wrongAttemptsCount);
        System.out.println("================\n");
        System.out.println("Осталось попыток: " + (MAX_ATTEMPTS - wrongAttemptsCount));
        System.out.println("Слово: " + maskedWord);

        if (!usedLetters.isEmpty()) {
            System.out.print("Использованные буквы: ");
            usedLetters.forEach(letter -> System.out.print(letter + " "));
            System.out.println();
        }
    }

    private static char readPlayerGuess(Scanner scanner) {
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

        drawHangman(wrongAttemptsCount);

    }

    private static void drawHangman(int wrongAttempts) {
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