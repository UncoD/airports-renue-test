package com.uncod;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException, CsvException {
        int colIndex = getColumnIndex(args);

        TreeMap<String, ArrayList<String>> lines =
                combineAirportsByColumnFromFile(colIndex, "airports.dat");

        String search = getUserInput();

        long start = System.currentTimeMillis();

        ArrayList<String> result = findAirports(lines, search);

        long finish = System.currentTimeMillis();
        long timeElapsed = finish - start;

        int count = result.size();
        if (count > 0) {
            System.out.println(String.join("\n", result));
        }
        System.out.println("\nКоличество найденных строк: " + count);
        System.out.println("Время, затраченное на поиск: " + timeElapsed + "мс");
    }

    private static TreeMap<String, ArrayList<String>> combineAirportsByColumnFromFile(
            int colIndex, String fileName
    ) throws IOException, CsvValidationException {
        BufferedReader br = getBufferedReaderForFile(fileName);
        CSVReader reader = new CSVReader(br);

        TreeMap<String, ArrayList<String>> lines = new TreeMap<>();
        String[] line;
        while ((line = reader.readNext()) != null) {
            if (colIndex >= line.length) {
                System.out.println("Строка \"" + String.join(",", line) + "\" не содержит " + (colIndex + 1) + " колонки");
                System.exit(1);
                return null;
            }
            String key = line[colIndex];
            if (!lines.containsKey(key)) {
                lines.put(key, new ArrayList<>());
            }
            lines.get(key).add(String.join(", ", line));
        }

        reader.close();
        return lines;
    }

    private static String getUserInput() {
        System.out.println("Введите строку:");
        Scanner sc = new Scanner(System.in);
        return sc.nextLine();
    }

    private static BufferedReader getBufferedReaderForFile(String fileName) {
        try {
            InputStream file = ClassLoader.getSystemResourceAsStream(fileName);
            InputStreamReader ir = new InputStreamReader(file, StandardCharsets.UTF_8);
            return new BufferedReader(ir);
        } catch (NullPointerException e) {
            System.out.println("Файл " + fileName + " не найден");
            System.exit(1);
            return null;
        }
    }

    private static int getColumnIndex(String[] args) throws IOException {
        int colIndex = 0;
        if (args.length > 0) {
            try {
                colIndex = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("Аргумент должен быть целым числом");
                System.exit(1);
            }
        } else {
            InputStream settings = ClassLoader.getSystemResourceAsStream("application.yml");
            try {
                Properties prop = new Properties();
                prop.load(settings);
                colIndex = Integer.parseInt(prop.getProperty("column"));
            } catch (NullPointerException e) {
                System.out.println("Не найден файл с настройками приложения application.yml");
                System.exit(1);
            } catch (NumberFormatException e) {
                System.out.println("Свойство column (application.yml) не найдено или не может быть преобразовано в целое цисло");
                System.exit(1);
            }
        }

        if (colIndex < 1) {
            System.out.println("Номер колонки не может быть меньше 1");
            System.exit(1);
        }

        colIndex--;
        return colIndex;
    }

    private static ArrayList<String> findAirports(TreeMap<String, ArrayList<String>> lines, String search) {
        ArrayList<String> result = new ArrayList<>();
        for (String key : lines.keySet()) {
            if (key.startsWith(search)) {
                result.addAll(lines.get(key));
            } else if (key.compareTo(search) > 0){
                return result;
            }
        }
        return result;
    }
}
