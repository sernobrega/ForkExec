package com.forkexec.hub.ws.it;

import com.forkexec.hub.ws.InvalidCreditCardFault_Exception;
import com.forkexec.hub.ws.InvalidMoneyFault_Exception;
import com.forkexec.hub.ws.InvalidUserIdFault_Exception;
import org.junit.*;

public class LoadAccountIT extends BaseIT {

    String userEmail = "sd2019@tecnico.ulisboa.pt";
    String validCc = "4532725865015441";

    @Before
    public void populate4Test() throws InvalidUserIdFault_Exception {
        client.activateAccount("sd2019@tecnico.ulisboa.pt");
        Assert.assertEquals(client.accountBalance("sd2019@tecnico.ulisboa.pt"), 100);
    }

    @Test
    public void successLoadAccount() throws InvalidUserIdFault_Exception, InvalidCreditCardFault_Exception, InvalidMoneyFault_Exception {
        client.loadAccount(userEmail, 10, validCc);
        Assert.assertEquals(client.accountBalance(userEmail), 1100);
    }

    @Test(expected = InvalidUserIdFault_Exception.class)
    public void wrongUser() throws InvalidUserIdFault_Exception, InvalidCreditCardFault_Exception, InvalidMoneyFault_Exception {
        client.loadAccount("sd@tecnico.ulisboa.pt", 10, validCc);
    }

    @Test(expected = InvalidMoneyFault_Exception.class)
    public void invalidMoney() throws InvalidUserIdFault_Exception, InvalidCreditCardFault_Exception, InvalidMoneyFault_Exception {
        client.loadAccount(userEmail, 15, validCc);
    }

    @Test(expected = InvalidCreditCardFault_Exception.class)
    public void invalidCc() throws InvalidUserIdFault_Exception, InvalidCreditCardFault_Exception, InvalidMoneyFault_Exception {
        client.loadAccount(userEmail, 15, "15" + validCc);
    }

    @Test
    public void doubleSuccessLoad() throws InvalidUserIdFault_Exception, InvalidCreditCardFault_Exception, InvalidMoneyFault_Exception {
        client.loadAccount(userEmail, 10, validCc);
        Assert.assertEquals(client.accountBalance(userEmail), 1100);

        client.loadAccount(userEmail, 20, validCc);
        Assert.assertEquals(client.accountBalance(userEmail), 3200);
    }

    @Test(expected = InvalidMoneyFault_Exception.class)
    public void oneSuccesOneFailure() throws InvalidUserIdFault_Exception, InvalidCreditCardFault_Exception, InvalidMoneyFault_Exception {
        client.loadAccount(userEmail, 10, validCc);
        Assert.assertEquals(client.accountBalance(userEmail), 1100);

        client.loadAccount(userEmail, 15, validCc);
    }

    @After
    public void clear() {
        client.ctrlClear();
    }
}
