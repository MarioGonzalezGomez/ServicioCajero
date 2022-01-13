package model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class Usuario {
    private String email;
    private String pin;
    private double saldo;
    private double limite;
    private double retiradoHoy;
}
