package io.shadowstack;

import io.shadowstack.model.*;
import org.junit.jupiter.api.Test;

import static io.shadowstack.Fluently.shoehorn;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
