# First Bank Uganda — New Account Opening Application

JavaFX desktop application for Object Oriented Programming coursework
(1201ST/1204FST/1301ST). Implements a new bank account opening form with
full input validation, an abstract `Account` class hierarchy with
polymorphic subtypes, and persistence to a real Microsoft Access
(`.accdb`) database file.

## 1. What's inside

```
firstbank-account-app/
├── pom.xml
├── README.md
├── data/                         <- firstbank.accdb is created here on first run
└── src/main/java/ug/co/firstbank/
    ├── App.java                  <- JavaFX UI (view + controller)
    ├── model/
    │   ├── Account.java          <- abstract base class
    │   ├── AccountType.java      <- enum + factory for the 5 subtypes
    │   ├── Branch.java           <- enum: Kampala, Gulu, Mbarara, Jinja, Mbale
    │   ├── SavingsAccount.java
    │   ├── CurrentAccount.java
    │   ├── FixedDepositAccount.java
    │   ├── StudentAccount.java
    │   └── JointAccount.java
    ├── validation/
    │   ├── Validator.java        <- all field validation rules
    │   └── ValidationResult.java <- collects field errors for inline + dialog display
    ├── db/
    │   └── DatabaseManager.java  <- creates/opens the .accdb, saves records
    └── util/
        └── AccountNumberGenerator.java
```

## 2. Why UCanAccess instead of the JDBC-ODBC bridge

The coursework asks for an **MS Access database file**. The classic way to
talk to Access from Java was the JDBC-ODBC bridge, but that bridge was
**removed from Java entirely in Java 8** and only ever worked on Windows
with a matching-bitness ODBC driver installed.

This project instead uses **UCanAccess**, a pure-Java JDBC driver (built
on HSQLDB + Jackcess) that reads and writes genuine `.accdb` files
directly — no ODBC, no Windows requirement, no MS Access installation
needed. It is added as a normal Maven dependency in `pom.xml`. The very
first time the app runs, it creates `data/firstbank.accdb` itself, with
two tables:

- **Accounts** — one row per successfully opened account
- **BranchYearCounters** — the per-branch, per-year sequence counter used
  to generate account numbers like `KLA-2026-000142`

You can open the resulting `.accdb` file directly in Microsoft Access on
Windows afterwards if you want to inspect it visually — UCanAccess writes
a standard Access 2010-format file.

## 3. Prerequisites

- **JDK 17 or newer** (the project was built/tested against JDK 21)
- **Apache Maven 3.6+**
- Internet access the first time you build (Maven needs to download the
  JavaFX and UCanAccess jars from Maven Central)

Check your versions:

```bash
java -version
mvn -version
```

## 4. Setup & compile

```bash
cd firstbank-account-app
mvn clean compile
```

This downloads JavaFX 21 and UCanAccess 5.0.1 and compiles all source
files. The class hierarchy and validators were also verified with a
standalone smoke test covering leap-year day counts, polymorphic
`minimumDeposit()`/age rules, and every validation rule — all 18 checks
pass.

## 5. Run the application

```bash
mvn javafx:run
```

A window titled **"First Bank Uganda - Account Opening"** opens. On first
run it will create the `data/` folder and `firstbank.accdb` inside it.

## 6. Build a standalone runnable jar (optional)

```bash
mvn clean package
java -jar target/firstbank-account-app-1.0.0.jar
```

## 7. Using the form

1. Fill in personal details, NIN, email (x2), phone, PIN (x2).
2. Pick Date of Birth — Year, Month, Day. The Day list automatically
   limits itself to the correct number of days for the chosen month/year
   (28/29 for February depending on leap year, 30 or 31 otherwise).
3. Pick **Account Type**. If you pick **Joint**, the "Second NIN" field
   becomes enabled and is required.
4. Pick **Branch**.
5. Enter the **Opening Deposit**. The minimum required amount depends on
   the Account Type selected (see table below) — this is enforced
   polymorphically: the form builds the matching `Account` subclass and
   calls its overridden `minimumDeposit()`.

| Account Type  | Minimum Deposit (UGX) | Extra rule          |
| ------------- | --------------------- | ------------------- |
| Savings       | 50,000                | —                   |
| Current       | 200,000               | —                   |
| Fixed Deposit | 1,000,000             | —                   |
| Student       | 10,000                | Age must be 18–25   |
| Joint         | 100,000               | Second NIN required |

6. Click **Submit**.
   - If anything is invalid: each offending field shows a small red
     message next to it, and a dialog box summarises every problem.
   - If everything is valid: an account number is generated in the
     format `BRANCHCODE-YYYY-xxxxxx` (sequential per branch per year),
     the record is saved into `firstbank.accdb`, and the formatted
     summary is shown in the read-only "Account Summary is Below:" box.
7. Click **Reset** at any time to clear the form.

## 8. Troubleshooting

- **"Could not open the Access database"** on startup — make sure the
  app has write permission to create a `data/` folder next to where you
  run it from.
- **Maven can't download dependencies** — you need internet access on
  first build; afterwards Maven caches everything in `~/.m2`.
- **JavaFX runtime components missing** when running the packaged jar
  directly with `java -jar` on some setups — use `mvn javafx:run`
  instead, or run with explicit module path flags pointing at a
  downloaded JavaFX SDK.

## 9. Possible extensions (not required by the brief)

- Hash the PIN before storing it instead of relying on UI-only entry.
- Add an "export to CSV/PDF" button for bank-staff reporting.
- Add a login screen for branch staff before the form is accessible.
