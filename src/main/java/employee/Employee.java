package employee;

import java.io.PrintStream;
import java.util.Locale;
import java.util.Scanner;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * A simple employee record (name, age, email, salary) that is captured from
 * the console and validated field by field.
 */
public class Employee {

    public static final int MIN_NAME_LENGTH = 2;
    public static final int MAX_NAME_LENGTH = 50;
    public static final int MIN_AGE = 18;
    public static final int MAX_AGE = 65;
    public static final double MAX_SALARY = 1_000_000.0;

    private static final Pattern NAME_PATTERN = Pattern.compile("[A-Za-z][A-Za-z' -]*");
    private static final Pattern AGE_PATTERN = Pattern.compile("\\d{1,3}");
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*\\.[A-Za-z]{2,}");
    private static final Pattern SALARY_PATTERN = Pattern.compile("\\d+(\\.\\d{1,2})?");

    private final String name;
    private final int age;
    private final String email;
    private final double salary;

    public Employee(String name, int age, String email, double salary) {
        this.name = validateName(name);
        this.age = validateAge(age);
        this.email = validateEmail(email);
        this.salary = validateSalary(salary);
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getEmail() {
        return email;
    }

    public double getSalary() {
        return salary;
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "Employee{name='%s', age=%d, email='%s', salary=%.2f}",
                name, age, email, salary);
    }

    // ---------------------------------------------------------------- validation

    /** Required, 2-50 characters once trimmed, letters/spaces/hyphens/apostrophes, starts with a letter. */
    public static String validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name is required.");
        }
        String trimmed = name.trim();
        if (trimmed.length() < MIN_NAME_LENGTH || trimmed.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException(
                    "Name must be " + MIN_NAME_LENGTH + "-" + MAX_NAME_LENGTH + " characters long.");
        }
        if (!NAME_PATTERN.matcher(trimmed).matches()) {
            throw new IllegalArgumentException(
                    "Name may only contain letters, spaces, hyphens and apostrophes, and must start with a letter.");
        }
        return trimmed;
    }

    /** Whole number between 18 and 65 (inclusive). */
    public static int validateAge(int age) {
        if (age < MIN_AGE || age > MAX_AGE) {
            throw new IllegalArgumentException(
                    "Age must be a whole number between " + MIN_AGE + " and " + MAX_AGE + ".");
        }
        return age;
    }

    /** Parses what the user typed, then applies {@link #validateAge(int)}. */
    public static int parseAge(String raw) {
        if (raw == null || !AGE_PATTERN.matcher(raw.trim()).matches()) {
            throw new IllegalArgumentException(
                    "Age must be a whole number between " + MIN_AGE + " and " + MAX_AGE + ".");
        }
        return validateAge(Integer.parseInt(raw.trim()));
    }

    /** Required and in the form name@domain.tld. */
    public static String validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email is required.");
        }
        String trimmed = email.trim();
        if (!EMAIL_PATTERN.matcher(trimmed).matches()) {
            throw new IllegalArgumentException("Email format is invalid (expected name@domain.com).");
        }
        return trimmed;
    }

    /** Greater than 0 and at most 1,000,000. */
    public static double validateSalary(double salary) {
        if (Double.isNaN(salary) || salary <= 0 || salary > MAX_SALARY) {
            throw new IllegalArgumentException(
                    "Salary must be greater than 0 and at most " + (long) MAX_SALARY + ".");
        }
        return salary;
    }

    /** Parses what the user typed (digits, optional 1-2 decimals), then applies {@link #validateSalary(double)}. */
    public static double parseSalary(String raw) {
        if (raw == null || !SALARY_PATTERN.matcher(raw.trim()).matches()) {
            throw new IllegalArgumentException("Salary must be a number with at most 2 decimal places.");
        }
        return validateSalary(Double.parseDouble(raw.trim()));
    }

    // ---------------------------------------------------------------- console input

    /** Asks for each field in turn, re-asking until the value is valid. */
    public static Employee readFromUser(Scanner in, PrintStream out) {
        String name = prompt(in, out, "Full name", Employee::validateName);
        int age = prompt(in, out, "Age (" + MIN_AGE + "-" + MAX_AGE + ")", Employee::parseAge);
        String email = prompt(in, out, "Email", Employee::validateEmail);
        double salary = prompt(in, out, "Monthly salary", Employee::parseSalary);
        return new Employee(name, age, email, salary);
    }

    private static <T> T prompt(Scanner in, PrintStream out, String label, Function<String, T> parser) {
        while (true) {
            out.print(label + ": ");
            String line = in.nextLine();
            try {
                return parser.apply(line);
            } catch (IllegalArgumentException e) {
                out.println("  Invalid input - " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.println("=== Employee registration ===");
        Employee employee = readFromUser(in, System.out);
        System.out.println();
        System.out.println("Employee captured successfully:");
        System.out.println(employee);
    }
}
