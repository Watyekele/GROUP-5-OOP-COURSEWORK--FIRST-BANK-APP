package ug.co.firstbank.util;

/**
 * Formats account numbers as BRANCHCODE-YYYY-xxxxxx. The actual
 * sequential counter is maintained transactionally in the database
 * (see DatabaseManager.nextSequenceFor) so it is safe across runs and
 * (within a single-writer Access file) across branches/years.
 */
public final class AccountNumberGenerator {

    private AccountNumberGenerator() {
    }

    public static String format(String branchCode, int year, int sequence) {
        return String.format("%s-%d-%06d", branchCode, year, sequence);
    }
}
