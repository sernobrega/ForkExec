package com.forkexec.hub.ws;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import javax.jws.WebService;

import com.forkexec.hub.domain.*;
import com.forkexec.hub.domain.exception.*;
import com.forkexec.rst.ws.cli.*;
import com.forkexec.pts.ws.cli.*;
import com.forkexec.cc.ws.cli.*;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDIRecord;

/**
 * This class implements the Web Service port type (interface). The annotations
 * below "map" the Java class to the WSDL definitions.
 */
@WebService(endpointInterface = "com.forkexec.hub.ws.HubPortType",
            wsdlLocation = "HubService.wsdl",
            name ="HubWebService",
            portName = "HubPort",
            targetNamespace="http://ws.hub.forkexec.com/",
            serviceName = "HubService"
)
public class HubPortImpl implements HubPortType {

	/**
	 * The Endpoint manager controls the Web Service instance during its whole
	 * lifecycle.
	 */
	private HubEndpointManager endpointManager;

	private Hub hub = Hub.getInstance();

	/** Constructor receives a reference to the endpoint manager. */
	public HubPortImpl(HubEndpointManager endpointManager) {
		this.endpointManager = endpointManager;
	}

	// Main operations -------------------------------------------------------
	
	@Override
	public void activateAccount(String userId) throws InvalidUserIdFault_Exception {
		if(userId == null)
			throwInvalidUser("userId can not be null.");

		try {                                                                                              
			hub.newAccount(userId);
		} catch(InvalidUserIdException e) {
			throwInvalidUser(e.getMessage());
		}                                                                                                  
	}

	@Override
	public void loadAccount(String userId, int moneyToAdd, String creditCardNumber)
			throws InvalidCreditCardFault_Exception, InvalidMoneyFault_Exception, InvalidUserIdFault_Exception {
		try {
			hub.chargeAccount(userId, moneyToAdd, creditCardNumber);
		} catch(InvalidUserIdException e) {
			throwInvalidUser(e.getMessage());
		} catch(InvalidMoneyException e) {
			throwInvalidMoney(e.getMessage());
		} catch(InvalidCreditCardException e) {
			throwInvalidCreditCard(e.getMessage());
		}
	}

	@Override
	public List<Food> searchDeal(String description) throws InvalidTextFault_Exception {
		List<Food> foodList = search(description);

		foodList.sort(new Comparator<Food>() {
			public int compare(Food o1, Food o2) {
				return Integer.compare(o1.getPrice(), o2.getPrice());
			}
		});
		
		return foodList;
	}

	@Override
	public List<Food> searchHungry(String description) throws InvalidTextFault_Exception {
		List<Food> foodList = search(description);

		foodList.sort(new Comparator<Food>() {
			public int compare(Food o1, Food o2) {
				return Integer.compare(o1.getPreparationTime(), o2.getPreparationTime());
			}
		});

		return foodList;
	}

	private List<Food> search(String description) throws InvalidTextFault_Exception {
		try {
			List<Food> foodList = new ArrayList<>();

			List<Product> pdtList = hub.search(description);
			for(Product p: pdtList)
				foodList.add(buildFood(p));

			return foodList;
		} catch(Exception e) {
			throwInvalidText("The description is invalid or empty.");
		}
		return null;
	}

	
	@Override
	public void addFoodToCart(String userId, FoodId foodId, int foodQuantity)
			throws InvalidFoodIdFault_Exception, InvalidFoodQuantityFault_Exception, InvalidUserIdFault_Exception {
		try {
			hub.addFoodToCart(userId, foodId.getRestaurantId(), foodId.getMenuId(), foodQuantity);
		} catch(InvalidFoodIdException e) {
			throwInvalidFoodId("The food ID is invalid or empty.");
		} catch(InvalidFoodQuantityException e) {
			throwInvalidFoodQuantity("The quantity of food is invalid.");
		} catch(InvalidUserIdException e) {
			throwInvalidUser("The user ID is invalid.");
		}
	}

	@Override
	public void clearCart(String userId) throws InvalidUserIdFault_Exception {
		try {
			hub.clearCart(userId);
		} catch(InvalidUserIdException e) {
			throwInvalidUser("The User ID is invalid or empty.");
		}
	}

	@Override
	public FoodOrder orderCart(String userId)
			throws EmptyCartFault_Exception, InvalidUserIdFault_Exception, NotEnoughPointsFault_Exception {
		try {
			String foodOrderId = hub.orderCart(userId);

			return buildFoodOrder(foodOrderId, cartContents(userId));
		} catch(EmptyCartException e) {
			throwEmptyCart(e.getMessage());
		} catch(InvalidUserIdException e) {
			throwInvalidUser(e.getMessage());
		} catch(NotEnoughPointsException e) {
			throwNotEnoughPoints(e.getMessage());
		} catch(Exception e) {

		}
		return null;
	}

	@Override
	public int accountBalance(String userId) throws InvalidUserIdFault_Exception {
		try {
			return hub.balanceAccount(userId);
		} catch(InvalidUserIdException e) {
			throwInvalidUser(e.getMessage());
		}
		return -1;
	}

	@Override
	public Food getFood(FoodId foodId) throws InvalidFoodIdFault_Exception {
		try {
			Product product = hub.getProduct(foodId.getRestaurantId(), foodId.getMenuId());

			return buildFood(product);
		} catch(InvalidFoodIdException e) {
			throwInvalidFoodId(e.getMessage());
		}
		return null;
	}

	@Override
	public List<FoodOrderItem> cartContents(String userId) throws InvalidUserIdFault_Exception {
		try {
			List<FoodOrderItem> foodOrder = new ArrayList<>();

			ArrayList<Product> arrayProducts = (ArrayList<Product>) hub.getCartProducts(userId);

			for (Product p : arrayProducts)
				foodOrder.add(buildFoodOrderItem(p));

			return foodOrder;
		} catch (InvalidUserIdException e) {
			throwInvalidUser(e.getMessage());
		}

		return null;
	}

	public void loadRestaurants() throws HubServerException  {
		try {
			HashMap<String, RestaurantClient> clientList = new HashMap<>();
			if (endpointManager.isVerbose())
				System.out.printf("Looking for restaurants%n");
			List<UDDIRecord> rstRecords = (List<UDDIRecord>) endpointManager.getUddiNaming().listRecords("A48_Restaurant%");
			System.out.println(rstRecords.size() + " restaurants were found.");

			for(UDDIRecord tmpRecord: rstRecords) {
				System.out.print(tmpRecord.getOrgName());
				RestaurantClient tmpClient = new RestaurantClient(endpointManager.getUddiNaming().getUDDIUrl(), tmpRecord.getOrgName());
				clientList.put(tmpRecord.getOrgName(), tmpClient);
			}

			hub.setRestaurantClients(clientList);

		} catch (Exception e) {
			String msg = String.format("Client failed lookup on UDDI at %s!", endpointManager.getUddiNaming().getUDDIUrl());
			throw new HubServerException(msg, e);
		}
	}

	public void loadPoints() throws HubServerException {
		try {
			HashMap<String, PointsClient> clientList = new HashMap<>();
			if (endpointManager.isVerbose())
				System.out.printf("Looking for points servers%n");
			List<UDDIRecord> ptsRecords = (List<UDDIRecord>) endpointManager.getUddiNaming().listRecords("A48_Points%");
			System.out.println(ptsRecords.size() + " point servers were found.");

			for(UDDIRecord tmpRecord: ptsRecords) {
				System.out.println(tmpRecord.getOrgName());
				PointsClient tmpClient = new PointsClient(endpointManager.getUddiNaming().getUDDIUrl(), tmpRecord.getOrgName());
				clientList.put(tmpRecord.getOrgName(), tmpClient);
			}

			PointsFrontEnd ptsFrontEnd = new PointsFrontEnd(clientList);
			hub.setPointsFrontEnd(ptsFrontEnd);

		} catch (Exception e) {
			String msg = String.format("Client failed lookup on UDDI at %s!", endpointManager.getUddiNaming().getUDDIUrl());
			throw new HubServerException(msg, e);
		}
	}

	public void loadCreditCard() throws HubServerException {
		try {
			CreditCardClient ccClient = new CreditCardClient("http://ws.sd.rnl.tecnico.ulisboa.pt:8080/cc");
			if(ccClient == null)
				throw new HubServerException("No credit card server was found", new Exception());

			hub.setCcClient(ccClient);
		} catch (Exception e) {
			String msg = String.format("Client failed lookup on UDDI at %s!", endpointManager.getUddiNaming().getUDDIUrl());
			throw new HubServerException(msg, e);
		}
	}

	// Control operations ----------------------------------------------------

	/** Diagnostic operation to check if service is running. */
	@Override
	public String ctrlPing(String inputMessage) {
		// If no input is received, return a default name.
		if (inputMessage == null || inputMessage.trim().length() == 0)
			inputMessage = "friend";

		// If the service does not have a name, return a default.
		String wsName = endpointManager.getWsName();
		if (wsName == null || wsName.trim().length() == 0)
			wsName = "Hub";

		// Build a string with a message to return.
		StringBuilder builder = new StringBuilder();
		builder.append("Hello ").append(inputMessage);
		builder.append(" from ").append(wsName);

		builder.append(hub.pingRestaurants(inputMessage));

		return builder.toString();
	}

	/** Return all variables to default values. */
	@Override
	public void ctrlClear() {
		hub.clear();
	}

	/** Set variables with specific values. */
	@Override
	public void ctrlInitFood(List<FoodInit> initialFoods) throws InvalidInitFault_Exception {
		List<Product> products = new ArrayList<>();

		for(FoodInit ini: initialFoods)
			products.add(buildProduct(ini));

		try {
			hub.initRestaurants(products);
		} catch(BadInitException e) {
			throwInvalidInit(e.getMessage());
		}

	}
	
	@Override
	public void ctrlInitUserPoints(int startPoints) throws InvalidInitFault_Exception {
		try {
			hub.initPoints(startPoints);
		} catch(BadInitException e) {
			throwInvalidInit(e.getMessage());
		}
	}

	// View helpers ----------------------------------------------------------

	/** Helper to convert to a domain object. */
	private Product buildProduct(FoodInit f) {
		Product p = new Product();
		p.setQuantity(f.getQuantity());
		Food food = f.getFood();
		FoodId fid = food.getId();
		p.setId(fid.getMenuId());
		p.setRestId(fid.getRestaurantId());
		p.setEntree(food.getEntree());
		p.setPlate(food.getPlate());
		p.setDessert(food.getDessert());
		p.setPreparationTime(food.getPreparationTime());
		p.setPrice(food.getPrice());
		return p;
	}

	private Food buildFood(Product p) {
		Food f = new Food();
		FoodId fid = new FoodId();
		fid.setRestaurantId(p.getRestId());
		fid.setMenuId(p.getId());
		f.setId(fid);
		f.setEntree(p.getEntree());
		f.setPlate(p.getPlate());
		f.setDessert(p.getDessert());
		f.setPrice(p.getPrice());
		f.setPreparationTime(p.getPreparationTime());
		return f;
	}

	private FoodOrder buildFoodOrder(String foodOrderId, List<FoodOrderItem> foodCart) {
		FoodOrderId id = new FoodOrderId();
		id.setId(foodOrderId);

		FoodOrder foodOrder = new FoodOrder();
		foodOrder.setFoodOrderId(id);

		foodOrder.getItems().addAll(foodCart);

		return foodOrder;
	}

	private FoodOrderItem buildFoodOrderItem(Product p) {
		FoodId fid = new FoodId();
		fid.setRestaurantId(p.getRestId());
		fid.setMenuId(p.getId());

		int quantity = p.getQuantity();

		FoodOrderItem foodOrderItem = new FoodOrderItem();
		foodOrderItem.setFoodId(fid);
		foodOrderItem.setFoodQuantity(quantity);
		return foodOrderItem;
	}
	
	// Exception helpers -----------------------------------------------------
	private void throwInvalidUser(final String message) throws InvalidUserIdFault_Exception {
		InvalidUserIdFault faultInfo = new InvalidUserIdFault();
		faultInfo.message = message;
		throw new InvalidUserIdFault_Exception(message, faultInfo);
	}

	private void throwInvalidCreditCard(final String message) throws InvalidCreditCardFault_Exception {
		InvalidCreditCardFault faultInfo = new InvalidCreditCardFault();
		faultInfo.message = message;
		throw new InvalidCreditCardFault_Exception(message, faultInfo);
	}

	private void throwEmptyCart(final String message) throws EmptyCartFault_Exception {
		EmptyCartFault faultInfo = new EmptyCartFault();
		faultInfo.message = message;
		throw new EmptyCartFault_Exception(message, faultInfo);
	}

	private void throwInvalidFoodId(final String message) throws InvalidFoodIdFault_Exception {
		InvalidFoodIdFault faultInfo = new InvalidFoodIdFault();
		faultInfo.message = message;
		throw new InvalidFoodIdFault_Exception(message, faultInfo);
	}

	private void throwInvalidFoodQuantity(final String message) throws InvalidFoodQuantityFault_Exception {
		InvalidFoodQuantityFault faultInfo = new InvalidFoodQuantityFault();
		faultInfo.message = message;
		throw new InvalidFoodQuantityFault_Exception(message, faultInfo);
	}

	private void throwInvalidInit(final String message) throws InvalidInitFault_Exception {
		InvalidInitFault faultInfo = new InvalidInitFault();
		faultInfo.message = message;
		throw new InvalidInitFault_Exception(message, faultInfo);
	}

	private void throwInvalidMoney(final String message) throws InvalidMoneyFault_Exception {
		InvalidMoneyFault faultInfo = new InvalidMoneyFault();
		faultInfo.message = message;
		throw new InvalidMoneyFault_Exception(message, faultInfo);
	}

	private void throwInvalidText(final String message) throws InvalidTextFault_Exception {
		InvalidTextFault faultInfo = new InvalidTextFault();
		faultInfo.message = message;
		throw new InvalidTextFault_Exception(message, faultInfo);
	}

	private void throwNotEnoughPoints(final String message) throws NotEnoughPointsFault_Exception {
		NotEnoughPointsFault faultInfo = new NotEnoughPointsFault();
		faultInfo.message = message;
		throw new NotEnoughPointsFault_Exception(message, faultInfo);
	}
}
