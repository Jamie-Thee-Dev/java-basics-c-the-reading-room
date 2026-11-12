/** Physical books: 14 days, or 28 days for students. */
public record BookPolicy() implements LoanPolicy {
    @Override
    public int loanDays(MemberKind kind) {
        // No default branch on purpose: adding a MemberKind stops this from compiling
        // until someone decides what that kind of member gets.
        return switch (kind) {
            case STANDARD -> 14;
            case STUDENT -> 28;
        };
    }
}
