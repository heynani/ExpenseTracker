import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class ExpenseTracker {

    // ================= GLOBAL VARIABLES =================
    static Scanner scanner = new Scanner(System.in);
    static String currentUser = "";

    // ================= MAIN METHOD =================
    public static void main(String[] args) {

        System.out.println("======================================");
        System.out.println("        WELCOME TO EXPENSE TRACKER    ");
        System.out.println("======================================\n");

        if (login()) {
            System.out.println("\nLogin Successful!");
            menu();
        } else {
            System.out.println("\nInvalid Credentials. Program Terminated.");
        }
    }

    // ================= LOGIN SYSTEM =================
    public static boolean login() {

        System.out.print("Enter Username: ");
        String username = scanner.nextLine();

        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        try {
            BufferedReader br = new BufferedReader(new FileReader("users.txt"));
            String line;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");

                if (parts[0].trim().equals(username.trim()) &&
                    parts[1].trim().equals(password.trim())) {

                    currentUser = username;
                    br.close();

                    // Create user expense file if not exists
                    File file = new File("data/" + currentUser + ".txt");
                    if (!file.exists()) {
                        file.createNewFile();
                    }

                    return true;
                }
            }

            br.close();

        } catch (Exception e) {
            System.out.println("Error reading users file.");
        }

        return false;
    }

    // ================= MENU =================
    public static void menu() {

        while (true) {

            System.out.println("\n--------------------------------------");
            System.out.println("1. Add Expense");
            System.out.println("2. View Expenses");
            System.out.println("3. Calculate Total Spending");
            System.out.println("4. Delete Expense");
            System.out.println("5. Analytics");
            System.out.println("6. Exit");
            System.out.println("--------------------------------------");

            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {

                case 1:
                    addExpense();
                    break;

                case 2:
                    viewExpenses();
                    break;

                case 3:
                    calculateTotal();
                    break;

                case 4:
                    deleteExpense();
                    break;

                case 5:
                    analytics();
                    break;

                case 6:
                    System.out.println("\nThank you for using Expense Tracker!");
                    return;

                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    // ================= ADD EXPENSE =================
    public static void addExpense() {

        try {
            System.out.print("Enter Category: ");
            String category = scanner.nextLine();

            System.out.print("Enter Amount: ");
            double amount = scanner.nextDouble();
            scanner.nextLine();

            String date = LocalDate.now().toString();

            FileWriter fw = new FileWriter("data/" + currentUser + ".txt", true);
            fw.write(date + " | " + category + " | " + amount + "\n");
            fw.close();

            System.out.println("\nExpense Added Successfully!");

        } catch (Exception e) {
            System.out.println("Error adding expense.");
        }
    }

    // ================= VIEW EXPENSES =================
    public static void viewExpenses() {

        try {
            BufferedReader br = new BufferedReader(new FileReader("data/" + currentUser + ".txt"));
            String line;
            int index = 1;

            System.out.println("\n========== YOUR EXPENSES ==========");

            while ((line = br.readLine()) != null) {
                System.out.println(index + ". " + line);
                index++;
            }

            br.close();

            if (index == 1) {
                System.out.println("No expenses recorded yet.");
            }

        } catch (Exception e) {
            System.out.println("Error reading expenses.");
        }
    }

    // ================= TOTAL SPENDING =================
    public static void calculateTotal() {

        double total = 0;

        try {
            BufferedReader br = new BufferedReader(new FileReader("data/" + currentUser + ".txt"));
            String line;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                total += Double.parseDouble(parts[2].trim());
            }

            br.close();

            System.out.println("\nTotal Spending: " + total);

        } catch (Exception e) {
            System.out.println("Error calculating total.");
        }
    }

    // ================= DELETE EXPENSE =================
    public static void deleteExpense() {

        try {
            File file = new File("data/" + currentUser + ".txt");
            BufferedReader br = new BufferedReader(new FileReader(file));
            ArrayList<String> expenses = new ArrayList<>();
            String line;

            while ((line = br.readLine()) != null) {
                expenses.add(line);
            }

            br.close();

            if (expenses.isEmpty()) {
                System.out.println("No expenses to delete.");
                return;
            }

            viewExpenses();

            System.out.print("\nEnter expense number to delete: ");
            int index = scanner.nextInt();
            scanner.nextLine();

            if (index < 1 || index > expenses.size()) {
                System.out.println("Invalid number.");
                return;
            }

            expenses.remove(index - 1);

            FileWriter fw = new FileWriter(file);

            for (String exp : expenses) {
                fw.write(exp + "\n");
            }

            fw.close();

            System.out.println("Expense deleted successfully!");

        } catch (Exception e) {
            System.out.println("Error deleting expense.");
        }
    }

    // ================= ANALYTICS =================
    public static void analytics() {

        double weeklyTotal = 0;
        double monthlyTotal = 0;
        double yearlyTotal = 0;

        LocalDate now = LocalDate.now();

        try {
            BufferedReader br = new BufferedReader(new FileReader("data/" + currentUser + ".txt"));
            String line;

            while ((line = br.readLine()) != null) {

                String[] parts = line.split("\\|");

                LocalDate expenseDate = LocalDate.parse(parts[0].trim());
                double amount = Double.parseDouble(parts[2].trim());

                if (expenseDate.isAfter(now.minusDays(7)))
                    weeklyTotal += amount;

                if (expenseDate.getMonth() == now.getMonth() &&
                    expenseDate.getYear() == now.getYear())
                    monthlyTotal += amount;

                if (expenseDate.getYear() == now.getYear())
                    yearlyTotal += amount;
            }

            br.close();

            double weeklyAvg = weeklyTotal / 7;
            double monthlyAvg = monthlyTotal / now.getDayOfMonth();
            double yearlyAvg = yearlyTotal / now.getDayOfYear();

            System.out.println("\n========== ANALYTICS ==========");
            System.out.println("Weekly Average  : " + weeklyAvg);
            System.out.println("Monthly Average : " + monthlyAvg);
            System.out.println("Yearly Average  : " + yearlyAvg);

            if (weeklyAvg < monthlyAvg && monthlyAvg < yearlyAvg) {
                System.out.println("\nGreat job! Your spending is decreasing over time!");
            } else if (weeklyAvg > monthlyAvg) {
                System.out.println("\nWarning! Your recent spending is increasing.");
            } else {
                System.out.println("\nYour spending is stable.");
            }

        } catch (Exception e) {
            System.out.println("Error generating analytics.");
        }
    }
}