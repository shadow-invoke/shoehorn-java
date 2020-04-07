package io.shadowstack.model;

import io.shadowstack.Convert;
import io.shadowstack.Mimic;
import io.shadowstack.service.Receipt2Confirmation3;
import io.shadowstack.service.VirtualCart2PhysicalCart3;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RetailCashier5 {
    private String name;

    @Mimic(type = RetailWebsite.class, method = "checkout")
    @Convert(to = Confirmation.class, use = Receipt2Confirmation3.class, singletonInstance = "SINGLETON")
    public Receipt checkout(@Convert(to = VirtualCart.class, use = VirtualCart2PhysicalCart3.class, factoryMethod = "getFoo") PhysicalCart cart) {
        double total = 0.0D;
        if(cart != null) {
            for(PhysicalProduct p : cart.getProducts()) {
                total += p.getPrice();
            }
        }
        return new Receipt(total, this.name);
    }
}
