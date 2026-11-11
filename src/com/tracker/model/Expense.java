package com.tracker.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Model class representing an individual expense entry.
 * Practice Area: Classes and Objects (Encapsulation, constructors, methods).
 */
public class Expense implements Comparable<Expense> {
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private int id;
    private double amount;
    private Category category;
    private LocalDate date;
    private String description;

    /**
     * Parameterized constructor.
     */
    public Expense(int id, double amount, Category category, LocalDate date, String description) {
        this.id = id;
        setAmount(amount);
        this.category = (category != null) ? category : Category.OTHER;
        this.date = (date != null) ? date : LocalDate.now();
        this.description = (description != null && !description.trim().isEmpty()) ? description.trim() : "Unspecified";
    }

    // Getters and Setters (Encapsulation)

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        if (amount <= 0.0) {
            throw new IllegalArgumentException("Expense amount must be greater than zero.");
        }
        this.amount = Math.round(amount * 100.0) / 100.0; // Keep 2 decimal places
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = (category != null) ? category : Category.OTHER;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = (date != null) ? date : LocalDate.now();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = (description != null && !description.trim().isEmpty()) ? description.trim() : "Unspecified";
    }

    /**
     * Converts expense to a CSV-formatted line.
     * Escapes commas and quotes in description if necessary.
     */
    public String toCsv() {
        String cleanDesc = description.replace("\"", "\"\"");
        if (cleanDesc.contains(",") || cleanDesc.contains("\"")) {
            cleanDesc = "\"" + cleanDesc + "\"";
        }
        return String.format("%d,%s,%s,%.2f,%s",
                id,
                date.format(DATE_FORMATTER),
                category.name(),
                amount,
                cleanDesc);
    }

    /**
     * Parses an Expense object from a CSV line.
     * Practice Area: File handling & parsing.
     */
    public static Expense fromCsv(String csvLine) {
        if (csvLine == null || csvLine.trim().isEmpty()) {
            return null;
        }

        // Parse CSV handling potential quoted description
        String[] parts = parseCsvLine(csvLine);
        if (parts.length < 5) {
            throw new IllegalArgumentException("Invalid CSV format: expected 5 columns, got " + parts.length);
        }

        int id = Integer.parseInt(parts[0].trim());
        LocalDate date = LocalDate.parse(parts[1].trim(), DATE_FORMATTER);
        Category category = Category.valueOf(parts[2].trim().toUpperCase());
        double amount = Double.parseDouble(parts[3].trim());
        String description = parts[4].trim();

        return new Expense(id, amount, category, date, description);
    }

    /**
     * Simple CSV line splitter that respects quoted strings.
     */
    private static String[] parseCsvLine(String line) {
        java.util.List<String> tokens = new java.util.ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '\"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '\"') {
                    sb.append('\"');
                    i++; // skip escaped quote
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                tokens.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        tokens.add(sb.toString());
        return tokens.toArray(new String[0]);
    }

    @Override
    public int compareTo(Expense other) {
        // Default ordering: newest date first, then higher amount
        int dateCompare = other.date.compareTo(this.date);
        if (dateCompare != 0) {
            return dateCompare;
        }
        return Double.compare(other.amount, this.amount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Expense expense = (Expense) o;
        return id == expense.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("#%-3d | %s | %-22s | $%8.2f | %s",
                id,
                date.format(DATE_FORMATTER),
                category.getDisplayName(),
                amount,
                description);
    }
}
