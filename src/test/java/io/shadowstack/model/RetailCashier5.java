package io.shadowstack.model;

import io.shadowstack.In;
import io.shadowstack.Mimic;
import io.shadowstack.Out;
import io.shadowstack.service.Receipt2Confirmation3;
import io.shadowstack.service.VirtualCart2PhysicalCart3;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RetailCashier5 {
    private String name;

    @Mimic(type = RetailWebsite.class, method = "checkout")
    @Out(to = Confirmation.class, with = Receipt2Confirmation3.class, singletonMember = "SINGLETON")
    public Receipt checkout(@In(from = VirtualCart.class, with = VirtualCart2PhysicalCart3.class, factoryMethods = "getFoo") PhysicalCart cart) {
        double total = 0.0D;
        if(cart != null) {
            for(PhysicalProduct p : cart.getProducts()) {
                total += p.getPrice();
            }
        }
        return new Receipt(total, this.name);
    }
}
