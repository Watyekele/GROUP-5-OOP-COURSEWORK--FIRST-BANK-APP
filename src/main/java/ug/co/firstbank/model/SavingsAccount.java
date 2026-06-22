package ug.co.firstbank.model;

import java.time.LocalDate;

/** Savings account: earns interest, no overdraft. Min deposit UGX 50,000. */
public class SavingsAccount extends Account {

    public SavingsAccount(String firstName, String lastName, String nin, String email,
                           String phone, Branch branch, LocalDate dateOfBirth, double openingDeposit) {
        super(firstName, lastName, nin, email, phone, branch, dateOfBirth, openingDeposit);
    }

    @Override
    public double minimumDeposit() {
        return 50_000;
    }

    @Override
    public String accountTypeName() {
        return "Savings";
    }
}
