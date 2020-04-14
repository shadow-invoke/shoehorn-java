package io.shadowstack.model;

import io.shadowstack.AdapterAdvice;
import io.shadowstack.In;
import io.shadowstack.Mimic;
import io.shadowstack.Out;
import io.shadowstack.service.Receipt2Confirmation;
import io.shadowstack.service.VirtualCart2PhysicalCart;
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
