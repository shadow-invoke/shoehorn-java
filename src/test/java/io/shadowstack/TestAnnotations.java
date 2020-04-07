package io.shadowstack;

import io.shadowstack.model.*;
import io.shadowstack.service.Microwave;
import org.junit.jupiter.api.Test;

import static io.shadowstack.Fluently.shoehorn;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestAnnotations {
    @Test
    public void testMimic() throws AdapterException {
        Confirmation expected = new Confirmation(2.99D + 3.99D, null, null);
        VirtualCart virtualCart = new VirtualCart();
        virtualCart.getProducts().add(new VirtualProduct("eggs", 2.99D, 1, 1));
        virtualCart.getProducts().add(new VirtualProduct("milk", 3.99D, 1, 1));
        RetailCashier cashier = new RetailCashier("Pat");
        RetailWebsite website = shoehorn(cashier).into(RetailWebsite.class).build();
        Confirmation confirmation = website.checkout(virtualCart);
        assertEquals(expected, confirmation);
    }

    @Test
    public void testBadConverters() throws AdapterException {
        Confirmation expected = new Confirmation(2.99D + 3.99D, null, null);
        VirtualCart virtualCart = new VirtualCart();
        virtualCart.getProducts().add(new VirtualProduct("eggs", 2.99D, 1, 1));
        virtualCart.getProducts().add(new VirtualProduct("milk", 3.99D, 1, 1));
        RetailCashier2 cashier = new RetailCashier2("Pat");
        assertThrows(AdapterException.class, () -> shoehorn(cashier).into(RetailWebsite.class).build());
    }
    @Test
    public void testCustomInstanceAccessors() throws AdapterException {
        Confirmation expected = new Confirmation(2.99D + 3.99D, null, null);
        VirtualCart virtualCart = new VirtualCart();
        virtualCart.getProducts().add(new VirtualProduct("eggs", 2.99D, 1, 1));
        virtualCart.getProducts().add(new VirtualProduct("milk", 3.99D, 1, 1));
        RetailCashier3 cashier = new RetailCashier3("Pat");
        RetailWebsite website = shoehorn(cashier).into(RetailWebsite.class).build();
        Confirmation confirmation = website.checkout(virtualCart);
        assertEquals(expected, confirmation);
    }
}
