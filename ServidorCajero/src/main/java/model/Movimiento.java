package model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class Movimiento {
    private LocalDateTime fecha;
    private tipoMovimiento tipo;
    private double cantidad;
    private String usuario; //que será su email

    public static enum tipoMovimiento {
        RETIRADA, INGRESO, CONSULTA, SALIR
    }
}
