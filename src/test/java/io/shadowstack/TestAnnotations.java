package io.shadowstack;

import io.shadowstack.model.*;
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
    public void testBadConverters() {
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

    @Test
    public void testBadCustomInstanceAccessors() {
        RetailCashier4 cashier4 = new RetailCashier4("Pat");
        assertThrows(AdapterException.class, () -> shoehorn(cashier4).into(RetailWebsite.class).build());
        RetailCashier5 cashier5 = new RetailCashier5("Pat");
        assertThrows(AdapterException.class, () -> shoehorn(cashier5).into(RetailWebsite.class).build());
    }

    @Test
    public void testAdapterAdvice() throws AdapterException {
        // Before and after interceptors will add to the total
        Confirmation expected = new Confirmation(2.99D + 3.99D + 1.99D + 0.99D, null, null);
        VirtualCart virtualCart = new VirtualCart();
        virtualCart.getProducts().add(new VirtualProduct("eggs", 2.99D, 1, 1));
        virtualCart.getProducts().add(new VirtualProduct("milk", 3.99D, 1, 1));
        RetailCashier6 cashier = new RetailCashier6("Pat");
        RetailWebsite website = shoehorn(cashier).into(RetailWebsite.class).build();
        Confirmation confirmation = website.checkout(virtualCart);
        assertEquals(expected, confirmation);
    }

    @Test
    public void testBadAdapterAdvice() {
        RetailCashier7 cashier = new RetailCashier7("Pat");
        assertThrows(AdapterException.class, () -> shoehorn(cashier).into(RetailWebsite.class).build());
    }
}
