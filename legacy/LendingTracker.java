// The Reading Room - community library lending tracker
// Original program, built by a volunteer. New volunteers are told not to touch it.
// Run it first to see the sort of thing they are worried about, and look closely
// at the items and active loans in the end-of-day report.

import java.util.*;

public class LendingTracker {

    public static void main(String[] args) {
        Library lib = new Library("The Reading Room");

        LendingItem novel = new LendingItem("Things Fall Apart", "BOOK");
        LendingItem audioCourse = new LendingItem("French Audio Course", "AUDIO_KIT");
        lib.addItem(novel);
        lib.addItem(audioCourse);

        Member kofi = new Member("Kofi Mensah", "STANDARD");
        Member ama = new Member("Ama Owusu", "STUDENT");
        lib.addMember(kofi);
        lib.addMember(ama);

        // a normal afternoon
        lib.borrow(kofi, novel);
        lib.borrow(ama, audioCourse);
        lib.returnItem(audioCourse);

        // ... and the sort of thing that worries them:
        lib.borrow(ama, novel); // the novel is already out with Kofi, but nothing stops this

        lib.printReport();
    }
}

class LendingItem {
    public String title;
    public String type;   // "BOOK" or "AUDIO_KIT"
    public String status; // "AVAILABLE" or "ON_LOAN"
    public String currentBorrower;
    public int id;
    static int counter = 1;

    public LendingItem(String title, String type) {
        this.id = counter;
        counter++;
        this.title = title;
        this.type = type;
        this.status = "AVAILABLE";
    }
}

class Member {
    public String name;
    public String kind; // "STANDARD" or "STUDENT"
    public int id;
    public List<String> history = new ArrayList<>();
    static int counter = 1;

    public Member(String name, String kind) {
        this.id = counter;
        counter++;
        this.name = name;
        this.kind = kind;
    }
}

class Loan {
    public int id;
    public String memberName;
    public String itemTitle;
    public int loanDays;
    public String status; // "ACTIVE" or "RETURNED"
    static int counter = 1;

    public Loan(String memberName, String itemTitle, int loanDays) {
        this.id = counter;
        counter++;
        this.memberName = memberName;
        this.itemTitle = itemTitle;
        this.loanDays = loanDays;
        this.status = "ACTIVE";
    }
}

class Library {
    public String name;
    public List<LendingItem> items = new ArrayList<>();
    public List<Member> members = new ArrayList<>();
    private List<Loan> loans = new ArrayList<>();

    public Library(String name) {
        this.name = name;
    }

    public void addItem(LendingItem i) {
        items.add(i);
    }

    public void addMember(Member m) {
        members.add(m);
    }

    // Lend an item to a member. The loan period depends on the kind of item and member.
    public void borrow(Member m, LendingItem item) {
        int loanDays = 0;
        if (item.type.equals("BOOK")) {
            loanDays = 14;
            if (m.kind.equals("STUDENT")) {
                loanDays = 28; // students get longer loans
            }
            item.status = "ON_LOAN";
            item.currentBorrower = m.name;
            System.out.println("Lent \"" + item.title + "\" to " + m.name + " for " + loanDays + " days");
        } else if (item.type.equals("AUDIO_KIT")) {
            loanDays = 7;
            if (m.kind.equals("STUDENT")) {
                loanDays = 14; // students get longer loans
            }
            item.status = "ON_LOAN";
            item.currentBorrower = m.name;
            System.out.println("Lent \"" + item.title + "\" to " + m.name + " for " + loanDays + " days");
        }
        m.history.add("Borrowed " + item.title);
        loans.add(new Loan(m.name, item.title, loanDays));
    }

    public void returnItem(LendingItem item) {
        item.status = "AVAILABLE";
        item.currentBorrower = null;
        for (Loan l : loans) {
            if (l.itemTitle.equals(item.title) && l.status.equals("ACTIVE")) {
                l.status = "RETURNED";
            }
        }
        System.out.println("Returned \"" + item.title + "\"");
    }

    public List<Loan> getLoans() {
        return loans;
    }

    public void printReport() {
        System.out.println();
        System.out.println("=== " + name + " - end of day ===");
        System.out.println("Items:");
        for (LendingItem i : items) {
            System.out.println("  \"" + i.title + "\" (" + i.type + "): " + i.status
                    + (i.currentBorrower != null ? " - with " + i.currentBorrower : ""));
        }
        System.out.println("Active loans:");
        for (Loan l : loans) {
            if (l.status.equals("ACTIVE")) {
                System.out.println("  Loan #" + l.id + ": \"" + l.itemTitle + "\" -> " + l.memberName);
            }
        }
    }
}
