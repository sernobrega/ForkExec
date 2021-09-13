package com.forkexec.hub.ws.it;

import com.forkexec.hub.ws.InvalidUserIdFault_Exception;
import org.junit.*;

public class ActivateAccountIT extends BaseIT {

    @Test
    public void successActivateAccount() throws InvalidUserIdFault_Exception {
        client.activateAccount("sd2019@tecnico.ulisboa.pt");
        Assert.assertEquals(client.accountBalance("sd2019@tecnico.ulisboa.pt"), 100);
    }

    @Test(expected = InvalidUserIdFault_Exception.class)
    public void invalidEmail_1() throws InvalidUserIdFault_Exception {
        client.activateAccount("sd2019tecnico.ulisboa.pt");
    }

    @Test(expected = InvalidUserIdFault_Exception.class)
    public void invalidEmail_2() throws InvalidUserIdFault_Exception {
        client.activateAccount(null);
    }

    @Test(expected = InvalidUserIdFault_Exception.class)
    public void invalidEmail_3() throws InvalidUserIdFault_Exception {
        client.activateAccount(" ");
    }

    @Test(expected = InvalidUserIdFault_Exception.class)
    public void doubleActivate() throws InvalidUserIdFault_Exception {
        client.activateAccount("sd2019@tecnico.ulisboa.pt");
        client.activateAccount("sd2019@tecnico.ulisboa.pt");
    }

    @After
    public void clear() {
        client.ctrlClear();
    }
}
