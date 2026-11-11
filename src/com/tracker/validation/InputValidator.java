package com.tracker.validation;

import com.tracker.model.Category;
import com.tracker.model.Expense;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Utility class providing robust console input validation.
 * Practice Area: Input Validation (Exception handling, loop validation, type casting).
 */
public class InputValidator {

    private InputValidator() {
        // Prevent instantiation
    }

    /**
     * Safely reads a positive decimal amount from the console.
     * Continuously prompts until the user enters a valid number > 0.
     *
     * @param scanner active Scanner
     * @param prompt  message shown to the user
     * @return validated positive double
     */
    public static double readPositiveDouble(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println("  [!] Input cannot be empty. Please enter an amount.");
                continue;
            }

            // Remove currency symbol if user typed it (e.g., "$50" or "₹50")
            if (input.startsWith("$") || input.startsWith("₹") || input.startsWith("€") || input.startsWith("£")) {
                input = input.substring(1).trim();
            }

            try {
                double value = Double.parseDouble(input);
                if (value <= 0.0) {
                    System.out.println("  [!] Amount must be strictly greater than 0.00. Please try again.");
                    continue;
                }
                return Math.round(value * 100.0) / 100.0;
            } catch (NumberFormatException e) {
                System.out.println("  [!] Invalid number format: '" + input + "'. Example: 45.50");
            }
        }
    }

    /**
     * Reads and validates a date in YYYY-MM-DD format.
     * Allows pressing Enter or typing 'today' to default to current date.
     *
     * @param scanner active Scanner
     * @param prompt  message shown to user
     * @return validated LocalDate
     */
    public static LocalDate readDate(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            if (input.isEmpty() || input.equalsIgnoreCase("today") || input.equalsIgnoreCase("t")) {
                return LocalDate.now();
            }

            try {
                LocalDate date = LocalDate.parse(input, Expense.DATE_FORMATTER);

                // Sensible date boundary checks
                LocalDate minDate = LocalDate.of(2000, 1, 1);
                LocalDate maxDate = LocalDate.now().plusMonths(1); // allow small forward scheduling

                if (date.isBefore(minDate)) {
                    System.out.println("  [!] Date cannot be before year 2000.");
                    continue;
                }
                if (date.isAfter(maxDate)) {
                    System.out.println("  [!] Date cannot be more than 1 month into the future.");
                    continue;
                }

                return date;
            } catch (DateTimeParseException e) {
                System.out.println("  [!] Invalid date format. Please use YYYY-MM-DD (e.g., 2026-11-11) or press Enter for today.");
            }
        }
    }

    /**
     * Prompts the user to pick a category either by number (1..N) or by name.
     *
     * @param scanner active Scanner
     * @return selected Category
     */
    public static Category readCategory(Scanner scanner) {
        Category[] categories = Category.values();
        System.out.println("\n  Available Categories:");
        for (int i = 0; i < categories.length; i++) {
            System.out.printf("   [%d] %-24s", (i + 1), categories[i].getDisplayName());
            if ((i + 1) % 2 == 0 || i == categories.length - 1) {
                System.out.println();
            }
        }

        while (true) {
            System.out.print("  Select Category (1-" + categories.length + " or name): ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println("  [!] Please select a category.");
                continue;
            }

            // Try parsing as numeric choice
            try {
                int choice = Integer.parseInt(input);
                Category cat = Category.fromIndex(choice);
                if (cat != null) {
                    return cat;
                }
                System.out.println("  [!] Please enter a number between 1 and " + categories.length + ".");
                continue;
            } catch (NumberFormatException ignored) {
                // Not a number, try parsing as name
            }

            // Try matching category by name
            for (Category cat : categories) {
                if (cat.name().equalsIgnoreCase(input) || cat.getDisplayName().equalsIgnoreCase(input)) {
                    return cat;
                }
            }

            System.out.println("  [!] Unrecognized category: '" + input + "'. Please choose from the list.");
        }
    }

    /**
     * Reads a required non-empty string.
     *
     * @param scanner active Scanner
     * @param prompt  message shown to user
     * @return trimmed non-empty string
     */
    public static String readNonEmptyString(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("  [!] This field cannot be left blank. Please enter a value.");
        }
    }

    /**
     * Reads an optional string, defaulting to defaultValue if user presses Enter.
     *
     * @param scanner      active Scanner
     * @param prompt       message shown to user
     * @param defaultValue default value if input is empty
     * @return string
     */
    public static String readOptionalString(Scanner scanner, String prompt, String defaultValue) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        return input.isEmpty() ? defaultValue : input;
    }

    /**
     * Reads an integer within a specified range [min, max].
     *
     * @param scanner active Scanner
     * @param prompt  message shown to user
     * @param min     minimum allowed value
     * @param max     maximum allowed value
     * @return validated integer
     */
    public static int readIntInRange(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            try {
                int val = Integer.parseInt(input);
                if (val >= min && val <= max) {
                    return val;
                }
                System.out.printf("  [!] Please enter a number between %d and %d.\n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("  [!] Invalid input. Please enter a whole number.");
            }
        }
    }

    /**
     * Prompts for confirmation (Y/N).
     *
     * @param scanner active Scanner
     * @param prompt  message shown to user
     * @return true if user enters Y/yes, false otherwise
     */
    public static boolean confirmAction(Scanner scanner, String prompt) {
        System.out.print(prompt + " (y/N): ");
        String input = scanner.nextLine().trim().toLowerCase();
        return input.equals("y") || input.equals("yes");
    }
}
