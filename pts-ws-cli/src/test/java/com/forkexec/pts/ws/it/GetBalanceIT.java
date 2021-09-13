package com.forkexec.pts.ws.it;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.forkexec.pts.ws.BadInitFault_Exception;
import com.forkexec.pts.ws.EmailAlreadyExistsFault_Exception;
import com.forkexec.pts.ws.InvalidEmailFault_Exception;

public class GetBalanceIT extends BaseIT {
	@Before
	public void setUp() throws EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception, BadInitFault_Exception {
		client.ctrlInit(USER_POINTS);
		client.activateUser(VALID_USER);
	}

	@After
	public void tearDown() {
		pointsTestClear();
	}

	@Test
	public void initialBalanceTest() throws InvalidEmailFault_Exception {
		assertEquals(USER_POINTS, client.getBalance(VALID_USER).getBalance());
	}

	@Test(expected = InvalidEmailFault_Exception.class)
	public void unknownUserTest() throws InvalidEmailFault_Exception {
		client.getBalance(UNKNOWN_USER).getBalance();
	}

	@Test(expected = InvalidEmailFault_Exception.class)
	public void nullEmailTest() throws InvalidEmailFault_Exception {
		client.getBalance(null).getBalance();
	}

}
