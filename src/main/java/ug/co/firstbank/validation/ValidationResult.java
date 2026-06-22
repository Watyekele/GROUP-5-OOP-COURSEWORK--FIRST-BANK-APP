package ug.co.firstbank.validation;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Collects validation errors keyed by field name so the UI can show an
 * inline message next to the offending control AND build one summary
 * dialog listing everything that is wrong.
 */
public class ValidationResult {

    private final Map<String, String> fieldErrors = new LinkedHashMap<>();

    public void addError(String field, String message) {
        fieldErrors.put(field, message);
    }

    public boolean isValid() {
        return fieldErrors.isEmpty();
    }

    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }

    /** One bullet line per field error, for the summary dialog. */
    public String toSummaryText() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> e : fieldErrors.entrySet()) {
            sb.append("\u2022 ").append(e.getKey()).append(": ").append(e.getValue()).append("\n");
        }
        return sb.toString();
    }
}
