package com.forkexec.pts.domain;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import com.forkexec.pts.domain.exception.EmailAlreadyExistsFaultException;
import com.forkexec.pts.domain.exception.InvalidEmailFaultException;
import com.forkexec.pts.domain.exception.InvalidPointsFaultException;

/**
 * Points
 * <p>
 * A points server.
 */
public class Points {

	/**
	 * Constant representing the default initial balance for every new client
	 */
	private static final int DEFAULT_INITIAL_BALANCE = 100;

	/**
	 * Global with the current value for the initial balancge of every new client
	 */
	private final AtomicInteger initialBalance = new AtomicInteger(DEFAULT_INITIAL_BALANCE);

	/**
	 * Accounts. Associates the user's email with a points balance. The collection
	 * uses a hash table supporting full concurrency of retrievals and updates. Each
	 * item is an AtomicInteger, a lock-free thread-safe single variable. This means
	 * that multiple threads can update this variable concurrently with correct
	 * synchronization.
	 */
	private Map<String, User> accounts = new ConcurrentHashMap<>();

	// Singleton -------------------------------------------------------------

	/**
	 * SingletonHolder is loaded on the first execution of Singleton.getInstance()
	 * or the first access to SingletonHolder.INSTANCE, not before.
	 */
	private static class SingletonHolder {
		private static final Points INSTANCE = new Points();
	}

	/**
	 * Retrieve single instance of class. Only method where 'synchronized' is used.
	 */
	public static synchronized Points getInstance() {
		return SingletonHolder.INSTANCE;
	}

	/**
	 * Private constructor prevents instantiation from other classes.
	 */
	private Points() {
		// initialization with default values
		reset();
	}

	/**
	 * Reset accounts. Synchronized is not required because we are using concurrent
	 * map and atomic integer.
	 */
	public void reset() {
		// clear current hash map
		accounts.clear();
		// set initial balance to default
		initialBalance.set(DEFAULT_INITIAL_BALANCE);
	}

	/**
	 * Set initial Reset accounts. Synchronized is not required because we are using
	 * atomic integer.
	 */
	public void setInitialBalance(int newInitialBalance) {
		initialBalance.set(newInitialBalance);
	}

	/** Access user. Throws exception if it does not exist. */
	public User getUser(final String accountId) throws InvalidEmailFaultException {
		checkValidEmail(accountId); 
		final User user = accounts.get(accountId);
		if (user == null) {
			throw new InvalidEmailFaultException("Account does not exist!");
		}

		return user;
	}

	/** Access points for account. Throws exception if it does not exist. */
	private AtomicInteger getPoints(final String accountId) throws InvalidEmailFaultException {
		checkValidEmail(accountId); 
		final AtomicInteger points = accounts.get(accountId).getBalance();
		if (points == null)
			throw new InvalidEmailFaultException("Account does not exist!");
		return points;
	}

	/** Access tag for account. Throws exception if it does not exist. */
	private long getSeq(final String accountId) throws InvalidEmailFaultException {
		checkValidEmail(accountId);
		final User points = accounts.get(accountId);
		final long seq = points.getTag();
		if (points == null)
			throw new InvalidEmailFaultException("Account does not exist!");
		return seq;
	}

	/**
	 * Access points for account. Throws exception if email is invalid or account
	 * does not exist.
	 */
	public int getAccountPoints(final String accountId) throws InvalidEmailFaultException {
		checkValidEmail(accountId);
		return getPoints(accountId).get();
	}

	/**
	 * Access current tag for account. Throws exception if email is invalid or account
	 * does not exist.
	 */
	public long getAccountSeq(final String accountId) throws InvalidEmailFaultException {
		checkValidEmail(accountId);
		if(!accounts.containsKey(accountId))
			throw new InvalidEmailFaultException("No account with that email");
		return getSeq(accountId);
	}

	/** Email address validation. */
	private void checkValidEmail(final String emailAddress) throws InvalidEmailFaultException {
		final String message;
		if (emailAddress == null) {
			message = "Null email is not valid";
		} else if (!Pattern.matches("(\\w\\.?)*\\w+@\\w+(\\.?\\w)*", emailAddress)) {
			message = String.format("Email: %s is not valid", emailAddress);
		} else {
			return;
		}
		throw new InvalidEmailFaultException(message);
	}

	/** Initialize account. */
	public void initAccount(final String accountId)
			throws EmailAlreadyExistsFaultException, InvalidEmailFaultException {
		checkValidEmail(accountId);
		if (accounts.containsKey(accountId)) {
			final String message = String.format("Account with email: %s already exists", accountId);
			throw new EmailAlreadyExistsFaultException(message);
		}

		User user = accounts.get(accountId);
		if (user == null) {
			AtomicInteger points = new AtomicInteger(initialBalance.get());
			accounts.put(accountId, new User(accountId, points, 0));
		}
	}

	/** Set points to account. */
	public void setPoints(final String accountId, final int pointsToSet, final long seq) throws InvalidPointsFaultException, InvalidEmailFaultException {
		checkValidEmail(accountId);
		User user = getUser(accountId);
		if (pointsToSet <= 0) {
			throw new InvalidPointsFaultException("Value cannot be negative or zero!");
		}
		user.newBalance(pointsToSet, seq);
	}

}
