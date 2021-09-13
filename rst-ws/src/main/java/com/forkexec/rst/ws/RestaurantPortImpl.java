package com.forkexec.rst.ws;

import java.util.ArrayList;
import java.util.List;

import javax.jws.WebService;

import com.forkexec.rst.domain.*;

/**
 * This class implements the Web Service port type (interface). The annotations
 * below "map" the Java class to the WSDL definitions.
 */
@WebService(endpointInterface = "com.forkexec.rst.ws.RestaurantPortType",
            wsdlLocation = "RestaurantService.wsdl",
            name ="RestaurantWebService",
            portName = "RestaurantPort",
            targetNamespace="http://ws.rst.forkexec.com/",
            serviceName = "RestaurantService"
)
public class RestaurantPortImpl implements RestaurantPortType {

	/**
	 * The Endpoint manager controls the Web Service instance during its whole
	 * lifecycle.
	 */
	private RestaurantEndpointManager endpointManager;

	private Restaurant restaurant = Restaurant.getInstance();

	/** Constructor receives a reference to the endpoint manager. */
	public RestaurantPortImpl(RestaurantEndpointManager endpointManager) {
		this.endpointManager = endpointManager;
	}
	
	// Main operations -------------------------------------------------------
	
	/**
	* -> O menu e menuOrder que vem do Restaurant não é o mesmo objeto que está a ser manipulado e que vem do 
	*	wsimport, ou seja, é preciso usar as views para transformar o objeto que vem do Restaurant no return type
	*	da função.
	*/

	@Override
	public Menu getMenu(MenuId menuId) throws BadMenuIdFault_Exception {
		MenuRestaurant menu = null;

		if(menuId == null)
			throwBadMenuId("MenuId can not be null");

		try {
			menu = restaurant.getMenu(menuId.getId());
		} catch(BadMenuIdException e) {
			throwBadMenuId("MenuId was not found");
		}

		return buildMenu(menu);
	}
	
	@Override
	public List<Menu> searchMenus(String descriptionText) throws BadTextFault_Exception {
		List<Menu> menuLst = new ArrayList<>();

		try {
			for(MenuRestaurant r: restaurant.search(descriptionText))
				menuLst.add(buildMenu(r));
		} catch(BadTextException e) {
			throwBadText("Menu with this id doesn't exist");
		}

		return menuLst;
	}

	@Override
	public MenuOrder orderMenu(MenuId mid, int quantity)
			throws BadMenuIdFault_Exception, BadQuantityFault_Exception, InsufficientQuantityFault_Exception {

		MenuOrderRestaurant menuOrder = null;

		try {
			menuOrder = restaurant.order(mid.getId(), quantity);
		} catch(BadMenuIdException e) {
			throwBadMenuId("Menu with this id doesn't exist");
		} catch(BadQuantityException e) {
			throwBadQuantity("Quantity needs to be higher than 0");
		} catch(InsufficientQuantityException e) {
			throwInsufficientQuantity("Not enough quantity available.");
		}

		return buildMenuOrder(menuOrder);
	}

	// Control operations ----------------------------------------------------

	/** Diagnostic operation to check if service is running. */
	@Override
	public String ctrlPing(String inputMessage) {
		// If no input is received, return a default name.
		if (inputMessage == null || inputMessage.trim().length() == 0)
			inputMessage = "friend";

		// If the park does not have a name, return a default.
		String wsName = endpointManager.getWsName();
		if (wsName == null || wsName.trim().length() == 0)
			wsName = "Restaurant";

		// Build a string with a message to return.
		StringBuilder builder = new StringBuilder();
		builder.append("Hello ").append(inputMessage);
		builder.append(" from ").append(wsName);
		return builder.toString();
	}

	/** Return all variables to default values. */
	@Override
	public void ctrlClear() {
		restaurant.resetAllValues();
	}

	/** Set variables with specific values. */
	@Override
	public void ctrlInit(List<MenuInit> initialMenus) throws BadInitFault_Exception {
		if (initialMenus == null || initialMenus.size() == 0)
			throwBadInit("Initial Menus empty or null");

		for(MenuInit m: initialMenus) {
			Menu menu = m.getMenu();
			int q = m.getQuantity();
			MenuId menuId = menu.getId();

			MenuRestaurant menuRestaurant = new MenuRestaurant(menuId.getId(), menu.getEntree(), menu.getPlate(), menu.getDessert(),
			menu.getPrice(), menu.getPreparationTime(), q);

			try {
				restaurant.addMenu(menuRestaurant);
			} catch(BadMenuIdException e) {
				throwBadInit("Initial menus contain menus with the same IDs");
			}
		}

		restaurant.resetCurrentOrder();
	}

	// View helpers ----------------------------------------------------------

	/** Helper to convert a domain object to a view. */
	private Menu buildMenu(MenuRestaurant menuR) {
		Menu menu = new Menu();
		MenuId menuId = new MenuId();

		menuId.setId(menuR.getId());

		menu.setId(menuId);
		menu.setEntree(menuR.getEntree());
		menu.setPlate(menuR.getPlate());
		menu.setDessert(menuR.getDessert());
		menu.setPrice(menuR.getPrice());
		menu.setPreparationTime(menuR.getPreparation());

		return menu;
	}

	/** Helper to convert a domain object to a view. */
	private MenuOrder buildMenuOrder(MenuOrderRestaurant menuR) {
		MenuOrder order = new MenuOrder();
		MenuOrderId menuOrderId = new MenuOrderId();
		MenuId mid = new MenuId();
		mid.setId(menuR.getMenuId());

		menuOrderId.setId(Integer.toString(menuR.getMenuOrderId()));

		order.setId(menuOrderId);
		order.setMenuId(mid);
		order.setMenuQuantity(menuR.getQuantity());

		return order;
	}

	// Exception helpers -----------------------------------------------------

	/** Helper to throw a new BadInit exception. */
	private void throwBadInit(final String message) throws BadInitFault_Exception {
		BadInitFault faultInfo = new BadInitFault();
		faultInfo.message = message;
		throw new BadInitFault_Exception(message, faultInfo);
	}

	private void throwBadMenuId(final String message) throws BadMenuIdFault_Exception {
		BadMenuIdFault faultInfo = new BadMenuIdFault();
		faultInfo.message = message;
		throw new BadMenuIdFault_Exception(message, faultInfo);
	}

	private void throwBadQuantity(final String message) throws BadQuantityFault_Exception {
		BadQuantityFault faultInfo = new BadQuantityFault();
		faultInfo.message = message;
		throw new BadQuantityFault_Exception(message, faultInfo);
	}

	private void throwInsufficientQuantity(final String message) throws InsufficientQuantityFault_Exception {
		InsufficientQuantityFault faultInfo = new InsufficientQuantityFault();
		faultInfo.message = message;
		throw new InsufficientQuantityFault_Exception(message, faultInfo);
	}

	private void throwBadText(final String message) throws BadTextFault_Exception {
		BadTextFault faultInfo = new BadTextFault();
		faultInfo.message = message;
		throw new BadTextFault_Exception(message, faultInfo);
	}

}