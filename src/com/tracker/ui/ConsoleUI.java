package com.tracker.ui;

import com.tracker.model.Category;
import com.tracker.model.Expense;
import com.tracker.model.SpendingSummary;
import com.tracker.service.ExpenseService;
import com.tracker.validation.InputValidator;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

/**
 * Handles the interactive console user interface, menus, table rendering, and visual summaries.
 */
public class ConsoleUI {
    private final ExpenseService service;
    private final Scanner scanner;

    public ConsoleUI(ExpenseService service, Scanner scanner) {
        this.service = service;
        this.scanner = scanner;
    }

    /**
     * Starts the main interactive console loop.
     */
    public void start() {
        printWelcomeBanner();

        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = InputValidator.readIntInRange(scanner, "  Enter your choice [1-8]: ", 1, 8);
            System.out.println();

            switch (choice) {
                case 1 -> handleAddExpense();
                case 2 -> handleViewAllExpenses();
                case 3 -> handleSpendingSummary();
                case 4 -> handleFilterAndSearch();
                case 5 -> handleEditExpense();
                case 6 -> handleDeleteExpense();
                case 7 -> handleManualSave();
                case 8 -> {
                    service.saveData();
                    System.out.println("  =======================================================");
                    System.out.println("   All data saved successfully to disk. Goodbye!");
                    System.out.println("  =======================================================\n");
                    running = false;
                }
            }
        }
    }

    private void printWelcomeBanner() {
        System.out.println();
        System.out.println("==================================================================");
        System.out.println("          PERSONAL EXPENSE TRACKER - JAVA CONSOLE APP             ");
        System.out.println("==================================================================");
        System.out.println(" Track your daily spending, categorize expenses, view summaries,  ");
        System.out.println(" and keep your budget organized with persistent local storage.    ");
        System.out.println("==================================================================");
    }

    private void printMainMenu() {
        System.out.println();
        System.out.println("------------------------- MAIN MENU -------------------------");
        System.out.println("  [1] Add New Expense");
        System.out.println("  [2] View All Expenses (with Sorting)");
        System.out.println("  [3] View Spending Summary & Analytics");
        System.out.println("  [4] Filter Expenses (by Category / Date)");
        System.out.println("  [5] Edit an Expense");
        System.out.println("  [6] Delete an Expense");
        System.out.println("  [7] Force Save Data to CSV File");
        System.out.println("  [8] Save and Exit");
        System.out.println("-------------------------------------------------------------");
    }

    private void handleAddExpense() {
        System.out.println("--- [ADD NEW EXPENSE] ---");

        double amount = InputValidator.readPositiveDouble(scanner, "  Amount ($): ");
        Category category = InputValidator.readCategory(scanner);
        LocalDate date = InputValidator.readDate(scanner, "  Date (YYYY-MM-DD or Enter for today): ");
        String description = InputValidator.readNonEmptyString(scanner, "  Description/Note: ");

        Expense created = service.addExpense(amount, category, date, description);
        System.out.println();
        System.out.println("  [SUCCESS] Expense recorded successfully!");
        System.out.println("  " + created);
    }

    private void handleViewAllExpenses() {
        List<Expense> all = service.getAllExpenses();
        if (all.isEmpty()) {
            System.out.println("  [INFO] No expenses recorded yet. Use Option 1 to add your first expense!");
            return;
        }

        System.out.println("--- [SORT OPTIONS] ---");
        System.out.println("  [1] Date (Newest first - Default)");
        System.out.println("  [2] Date (Oldest first)");
        System.out.println("  [3] Amount (Highest first)");
        System.out.println("  [4] Amount (Lowest first)");
        int sortChoice = InputValidator.readIntInRange(scanner, "  Select sort order [1-4]: ", 1, 4);

        List<Expense> sorted = service.getSortedExpenses(sortChoice);
        renderExpenseTable("ALL EXPENSES (" + sorted.size() + " total)", sorted);
    }

    private void handleSpendingSummary() {
        SpendingSummary summary = service.calculateSummary();

        if (summary.getTotalCount() == 0) {
            System.out.println("  [INFO] No expenses to analyze yet. Add some expenses first!");
            return;
        }

        System.out.println("==================================================================");
        System.out.println("                    SPENDING SUMMARY & ANALYSIS                   ");
        System.out.println("==================================================================");
        System.out.printf("  Total Spending       : $%,.2f\n", summary.getTotalSpending());
        System.out.printf("  Total Transactions   : %d\n", summary.getTotalCount());
        System.out.printf("  Average per Expense  : $%,.2f\n", summary.getAverageSpending());

        if (summary.getHighestExpense() != null) {
            Expense high = summary.getHighestExpense();
            System.out.printf("  Highest Single Item  : $%,.2f (%s - %s)\n",
                    high.getAmount(), high.getDescription(), high.getCategory().getDisplayName());
        }

        if (summary.getLowestExpense() != null) {
            Expense low = summary.getLowestExpense();
            System.out.printf("  Lowest Single Item   : $%,.2f (%s - %s)\n",
                    low.getAmount(), low.getDescription(), low.getCategory().getDisplayName());
        }

        System.out.println("\n----------------- SPENDING BREAKDOWN BY CATEGORY -----------------");
        System.out.printf("  %-25s | %10s | %7s | %s\n", "Category", "Amount", "Share", "Distribution");
        System.out.println("  --------------------------+------------+---------+----------------------");

        // Sort categories by spending descending
        summary.getSpendingByCategory().entrySet().stream()
                .sorted(Map.Entry.<Category, Double>comparingByValue().reversed())
                .forEach(entry -> {
                    Category cat = entry.getKey();
                    double amount = entry.getValue();
                    double percent = summary.getPercentageByCategory().getOrDefault(cat, 0.0);
                    String bar = renderProgressBar(percent, 16);
                    System.out.printf("  %-25s | $%9.2f | %5.1f%%  | %s\n",
                            cat.getDisplayName(), amount, percent, bar);
                });

        // Monthly Breakdown
        if (!summary.getSpendingByMonth().isEmpty()) {
            System.out.println("\n----------------- SPENDING BREAKDOWN BY MONTH -------------------");
            System.out.printf("  %-15s | %12s\n", "Month (YYYY-MM)", "Total Spent");
            System.out.println("  ----------------+--------------");
            for (Map.Entry<String, Double> entry : summary.getSpendingByMonth().entrySet()) {
                System.out.printf("  %-15s | $%11.2f\n", entry.getKey(), entry.getValue());
            }
        }
        System.out.println("==================================================================");
    }

    private void handleFilterAndSearch() {
        System.out.println("--- [FILTER EXPENSES] ---");
        System.out.println("  [1] Filter by Category");
        System.out.println("  [2] Filter by Date Range");
        int subChoice = InputValidator.readIntInRange(scanner, "  Select filter mode [1-2]: ", 1, 2);

        if (subChoice == 1) {
            Category cat = InputValidator.readCategory(scanner);
            List<Expense> filtered = service.filterByCategory(cat);
            renderExpenseTable("EXPENSES IN CATEGORY: " + cat.getDisplayName(), filtered);
        } else {
            System.out.println("  Define date range (YYYY-MM-DD):");
            LocalDate start = InputValidator.readDate(scanner, "  Start Date (or Enter for today): ");
            LocalDate end = InputValidator.readDate(scanner, "  End Date (or Enter for today): ");

            if (start.isAfter(end)) {
                System.out.println("  [!] Start date was after end date. Swapping automatically...");
                LocalDate temp = start;
                start = end;
                end = temp;
            }

            List<Expense> filtered = service.filterByDateRange(start, end);
            renderExpenseTable(String.format("EXPENSES FROM %s TO %s", start, end), filtered);
        }
    }

    private void handleEditExpense() {
        System.out.println("--- [EDIT EXPENSE] ---");
        int id = InputValidator.readIntInRange(scanner, "  Enter Expense ID to edit: ", 1, Integer.MAX_VALUE);

        Optional<Expense> opt = service.findById(id);
        if (opt.isEmpty()) {
            System.out.println("  [!] No expense found with ID #" + id);
            return;
        }

        Expense current = opt.get();
        System.out.println("\n  Current Record:");
        System.out.println("  " + current);
        System.out.println("\n  Enter new values (or press Enter to keep current value):");

        // Amount
        System.out.print("  Amount [Current: $" + String.format("%.2f", current.getAmount()) + "]: ");
        String amtStr = scanner.nextLine().trim();
        double amount = current.getAmount();
        if (!amtStr.isEmpty()) {
            try {
                if (amtStr.startsWith("$") || amtStr.startsWith("₹")) amtStr = amtStr.substring(1);
                double parsed = Double.parseDouble(amtStr);
                if (parsed > 0) amount = parsed;
                else System.out.println("  [!] Non-positive amount ignored; keeping current.");
            } catch (NumberFormatException e) {
                System.out.println("  [!] Invalid number; keeping current.");
            }
        }

        // Category
        System.out.print("  Change category? (Current: " + current.getCategory().getDisplayName() + ") (y/N): ");
        String changeCat = scanner.nextLine().trim().toLowerCase();
        Category category = current.getCategory();
        if (changeCat.equals("y") || changeCat.equals("yes")) {
            category = InputValidator.readCategory(scanner);
        }

        // Date
        System.out.print("  Date (YYYY-MM-DD) [Current: " + current.getDate() + "]: ");
        String dateStr = scanner.nextLine().trim();
        LocalDate date = current.getDate();
        if (!dateStr.isEmpty()) {
            try {
                date = LocalDate.parse(dateStr, Expense.DATE_FORMATTER);
            } catch (Exception e) {
                System.out.println("  [!] Invalid date format; keeping current.");
            }
        }

        // Description
        String desc = InputValidator.readOptionalString(scanner,
                "  Description [Current: " + current.getDescription() + "]: ", current.getDescription());

        boolean ok = service.updateExpense(id, amount, category, date, desc);
        if (ok) {
            System.out.println("\n  [SUCCESS] Expense #" + id + " updated successfully!");
        } else {
            System.out.println("\n  [ERROR] Failed to update expense #" + id);
        }
    }

    private void handleDeleteExpense() {
        System.out.println("--- [DELETE EXPENSE] ---");
        int id = InputValidator.readIntInRange(scanner, "  Enter Expense ID to delete: ", 1, Integer.MAX_VALUE);

        Optional<Expense> opt = service.findById(id);
        if (opt.isEmpty()) {
            System.out.println("  [!] No expense found with ID #" + id);
            return;
        }

        Expense current = opt.get();
        System.out.println("\n  Found Record:");
        System.out.println("  " + current);

        boolean confirm = InputValidator.confirmAction(scanner, "  Are you sure you want to delete this expense?");
        if (confirm) {
            boolean removed = service.deleteExpense(id);
            if (removed) {
                System.out.println("  [SUCCESS] Expense #" + id + " has been permanently deleted.");
            } else {
                System.out.println("  [ERROR] Failed to delete expense #" + id);
            }
        } else {
            System.out.println("  [INFO] Deletion canceled.");
        }
    }

    private void handleManualSave() {
        boolean saved = service.saveData();
        if (saved) {
            System.out.println("  [SUCCESS] All records saved successfully to file.");
        } else {
            System.out.println("  [ERROR] Could not save records to file.");
        }
    }

    /**
     * Renders a neatly aligned ASCII table for a list of expenses.
     */
    private void renderExpenseTable(String title, List<Expense> list) {
        System.out.println();
        System.out.println("==========================================================================================");
        System.out.println("  " + title);
        System.out.println("==========================================================================================");
        System.out.printf("  %-4s | %-10s | %-24s | %10s | %s\n", "ID", "Date", "Category", "Amount", "Description");
        System.out.println("  -----+------------+--------------------------+------------+-----------------------------");

        if (list.isEmpty()) {
            System.out.println("  No matching expense records found.");
        } else {
            double subtotal = 0.0;
            for (Expense e : list) {
                System.out.printf("  #%-3d | %-10s | %-24s | $%9.2f | %s\n",
                        e.getId(),
                        e.getDate().format(Expense.DATE_FORMATTER),
                        e.getCategory().getDisplayName(),
                        e.getAmount(),
                        e.getDescription());
                subtotal += e.getAmount();
            }
            System.out.println("  -----+------------+--------------------------+------------+-----------------------------");
            System.out.printf("  TOTAL (%d item%s):                              | $%9.2f |\n",
                    list.size(), (list.size() == 1 ? "" : "s"), subtotal);
        }
        System.out.println("==========================================================================================");
    }

    /**
     * Generates an ASCII visual bar indicator for percentages.
     */
    private String renderProgressBar(double percent, int barLength) {
        int filled = (int) Math.round((percent / 100.0) * barLength);
        filled = Math.max(0, Math.min(barLength, filled));
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < filled; i++) {
            sb.append("=");
        }
        for (int i = filled; i < barLength; i++) {
            sb.append(" ");
        }
        sb.append("]");
        return sb.toString();
    }
}
