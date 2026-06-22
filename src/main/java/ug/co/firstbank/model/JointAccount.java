package ug.co.firstbank.model;

import java.time.LocalDate;

/** Joint account: requires a second NIN. Min deposit UGX 100,000. */
public class JointAccount extends Account {

    private final String secondNin;

    public JointAccount(String firstName, String lastName, String nin, String email,
                         String phone, Branch branch, LocalDate dateOfBirth, double openingDeposit,
                         String secondNin) {
        super(firstName, lastName, nin, email, phone, branch, dateOfBirth, openingDeposit);
        this.secondNin = secondNin;
    }

    public String getSecondNin() {
        return secondNin;
    }

    @Override
    public double minimumDeposit() {
        return 100_000;
    }

    @Override
    public String accountTypeName() {
        return "Joint";
    }

    @Override
    public String toSummaryRecord() {
        return super.toSummaryRecord() + " | 2nd NIN " + secondNin;
    }
}
