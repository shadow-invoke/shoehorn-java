package io.shadowstack.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RetailWebsite {
    private String currentCustomerEmail;

    public Confirmation checkout(VirtualCart cart) {
        // Does not calculate a total; mimicking class does this for us.
        return new Confirmation(0.0D, this.currentCustomerEmail, LocalDateTime.now().plusDays(2));
    }
}
