package model;

public enum InvoiceStatus {
    CREATED("Создана"),
    SENT("Отправлена"),
    RECEIVED("Получена"),
    CANCELLED("Отменена");

    private final String displayName;

    InvoiceStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}