import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * The lending ledger. It is the only place that records which items are on loan.
 *
 * <p>Active loans live in a map keyed by item, so an item can have at most one active
 * loan. Whether an item is on loan, and who has it, is worked out from that map and
 * is never stored on the item itself.
 */
public final class Library {
    private final String name;
    private final PrintStream out;
    private final List<Item> items = new ArrayList<>();
    private final List<Member> members = new ArrayList<>();
    private final Map<Item, Loan> activeLoans = new LinkedHashMap<>();
    private final List<Loan> returnedLoans = new ArrayList<>();
    private int nextLoanId = 1;

    public Library(String name) {
        this(name, System.out);
    }

    /** The print stream is a parameter so tests can capture what the library says. */
    public Library(String name, PrintStream out) {
        this.name = Objects.requireNonNull(name);
        this.out = Objects.requireNonNull(out);
    }

    public void addItem(Item item) {
        items.add(Objects.requireNonNull(item));
    }

    public void addMember(Member member) {
        members.add(Objects.requireNonNull(member));
    }

    /**
     * Lends an item to a member for the period the item's policy gives that kind of
     * member.
     *
     * @throws LendingException if the item is already on loan. Nothing changes in that case.
     */
    public Loan borrow(Member member, Item item) throws LendingException {
        Objects.requireNonNull(member);
        Objects.requireNonNull(item);

        Loan existing = activeLoans.get(item);
        if (existing != null) {
            throw new LendingException("Cannot lend \"" + item.title() + "\" to " + member.name()
                    + ": already on loan to " + existing.member().name());
        }

        int loanDays = item.loanDaysFor(member.kind());
        Loan loan = new Loan(nextLoanId++, member, item, loanDays);
        activeLoans.put(item, loan);
        member.recordBorrow(item);
        out.println("Lent \"" + item.title() + "\" to " + member.name() + " for " + loanDays + " days");
        return loan;
    }

    /**
     * Takes an item back and closes its loan.
     *
     * @throws LendingException if the item is not on loan. Nothing changes in that case.
     */
    public void returnItem(Item item) throws LendingException {
        Objects.requireNonNull(item);

        Loan loan = activeLoans.remove(item);
        if (loan == null) {
            throw new LendingException("Cannot return \"" + item.title() + "\": it is not on loan");
        }
        returnedLoans.add(loan);
        out.println("Returned \"" + item.title() + "\"");
    }

    public boolean isOnLoan(Item item) {
        return activeLoans.containsKey(item);
    }

    public Optional<Member> borrowerOf(Item item) {
        return Optional.ofNullable(activeLoans.get(item)).map(Loan::member);
    }

    public List<Item> items() {
        return Collections.unmodifiableList(items);
    }

    public List<Member> members() {
        return Collections.unmodifiableList(members);
    }

    /** Read-only snapshot of the loans that are out right now, oldest first. */
    public List<Loan> activeLoans() {
        return List.copyOf(activeLoans.values());
    }

    /** Read-only snapshot of the loans that have been closed, in the order they were returned. */
    public List<Loan> returnedLoans() {
        return List.copyOf(returnedLoans);
    }

    public void printReport() {
        out.println();
        out.println("=== " + name + " - end of day ===");
        out.println("Items:");
        for (Item item : items) {
            String status = borrowerOf(item)
                    .map(borrower -> "ON_LOAN - with " + borrower.name())
                    .orElse("AVAILABLE");
            out.println("  \"" + item.title() + "\" (" + item.type() + "): " + status);
        }
        out.println("Active loans:");
        for (Loan loan : activeLoans.values()) {
            out.println("  Loan #" + loan.id() + ": \"" + loan.item().title() + "\" -> " + loan.member().name());
        }
    }
}
