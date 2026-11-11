package com.tracker.model;

import java.util.Collections;
import java.util.Map;

/**
 * Data Transfer Object / Statistics model representing spending analysis.
 * Practice Area: Classes and Objects, Collections (Maps, aggregation).
 */
public class SpendingSummary {
    private final double totalSpending;
    private final int totalCount;
    private final double averageSpending;
    private final Expense highestExpense;
    private final Expense lowestExpense;
    private final Map<Category, Double> spendingByCategory;
    private final Map<Category, Double> percentageByCategory;
    private final Map<String, Double> spendingByMonth;

    public SpendingSummary(double totalSpending,
                           int totalCount,
                           double averageSpending,
                           Expense highestExpense,
                           Expense lowestExpense,
                           Map<Category, Double> spendingByCategory,
                           Map<Category, Double> percentageByCategory,
                           Map<String, Double> spendingByMonth) {
        this.totalSpending = Math.round(totalSpending * 100.0) / 100.0;
        this.totalCount = totalCount;
        this.averageSpending = Math.round(averageSpending * 100.0) / 100.0;
        this.highestExpense = highestExpense;
        this.lowestExpense = lowestExpense;
        this.spendingByCategory = Collections.unmodifiableMap(spendingByCategory);
        this.percentageByCategory = Collections.unmodifiableMap(percentageByCategory);
        this.spendingByMonth = Collections.unmodifiableMap(spendingByMonth);
    }

    public double getTotalSpending() {
        return totalSpending;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public double getAverageSpending() {
        return averageSpending;
    }

    public Expense getHighestExpense() {
        return highestExpense;
    }

    public Expense getLowestExpense() {
        return lowestExpense;
    }

    public Map<Category, Double> getSpendingByCategory() {
        return spendingByCategory;
    }

    public Map<Category, Double> getPercentageByCategory() {
        return percentageByCategory;
    }

    public Map<String, Double> getSpendingByMonth() {
        return spendingByMonth;
    }
}
