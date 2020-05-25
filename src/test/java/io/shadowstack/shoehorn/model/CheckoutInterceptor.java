package io.shadowstack.shoehorn.model;

import io.shadowstack.shoehorn.AdapterException;
import io.shadowstack.shoehorn.MethodForwardingInterceptor;

public class CheckoutInterceptor implements MethodForwardingInterceptor {
    @Override
    public Object intercept(Object[] inputs, Object adaptedInstance, Object result) throws AdapterException {
        PhysicalCart physicalCart = (PhysicalCart) inputs[0];
        // Your kid slipped these into the basket when you weren't looking.
        physicalCart.getProducts().add(new PhysicalProduct("M&Ms", 1.99D));
        if(result != null) { // we're being called after the adapted instance's invocation
            Receipt receipt = (Receipt) result;
            receipt.setTotal(receipt.getTotal() + 0.99D); // sales tax!
        }
        return result;
    }
}
