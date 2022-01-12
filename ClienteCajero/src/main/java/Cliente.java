import client.ClienteHilo;

import java.io.IOException;
import java.net.InetAddress;

public class Cliente {
    public static void main(String[] args) throws IOException {
        InetAddress direccion;
        int puerto = 12345;

        direccion = InetAddress.getLocalHost();
        System.out.println("Conectando al servidor...");
        ClienteHilo cliente = new ClienteHilo(direccion, puerto);
        cliente.start();

    }
}
