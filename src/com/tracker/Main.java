package com.tracker;

import com.tracker.service.ExpenseService;
import com.tracker.storage.FileHandler;
import com.tracker.ui.ConsoleUI;

import java.util.Scanner;

/**
 * Entry point for the Personal Expense Tracker console application.
 *
 * Practice areas covered:
 * 1. Classes and Objects: Clean object-oriented design, encapsulation, separation of concerns.
 * 2. Collections: Using List, Map, Streams, and Comparators for aggregation and filtering.
 * 3. Input Validation: Robust Scanner parsing, range checking, date & currency validation.
 * 4. File Handling: Persistent CSV read/write using modern Java NIO with proper exception handling.
 */
public class Main {
    private static final String DEFAULT_DATA_PATH = "data/expenses.csv";

    public static void main(String[] args) {
        String dataPath = (args.length > 0) ? args[0] : DEFAULT_DATA_PATH;

        // Initialize storage and ensure data file exists
        FileHandler fileHandler = new FileHandler(dataPath);
        fileHandler.initializeStorage();

        // Initialize service and load existing data
        ExpenseService expenseService = new ExpenseService(fileHandler);
        expenseService.loadData();

        // Register shutdown hook to guarantee data persistence on abnormal termination (e.g. Ctrl+C)
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            expenseService.saveData();
        }));

        // Start console user interface
        try (Scanner scanner = new Scanner(System.in)) {
            ConsoleUI ui = new ConsoleUI(expenseService, scanner);
            ui.start();
        } catch (Exception e) {
            System.err.println("Fatal Application Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
