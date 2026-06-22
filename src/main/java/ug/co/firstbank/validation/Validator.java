package ug.co.firstbank.validation;

import ug.co.firstbank.model.Account;

import java.time.LocalDate;
import java.util.regex.Pattern;

/**
 * Stateless validation rules. Every method returns an error message
 * String when the value is invalid, or null when it passes -- callers
 * feed the non-null results into a {@link ValidationResult}.
 */
public final class Validator {

    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z]{2,30}$");
    private static final Pattern NIN_PATTERN = Pattern.compile("^[A-Z0-9]{14}$");
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+256\\d{9}$");
    private static final Pattern PIN_PATTERN = Pattern.compile("^\\d{4,6}$");
    private static final Pattern ALL_SAME_DIGIT = Pattern.compile("^(\\d)\\1*$");

    private Validator() {
    }

    public static String validateName(String label, String value) {
        if (value == null) return label + " is required.";
        String trimmed = value.trim();
        if (trimmed.isEmpty()) return label + " is required.";
        if (!NAME_PATTERN.matcher(trimmed).matches()) {
            return label + " must be letters only, 2-30 characters.";
        }
        return null;
    }

    public static String validateNin(String label, String value) {
        if (value == null) return label + " is required.";
        String trimmed = value.trim();
        if (trimmed.isEmpty()) return label + " is required.";
        if (!NIN_PATTERN.matcher(trimmed).matches()) {
            return label + " must be exactly 14 UPPERCASE alphanumeric characters.";
        }
        return null;
    }

    public static String validateEmailFormat(String label, String value) {
        if (value == null) return label + " is required.";
        String trimmed = value.trim();
        if (trimmed.isEmpty()) return label + " is required.";
        if (!EMAIL_PATTERN.matcher(trimmed).matches()) {
            return label + " is not a valid email address.";
        }
        return null;
    }

    public static String validateEmailsMatch(String email, String confirmEmail) {
        if (email != null && confirmEmail != null && !email.trim().equalsIgnoreCase(confirmEmail.trim())) {
            return "Email and Confirm Email must match.";
        }
        return null;
    }

    public static String validatePhone(String value) {
        if (value == null) return "Phone Number is required.";
        String trimmed = value.trim();
        if (trimmed.isEmpty()) return "Phone Number is required.";
        if (!PHONE_PATTERN.matcher(trimmed).matches()) {
            return "Phone Number must be in the format +256XXXXXXXXX (9 digits after +256).";
        }
        return null;
    }

    public static String validatePinFormat(String label, String value) {
        if (value == null) return label + " is required.";
        if (value.isEmpty()) return label + " is required.";
        if (!PIN_PATTERN.matcher(value).matches()) {
            return label + " must be numeric, 4-6 digits.";
        }
        if (ALL_SAME_DIGIT.matcher(value).matches()) {
            return label + " must not be all-identical digits (e.g. 0000).";
        }
        return null;
    }

    public static String validatePinsMatch(String pin, String confirmPin) {
        if (pin != null && confirmPin != null && !pin.equals(confirmPin)) {
            return "PIN and Confirm PIN must match.";
        }
        return null;
    }

    /** Validates derived age sits within 18-75, and within the account's own band (e.g. Student 18-25). */
    public static String validateAge(LocalDate dob, Account provisionalAccountForAgeRule) {
        if (dob == null) return "Date of Birth is required.";
        if (dob.isAfter(LocalDate.now())) return "Date of Birth cannot be in the future.";
        int age = java.time.Period.between(dob, LocalDate.now()).getYears();
        if (age < 18 || age > 75) {
            return "Age must be between 18 and 75 (computed age: " + age + ").";
        }
        if (provisionalAccountForAgeRule != null && !provisionalAccountForAgeRule.isAgeAcceptable(age)) {
            return "Student accounts require applicant age 18-25 (computed age: " + age + ").";
        }
        return null;
    }

    public static String validateDeposit(String rawValue, Account provisionalAccount) {
        if (rawValue == null || rawValue.trim().isEmpty()) return "Opening Deposit is required.";
        double deposit;
        try {
            deposit = Double.parseDouble(rawValue.trim());
        } catch (NumberFormatException ex) {
            return "Opening Deposit must be numeric.";
        }
        if (deposit < 0) return "Opening Deposit cannot be negative.";
        if (provisionalAccount != null && deposit < provisionalAccount.minimumDeposit()) {
            return String.format("Opening Deposit must be at least UGX %,.0f for a %s account.",
                    provisionalAccount.minimumDeposit(), provisionalAccount.accountTypeName());
        }
        return null;
    }

    public static String validateSecondNin(boolean isJoint, String value) {
        if (!isJoint) return null;
        return validateNin("Second NIN", value);
    }

    /** True if {@code year} is a leap year (handles century rule correctly). */
    public static boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }

    /** Days in the given month/year, accounting for leap years. */
    public static int daysInMonth(int month, int year) {
        switch (month) {
            case 1: case 3: case 5: case 7: case 8: case 10: case 12:
                return 31;
            case 4: case 6: case 9: case 11:
                return 30;
            case 2:
                return isLeapYear(year) ? 29 : 28;
            default:
                throw new IllegalArgumentException("Invalid month: " + month);
        }
    }
}
