import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

class HuffmanNode implements Serializable {
    int frequency;
    char character;
    HuffmanNode left, right;

    HuffmanNode(char character, int frequency) {
        this.character = character;
        this.frequency = frequency;
    }

    HuffmanNode(HuffmanNode left, HuffmanNode right) {
        this.left = left;
        this.right = right;
        this.frequency = left.frequency + right.frequency;
    }
}

public class HuffmanCoding {

    private static Map<Character, String> huffmanCodes = new HashMap<>();
    private static Map<Character, Integer> frequencyMap = new HashMap<>();
    private static HuffmanNode root;

    // Шаг 1: Создание частотной таблицы
    public static void buildFrequencyTable(String input) {
        for (char c : input.toCharArray()) {
            frequencyMap.put(c, frequencyMap.getOrDefault(c, 0) + 1);
        }
    }

    // Шаг 2: Построение дерева Хаффмана
    public static void buildHuffmanTree() {
        PriorityQueue<HuffmanNode> priorityQueue = new PriorityQueue<>(Comparator.comparingInt(node -> node.frequency));

        // Создаем узлы для каждого символа
        for (Map.Entry<Character, Integer> entry : frequencyMap.entrySet()) {
            priorityQueue.add(new HuffmanNode(entry.getKey(), entry.getValue()));
        }

        // Строим дерево Хаффмана
        while (priorityQueue.size() > 1) {
            HuffmanNode left = priorityQueue.poll();
            HuffmanNode right = priorityQueue.poll();
            HuffmanNode newNode = new HuffmanNode(left, right);
            priorityQueue.add(newNode);
        }

        root = priorityQueue.poll();
        generateCodes(root, "");
    }

    // Шаг 3: Генерация кодов
    public static void generateCodes(HuffmanNode node, String code) {
        if (node == null) return;

        if (node.character != 0) {
            huffmanCodes.put(node.character, code);
        }

        generateCodes(node.left, code + "0");
        generateCodes(node.right, code + "1");
    }

    // Шаг 4: Кодирование
    public static String encode(String input) {
        StringBuilder encodedString = new StringBuilder();
        for (char c : input.toCharArray()) {
            encodedString.append(huffmanCodes.get(c));
        }
        return encodedString.toString();
    }

    // Шаг 5: Декодирование
    public static String decode(String encodedString) {
        StringBuilder decodedString = new StringBuilder();
        HuffmanNode currentNode = root;

        for (int i = 0; i < encodedString.length(); i++) {
            char bit = encodedString.charAt(i);
            currentNode = (bit == '0') ? currentNode.left : currentNode.right;

            if (currentNode.left == null && currentNode.right == null) {
                decodedString.append(currentNode.character);
                currentNode = root;
            }
        }

        return decodedString.toString();
    }

    // Сериализация дерева для декодирования
    public static void serializeTree(String filename) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(root);
        }
    }

    // Десериализация дерева для декодирования
    public static void deserializeTree(String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            root = (HuffmanNode) in.readObject();
        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        String inputFile = args[0];
        String mode = args[1];

        // Чтение файла
        String input = new String(Files.readAllBytes(Paths.get(inputFile)));

        // Кодирование или декодирование
        if (mode.equals("encode")) {
            buildFrequencyTable(input);
            buildHuffmanTree();
            String encoded = encode(input);
            System.out.println("Encoded: " + encoded);
            serializeTree("huffman_tree.ser");
            Files.write(Paths.get(inputFile + ".encoded"), encoded.getBytes());
        } else if (mode.equals("decode")) {
            deserializeTree("huffman_tree.ser");
            String encodedData = new String(Files.readAllBytes(Paths.get(inputFile)));
            String decoded = decode(encodedData);
            System.out.println("Decoded: " + decoded);
        }
    }
}
