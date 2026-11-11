# Personal Expense Tracker (Java Console Application)

A robust, object-oriented Java console application designed to help users track personal expenses, categorize spending, inspect analytical spending summaries, and maintain persistent storage across sessions.

---

## 🎯 Target Specifications & Practice Areas

| Practice Area | How It Is Implemented |
|---|---|
| **Classes and Objects** | Clean object-oriented architecture (`Expense`, `Category`, `SpendingSummary`, `ExpenseService`, `FileHandler`, `InputValidator`, `ConsoleUI`, `Main`). Proper encapsulation with private fields, validation in constructors and setters, clear separation of concerns. |
| **Collections** | Uses `List<Expense>` (`ArrayList`) for dynamic records, `Map<Category, Double>` for category aggregations, `Map<String, Double>` (`TreeMap`) for monthly breakdowns, Java Streams API (`filter`, `mapToDouble`, `sum`, `groupingBy`), and `Comparator` for multi-criteria sorting. |
| **Input Validation** | Defensive console input parsing using `InputValidator`. Loops until valid input is received, handles `NumberFormatException` and `DateTimeParseException`, validates positive amounts, enforces date constraints (`YYYY-MM-DD` or 'today'), and validates category selections. |
| **File Handling** | CSV persistence in `data/expenses.csv` via `FileHandler`. Employs modern `java.nio.file.Path`, `BufferedReader`, `BufferedWriter`, `try-with-resources`, safe directory/file initialization, and shutdown hooks to guarantee data is saved even on abnormal exits. |

---

## 📂 Project Directory Structure

```
PersonalExpenseTracker/
├── bin/                          # Compiled .class bytecode
├── data/
│   └── expenses.csv              # Persistent CSV database
├── src/
│   └── com/
│       └── tracker/
│           ├── Main.java                 # Entry point & shutdown hook
│           ├── model/
│           │   ├── Category.java         # Enum with display names & lookups
│           │   ├── Expense.java          # Core domain model with CSV serialization
│           │   └── SpendingSummary.java  # Analytical DTO for totals, averages & breakdowns
│           ├── service/
│           │   └── ExpenseService.java   # Business logic, collections, stream calculations
│           ├── storage/
│           │   └── FileHandler.java      # Buffered file I/O & CSV persistence
│           ├── ui/
│           │   └── ConsoleUI.java        # Interactive ASCII menus, tables & progress bars
│           └── validation/
│               └── InputValidator.java   # Safe input parsing & boundary validation
├── compile.bat                   # Batch script to compile all classes
├── run.bat                       # Double-click script to compile and run
└── README.md                     # Documentation
```

---

## 🚀 How to Run

### Method 1: Double-Click Launcher (Windows)
Double-click `run.bat` in the `PersonalExpenseTracker` folder. It will compile any changed files and launch the console application immediately.

### Method 2: Command Line (PowerShell or Command Prompt)

1. Open PowerShell or Command Prompt.
2. Navigate to the project root:
   ```powershell
   cd D:\Java\PersonalExpenseTracker
   ```
3. Compile the classes into the `bin` directory:
   ```powershell
   javac -d bin src\com\tracker\model\*.java src\com\tracker\validation\*.java src\com\tracker\storage\*.java src\com\tracker\service\*.java src\com\tracker\ui\*.java src\com\tracker\Main.java
   ```
4. Run the application:
   ```powershell
   java -cp bin com.tracker.Main
   ```

---

## 💡 Features Walkthrough

### 1. Add New Expense
- Prompts for amount (must be positive; strips currency symbols like `$` or `₹` automatically).
- Displays numbered category list (Food, Transportation, Utilities, Entertainment, Health, Shopping, Education, Personal Care, Other).
- Prompts for date in `YYYY-MM-DD` format (press **Enter** or type `today` to automatically use the current date).
- Prompts for an optional description or note.

### 2. View All Expenses (with Sorting)
- Tabular ASCII table with ID, Date, Category, Amount, and Description.
- Supports 4 sorting options:
  - Date (Newest first)
  - Date (Oldest first)
  - Amount (Highest first)
  - Amount (Lowest first)

### 3. Spending Summary & Analytics
- **Key Metrics**: Total spending, total transaction count, average amount spent per item.
- **Outliers**: Highlights the highest and lowest single expense.
- **Category Breakdown**: Total amount spent per category with percentage share and visual ASCII distribution bars (`[=====           ] 34.0%`).
- **Monthly Breakdown**: Spending grouped by month (`YYYY-MM`).

### 4. Filter & Search
- Filter records by a specific category.
- Filter records within a custom date range (e.g. `2026-11-01` to `2026-11-15`).

### 5. Edit & Delete
- Edit amount, category, date, or note for any expense by its ID.
- Delete records with confirmation prompt (`y/N`).

### 6. Persistent Local Storage
- Automatically saves changes to `data/expenses.csv`.
- Loads saved expenses automatically on startup.
- Safe shutdown hook ensures data is not lost if the console window is closed.
