package com.forkexec.rst.domain;

import com.forkexec.rst.ws.BadQuantityFault_Exception;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

/**
 * Restaurant
 *
 * A restaurant server.
 *
 */
public class Restaurant {

	private HashMap<String, MenuRestaurant> menus = new HashMap<>();

	private ArrayList<MenuOrderRestaurant> menuOrders = new ArrayList<>();

	private int currentOrderId;

	// Singleton -------------------------------------------------------------

	/** Private constructor prevents instantiation from other classes. */
	private Restaurant() {
		currentOrderId = 0;
	}

	/**
	 * SingletonHolder is loaded on the first execution of Singleton.getInstance()
	 * or the first access to SingletonHolder.INSTANCE, not before.
	 */
	private static class SingletonHolder {
		private static final Restaurant INSTANCE = new Restaurant();
	}

	public static synchronized Restaurant getInstance() {
		return SingletonHolder.INSTANCE;
	}

	public synchronized void addMenu(MenuRestaurant menu) throws BadMenuIdException {
		if(menu == null || menus.containsKey(menu.getId()))
			throw new BadMenuIdException("Menu is not valid.");

		menus.put(menu.getId(), menu);
	}

	public synchronized MenuRestaurant getMenu(String menuId) throws BadMenuIdException {
		if(menuId == null || !menus.containsKey(menuId))
			throw new BadMenuIdException("Menu identifier can not be found.");

		return menus.get(menuId);
	}

	public synchronized List<MenuRestaurant> search(String descriptionText) throws BadTextException {
		if(descriptionText == null || descriptionText.trim().equals(""))
			throw new BadTextException("Description is empty or null");

		ArrayList<MenuRestaurant> resultMenus = new ArrayList<>();

		for (MenuRestaurant m: menus.values())
			if (m.getEntree().contains(descriptionText) || m.getPlate().contains(descriptionText) || m.getDessert().contains(descriptionText))
				resultMenus.add(m);

		return resultMenus;
	}

	public synchronized MenuOrderRestaurant order(String menuId, int qt) throws BadMenuIdException, BadQuantityException, InsufficientQuantityException {
		if(qt <= 0)
			throw new BadQuantityException("Quantity needs to be higher than 0");

		MenuRestaurant menuR = getMenu(menuId);

		if(menuR.getQuantity() < qt)
			throw new InsufficientQuantityException("Not enough quantity available.");

		int next = currentOrderId++;

		MenuOrderRestaurant menuOrder = new MenuOrderRestaurant(next, menuId, qt);
		menuOrders.add(menuOrder);
		menuR.setQuantity(menuR.getQuantity() - qt);

		return menuOrder;
	}

	public synchronized void resetCurrentOrder() {
		currentOrderId = 0;
	}

	public synchronized void resetAllValues() {
		menus.clear();
		menuOrders.clear();
		resetCurrentOrder();
	}
	
}