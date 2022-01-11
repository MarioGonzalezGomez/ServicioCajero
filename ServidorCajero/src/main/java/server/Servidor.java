package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
    public static void main(String[] args) throws IOException {
        ServerSocket servidor = null;
        Socket cliente;
        int puerto = 12345;

        System.out.println("Arrancando el servicio de cajero automático");


        try {
            servidor = new ServerSocket(puerto);
            while (true) {
                System.out.println("Esperando usuarios...");
                cliente = servidor.accept();

                System.out.println("Peticion de cliente -> " + cliente.getInetAddress() + " --- " + cliente.getPort());

                GestionCliente gc = new GestionCliente(cliente);
                gc.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            servidor.close();
        }


    }
}
