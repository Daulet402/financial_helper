package kz.techsolutions.bot.api.exception;

public class FinancialControlException extends Exception {

    public FinancialControlException() {
    }

    public FinancialControlException(String s) {
        super(s);
    }

    public FinancialControlException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public FinancialControlException(Throwable throwable) {
        super(throwable);
    }
}