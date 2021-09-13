package com.forkexec.pts.ws.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.forkexec.pts.ws.BadInitFault_Exception;
import com.forkexec.pts.ws.EmailAlreadyExistsFault_Exception;
import com.forkexec.pts.ws.InvalidEmailFault_Exception;
import com.forkexec.pts.ws.InvalidPointsFault_Exception;

import com.forkexec.pts.ws.AccountView;

public class SetBalanceIT extends BaseIT {
	@Before
	public void setUp() throws BadInitFault_Exception, EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception {
		client.ctrlInit(USER_POINTS);
		client.activateUser(VALID_USER);
	}

	@After
	public void tearDown() {
		pointsTestClear();
	}

	@Test
	public void setBalanceTest() throws InvalidPointsFault_Exception, InvalidEmailFault_Exception {
		client.setBalance(VALID_USER, 1100, 0);
		assertEquals(1100, client.getBalance(VALID_USER).getBalance());
	}

	@Test
	public void noPaymentTest() throws InvalidEmailFault_Exception {
		try {
			AccountView view = client.getBalance(VALID_USER);
			client.setBalance(VALID_USER, 0, view.getSeq());
			fail();
		} catch (InvalidPointsFault_Exception e) {
			assertEquals(USER_POINTS, client.getBalance(VALID_USER).getBalance());
		}
	}

	@Test(expected = InvalidPointsFault_Exception.class)
	public void negativeValueTest() throws InvalidEmailFault_Exception, InvalidPointsFault_Exception {
		client.setBalance(VALID_USER, -10, 0);
	}

	@Test(expected = InvalidEmailFault_Exception.class)
	public void unknownUserTest() throws InvalidPointsFault_Exception, InvalidEmailFault_Exception {
		client.setBalance(UNKNOWN_USER, 10, 0);
	}

	@Test(expected = InvalidEmailFault_Exception.class)
	public void nullEmailTest()
			throws InvalidEmailFault_Exception, InvalidPointsFault_Exception {
        AccountView view = client.getBalance(null);
		client.setBalance(null, 20, view.getSeq());
	}
}
