import java.util.Objects;

/**
 * Something the library lends. An item does not know whether it is on loan. The
 * {@link Library} is the only place that records that, so there is one source of truth.
 */
public final class Item {
    private final String title;
    private final ItemType type;

    public Item(String title, ItemType type) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("An item needs a title");
        }
        this.title = title;
        this.type = Objects.requireNonNull(type, "An item needs a type");
    }

    public String title() {
        return title;
    }

    public ItemType type() {
        return type;
    }

    public int loanDaysFor(MemberKind kind) {
        return type.loanDaysFor(kind);
    }

    @Override
    public String toString() {
        return "\"" + title + "\" (" + type + ")";
    }
}
