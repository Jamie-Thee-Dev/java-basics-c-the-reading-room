/** Runs the same afternoon as the original program, so the two can be compared. */
public class LendingTracker {

    public static void main(String[] args) {
        Library lib = new Library("The Reading Room");

        Item novel = new Item("Things Fall Apart", ItemType.BOOK);
        Item audioCourse = new Item("French Audio Course", ItemType.AUDIO_KIT);
        lib.addItem(novel);
        lib.addItem(audioCourse);

        Member kofi = new Member("Kofi Mensah", MemberKind.STANDARD);
        Member ama = new Member("Ama Owusu", MemberKind.STUDENT);
        lib.addMember(kofi);
        lib.addMember(ama);

        try {
            // a normal afternoon
            lib.borrow(kofi, novel);
            lib.borrow(ama, audioCourse);
            lib.returnItem(audioCourse);

            // the novel is already out with Kofi, so the library now refuses this
            lib.borrow(ama, novel);
        } catch (LendingException e) {
            System.out.println(e.getMessage());
        }

        lib.printReport();
    }
}
