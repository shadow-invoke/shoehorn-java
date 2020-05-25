package io.shadowstack.shoehorn.model;

import io.shadowstack.shoehorn.AdapterAdvice;
import io.shadowstack.shoehorn.In;
import io.shadowstack.shoehorn.Mimic;
import io.shadowstack.shoehorn.Out;
import io.shadowstack.shoehorn.service.Receipt2Confirmation;
import io.shadowstack.shoehorn.service.VirtualCart2PhysicalCart;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RetailCashier8 {
    private String name;

    @Mimic(type = RetailWebsite.class, method = "nope")
    @Out(to = Confirmation.class, with = Receipt2Confirmation.class)
    @AdapterAdvice(pointcut = AdapterAdvice.Pointcut.BEFORE, interceptor = BadCheckoutInterceptor.class)
    @AdapterAdvice(pointcut = AdapterAdvice.Pointcut.AFTER, interceptor = BadCheckoutInterceptor.class)
    public Receipt checkout(@In(from = VirtualCart.class, with = VirtualCart2PhysicalCart.class) PhysicalCart cart) {
        double total = 0.0D;
        if(cart != null) {
            for(PhysicalProduct p : cart.getProducts()) {
                total += p.getPrice();
            }
        }
        return new Receipt(total, this.name);
    }
}
