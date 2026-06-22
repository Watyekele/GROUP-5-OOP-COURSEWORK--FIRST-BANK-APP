package ug.co.firstbank;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import ug.co.firstbank.db.DatabaseManager;
import ug.co.firstbank.model.*;
import ug.co.firstbank.validation.ValidationResult;
import ug.co.firstbank.validation.Validator;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * First Bank Uganda -- New Bank Account Opening Application.
 * Single-class JavaFX UI for the OOP coursework. Field validation lives
 * in {@link Validator}; persistence lives in {@link DatabaseManager};
 * the Account class hierarchy lives in the model package -- this class
 * is purely the View/Controller gluing them together.
 */
public class App extends Application {

    private static final String DB_PATH = "data/firstbank.accdb";

    // ----- form controls -----
    private TextField firstNameField, lastNameField, ninField, emailField,
            confirmEmailField, phoneField, secondNinField, depositField;
    private PasswordField pinField, confirmPinField;
    private ComboBox<Integer> yearCombo, dayCombo;
    private ComboBox<Month> monthCombo;
    private ComboBox<AccountType> accountTypeCombo;
    private ComboBox<Branch> branchCombo;
    private TextArea summaryArea;

    private final Map<String, Label> errorLabels = new LinkedHashMap<>();
    private DatabaseManager db;

    @Override
    public void start(Stage stage) {
        try {
            db = new DatabaseManager(DB_PATH);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Database Error",
                    "Could not open the Access database at " + DB_PATH + ":\n" + e.getMessage());
        }

        GridPane form = buildForm();
        ScrollPane scroll = new ScrollPane(form);
        scroll.setFitToWidth(true);

        Label title = new Label("First Bank Uganda \u2013 New Account Opening");
        title.setFont(Font.font("System", FontWeight.BOLD, 20));

        VBox root = new VBox(12, title, scroll);
        root.setPadding(new Insets(16));

        Scene scene = new Scene(root, 760, 720);
        stage.setTitle("First Bank Uganda - Account Opening");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        if (db != null) {
            try {
                db.close();
            } catch (Exception ignored) {
            }
        }
    }

    // ------------------------------------------------------------------
    // UI construction
    // ------------------------------------------------------------------

    private GridPane buildForm() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(6);
        grid.setPadding(new Insets(10));
        ColumnConstraints c0 = new ColumnConstraints(150);
        ColumnConstraints c1 = new ColumnConstraints(260);
        ColumnConstraints c2 = new ColumnConstraints(280);
        grid.getColumnConstraints().addAll(c0, c1, c2);

        int row = 0;

        firstNameField = new TextField();
        row = addRow(grid, row, "First Name", firstNameField);

        lastNameField = new TextField();
        row = addRow(grid, row, "Last Name", lastNameField);

        ninField = new TextField();
        row = addRow(grid, row, "National ID (NIN)", ninField);

        emailField = new TextField();
        row = addRow(grid, row, "Email", emailField);

        confirmEmailField = new TextField();
        row = addRow(grid, row, "Confirm Email", confirmEmailField);

        phoneField = new TextField();
        phoneField.setPromptText("+256XXXXXXXXX");
        row = addRow(grid, row, "Phone Number", phoneField);

        pinField = new PasswordField();
        row = addRow(grid, row, "PIN", pinField);

        confirmPinField = new PasswordField();
        row = addRow(grid, row, "Confirm PIN", confirmPinField);

        // ---- Date of birth: three linked combo boxes ----
        HBox dobBox = buildDobControls();
        row = addRow(grid, row, "Date of Birth", dobBox);

        accountTypeCombo = new ComboBox<>();
        accountTypeCombo.getItems().addAll(AccountType.values());
        accountTypeCombo.setPromptText("Select account type");
        accountTypeCombo.valueProperty().addListener((obs, old, val) -> {
            boolean joint = val == AccountType.JOINT;
            secondNinField.setDisable(!joint);
            if (!joint) secondNinField.clear();
        });
        row = addRow(grid, row, "Account Type", accountTypeCombo);

        secondNinField = new TextField();
        secondNinField.setPromptText("Required only for Joint accounts");
        secondNinField.setDisable(true);
        row = addRow(grid, row, "Second NIN (Joint only)", secondNinField);

        branchCombo = new ComboBox<>();
        branchCombo.getItems().addAll(Branch.values());
        branchCombo.setPromptText("Select branch");
        row = addRow(grid, row, "Branch", branchCombo);

        depositField = new TextField();
        depositField.setPromptText("e.g. 50000");
        row = addRow(grid, row, "Opening Deposit (UGX)", depositField);

        // ---- buttons ----
        Button submitBtn = new Button("Submit");
        submitBtn.setDefaultButton(true);
        submitBtn.setOnAction(e -> onSubmit());

        Button resetBtn = new Button("Reset");
        resetBtn.setOnAction(e -> onReset());

        HBox buttonBar = new HBox(10, submitBtn, resetBtn);
        buttonBar.setAlignment(Pos.CENTER_LEFT);
        grid.add(buttonBar, 1, row);
        row++;

        Label summaryHeading = new Label("Account Summary is Below:");
        summaryHeading.setFont(Font.font("System", FontWeight.BOLD, 13));
        grid.add(summaryHeading, 0, row, 3, 1);
        row++;

        summaryArea = new TextArea();
        summaryArea.setEditable(false);
        summaryArea.setWrapText(true);
        summaryArea.setPrefRowCount(4);
        grid.add(summaryArea, 0, row, 3, 1);

        return grid;
    }

    /** Adds Label | Control | ErrorLabel as one row and returns the next free row index. */
    private int addRow(GridPane grid, int row, String labelText, javafx.scene.Node control) {
        Label label = new Label(labelText + ":");
        Label error = new Label();
        error.setStyle("-fx-text-fill: #c0392b; -fx-font-size: 11px;");
        error.setWrapText(true);
        errorLabels.put(labelText, error);

        grid.add(label, 0, row);
        grid.add(control, 1, row);
        grid.add(error, 2, row);
        return row + 1;
    }

    private HBox buildDobControls() {
        int currentYear = Year.now().getValue();
        yearCombo = new ComboBox<>();
        yearCombo.getItems().addAll(
                IntStream.rangeClosed(currentYear - 100, currentYear)
                        .boxed()
                        .sorted((a, b) -> b - a) // most recent first
                        .toArray(Integer[]::new));
        yearCombo.setPromptText("Year");

        monthCombo = new ComboBox<>();
        monthCombo.getItems().addAll(Month.values());
        monthCombo.setPromptText("Month");
        monthCombo.setConverter(new javafx.util.StringConverter<Month>() {
            @Override
            public String toString(Month m) {
                if (m == null) return "";
                String name = m.name();
                return name.charAt(0) + name.substring(1).toLowerCase();
            }

            @Override
            public Month fromString(String s) {
                return Month.valueOf(s.toUpperCase());
            }
        });

        dayCombo = new ComboBox<>();
        dayCombo.setPromptText("Day");
        refreshDayCombo();

        // Re-populate the day list whenever month or year changes, preserving
        // the selected day if it is still valid for the new month/year.
        yearCombo.valueProperty().addListener((obs, old, val) -> refreshDayCombo());
        monthCombo.valueProperty().addListener((obs, old, val) -> refreshDayCombo());

        HBox box = new HBox(6, yearCombo, monthCombo, dayCombo);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    private void refreshDayCombo() {
        Integer previousDay = dayCombo.getValue();
        Integer year = yearCombo.getValue();
        Month month = monthCombo.getValue();
        int y = year != null ? year : Year.now().getValue();
        int m = month != null ? month.getValue() : 1;
        int maxDay = Validator.daysInMonth(m, y);

        dayCombo.getItems().setAll(
                IntStream.rangeClosed(1, maxDay).boxed().toArray(Integer[]::new));

        if (previousDay != null && previousDay <= maxDay) {
            dayCombo.setValue(previousDay);
        } else {
            dayCombo.setValue(null);
        }
    }

    // ------------------------------------------------------------------
    // Submit / Reset
    // ------------------------------------------------------------------

    private void onSubmit() {
        clearErrors();
        ValidationResult result = new ValidationResult();

        String firstName = firstNameField.getText() == null ? "" : firstNameField.getText().trim();
        String lastName = lastNameField.getText() == null ? "" : lastNameField.getText().trim();
        String nin = ninField.getText() == null ? "" : ninField.getText().trim().toUpperCase();
        String email = emailField.getText() == null ? "" : emailField.getText().trim();
        String confirmEmail = confirmEmailField.getText() == null ? "" : confirmEmailField.getText().trim();
        String phone = phoneField.getText() == null ? "" : phoneField.getText().trim();
        String pin = pinField.getText() == null ? "" : pinField.getText().trim();
        String confirmPin = confirmPinField.getText() == null ? "" : confirmPinField.getText().trim();
        String depositRaw = depositField.getText() == null ? "" : depositField.getText().trim();
        String secondNin = secondNinField.getText() == null ? "" : secondNinField.getText().trim().toUpperCase();

        AccountType type = accountTypeCombo.getValue();
        Branch branch = branchCombo.getValue();
        Integer year = yearCombo.getValue();
        Month month = monthCombo.getValue();
        Integer day = dayCombo.getValue();

        putError(result, "First Name", Validator.validateName("First Name", firstName));
        putError(result, "Last Name", Validator.validateName("Last Name", lastName));
        putError(result, "National ID (NIN)", Validator.validateNin("National ID (NIN)", nin));
        putError(result, "Email", Validator.validateEmailFormat("Email", email));
        putError(result, "Confirm Email", Validator.validateEmailFormat("Confirm Email", confirmEmail));
        putError(result, "Confirm Email", Validator.validateEmailsMatch(email, confirmEmail));
        putError(result, "Phone Number", Validator.validatePhone(phone));
        putError(result, "PIN", Validator.validatePinFormat("PIN", pin));
        putError(result, "Confirm PIN", Validator.validatePinFormat("Confirm PIN", confirmPin));
        putError(result, "Confirm PIN", Validator.validatePinsMatch(pin, confirmPin));

        if (type == null) {
            putError(result, "Account Type", "Please select an account type.");
        }
        if (branch == null) {
            putError(result, "Branch", "Please select a branch.");
        }

        LocalDate dob = null;
        if (year == null || month == null || day == null) {
            putError(result, "Date of Birth", "Please select year, month and day.");
        } else {
            dob = LocalDate.of(year, month, day);
        }

        // Build a provisional account (needs type + branch + dob) purely to
        // evaluate the polymorphic minimumDeposit()/isAgeAcceptable() rules.
        Account provisional = null;
        if (type != null && branch != null && dob != null) {
            provisional = type.create(firstName, lastName, nin, email, phone, branch, dob,
                    safeParse(depositRaw), secondNin);
        }

        if (dob != null) {
            putError(result, "Date of Birth", Validator.validateAge(dob, provisional));
        }
        putError(result, "Opening Deposit (UGX)", Validator.validateDeposit(depositRaw, provisional));

        boolean isJoint = type == AccountType.JOINT;
        putError(result, "Second NIN (Joint only)", Validator.validateSecondNin(isJoint, secondNin));

        if (!result.isValid()) {
            showFieldErrors(result);
            showAlert(Alert.AlertType.ERROR, "Please fix the following", result.toSummaryText());
            return;
        }

        // Everything valid -- generate account number, persist, display.
        try {
            db.generateAndAssignAccountNumber(provisional);
            db.saveAccount(provisional, isJoint ? secondNin : null);
            summaryArea.setText(provisional.toSummaryRecord());
            showAlert(Alert.AlertType.INFORMATION, "Account Created",
                    "Account " + provisional.getAccountNumber() + " was created and saved successfully.");
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Save Failed",
                    "The form was valid but the record could not be saved:\n" + ex.getMessage());
        }
    }

    private double safeParse(String s) {
        try {
            return Double.parseDouble(s);
        } catch (Exception e) {
            return 0;
        }
    }

    private void putError(ValidationResult result, String field, String message) {
        if (message != null) {
            result.addError(field, message);
        }
    }

    private void showFieldErrors(ValidationResult result) {
        result.getFieldErrors().forEach((field, message) -> {
            Label label = errorLabels.get(field);
            if (label != null) {
                label.setText(message);
            }
        });
    }

    private void clearErrors() {
        errorLabels.values().forEach(l -> l.setText(""));
    }

    private void onReset() {
        firstNameField.clear();
        lastNameField.clear();
        ninField.clear();
        emailField.clear();
        confirmEmailField.clear();
        phoneField.clear();
        pinField.clear();
        confirmPinField.clear();
        secondNinField.clear();
        depositField.clear();
        yearCombo.setValue(null);
        monthCombo.setValue(null);
        dayCombo.setValue(null);
        accountTypeCombo.setValue(null);
        branchCombo.setValue(null);
        summaryArea.clear();
        clearErrors();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.getDialogPane().setPrefWidth(420);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
