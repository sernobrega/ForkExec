package com.forkexec.rst.ws.it;

import com.forkexec.rst.ws.*;
import org.junit.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that tests getMenu operation
 */
public class GetMenuIT extends BaseIT {

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
    public void successGetMenu() throws BadMenuIdFault_Exception {
        Menu res = client.getMenu(mid1);
        Assert.assertEquals(res.getId().getId(), mid1.getId());
    }

    @Test(expected = BadMenuIdFault_Exception.class)
    public void badMenuId() throws BadMenuIdFault_Exception {
        MenuId badId = new MenuId();
        badId.setId("03");
        client.getMenu(badId);
    }

    @Test(expected = BadMenuIdFault_Exception.class)
    public void nullMenuId() throws BadMenuIdFault_Exception {
        client.getMenu(null);
    }

    @Test
    public void successDoubleGetMenu() throws BadMenuIdFault_Exception {
        Menu res = client.getMenu(mid2);
        Assert.assertEquals(res.getId().getId(), mid2.getId());

        Menu res2 = client.getMenu(mid1);
        Assert.assertEquals(res2.getId().getId(), mid1.getId());
    }

    @After
    public void clear() {
        client.ctrlClear();
    }

}
