package io.shadowstack.shoehorn.model;

import io.shadowstack.shoehorn.In;
import io.shadowstack.shoehorn.Mimic;
import io.shadowstack.shoehorn.Out;
import io.shadowstack.shoehorn.service.Receipt2Confirmation2;
import io.shadowstack.shoehorn.service.VirtualCart2PhysicalCart2;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RetailCashier2 {
    private String name;

    @Mimic(type = RetailWebsite.class, method = "checkout")
    @Out(to = Confirmation.class, with = Receipt2Confirmation2.class)
    public Receipt checkout(@In(from = VirtualCart.class, with = VirtualCart2PhysicalCart2.class) PhysicalCart cart) {
        double total = 0.0D;
        if(cart != null) {
            for(PhysicalProduct p : cart.getProducts()) {
                total += p.getPrice();
            }
        }
        return new Receipt(total, this.name);
    }
}
