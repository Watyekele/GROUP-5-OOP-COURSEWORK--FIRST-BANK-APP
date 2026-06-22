package ug.co.firstbank.model;

import java.time.LocalDate;

/** Current account: overdraft allowed, no interest. Min deposit UGX 200,000. */
public class CurrentAccount extends Account {

    public CurrentAccount(String firstName, String lastName, String nin, String email,
                           String phone, Branch branch, LocalDate dateOfBirth, double openingDeposit) {
        super(firstName, lastName, nin, email, phone, branch, dateOfBirth, openingDeposit);
    }

    @Override
    public double minimumDeposit() {
        return 200_000;
    }

    @Override
    public String accountTypeName() {
        return "Current";
    }
}
