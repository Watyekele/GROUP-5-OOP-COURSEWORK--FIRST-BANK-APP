package ug.co.firstbank.model;

import java.time.LocalDate;

/**
 * Student account: applicant age must be 18-25. Min deposit UGX 10,000.
 * Overrides {@link #isAgeAcceptable(int)} to narrow the general 18-75
 * band down to the stricter 18-25 student band -- a clean demonstration
 * of polymorphism driving validation.
 */
public class StudentAccount extends Account {

    public StudentAccount(String firstName, String lastName, String nin, String email,
                           String phone, Branch branch, LocalDate dateOfBirth, double openingDeposit) {
        super(firstName, lastName, nin, email, phone, branch, dateOfBirth, openingDeposit);
    }

    @Override
    public double minimumDeposit() {
        return 10_000;
    }

    @Override
    public String accountTypeName() {
        return "Student";
    }

    @Override
    public boolean isAgeAcceptable(int age) {
        return age >= 18 && age <= 25;
    }
}
