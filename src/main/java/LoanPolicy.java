/**
 * The borrowing rules for one type of item. Today that is only the loan period,
 * which depends on the kind of member borrowing.
 */
@FunctionalInterface
public interface LoanPolicy {
    int loanDays(MemberKind kind);
}
