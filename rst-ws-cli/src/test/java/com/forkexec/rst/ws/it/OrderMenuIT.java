package com.forkexec.rst.ws.it;

import com.forkexec.rst.ws.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class OrderMenuIT extends BaseIT {

    private MenuId mid1;
    private MenuId mid2;

    @Before
    public void populate4Test() throws BadInitFault_Exception {
        mid1 = new MenuId();
        mid1.setId("01");
        mid2 = new MenuId();
        mid2.setId("02");

        Menu menu1 = new Menu();
        menu1.setId(mid1);
        menu1.setEntree("Entree1");
        menu1.setPlate("Plate1");
        menu1.setDessert("Dessert1");
        menu1.setPrice(20);
        menu1.setPreparationTime(15);

        Menu menu2 = new Menu();
        menu2.setId(mid2);
        menu2.setEntree("Entree2");
        menu2.setPlate("Plate2");
        menu2.setDessert("Dessert2");
        menu2.setPrice(15);
        menu2.setPreparationTime(10);

        MenuInit menui1 = new MenuInit();
        menui1.setMenu(menu1);
        menui1.setQuantity(580);

        MenuInit menui2 = new MenuInit();
        menui2.setMenu(menu2);
        menui2.setQuantity(120);

        List<MenuInit> initialMenus = new ArrayList<>();
        initialMenus.add(menui1);
        initialMenus.add(menui2);

        client.ctrlInit(initialMenus);
    }

    @Test
    public void successOrderMenu() throws BadMenuIdFault_Exception, BadQuantityFault_Exception, InsufficientQuantityFault_Exception {
        MenuOrder menu = client.orderMenu(mid1, 1);
        Assert.assertEquals(mid1.getId(), menu.getMenuId().getId());
    }

    @Test(expected = BadMenuIdFault_Exception.class)
    public void wrongMenuId() throws BadMenuIdFault_Exception, BadQuantityFault_Exception, InsufficientQuantityFault_Exception {
        MenuId mid3 = new MenuId();
        mid3.setId("03");
        client.orderMenu(mid3, 1);
    }

    @Test(expected = BadQuantityFault_Exception.class)
    public void badQuantityOrder() throws BadMenuIdFault_Exception, BadQuantityFault_Exception, InsufficientQuantityFault_Exception {
        client.orderMenu(mid1, 0);
    }

    @Test(expected = BadQuantityFault_Exception.class)
    public void badQuantityOrder_2() throws BadMenuIdFault_Exception, BadQuantityFault_Exception, InsufficientQuantityFault_Exception {
        client.orderMenu(mid1, -1);
    }

    @Test(expected = InsufficientQuantityFault_Exception.class)
    public void tooMuchOrder() throws BadMenuIdFault_Exception, BadQuantityFault_Exception, InsufficientQuantityFault_Exception {
        client.orderMenu(mid1, 1000);
    }

    @Test(expected = InsufficientQuantityFault_Exception.class)
    public void firstOneValidSecondTooMuch() throws BadMenuIdFault_Exception, BadQuantityFault_Exception, InsufficientQuantityFault_Exception {
        client.orderMenu(mid1, 300);
        client.orderMenu(mid1, 800);
    }

    @Test
    public void incrementOrderNo() throws BadMenuIdFault_Exception, BadQuantityFault_Exception, InsufficientQuantityFault_Exception {
        MenuOrder menu = client.orderMenu(mid1, 1);
        Assert.assertEquals(mid1.getId(), menu.getMenuId().getId());

        MenuOrder menu2 = client.orderMenu(mid1, 1);
        Assert.assertEquals(mid1.getId(), menu2.getMenuId().getId());

        Assert.assertTrue(Integer.parseInt(menu.getId().getId()) == Integer.parseInt(menu2.getId().getId()) - 1 );
    }

    @After
    public void clear() {
        client.ctrlClear();
    }
}
