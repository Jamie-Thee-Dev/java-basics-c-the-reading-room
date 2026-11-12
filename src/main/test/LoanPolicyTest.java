import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Locks in the loan-period table from the original program. */
class LoanPolicyTest {

    @Test
    void bookLoanPeriods() {
        assertEquals(14, new Item("Book", ItemType.BOOK).loanDaysFor(MemberKind.STANDARD));
        assertEquals(28, new Item("Book", ItemType.BOOK).loanDaysFor(MemberKind.STUDENT));
    }

    @Test
    void audioKitLoanPeriods() {
        assertEquals(7, new Item("Kit", ItemType.AUDIO_KIT).loanDaysFor(MemberKind.STANDARD));
        assertEquals(14, new Item("Kit", ItemType.AUDIO_KIT).loanDaysFor(MemberKind.STUDENT));
    }

    @Test
    void everyTypeAndKindHasAPositiveLoanPeriod() {
        for (ItemType type : ItemType.values()) {
            for (MemberKind kind : MemberKind.values()) {
                int days = new Item("Anything", type).loanDaysFor(kind);
                if (days <= 0) {
                    throw new AssertionError(type + " / " + kind + " gives " + days + " days");
                }
            }
        }
    }
}
