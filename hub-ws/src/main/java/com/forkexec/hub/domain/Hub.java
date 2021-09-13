package com.forkexec.hub.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.forkexec.hub.domain.exception.*;
import com.forkexec.pts.ws.*;
import com.forkexec.rst.ws.BadTextFault_Exception;
import com.forkexec.rst.ws.BadMenuIdFault_Exception;
import com.forkexec.rst.ws.Menu;
import com.forkexec.rst.ws.MenuId;
import com.forkexec.rst.ws.MenuInit;
import com.forkexec.rst.ws.cli.*;
import com.forkexec.cc.ws.cli.*;

/**
 * Hub
 *
 * A restaurants hub server.
 *
 */
public class Hub {

	private HashMap<String, RestaurantClient> rstClients;

	private PointsFrontEnd ptsFrontEnd;

	private CreditCardClient ccClient;

	private final static HashMap<Integer, Integer> ptsConversion = new HashMap<>();

	private HashMap<String, Cart> carts;

	private int currentCartId = 0;

	private int foodOrderId = 0;

	private void createPtsConversion() {
		ptsConversion.put(10, 1000);
		ptsConversion.put(20, 2100);
		ptsConversion.put(30, 3300);
		ptsConversion.put(50, 5500);
	}

	// Singleton -------------------------------------------------------------

	/** Private constructor prevents instantiation from other classes. */
	private Hub() {
		rstClients = null;
		ptsFrontEnd = null;
		ccClient = null;
		carts = new HashMap<String, Cart>();
		createPtsConversion();
	}

	/**
	 * SingletonHolder is loaded on the first execution of Singleton.getInstance()
	 * or the first access to SingletonHolder.INSTANCE, not before.
	 */
	private static class SingletonHolder {
		private static final Hub INSTANCE = new Hub();
	}

	public static synchronized Hub getInstance() {
		return SingletonHolder.INSTANCE;
	}

	//Restaurant Client related operations
	public synchronized void setRestaurantClients(HashMap<String, RestaurantClient> map) {
		this.rstClients = map;
	}

	public synchronized void setPointsFrontEnd(PointsFrontEnd frontend) {
		this.ptsFrontEnd = frontend;
	}

	public synchronized HashMap<String, RestaurantClient> getRestaurantClients() {
		return rstClients;
	}

	public void initRestaurants(List<Product> products) throws BadInitException {
		if(products == null || products.size() == 0)
			throw new BadInitException("Products are null or size is 0.");

		HashMap<String, List<MenuInit>> initialMenus = new HashMap<>();

		for(Product p: products) {
			if(!initialMenus.containsKey(p.getRestId())) {
				initialMenus.put(p.getRestId(), new ArrayList<>());
			}
			List<MenuInit> tmpList = initialMenus.get(p.getRestId());
			MenuInit tmpMenu = buildMenuInit(p);
			tmpList.add(tmpMenu);
			initialMenus.replace(p.getRestId(), tmpList);
		}

		//Add to each Client, the respective List<MenuInit>
		for(String s: initialMenus.keySet()) {
			List<MenuInit> menu = initialMenus.get(s);
			RestaurantClient rst = rstClients.get(s);
			try {
				rst.ctrlInit(menu);
			} catch (com.forkexec.rst.ws.BadInitFault_Exception e) {
				throw new BadInitException(e.getMessage());
			}

		}
	}

	public List<Product> search(String description) throws InvalidTextException {
		List<Product> results = new ArrayList<>();

		for (String tmpRstId : rstClients.keySet()) {
			List<Menu> menuList = null;

			try {
				menuList = rstClients.get(tmpRstId).searchMenus(description);
			} catch (BadTextFault_Exception e) {
				throw new InvalidTextException(e.getMessage());
			}

			for (Menu menu : menuList)
				results.add(buildProduct(menu, tmpRstId));
		}

		return results;
	}

	public String pingRestaurants(String inputMessage) {
		StringBuilder builder = new StringBuilder();
		for(RestaurantClient rst: rstClients.values()) {
			builder.append(System.getProperty("line.separator"));
			builder.append(rst.ctrlPing(inputMessage));
		}
		return builder.toString();
	}

	public void clear() {
		ptsFrontEnd.ctrlClear();
		for(RestaurantClient r: rstClients.values())
			r.ctrlClear();
//		this.rstClients = null;
//		this.ptsClient = null;
//		this.ccClient = null;

	}


	//Points Client related operations

	public synchronized PointsFrontEnd getPointsClient() {
		return ptsFrontEnd;
	}

	public synchronized void newAccount(String userId) throws InvalidUserIdException {
		try {
			ptsFrontEnd.activateUser(userId);
		} catch(EmailAlreadyExistsFault_Exception e) {
			throw new InvalidUserIdException("Email has already signed up");
		} catch(InvalidEmailFault_Exception e) {
			throw new InvalidUserIdException("Email does not have the correct format");
		}
	}

	public synchronized void chargeAccount(String userId, int moneyToAdd, String creditCardNumber)
			throws InvalidCreditCardException, InvalidMoneyException, InvalidUserIdException {
		if(!ccClient.validateNumber(creditCardNumber))
			throw new InvalidCreditCardException("Credit Card number is not valid.");

		if(!ptsConversion.containsKey(moneyToAdd))
			throw new InvalidMoneyException("Amount to add is invalid.");

		try {
			int ptsToAdd = ptsConversion.get(moneyToAdd);
			ptsFrontEnd.addPoints(userId, ptsToAdd);
		} catch(InvalidEmailFault_Exception e) {
			throw new InvalidUserIdException(e.getMessage());
		} catch(InvalidPointsFault_Exception e) {
			throw new InvalidMoneyException(e.getMessage());
		}
	}

	public synchronized int balanceAccount(String userId) throws InvalidUserIdException {
		try {
			return ptsFrontEnd.pointsBalance(userId);
		} catch(InvalidEmailFault_Exception e) {
			throw new InvalidUserIdException(e.getMessage());
		}
	}

	public synchronized void initPoints(int startPoints) throws BadInitException {
		try {
			ptsFrontEnd.ctrlInit(startPoints);
		} catch (BadInitFault_Exception e) {
			throw new BadInitException(e.getMessage());
		}
	}

	//Credit Card Client related operations
	public synchronized void setCcClient(CreditCardClient ccClient) {
		this.ccClient = ccClient;
	}

	public synchronized CreditCardClient getCcClient() {
		return ccClient;
	}


	//Restaurant Client related operations
	public Product getProduct(String restaurantId, String menuId) throws InvalidFoodIdException {
		if (rstClients.get(restaurantId) == null || menuId == null)
			throw new InvalidFoodIdException("The restaurant with this id does not exist.");

		try {
			MenuId mId = new MenuId();
			mId.setId(menuId);
			return buildProduct(rstClients.get(restaurantId).getMenu(mId), restaurantId);
		} catch(BadMenuIdFault_Exception e) {
			throw new InvalidFoodIdException(e.getMessage());
		}
	}

	public void addFoodToCart(String userId, String restaurantId, String menuId, int foodQuantity) 
		throws InvalidFoodIdException, InvalidFoodQuantityException, InvalidUserIdException{

		if (rstClients.get(restaurantId) == null)
			throw new InvalidFoodIdException();

		if (foodQuantity <= 0)
			throw new InvalidFoodQuantityException();

		try {
			ptsFrontEnd.pointsBalance(userId);
		} catch(InvalidEmailFault_Exception e) {
			throw new InvalidUserIdException();
		}

		MenuId mid = new MenuId();
		mid.setId(menuId);

		try {
			Menu menu = rstClients.get(restaurantId).getMenu(mid);
			Product product = buildProduct(menu, restaurantId);
			product.setQuantity(foodQuantity);
			if (carts.get(userId) == null) {
				String cartId = Integer.toString(currentCartId);
				currentCartId++;

				Cart userCart = new Cart(cartId);
				carts.put(userId, userCart);
			}
			else {
				Cart tmpC = carts.get(userId);
				tmpC.addProduct(product, foodQuantity);
				carts.replace(userId, tmpC);
			}
		} catch(BadMenuIdFault_Exception e) {
			throw new InvalidFoodIdException(e.getMessage());
		}
	}

	public void clearCart(String userId) throws InvalidUserIdException {
		try {
			ptsFrontEnd.pointsBalance(userId);
		} catch(InvalidEmailFault_Exception e) {
			throw new InvalidUserIdException();
		}

		carts.get(userId).clearProducts();
	}

	public String orderCart(String userId) throws EmptyCartException, InvalidUserIdException, NotEnoughPointsException, InvalidPointsException {
		int totalPrice = 0;

		ArrayList<Product> products = (ArrayList<Product>) carts.get(userId).getProducts();

		if (products == null)
			throw new EmptyCartException("The user's cart is empty.");

		for(Product p : products) {
			String restaurantId = p.getRestId();
			String menuId = p.getId();
			int qt = p.getQuantity();
			totalPrice += p.getPrice();

			MenuId mId = new MenuId();
			mId.setId(menuId);

			try {
				rstClients.get(restaurantId).orderMenu(mId, qt);
			} catch(Exception e) {

			}
		}

		try {
			if (ptsFrontEnd.pointsBalance(userId) < totalPrice)
				throw new NotEnoughPointsException("The user doesn't have enough points to make order.");
			ptsFrontEnd.spendPoints(userId, totalPrice);
		} catch(InvalidEmailFault_Exception e) {
			throw new InvalidUserIdException();
		} catch(InvalidPointsFault_Exception e) {
			throw new InvalidPointsException(e.getMessage());
		} catch(NotEnoughBalanceFault_Exception e) {
			throw new NotEnoughPointsException(e.getMessage());
		}



		foodOrderId++;
		return Integer.toString(foodOrderId);
	}

	public List<Product> getCartProducts(String userId) throws InvalidUserIdException {
		try {
			ptsFrontEnd.pointsBalance(userId);
		} catch(InvalidEmailFault_Exception e) {
			throw new InvalidUserIdException(e.getMessage());
		}

		return carts.get(userId).getProducts();
	}

	//Helpers from domain object to client object
	public MenuInit buildMenuInit(Product p) {
		MenuInit ini = new MenuInit();
		ini.setQuantity(p.getQuantity());
		Menu menu = new Menu();
		MenuId mid = new MenuId();
		mid.setId(p.getId());
		menu.setId(mid);
		menu.setEntree(p.getEntree());
		menu.setPlate(p.getPlate());
		menu.setDessert(p.getDessert());
		menu.setPrice(p.getPrice());
		menu.setPreparationTime(p.getPreparationTime());
		ini.setMenu(menu);
		return ini;
	}

	public Product buildProduct(Menu m, String restaurantId) {
		Product p = new Product();
		p.setId(m.getId().getId());
		p.setRestId(restaurantId);
		p.setEntree(m.getEntree());
		p.setPlate(m.getPlate());
		p.setDessert(m.getDessert());
		p.setPrice(m.getPrice());
		p.setPreparationTime(m.getPreparationTime());
		return p;
	}

}
