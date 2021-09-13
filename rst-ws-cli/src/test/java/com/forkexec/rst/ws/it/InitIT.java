package com.forkexec.rst.ws.it;

import com.forkexec.rst.ws.BadInitFault_Exception;
import com.forkexec.rst.ws.Menu;
import com.forkexec.rst.ws.MenuId;
import com.forkexec.rst.ws.MenuInit;
import org.junit.After;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class InitIT extends BaseIT {

    @Test
    public void successInit() throws BadInitFault_Exception {
        MenuId mid = new MenuId();
        mid.setId("01");

        Menu menu = new Menu();
        menu.setId(mid);
        menu.setEntree("Entree1");
        menu.setPlate("Plate1");
        menu.setDessert("Dessert1");
        menu.setPrice(20);
        menu.setPreparationTime(15);

        MenuInit menui = new MenuInit();
        menui.setMenu(menu);
        menui.setQuantity(580);

        List<MenuInit> initialMenus = new ArrayList<>();
        initialMenus.add(menui);

        client.ctrlInit(initialMenus);
    }

    @Test(expected = BadInitFault_Exception.class)
    public void emptyInitialMenus() throws BadInitFault_Exception {
        List<MenuInit> initialMenus = new ArrayList<>();
        client.ctrlInit(initialMenus);
    }

    @Test(expected = BadInitFault_Exception.class)
    public void nullInitialMenus() throws BadInitFault_Exception {
        client.ctrlInit(null);
    }

    @After
    public void clear() {
        client.ctrlClear();
    }
}
