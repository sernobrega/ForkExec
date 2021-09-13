package com.forkexec.rst.ws.it;

import com.forkexec.rst.ws.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ClearIT extends BaseIT {

    @Test(expected = BadMenuIdFault_Exception.class)
    public void successClear() throws BadInitFault_Exception, BadMenuIdFault_Exception {
        MenuId mid1 = new MenuId();
        mid1.setId("01");

        Menu menu1 = new Menu();
        menu1.setId(mid1);
        menu1.setEntree("Entree1");
        menu1.setPlate("Plate1");
        menu1.setDessert("Dessert1");
        menu1.setPrice(20);
        menu1.setPreparationTime(15);

        MenuInit menui1 = new MenuInit();
        menui1.setMenu(menu1);
        menui1.setQuantity(580);

        List<MenuInit> initialMenus = new ArrayList<>();
        initialMenus.add(menui1);

        client.ctrlInit(initialMenus);

        Menu res = client.getMenu(mid1);
        Assert.assertEquals(res.getId().getId(), mid1.getId());

        client.ctrlClear();
        client.getMenu(mid1);
    }
}
