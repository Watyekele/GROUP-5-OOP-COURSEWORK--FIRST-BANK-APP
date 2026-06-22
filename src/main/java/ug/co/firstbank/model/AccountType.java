package ug.co.firstbank.model;

import java.time.LocalDate;

/**
 * Drives the Account Type ComboBox and acts as a small factory that
 * builds the correct {@link Account} subclass for whichever type the
 * client selected. This is the single point where the UI layer is
 * decoupled from the concrete Account subclasses -- everywhere else
 * the code only ever talks to the abstract {@link Account} type.
 */
public enum AccountType {
    SAVINGS("Savings"),
    CURRENT("Current"),
    FIXED_DEPOSIT("Fixed Deposit"),
    STUDENT("Student"),
    JOINT("Joint");

    private final String displayName;

    AccountType(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }

    /**
     * Builds a new concrete Account instance of this type.
     * secondNin may be null/ignored for every type except JOINT.
     */
    public Account create(String firstName, String lastName, String nin, String email,
                           String phone, Branch branch, LocalDate dob, double deposit, String secondNin) {
        switch (this) {
            case SAVINGS:
                return new SavingsAccount(firstName, lastName, nin, email, phone, branch, dob, deposit);
            case CURRENT:
                return new CurrentAccount(firstName, lastName, nin, email, phone, branch, dob, deposit);
            case FIXED_DEPOSIT:
                return new FixedDepositAccount(firstName, lastName, nin, email, phone, branch, dob, deposit);
            case STUDENT:
                return new StudentAccount(firstName, lastName, nin, email, phone, branch, dob, deposit);
            case JOINT:
                return new JointAccount(firstName, lastName, nin, email, phone, branch, dob, deposit, secondNin);
            default:
                throw new IllegalStateException("Unhandled account type: " + this);
        }
    }
}
