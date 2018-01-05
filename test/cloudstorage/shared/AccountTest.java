package cloudstorage.shared;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {
    private Account account = new Account(1, "jorin", "jorin@mail.com");

    @Test
    void getId() {
        assertEquals(1, account.getId());
    }

    @Test
    void getName() {
        assertEquals("jorin", account.getName());
    }
}