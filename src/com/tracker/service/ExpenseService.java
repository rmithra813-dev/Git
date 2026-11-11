package com.tracker.service;

import com.tracker.model.Category;
import com.tracker.model.Expense;
import com.tracker.model.SpendingSummary;
import com.tracker.storage.FileHandler;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service managing in-memory expense records, business operations, and analytics.
 * Practice Areas: Collections (List, Map, Streams, Comparator), Classes and Objects.
 */
public class ExpenseService {
    private final List<Expense> expenses;
    private final FileHandler fileHandler;
    private int nextId;

    public ExpenseService(FileHandler fileHandler) {
        this.fileHandler = fileHandler;
        this.expenses = new ArrayList<>();
        this.nextId = 1;
    }

    /**
     * Initializes data from persistent storage.
     */
    public void loadData() {
        expenses.clear();
        List<Expense> loaded = fileHandler.loadExpenses();
        expenses.addAll(loaded);

        // Compute next available ID
        int maxId = expenses.stream()
                .mapToInt(Expense::getId)
                .max()
                .orElse(0);
        this.nextId = maxId + 1;
    }

    /**
     * Persists the current in-memory list to the storage file.
     *
     * @return true if successful
     */
    public boolean saveData() {
        return fileHandler.saveExpenses(expenses);
    }

    /**
     * Adds a new expense and auto-saves to storage.
     */
    public Expense addExpense(double amount, Category category, LocalDate date, String description) {
        Expense expense = new Expense(nextId++, amount, category, date, description);
        expenses.add(expense);
        saveData();
        return expense;
    }

    /**
     * Returns an unmodifiable view of all recorded expenses.
     */
    public List<Expense> getAllExpenses() {
        return Collections.unmodifiableList(expenses);
    }

    /**
     * Finds an expense by its unique ID.
     */
    public Optional<Expense> findById(int id) {
        return expenses.stream()
                .filter(e -> e.getId() == id)
                .findFirst();
    }

    /**
     * Updates an existing expense.
     *
     * @return true if updated, false if not found
     */
    public boolean updateExpense(int id, double amount, Category category, LocalDate date, String description) {
        Optional<Expense> opt = findById(id);
        if (opt.isPresent()) {
            Expense expense = opt.get();
            expense.setAmount(amount);
            expense.setCategory(category);
            expense.setDate(date);
            expense.setDescription(description);
            saveData();
            return true;
        }
        return false;
    }

    /**
     * Deletes an expense by ID.
     *
     * @return true if deleted, false if not found
     */
    public boolean deleteExpense(int id) {
        boolean removed = expenses.removeIf(e -> e.getId() == id);
        if (removed) {
            saveData();
        }
        return removed;
    }

    /**
     * Filters expenses by category.
     */
    public List<Expense> filterByCategory(Category category) {
        return expenses.stream()
                .filter(e -> e.getCategory() == category)
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Filters expenses within a date range (inclusive).
     */
    public List<Expense> filterByDateRange(LocalDate start, LocalDate end) {
        return expenses.stream()
                .filter(e -> !e.getDate().isBefore(start) && !e.getDate().isAfter(end))
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Returns a sorted copy of expenses.
     *
     * @param sortBy   1: Date descending, 2: Date ascending, 3: Amount descending, 4: Amount ascending
     */
    public List<Expense> getSortedExpenses(int sortBy) {
        List<Expense> copy = new ArrayList<>(expenses);
        switch (sortBy) {
            case 1 -> copy.sort(Comparator.comparing(Expense::getDate).reversed());
            case 2 -> copy.sort(Comparator.comparing(Expense::getDate));
            case 3 -> copy.sort(Comparator.comparing(Expense::getAmount).reversed());
            case 4 -> copy.sort(Comparator.comparing(Expense::getAmount));
            default -> copy.sort(Comparator.naturalOrder());
        }
        return copy;
    }

    /**
     * Computes a full spending summary including totals, averages, category aggregates, and percentages.
     * Practice Area: Collections aggregation using Map and Streams.
     */
    public SpendingSummary calculateSummary() {
        if (expenses.isEmpty()) {
            return new SpendingSummary(0.0, 0, 0.0, null, null,
                    Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap());
        }

        double total = expenses.stream()
                .mapToDouble(Expense::getAmount)
                .sum();
        int count = expenses.size();
        double avg = total / count;

        Expense highest = expenses.stream()
                .max(Comparator.comparingDouble(Expense::getAmount))
                .orElse(null);

        Expense lowest = expenses.stream()
                .min(Comparator.comparingDouble(Expense::getAmount))
                .orElse(null);

        // Group spending by category
        Map<Category, Double> byCategory = new EnumMap<>(Category.class);
        for (Expense e : expenses) {
            byCategory.put(e.getCategory(), byCategory.getOrDefault(e.getCategory(), 0.0) + e.getAmount());
        }

        // Calculate percentage for each category
        Map<Category, Double> percentages = new EnumMap<>(Category.class);
        for (Map.Entry<Category, Double> entry : byCategory.entrySet()) {
            double percent = (total > 0.0) ? (entry.getValue() / total) * 100.0 : 0.0;
            percentages.put(entry.getKey(), Math.round(percent * 10.0) / 10.0);
        }

        // Group spending by Month (YYYY-MM)
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
        Map<String, Double> byMonth = new TreeMap<>();
        for (Expense e : expenses) {
            String monthKey = e.getDate().format(monthFormatter);
            byMonth.put(monthKey, byMonth.getOrDefault(monthKey, 0.0) + e.getAmount());
        }

        return new SpendingSummary(total, count, avg, highest, lowest, byCategory, percentages, byMonth);
    }
}
