package com.tracker.storage;

import com.tracker.model.Category;
import com.tracker.model.Expense;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles persistent storage of expenses to and from a CSV file.
 * Practice Area: File Handling (Java I/O, NIO Files, BufferedReader/BufferedWriter, Exception handling).
 */
public class FileHandler {
    private static final String CSV_HEADER = "id,date,category,amount,description";
    private final Path filePath;

    /**
     * Constructs a FileHandler with the target file path.
     *
     * @param filePathString path to the CSV file (e.g., "data/expenses.csv")
     */
    public FileHandler(String filePathString) {
        this.filePath = Paths.get(filePathString);
    }

    /**
     * Ensures parent directories and data file exist.
     * If the file is newly created, populates it with standard header and starter demo records.
     */
    public void initializeStorage() {
        try {
            if (filePath.getParent() != null && !Files.exists(filePath.getParent())) {
                Files.createDirectories(filePath.getParent());
            }

            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
                // Seed with friendly starter demo data so the user can immediately explore summaries
                List<Expense> starterData = createStarterExpenses();
                saveExpenses(starterData);
            }
        } catch (IOException e) {
            System.err.println("  [Error] Failed to initialize storage file: " + e.getMessage());
        }
    }

    /**
     * Loads all expenses from the CSV file.
     * Practice Area: File reading using try-with-resources and BufferedReader.
     *
     * @return List of parsed Expense objects
     */
    public List<Expense> loadExpenses() {
        List<Expense> expenses = new ArrayList<>();

        if (!Files.exists(filePath)) {
            return expenses;
        }

        try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            String line = reader.readLine(); // Header line

            // Check if header is present
            if (line == null) {
                return expenses;
            }

            int lineNum = 1;
            while ((line = reader.readLine()) != null) {
                lineNum++;
                line = line.trim();
                if (line.isEmpty()) {
                    continue; // Skip blank lines
                }

                try {
                    Expense expense = Expense.fromCsv(line);
                    if (expense != null) {
                        expenses.add(expense);
                    }
                } catch (Exception ex) {
                    System.err.printf("  [Warning] Skipping malformed line %d in %s: %s\n",
                            lineNum, filePath.getFileName(), ex.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("  [Error] Failed to read from file " + filePath + ": " + e.getMessage());
        }

        return expenses;
    }

    /**
     * Saves the entire list of expenses to the CSV file.
     * Practice Area: File writing using try-with-resources and BufferedWriter.
     *
     * @param expenses list of expenses to persist
     * @return true if successful, false otherwise
     */
    public boolean saveExpenses(List<Expense> expenses) {
        try {
            if (filePath.getParent() != null && !Files.exists(filePath.getParent())) {
                Files.createDirectories(filePath.getParent());
            }

            try (BufferedWriter writer = Files.newBufferedWriter(filePath,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE)) {

                // Write CSV header
                writer.write(CSV_HEADER);
                writer.newLine();

                // Write each expense row
                for (Expense expense : expenses) {
                    writer.write(expense.toCsv());
                    writer.newLine();
                }
            }
            return true;
        } catch (IOException e) {
            System.err.println("  [Error] Failed to save expenses to file: " + e.getMessage());
            return false;
        }
    }

    /**
     * Gets the path to the CSV file.
     */
    public Path getFilePath() {
        return filePath;
    }

    /**
     * Generates a sample set of starter expenses for realistic testing.
     */
    private List<Expense> createStarterExpenses() {
        List<Expense> list = new ArrayList<>();
        LocalDate today = LocalDate.now();

        list.add(new Expense(1, 14.50, Category.FOOD, today.minusDays(5), "Lunch at Subway"));
        list.add(new Expense(2, 45.00, Category.TRANSPORT, today.minusDays(4), "Monthly Metro Pass reload"));
        list.add(new Expense(3, 89.90, Category.UTILITIES, today.minusDays(3), "High-speed Internet Bill"));
        list.add(new Expense(4, 32.25, Category.FOOD, today.minusDays(2), "Weekly grocery store"));
        list.add(new Expense(5, 18.00, Category.ENTERTAINMENT, today.minusDays(1), "Movie tickets (Cinema)"));
        list.add(new Expense(6, 65.00, Category.SHOPPING, today, "Winter Jacket on discount"));

        return list;
    }
}
