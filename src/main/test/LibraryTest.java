import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class LibraryTest {

    private final ByteArrayOutputStream captured = new ByteArrayOutputStream();
    private final Library lib = new Library("The Reading Room", new PrintStream(captured, true));

    private final Item novel = new Item("Things Fall Apart", ItemType.BOOK);
    private final Item audioCourse = new Item("French Audio Course", ItemType.AUDIO_KIT);
    private final Member kofi = new Member("Kofi Mensah", MemberKind.STANDARD);
    private final Member ama = new Member("Ama Owusu", MemberKind.STUDENT);

    LibraryTest() {
        lib.addItem(novel);
        lib.addItem(audioCourse);
        lib.addMember(kofi);
        lib.addMember(ama);
    }

    private String output() {
        return captured.toString().replace("\r\n", "\n");
    }

    // The rule the business says must never be broken

    @Test
    void anItemOnLoanCannotBeLentToAnotherMember() throws Exception {
        lib.borrow(kofi, novel);

        assertThrows(LendingException.class, () -> lib.borrow(ama, novel));
    }

    @Test
    void aRefusedBorrowChangesNothing() throws Exception {
        lib.borrow(kofi, novel);

        assertThrows(LendingException.class, () -> lib.borrow(ama, novel));

        assertEquals(Optional.of(kofi), lib.borrowerOf(novel));
        assertEquals(1, lib.activeLoans().size());
        assertTrue(ama.history().isEmpty());
        assertFalse(output().contains("Ama Owusu"));
    }

    @Test
    void aRefusedBorrowDoesNotUseUpALoanNumber() throws Exception {
        lib.borrow(kofi, novel);
        assertThrows(LendingException.class, () -> lib.borrow(ama, novel));

        Loan next = lib.borrow(ama, audioCourse);

        assertEquals(2, next.id());
    }

    @Test
    void anItemCanBeLentAgainOnceItIsReturned() throws Exception {
        lib.borrow(kofi, novel);
        lib.returnItem(novel);

        lib.borrow(ama, novel);

        assertEquals(Optional.of(ama), lib.borrowerOf(novel));
        assertEquals(1, lib.activeLoans().size());
        assertEquals(1, lib.returnedLoans().size());
    }

    // Returns

    @Test
    void returningAnItemThatIsNotOnLoanIsRefused() {
        assertThrows(LendingException.class, () -> lib.returnItem(novel));
    }

    @Test
    void returningOneItemLeavesLoansOfOtherItemsAlone() throws Exception {
        Item secondCopy = new Item("Things Fall Apart", ItemType.BOOK); // same title, different copy
        lib.addItem(secondCopy);
        lib.borrow(kofi, novel);
        lib.borrow(ama, secondCopy);

        lib.returnItem(novel);

        assertFalse(lib.isOnLoan(novel));
        assertTrue(lib.isOnLoan(secondCopy));
        assertEquals(Optional.of(ama), lib.borrowerOf(secondCopy));
    }

    // Behavior carried over from the original

    @Test
    void borrowRecordsTheLoanPeriodForTheMemberKind() throws Exception {
        assertEquals(14, lib.borrow(kofi, novel).loanDays());
        assertEquals(14, lib.borrow(ama, audioCourse).loanDays());
    }

    @Test
    void borrowAddsToTheMembersHistory() throws Exception {
        lib.borrow(kofi, novel);

        assertEquals(java.util.List.of("Borrowed Things Fall Apart"), kofi.history());
    }

    @Test
    void normalAfternoonPrintsExactlyWhatTheOriginalPrinted() throws Exception {
        lib.borrow(kofi, novel);
        lib.borrow(ama, audioCourse);
        lib.returnItem(audioCourse);
        lib.printReport();

        String expected = """
                Lent "Things Fall Apart" to Kofi Mensah for 14 days
                Lent "French Audio Course" to Ama Owusu for 14 days
                Returned "French Audio Course"

                === The Reading Room - end of day ===
                Items:
                  "Things Fall Apart" (BOOK): ON_LOAN - with Kofi Mensah
                  "French Audio Course" (AUDIO_KIT): AVAILABLE
                Active loans:
                  Loan #1: "Things Fall Apart" -> Kofi Mensah
                """;
        assertEquals(expected, output());
    }

    // Encapsulation

    @Test
    void theLibraryHandsOutReadOnlyViews() throws Exception {
        lib.borrow(kofi, novel);

        assertThrows(UnsupportedOperationException.class, () -> lib.activeLoans().clear());
        assertThrows(UnsupportedOperationException.class, () -> lib.returnedLoans().clear());
        assertThrows(UnsupportedOperationException.class, () -> lib.items().clear());
        assertThrows(UnsupportedOperationException.class, () -> lib.members().clear());
        assertThrows(UnsupportedOperationException.class, () -> kofi.history().clear());
        assertEquals(1, lib.activeLoans().size());
    }

    @Test
    void itemsAndMembersNeedValidDetails() {
        assertThrows(IllegalArgumentException.class, () -> new Item(" ", ItemType.BOOK));
        assertThrows(NullPointerException.class, () -> new Item("Title", null));
        assertThrows(IllegalArgumentException.class, () -> new Member("", MemberKind.STANDARD));
        assertThrows(NullPointerException.class, () -> new Member("Name", null));
    }
}
