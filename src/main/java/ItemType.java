/**
 * The kinds of item the library lends. Each type owns its loan policy, so a new kind
 * of item is one new constant here plus one policy class.
 */
public enum ItemType {
    BOOK(new BookPolicy()),
    AUDIO_KIT(new AudioKitPolicy());

    private final LoanPolicy policy;

    ItemType(LoanPolicy policy) {
        this.policy = policy;
    }

    int loanDaysFor(MemberKind kind) {
        return policy.loanDays(kind);
    }
}
