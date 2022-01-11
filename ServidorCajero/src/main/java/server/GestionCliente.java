package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class GestionCliente extends Thread {
    private Socket cliente;
    private DataOutputStream dos;
    private DataInputStream dis;

    public GestionCliente(Socket cliente) throws IOException {
        this.cliente = cliente;
        dos = new DataOutputStream(cliente.getOutputStream());
        dis = new DataInputStream(cliente.getInputStream());
    }

    @Override
    public void run() {
        String email;
        int option;
        double cantidad;
        boolean salir = false;
        try {
            //no olvidar ezcribir en el log estos mensajes
            dos.writeUTF("Introduzca su email");
            email = dis.readUTF();
            //if(email.isCorrect())
            dos.writeUTF("Introduzca su pin de 4 dígitos");
            //if(pin.isCorrect())
            while (!salir) {
                dos.writeUTF("¿Qué operación desea realizar:\n1.\tRetirar efectivo\n2.\tIngresar dinero\n3.\tConsultar saldo\n4.\tSalir");
                option = dis.readInt();
                switch (option) {
                    case 1:
                        dos.writeUTF("Qué cantidad desea retirar");
                        cantidad = dis.readDouble();
                        retirarEfectivo(cantidad);
                        break;
                    case 2:
                        dos.writeUTF("Qué cantidad desea ingresar");
                        cantidad = dis.readDouble();
                        ingresarDinero(cantidad);
                        break;
                    case 3:
                        consultarSaldo(email);
                        break;
                    case 4:
                        dos.writeUTF("Gracias por utilizar nuestro servicio, adiós");
                        salir = true;
                        break;
                    default:
                        dos.writeUTF("La opción seleccionada no es correcta. Introduzca 1 para retirar, 2 para ingresar , 3 para consultar o 4 para salir");
                        break;
                }
            }
            //else{pin incorrecto
            //else{email incorrecto, y que vuelva arriba. Utilizar un booleano?

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                cliente.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void consultarSaldo(String email) {
    }

    private void ingresarDinero(double cantidad) {
    }

    private void retirarEfectivo(double cantidad) {
    }
}
