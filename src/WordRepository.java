import java.io.*;
import java.util.*;

public class WordRepository {
    private static final String WORDS_FILE_PATH = "resources/words.txt";

    public List<String> loadWordsFromFile() throws FileNotFoundException {
        File file = new File(WORDS_FILE_PATH);
        String absolutePath = file.getAbsolutePath();

        if (!file.exists()) {
            throw new FileNotFoundException("Файл со словами не найден: " + absolutePath);
        }

        List<String> words = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
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
                    System.out.printf("Предупреждение: Строка %d содержит некорректные символы: %s%n", lineNumber, line);
                }
            }
            System.out.printf("Загружено %d слов из файла%n", words.size());

        } catch (IOException e) {
            throw new RuntimeException("Ошибка чтения файла: " + absolutePath + " - " + e.getMessage(), e);
        }

        return words;
    }

    public static boolean isValidRussianWord(String word) {
        return word.matches("[а-яё]+");
    }

    public static String selectRandomWord(List<String> words) {
        Random random = new Random();
        return words.get(random.nextInt(words.size()));
    }
}

