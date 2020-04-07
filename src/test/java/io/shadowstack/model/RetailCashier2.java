package io.shadowstack.model;

import io.shadowstack.Convert;
import io.shadowstack.Mimic;
import io.shadowstack.service.Receipt2Confirmation2;
import io.shadowstack.service.VirtualCart2PhysicalCart2;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RetailCashier2 {
    private String name;

    @Mimic(type = RetailWebsite.class, method = "checkout")
    @Convert(to = Confirmation.class, use = Receipt2Confirmation2.class)
    public Receipt checkout(@Convert(to = VirtualCart.class, use = VirtualCart2PhysicalCart2.class) PhysicalCart cart) {
        double total = 0.0D;
        if(cart != null) {
            for(PhysicalProduct p : cart.getProducts()) {
                total += p.getPrice();
            }
        }
        return new Receipt(total, this.name);
    }
}
