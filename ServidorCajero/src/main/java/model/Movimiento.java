package model;

import java.time.LocalDateTime;

public class Movimiento {
    private LocalDateTime fecha;
    private tipoMovimiento tipo;
    private double cantidad;
    private String usuario; //que será su email

    private enum tipoMovimiento {
        RETIRADA, INGRESO, CONSULTA, SALIR
    }
}
