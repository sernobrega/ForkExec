package com.forkexec.hub.ws.it;

import com.forkexec.hub.ws.*;
import org.junit.*;

import java.util.ArrayList;
import java.util.List;

public class ClearCartIT extends BaseIT {

    FoodId fid1;
    FoodId fid2;
    FoodId fid3;
    String userEmail = "sd2019@tecnico.ulisboa.pt";

    @Before
    public void populate4Test() throws InvalidInitFault_Exception, InvalidUserIdFault_Exception {
        Food food1 = new Food();
        fid1 = new FoodId();

        fid1.setMenuId("1");
        fid1.setRestaurantId("A48_Restaurant1");

        food1.setId(fid1);
        food1.setEntree("Entree1");
        food1.setPlate("Plate1");
        food1.setDessert("Dessert1");
        food1.setPrice(10);
        food1.setPreparationTime(5);

        FoodInit fini1 = new FoodInit();
        fini1.setFood(food1);
        fini1.setQuantity(20);

        //FoodInit2
        Food food2 = new Food();
        fid2 = new FoodId();

        fid2.setMenuId("2");
        fid2.setRestaurantId("A48_Restaurant1");

        food2.setId(fid2);
        food2.setEntree("Entree2");
        food2.setPlate("Plate2");
        food2.setDessert("Dessert2");
        food2.setPrice(4);
        food2.setPreparationTime(50);

        FoodInit fini2 = new FoodInit();
        fini2.setFood(food2);
        fini2.setQuantity(5);

        Food food3 = new Food();
        fid3 = new FoodId();

        fid3.setMenuId("1");
        fid3.setRestaurantId("A48_Restaurant2");

        food3.setId(fid3);
        food3.setEntree("Entree3");
        food3.setPlate("Plate3");
        food3.setDessert("Dessert3");
        food3.setPrice(10);
        food3.setPreparationTime(5);

        FoodInit fini3 = new FoodInit();
        fini3.setFood(food3);
        fini3.setQuantity(50);

        List<FoodInit> initialMenu = new ArrayList<>();
        initialMenu.add(fini1);
        initialMenu.add(fini2);
        initialMenu.add(fini3);

        client.ctrlInitFood(initialMenu);
        client.activateAccount(userEmail);
    }

    @Test
    public void successfulClear() throws InvalidFoodIdFault_Exception, InvalidUserIdFault_Exception, InvalidFoodQuantityFault_Exception  {
        client.addFoodToCart(userEmail, fid1, 1);
        client.addFoodToCart(userEmail, fid2, 5);
        client.addFoodToCart(userEmail, fid3, 3);
        client.clearCart(userEmail);
        Assert.assertEquals(client.cartContents(userEmail).size(), 0);
    }

    @Test(expected = InvalidUserIdFault_Exception.class)
    public void nullUserId() throws InvalidUserIdFault_Exception  {
        client.clearCart(null);
    }

    @Test(expected = InvalidUserIdFault_Exception.class)
    public void emptyUserId() throws InvalidUserIdFault_Exception  {
        client.clearCart("");
    }

    @Test(expected = InvalidUserIdFault_Exception.class)
    public void whiteSpaceUserID() throws InvalidUserIdFault_Exception  {
        client.clearCart("    ");
    }

    @Test(expected = InvalidUserIdFault_Exception.class)
    public void wrongFormatUserId() throws InvalidUserIdFault_Exception {
        client.clearCart("sd@tecn");
    }

    @After
    public void clear() {
        client.ctrlClear();
    }
}
