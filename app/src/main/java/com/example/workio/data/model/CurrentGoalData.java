package com.example.workio.data.model;

public class CurrentGoalData {
    private SalaryGoal goal;
    private Progress progress;
    private Comparison comparison;

    public static class Progress {
        private int percentage;
        private boolean isAchieved;

        // Getters
        public int getPercentage() { return percentage; }
        public void setPercentage(int percentage) { this.percentage = percentage; }

        public boolean isAchieved() { return isAchieved; }
        public void setAchieved(boolean achieved) { isAchieved = achieved; }
    }

    public static class Comparison {
        private double previousMonthEarnings;
        private double difference;
        private double percentageChange;

        // Getters
        public double getPreviousMonthEarnings() { return previousMonthEarnings; }
        public void setPreviousMonthEarnings(double previousMonthEarnings) {
            this.previousMonthEarnings = previousMonthEarnings;
        }

        public double getDifference() { return difference; }
        public void setDifference(double difference) { this.difference = difference; }

        public double getPercentageChange() { return percentageChange; }
        public void setPercentageChange(double percentageChange) {
            this.percentageChange = percentageChange;
        }

        public boolean isImprovement() {
            return difference > 0;
        }

        public String getChangeText() {
            if (difference > 0) {
                return "+" + String.format("%.1f%%", percentageChange);
            } else if (difference < 0) {
                return String.format("%.1f%%", percentageChange);
            }
            return "0%";
        }
    }

    // Getters
    public SalaryGoal getGoal() { return goal; }
    public void setGoal(SalaryGoal goal) { this.goal = goal; }

    public Progress getProgress() { return progress; }
    public void setProgress(Progress progress) { this.progress = progress; }

    public Comparison getComparison() { return comparison; }
    public void setComparison(Comparison comparison) { this.comparison = comparison; }
}
