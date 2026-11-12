import java.util.Objects;

/**
 * One loan of one item to one member. A loan never changes after it is created.
 * Whether it is active or returned depends on which list in {@link Library} holds it.
 */
public record Loan(int id, Member member, Item item, int loanDays) {
    public Loan {
        Objects.requireNonNull(member, "A loan needs a member");
        Objects.requireNonNull(item, "A loan needs an item");
        if (loanDays <= 0) {
            throw new IllegalArgumentException("A loan must be for at least one day");
        }
    }
}
