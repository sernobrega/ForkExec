package com.forkexec.rst.ws.it;

import com.forkexec.rst.ws.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class SearchMenuIT extends BaseIT {

    @Before
    public void populate4Test() throws BadInitFault_Exception {
        MenuId mid1 = new MenuId();
        mid1.setId("01");
        MenuId mid2 = new MenuId();
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
    public void successSearchBothMenu() throws BadTextFault_Exception {
        List<Menu> menus = client.searchMenus("Entree");
        Assert.assertEquals(menus.size(), 2);
    }

    @Test
    public void successSearchOneMenu() throws BadTextFault_Exception {
        List<Menu> menus = client.searchMenus("1");
        Assert.assertEquals(menus.size(), 1);
    }

    @Test(expected = BadTextFault_Exception.class)
    public void emptyDescription() throws BadTextFault_Exception {
        client.searchMenus("");
    }

    @Test(expected = BadTextFault_Exception.class)
    public void nullDescription() throws BadTextFault_Exception {
        client.searchMenus(null);
    }

    @Test
    public void noDescriptionMatch() throws BadTextFault_Exception {
        List<Menu> menus = client.searchMenus("SD");
        Assert.assertEquals(menus.size(), 0);
    }

    @After
    public void clear() {
        client.ctrlClear();
    }
}
