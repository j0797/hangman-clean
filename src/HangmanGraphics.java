import java.util.Set;

public class HangmanGraphics {
    public static final int MAX_ATTEMPTS = 6;
    public static final char START = '1';
    public static final char QUIT = '2';
    private static final String[] PICTURES = {
            """
  _______
  |     |
  |
  |
  |
  |
__|________
""",
            """
  _______
  |     |
  |     O
  |
  |
  |
__|________
""",
            """
  _______
  |     |
  |     O
  |     |
  |
  |
__|________
""",
            """
  _______
  |     |
  |     O
  |    /|
  |
  |
__|________
""",
            """
  _______
  |     |
  |     O
  |    /|\\
  |
  |
__|________
""",
            """
  _______
  |     |
  |     O
  |    /|\\
  |    /
  |
__|________
""",
            """
  _______
  |     |
  |     O
  |    /|\\
  |    / \\
  |
__|________
"""
    };

    public static void displayMainMenu() {
        System.out.println("\n=== ИГРА ВИСЕЛИЦА ===");
        System.out.println(START + " - начать новую игру");
        System.out.println(QUIT + " - выйти из игры");
        System.out.println("Выберите действие: ");
    }

    public static void displayGameState(String maskedWord, Set<Character> usedLetters, int wrongAttemptsCount) {
        System.out.println("\n=== ВИСЕЛИЦА ===");
        displayHangman(wrongAttemptsCount);
        System.out.println("================\n");
        System.out.printf("Осталось попыток: %d%n", MAX_ATTEMPTS - wrongAttemptsCount);
        System.out.println("Слово: " + maskedWord);

        if (!usedLetters.isEmpty()) {
            System.out.print("Использованные буквы: ");
            usedLetters.stream()
                    .sorted()
                    .forEach(letter -> System.out.print(letter + " "));
            System.out.println();
        }
    }

    public static void displayGameResult(String secretWord, int wrongAttemptsCount, boolean isGameWon) {
        if (isGameWon) {
            System.out.println("Поздравляем! Вы отгадали слово: " + secretWord);
        } else {
            System.out.println("Вы проиграли! Загаданное слово: " + secretWord);
            displayHangman(wrongAttemptsCount);
        }
    }

    public static void displayHangman(int numPicture) {
        if (numPicture >= 0 && numPicture < PICTURES.length) {
            System.out.println(PICTURES[numPicture]);
        }
    }
}