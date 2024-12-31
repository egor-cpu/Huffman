import java.io.*;
import java.util.*;

class Node implements Comparable<Node> {
    char character;
    int frequency;
    Node left;
    Node right;

    Node(char character, int frequency) {
        this.character = character;
        this.frequency = frequency;
        this.left = null;
        this.right = null;
    }

    @Override
    public int compareTo(Node other) {
        return Integer.compare(this.frequency, other.frequency);
    }
}

public class HuffmanCoding {
    public static Map<Character, String> huffmanCodes = new HashMap<>();

    public static void generateCodes(Node node, String currentCode) {
        if (node == null) return;
        if (node.character != '\0') {
            huffmanCodes.put(node.character, currentCode);
        }
        generateCodes(node.left, currentCode + "0");
        generateCodes(node.right, currentCode + "1");
    }

    public static String huffmanEncode(String data) {
        if (data == null || data.isEmpty()) return "";

        Map<Character, Integer> frequencyMap = new HashMap<>();
        for (char c : data.toCharArray()) {
            frequencyMap.put(c, frequencyMap.getOrDefault(c, 0) + 1);
        }

        PriorityQueue<Node> heap = new PriorityQueue<>();
        for (Map.Entry<Character, Integer> entry : frequencyMap.entrySet()) {
            heap.add(new Node(entry.getKey(), entry.getValue()));
        }

        if (heap.size() == 1) {
            Node singleNode = heap.poll();
            huffmanCodes.put(singleNode.character, "0");
            return "0".repeat(data.length());
        }

        while (heap.size() > 1) {
            Node left = heap.poll();
            Node right = heap.poll();
            Node merged = new Node('\0', left.frequency + right.frequency);
            merged.left = left;
            merged.right = right;
            heap.add(merged);
        }

        Node root = heap.poll();
        generateCodes(root, "");

        StringBuilder encodedData = new StringBuilder();
        for (char c : data.toCharArray()) {
            encodedData.append(huffmanCodes.get(c));
        }

        return encodedData.toString();
    }

    public static String huffmanDecode(String encodedData, Map<String, Character> reverseCodes) {
        if (encodedData == null || encodedData.isEmpty()) return "";

        StringBuilder decodedData = new StringBuilder();
        StringBuilder currentCode = new StringBuilder();

        for (char bit : encodedData.toCharArray()) {
            currentCode.append(bit);
            if (reverseCodes.containsKey(currentCode.toString())) {
                decodedData.append(reverseCodes.get(currentCode.toString()));
                currentCode.setLength(0);
            }
        }

        return decodedData.toString();
    }

    public static void saveToFile(String filename, String data) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(data);
        }
    }

    public static String loadFromFile(String filename) throws IOException {
        StringBuilder data = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                data.append(line);
            }
        }
        return data.toString();
    }

    public static void saveTreeToFile(String filename, Map<Character, String> codes) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (Map.Entry<Character, String> entry : codes.entrySet()) {
                writer.write(entry.getKey() + ":" + entry.getValue());
                writer.newLine();
            }
        }
    }

    public static Map<Character, String> loadTreeFromFile(String filename) throws IOException {
        Map<Character, String> codes = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":", 2);
                if (parts.length == 2) {
                    char character = parts[0].charAt(0);
                    String code = parts[1];
                    codes.put(character, code);
                }
            }
        }
        return codes;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Выберите режим работы:");
        System.out.println("1. Кодирование текста");
        System.out.println("2. Декодирование текста");
        String mode = scanner.nextLine();

        try {
            if (mode.equals("1")) {
                // Кодирование текста
                System.out.println("Введите имя файла с исходным текстом (файл должен быть txt формата):");
                String inputFile = scanner.nextLine() + ".txt";

                System.out.println("Введите имя файла для сохранения закодированного текста:");
                String encodedFile = scanner.nextLine() + ".txt";

                System.out.println("Введите имя файла для сохранения дерева:");
                String treeFile = scanner.nextLine() + ".txt";

                // Загрузка данных из файла
                String data = loadFromFile(inputFile);

                // Кодирование
                String encodedData = huffmanEncode(data);

                // Сохранение результатов
                saveToFile(encodedFile, encodedData);
                saveTreeToFile(treeFile, huffmanCodes);

                System.out.println("Кодирование завершено.");
                System.out.println("Закодированный текст сохранен в файл " + encodedFile);
                System.out.println("Дерево сохранено в файл " + treeFile);

            } else if (mode.equals("2")) {
                // Декодирование текста
                System.out.println("Введите имя файла с закодированным текстом (txt файл):");
                String encodedFile = scanner.nextLine() + ".txt";

                System.out.println("Введите имя файла с деревом (txt файл):");
                String treeFile = scanner.nextLine() + ".txt";

                System.out.println("Введите имя файла для сохранения расшифрованного текста:");
                String decodedFile = scanner.nextLine() + ".txt";

                // Загрузка данных
                String encodedData = loadFromFile(encodedFile);
                Map<Character, String> codes = loadTreeFromFile(treeFile);

                // Создание обратного отображения
                Map<String, Character> reverseCodes = new HashMap<>();
                for (Map.Entry<Character, String> entry : codes.entrySet()) {
                    reverseCodes.put(entry.getValue(), entry.getKey());
                }

                // Декодирование
                String decodedData = huffmanDecode(encodedData, reverseCodes);

                // Сохранение результата
                saveToFile(decodedFile, decodedData);

                System.out.println("Декодирование завершено.");
                System.out.println("Расшифрованный текст сохранен в файл " + decodedFile);

            } else {
                System.out.println("Неверный режим. Попробуйте снова.");
            }
        } catch (IOException e) {
            System.out.println("Произошла ошибка: " + e.getMessage());
        }

        scanner.close();
    }
}
