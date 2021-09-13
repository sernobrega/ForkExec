package com.forkexec.pts.ws.it;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.forkexec.pts.ws.BadInitFault_Exception;
import com.forkexec.pts.ws.EmailAlreadyExistsFault_Exception;
import com.forkexec.pts.ws.InvalidEmailFault_Exception;

/*
 * Class tests if the user creation has succeeded or not
 */
public class ActivateUserIT extends BaseIT {

	// one-time initialization and clean-up
	@BeforeClass
	public static void oneTimeSetUp() {
	}

	@AfterClass
	public static void oneTimeTearDown() {
	}

	// members

	// initialization and clean-up for each test
	@Before
	public void setUp() throws BadInitFault_Exception {
		client.ctrlInit(USER_POINTS);
	}

	@After
	public void tearDown() {
		pointsTestClear();
	}

	@Test
	public void createUserValidTest() throws EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception {
		client.activateUser(VALID_USER);
		assertEquals(USER_POINTS, client.getBalance(VALID_USER).getBalance());
	}

	@Test
	public void createUserDotValidTest() throws EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception {
		String email = "sd.teste@tecnico";
		client.activateUser(email);
		assertEquals(USER_POINTS, client.getBalance(email).getBalance());
	}

	@Test
	public void createShortUserValidTest() throws EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception {
		String email = "sd@tecnico";
		client.activateUser(email);
		assertEquals(USER_POINTS, client.getBalance(email).getBalance());
	}

	@Test(expected = EmailAlreadyExistsFault_Exception.class)
	public void createUserDuplicateTest() throws EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception {
		client.activateUser(VALID_USER);
		client.activateUser(VALID_USER);
	}

	@Test(expected = InvalidEmailFault_Exception.class)
	public void createUserInvalidEmail1Test() throws EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception {
		String email = "@tecnico.ulisboa";
		client.activateUser(email);
	}

	@Test(expected = InvalidEmailFault_Exception.class)
	public void createUserInvalidEmail2Test() throws EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception {
		String email = "teste";
		client.activateUser(email);
	}

	@Test(expected = InvalidEmailFault_Exception.class)
	public void createUserInvalidEmail3Test() throws EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception {
		String email = "teste@tecnico.";
		client.activateUser(email);
	}

	@Test(expected = InvalidEmailFault_Exception.class)
	public void createUserInvalidEmail4Test() throws EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception {
		String email = "sd.@tecnico";
		client.activateUser(email);
	}

	@Test(expected = InvalidEmailFault_Exception.class)
	public void createUserNullEmailTest() throws EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception {
		client.activateUser(null);
	}

}
