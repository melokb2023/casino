import java.util.Random;
import java.util.Scanner;

public class casino {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Random rng = new Random();

        System.out.println("Hello, welcome to the mini casino world! Please enter your name, Manager: ");
        String name = scanner.nextLine();
        System.out.println("Hello, " + name + "Welcome to the Mini Casino!");
        System.out.println("Manager, please set up the environment parameters for the Agent. The game needs to be done fairly and perfectly.");

        // Getting the initial money as well as the total play time (with error retry)
        double initialMoney = 0.0;
        while (true) {
            System.out.println("Define the start-up money: ");
            if (scanner.hasNextDouble()) {
                initialMoney = scanner.nextDouble();
                if (initialMoney >= 1.0) break;
                System.out.println("[ERROR] Start-up money must be at least Php 1.00 to play! Please try again.");
            } else {
                System.out.println("[ERROR] Invalid numeric input! Please enter a valid decimal number.");
                scanner.next(); // Clear invalid token
            }
        }

        int totalplayTime = 0;
        while (true) {
            System.out.println("How much play time is allowed? Kindly define in seconds: ");
            if (scanner.hasNextInt()) {
                totalplayTime = scanner.nextInt();
                if (totalplayTime >= 10) break;
                System.out.println("[ERROR] Play time must be at least 10 seconds! Please try again.");
            } else {
                System.out.println("[ERROR] Invalid numeric input! Please enter a valid integer.");
                scanner.next(); // Clear invalid token
            }
        }

        // Starting the game today
        System.out.println("You have completed the setup! The agent starts with Php " + initialMoney + " dollars for " + totalplayTime + " seconds. ");

        // Machine 1 Probabilities (with error retry)
        double m1_p0 = 0, m1_p1 = 0, m1_p5 = 0, m1_p100 = 0;
        while (true) {
            System.out.println("Let's begin with the first machine!");
            System.out.println("Please enter the probability of winning between 0 and 1!Let's play fair!");

            m1_p0 = getValidProbability(scanner, "Set a chance rate of winning Php 0: ");
            m1_p1 = getValidProbability(scanner, "Set a chance rate of winning Php 1: ");
            m1_p5 = getValidProbability(scanner, "Set a chance rate of winning Php 5: ");
            m1_p100 = getValidProbability(scanner, "Set a chance rate of winning Php 100: ");

            double sum1 = m1_p0 + m1_p1 + m1_p5 + m1_p100;
            if (Math.abs(sum1 - 1.0) < 0.0001) break;
            System.out.printf("[ERROR] Probabilities must sum to 1.0 (100%%)! Current sum: %.4f. Please re-enter Machine 1 rates.%n%n", sum1);
        }

        // Machine 2 Probabilities (with error retry)
        double m2_p0 = 0, m2_p1 = 0, m2_p5 = 0, m2_p100 = 0;
        while (true) {
            System.out.println("\nNow lets move on to the second machine!");

            m2_p0 = getValidProbability(scanner, "Set a chance rate of winning Php 0: ");
            m2_p1 = getValidProbability(scanner, "Set a chance rate of winning Php 1: ");
            m2_p5 = getValidProbability(scanner, "Set a chance rate of winning Php 5: ");
            m2_p100 = getValidProbability(scanner, "Set a chance rate of winning Php 100: ");

            double sum2 = m2_p0 + m2_p1 + m2_p5 + m2_p100;
            if (Math.abs(sum2 - 1.0) < 0.0001) break;
            System.out.printf("[ERROR] Probabilities must sum to 1.0 (100%%)! Current sum: %.4f. Please re-enter Machine 2 rates.%n%n", sum2);
        }

        // --- AGENT DECISION LOGIC (Expected Value Calculation) ---
        double ev1 = (0 * m1_p0) + (1 * m1_p1) + (5 * m1_p5) + (100 * m1_p100);
        double ev2 = (0 * m2_p0) + (1 * m2_p1) + (5 * m2_p5) + (100 * m2_p100);

        System.out.println("\n================ AGENT ANALYSIS ================");
        System.out.printf("Machine 1 Expected Winning Gains: Php %.2f per play\n", ev1);
        System.out.printf("Machine 2 Expected Winning Gains: Php %.2f per play\n", ev2);

        // Reflex Decision: Agent picks the machine with higher expected payout
        int chosenMachine = (ev2 > ev1) ? 2 : 1;
        System.out.println("Strategy Selected: Agent decides to play Machine " + chosenMachine + " for maximum profit.");

        // --- SIMULATION PROCESS ---
        System.out.println("\n================ NARRATIVE LOG ================");
        int playCount = 0;
        double currentMoney = initialMoney;
        int timeRemaining = totalplayTime;

        while (currentMoney >= 1.0 && timeRemaining >= 10) {
            playCount++;
            currentMoney -= 1.0;   // Cost Php 1 per play
            timeRemaining -= 10;  // 10 seconds per play

            double roll = rng.nextDouble();
            int payoff = 0;

            if (chosenMachine == 1) {
                if (roll < m1_p0) payoff = 0;
                else if (roll < m1_p0 + m1_p1) payoff = 1;
                else if (roll < m1_p0 + m1_p1 + m1_p5) payoff = 5;
                else payoff = 100;
            } else {
                if (roll < m2_p0) payoff = 0;
                else if (roll < m2_p0 + m2_p1) payoff = 1;
                else if (roll < m2_p0 + m2_p1 + m2_p5) payoff = 5;
                else payoff = 100;
            }

            currentMoney += payoff;

            System.out.printf("[Play %02d | Time Left: %3ds] Agent plays Machine %d (Cost: Php 1) -> Won: Php %3d | Current Balance: Php %.2f\n",
                    playCount, timeRemaining, chosenMachine, payoff, currentMoney);
        }

        System.out.println("===============================================");
        System.out.println("SIMULATION RESULTS");
        System.out.printf("Manager          : %s\n", name);
        System.out.printf("Number of Total Plays      : %d\n", playCount);
        System.out.printf("Final Bankroll/ Current Money   : Php %.2f\n", currentMoney);
        System.out.println("Termination Cause: " + (currentMoney < 1.0 ? "Agent ran out of money." : "Agent ran out of time."));

        scanner.close();
    }

    /**
     * Helper method to validate individual probability entries while keeping your exact prompt text.
     */
    private static double getValidProbability(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            if (scanner.hasNextDouble()) {
                double prob = scanner.nextDouble();
                if (prob >= 0.0 && prob <= 1.0) {
                    return prob;
                }
                System.out.println("[ERROR] Rate must be between 0.0 and 1.0! Please try again.");
            } else {
                System.out.println("[ERROR] Invalid numeric input! Please enter a valid decimal.");
                scanner.next(); // Clear invalid token
            }
        }
    }
}