package com.tracker.model;

/**
 * Represents the category of an expense.
 * Demonstrates Java Enum with custom fields, methods, and lookup capabilities.
 */
public enum Category {
    FOOD("Food & Dining"),
    TRANSPORT("Transportation"),
    UTILITIES("Bills & Utilities"),
    ENTERTAINMENT("Entertainment & Leisure"),
    HEALTH("Healthcare & Medical"),
    SHOPPING("Shopping"),
    EDUCATION("Education"),
    PERSONAL("Personal Care"),
    OTHER("Miscellaneous / Other");

    private final String displayName;

    Category(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Finds a Category from a 1-based index (for menu selection).
     *
     * @param index 1-based choice index
     * @return Category or null if invalid
     */
    public static Category fromIndex(int index) {
        Category[] values = values();
        if (index >= 1 && index <= values.length) {
            return values[index - 1];
        }
        return null;
    }

    /**
     * Parses category from a case-insensitive string (enum name or display name).
     *
     * @param text input string
     * @return matched Category or OTHER as fallback
     */
    public static Category fromString(String text) {
        if (text == null || text.trim().isEmpty()) {
            return OTHER;
        }
        String clean = text.trim();
        for (Category cat : values()) {
            if (cat.name().equalsIgnoreCase(clean) || cat.displayName.equalsIgnoreCase(clean)) {
                return cat;
            }
        }
        return OTHER;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
