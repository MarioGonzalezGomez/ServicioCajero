package model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Usuario {
    private String email;
    private int pin;
    private double saldo;
    private double limite;
}
