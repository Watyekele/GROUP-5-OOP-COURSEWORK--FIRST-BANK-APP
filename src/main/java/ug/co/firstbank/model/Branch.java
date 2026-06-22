package ug.co.firstbank.model;

/**
 * The five branches First Bank Uganda operates, each with a 3-letter
 * code used as the prefix of generated account numbers.
 */
public enum Branch {
    KAMPALA("KLA"),
    GULU("GUL"),
    MBARARA("MBR"),
    JINJA("JIN"),
    MBALE("MBL");

    private final String code;

    Branch(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        // Nicely capitalised name for display in the ComboBox
        String name = name();
        return name.charAt(0) + name.substring(1).toLowerCase();
    }
}
