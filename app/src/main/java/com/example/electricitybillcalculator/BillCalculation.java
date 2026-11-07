package com.example.electricitybillcalculator;

public class BillCalculation {
    private static final double rate1 = 0.218; // 1   - 200 kWh
    private static final double rate2 = 0.334; // 201 - 300 kWh
    private static final double rate3 = 0.516; // 301 - 600 kWh
    private static final double rate4 = 0.546; // 601 - 1000 kWh

    // Calculate total charge before rebate
    public static double calculateTotal(int units) {
        int b1 = Math.min(units, 200);
        int b2 = Math.min(Math.max(units - 200, 0), 100);
        int b3 = Math.min(Math.max(units - 300, 0), 300);
        int b4 = Math.max(units - 600, 0);

        return b1 * rate1 + b2 * rate2 + b3 * rate3 + b4 * rate4;
    }

    // Calculate final charge after rebate
    public static double calculateFinal(double total, double rebatePercent) {
        return total - (total * rebatePercent / 100.0);
    }

    public static void main(String[] args) {
        int[] units = {150, 250, 467, 551, 115, 1000};
        for (int u : units) {
            double t = calculateTotal(u);
            System.out.printf("%4d kWh -> total RM %.3f | final @5%% RM %.2f%n",
                    u, t, calculateFinal(t, 5));
        }
    }

}

