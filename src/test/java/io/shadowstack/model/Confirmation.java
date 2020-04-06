package io.shadowstack.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Confirmation {
    private double total;
    private String customerEmail;
    private LocalDateTime estimatedDelivery;
}
