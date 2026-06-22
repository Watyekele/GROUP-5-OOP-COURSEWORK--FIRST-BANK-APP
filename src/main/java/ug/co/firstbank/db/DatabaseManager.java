package ug.co.firstbank.db;

import ug.co.firstbank.model.Account;
import ug.co.firstbank.model.Branch;
import ug.co.firstbank.util.AccountNumberGenerator;

import java.io.File;
import java.sql.*;
import java.time.LocalDate;
import java.time.Year;

/**
 * Handles all persistence against an MS Access (.accdb) database file
 * using the UCanAccess JDBC driver. UCanAccess is pure Java (built on
 * HSQLDB + Jackcess) so it works on Windows, macOS and Linux alike and
 * needs no ODBC configuration -- unlike the legacy JDBC-ODBC bridge,
 * which Java removed entirely back in Java 8.
 * <p>
 * If the .accdb file does not yet exist, UCanAccess creates a brand new
 * empty Access 2010-format database the first time we connect to it.
 */
public class DatabaseManager implements AutoCloseable {

    private final Connection connection;

    public DatabaseManager(String accdbPath) throws SQLException {
        boolean isNew = !new File(accdbPath).exists();
        String url = "jdbc:ucanaccess://" + accdbPath + ";newdatabaseversion=V2010";
        this.connection = DriverManager.getConnection(url);
        if (isNew) {
            createSchema();
        } else {
            ensureSchema();
        }
    }

    private void createSchema() throws SQLException {
        try (Statement st = connection.createStatement()) {
            st.execute(
                "CREATE TABLE Accounts (" +
                "  AccountNumber VARCHAR(20) PRIMARY KEY, " +
                "  FirstName VARCHAR(30), " +
                "  LastName VARCHAR(30), " +
                "  Nin VARCHAR(14), " +
                "  SecondNin VARCHAR(14), " +
                "  Email VARCHAR(100), " +
                "  Phone VARCHAR(20), " +
                "  AccountType VARCHAR(20), " +
                "  Branch VARCHAR(20), " +
                "  DateOfBirth VARCHAR(10), " +
                "  OpeningDeposit DOUBLE, " +
                "  CreatedAt VARCHAR(30)" +
                ")"
            );
            st.execute(
                "CREATE TABLE BranchYearCounters (" +
                "  BranchCode VARCHAR(5), " +
                "  Yr INTEGER, " +
                "  LastSeq INTEGER, " +
                "  PRIMARY KEY (BranchCode, Yr)" +
                ")"
            );
        }
    }

    /** No-op safety net for databases created by an earlier run of this app. */
    private void ensureSchema() {
        // Tables are created on first run; nothing to migrate for this coursework.
    }

    /**
     * Atomically reserves and returns the next sequence number for the
     * given branch+year, creating the counter row if needed.
     */
    public synchronized int nextSequenceFor(String branchCode, int year) throws SQLException {
        connection.setAutoCommit(false);
        try {
            int next;
            try (PreparedStatement sel = connection.prepareStatement(
                    "SELECT LastSeq FROM BranchYearCounters WHERE BranchCode = ? AND Yr = ?")) {
                sel.setString(1, branchCode);
                sel.setInt(2, year);
                try (ResultSet rs = sel.executeQuery()) {
                    if (rs.next()) {
                        next = rs.getInt(1) + 1;
                        try (PreparedStatement upd = connection.prepareStatement(
                                "UPDATE BranchYearCounters SET LastSeq = ? WHERE BranchCode = ? AND Yr = ?")) {
                            upd.setInt(1, next);
                            upd.setString(2, branchCode);
                            upd.setInt(3, year);
                            upd.executeUpdate();
                        }
                    } else {
                        next = 1;
                        try (PreparedStatement ins = connection.prepareStatement(
                                "INSERT INTO BranchYearCounters (BranchCode, Yr, LastSeq) VALUES (?, ?, ?)")) {
                            ins.setString(1, branchCode);
                            ins.setInt(2, year);
                            ins.setInt(3, next);
                            ins.executeUpdate();
                        }
                    }
                }
            }
            connection.commit();
            return next;
        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    /**
     * Generates the next account number for the account's branch
     * (using the current year) and stamps it onto the account.
     */
    public String generateAndAssignAccountNumber(Account account) throws SQLException {
        Branch branch = account.getBranch();
        int year = Year.now().getValue();
        int seq = nextSequenceFor(branch.getCode(), year);
        String accNo = AccountNumberGenerator.format(branch.getCode(), year, seq);
        account.setAccountNumber(accNo);
        return accNo;
    }

    /** Persists a fully-validated, numbered account as one row in Accounts. */
    public void saveAccount(Account account, String secondNin) throws SQLException {
        String sql = "INSERT INTO Accounts (AccountNumber, FirstName, LastName, Nin, SecondNin, " +
                "Email, Phone, AccountType, Branch, DateOfBirth, OpeningDeposit, CreatedAt) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, account.getAccountNumber());
            ps.setString(2, account.getFirstName());
            ps.setString(3, account.getLastName());
            ps.setString(4, account.getNin());
            ps.setString(5, secondNin);
            ps.setString(6, account.getEmail());
            ps.setString(7, account.getPhone());
            ps.setString(8, account.accountTypeName());
            ps.setString(9, account.getBranch().toString());
            ps.setString(10, account.getDateOfBirth().toString());
            ps.setDouble(11, account.getOpeningDeposit());
            ps.setString(12, LocalDate.now().toString());
            ps.executeUpdate();
        }
    }

    @Override
    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
