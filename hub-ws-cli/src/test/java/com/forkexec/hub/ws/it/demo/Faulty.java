package com.forkexec.hub.ws.it.demo;

import com.forkexec.hub.ws.it.BaseIT;
import com.forkexec.hub.ws.*;
import org.junit.*;

import java.util.Scanner;

public class Faulty extends BaseIT {

    private final String user1 = "sd2019@tecnico.ulisboa.pt";
    private final String user2 = "sd.2019@tecnico.ulisboa.pt";
    private final String cc = "4532725865015441";

    @Test
    public void faultyFunction() throws InvalidUserIdFault_Exception, InvalidCreditCardFault_Exception, InvalidMoneyFault_Exception {
        client.activateAccount(user1);
        Assert.assertEquals(client.accountBalance(user1), 100);
        System.out.println("User1: Current balance is 100");
        System.out.println("User1: Loads 10 (1000 points)");
        client.loadAccount(user1, 10, cc);
        Assert.assertEquals(client.accountBalance(user1), 1100);
        System.out.println("User1: Current balance is 1100");

        client.activateAccount(user2);
        Assert.assertEquals(client.accountBalance(user2), 100);
        System.out.println("User2: Current balance is 100");
        System.out.println("User2: Loads 10 (1000 points)");
        client.loadAccount(user2, 10, cc);
        Assert.assertEquals(client.accountBalance(user2), 1100);
        System.out.println("User2: Current balance is 1100");
        Assert.assertEquals(client.accountBalance(user1), 1100);
        System.out.println("User1: Current balance is 1100");

        System.out.println("Please, disconnect one Points Servers within 10seconds");
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            //Do nothing
        }


        client.loadAccount(user2, 10, cc);
        client.loadAccount(user2, 10, cc);
        Assert.assertEquals(client.accountBalance(user2), 3100);
        Assert.assertEquals(client.accountBalance(user2), 3100);
        Assert.assertEquals(client.accountBalance(user2), 3100);

        client.ctrlClear();
    }
}

