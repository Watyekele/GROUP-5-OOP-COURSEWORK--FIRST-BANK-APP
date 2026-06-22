package ug.co.firstbank.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Abstract base class for every account type offered by First Bank Uganda.
 * <p>
 * Holds the state that is common to all accounts and declares the
 * polymorphic hook {@link #minimumDeposit()} that each concrete subclass
 * must implement. The form validates the client's opening deposit purely
 * through this abstraction -- it never needs to know which concrete
 * subtype it is holding.
 */
public abstract class Account {

    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE;

    protected String accountNumber;     // assigned only after a successful submit
    protected final String firstName;
    protected final String lastName;
    protected final String nin;
    protected final String email;
    protected final String phone;
    protected final Branch branch;
    protected final LocalDate dateOfBirth;
    protected final double openingDeposit;

    protected Account(String firstName, String lastName, String nin, String email,
                       String phone, Branch branch, LocalDate dateOfBirth, double openingDeposit) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.nin = nin;
        this.email = email;
        this.phone = phone;
        this.branch = branch;
        this.dateOfBirth = dateOfBirth;
        this.openingDeposit = openingDeposit;
    }

    /** The minimum opening deposit (UGX) required for this account subtype. */
    public abstract double minimumDeposit();

    /** Human readable name of the account type, e.g. "Savings". */
    public abstract String accountTypeName();

    /**
     * Hook that subclasses with extra age constraints (e.g. Student) can
     * override. By default any age in the general 18-75 band is acceptable;
     * the caller is still responsible for enforcing the 18-75 band itself.
     */
    public boolean isAgeAcceptable(int age) {
        return age >= 18 && age <= 75;
    }

    public boolean meetsMinimumDeposit() {
        return openingDeposit >= minimumDeposit();
    }

    public int ageInYears() {
        return java.time.Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getNin() {
        return nin;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public Branch getBranch() {
        return branch;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public double getOpeningDeposit() {
        return openingDeposit;
    }

    /**
     * Builds the formatted summary record shown in the read-only
     * "Account Summary" area and persisted to the database, e.g.:
     * ACC: KLA-2026-000142 | Okello Allan | Savings | Kampala | DOB 2004-02-29 |
     * +256772123456 | Deposit 50,000 | okello.allan@firstbank.co.ug
     */
    public String toSummaryRecord() {
        return String.format(
                "ACC: %s | %s %s | %s | %s | DOB %s | %s | Deposit %,.0f | %s",
                accountNumber,
                firstName, lastName,
                accountTypeName(),
                branch,
                dateOfBirth.format(ISO),
                phone,
                openingDeposit,
                email
        );
    }
}
