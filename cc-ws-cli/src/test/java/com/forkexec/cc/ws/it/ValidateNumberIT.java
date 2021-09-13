package com.forkexec.cc.ws.it;

import org.junit.Test;
import org.junit.Assert;

public class ValidateNumberIT extends BaseIT {

    @Test
    public void successValidateNumber() {
        Assert.assertTrue(client.validateNumber("4207293416018426"));
        Assert.assertTrue(client.validateNumber("5038295668443717"));
        Assert.assertTrue(client.validateNumber("6011552743698197"));
        Assert.assertTrue(client.validateNumber("6759898742131185"));
        Assert.assertTrue(client.validateNumber("0604509918701632"));

        Assert.assertFalse(client.validateNumber(" "));
        Assert.assertFalse(client.validateNumber("     "));
        Assert.assertFalse(client.validateNumber(null));
        Assert.assertFalse(client.validateNumber("sd2019"));
        Assert.assertFalse(client.validateNumber("totallyinvalid..."));
        Assert.assertFalse(client.validateNumber("%##%/%/"));
        Assert.assertFalse(client.validateNumber("1604509918701632"));
    }
}
