import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/** A person who can borrow from the library. */
public final class Member {
    private final String name;
    private final MemberKind kind;
    private final List<String> history = new ArrayList<>();

    public Member(String name, MemberKind kind) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("A member needs a name");
        }
        this.name = name;
        this.kind = Objects.requireNonNull(kind, "A member needs a kind");
    }

    public String name() {
        return name;
    }

    public MemberKind kind() {
        return kind;
    }

    /** Read-only view. Only the library adds to a member's history. */
    public List<String> history() {
        return Collections.unmodifiableList(history);
    }

    void recordBorrow(Item item) {
        history.add("Borrowed " + item.title());
    }

    @Override
    public String toString() {
        return name + " (" + kind + ")";
    }
}
