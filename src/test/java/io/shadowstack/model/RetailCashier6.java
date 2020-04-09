package io.shadowstack.model;

import io.shadowstack.AdapterAdvice;
import io.shadowstack.Convert;
import io.shadowstack.Mimic;
import io.shadowstack.service.Receipt2Confirmation;
import io.shadowstack.service.VirtualCart2PhysicalCart;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RetailCashier6 {
    private String name;

    @Mimic(type = RetailWebsite.class, method = "checkout")
    @Convert(to = Confirmation.class, use = Receipt2Confirmation.class)
    @AdapterAdvice(pointcut = AdapterAdvice.Pointcut.BEFORE, interceptor = CheckoutInterceptor.class)
    @AdapterAdvice(pointcut = AdapterAdvice.Pointcut.AFTER, interceptor = CheckoutInterceptor.class)
    public Receipt checkout(@Convert(to = VirtualCart.class, use = VirtualCart2PhysicalCart.class) PhysicalCart cart) {
        double total = 0.0D;
        if(cart != null) {
            for(PhysicalProduct p : cart.getProducts()) {
                total += p.getPrice();
            }
        }
        return new Receipt(total, this.name);
    }
}
