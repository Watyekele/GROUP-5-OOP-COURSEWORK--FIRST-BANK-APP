package ug.co.firstbank.model;

import java.time.LocalDate;

/** Fixed Deposit account: locked term, highest interest. Min deposit UGX 1,000,000. */
public class FixedDepositAccount extends Account {

    public FixedDepositAccount(String firstName, String lastName, String nin, String email,
                                String phone, Branch branch, LocalDate dateOfBirth, double openingDeposit) {
        super(firstName, lastName, nin, email, phone, branch, dateOfBirth, openingDeposit);
    }

    @Override
    public double minimumDeposit() {
        return 1_000_000;
    }

    @Override
    public String accountTypeName() {
        return "Fixed Deposit";
    }
}
