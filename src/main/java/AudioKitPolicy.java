/** Audio kits: 7 days, or 14 days for students. */
public record AudioKitPolicy() implements LoanPolicy {
    @Override
    public int loanDays(MemberKind kind) {
        return switch (kind) {
            case STANDARD -> 7;
            case STUDENT -> 14;
        };
    }
}
