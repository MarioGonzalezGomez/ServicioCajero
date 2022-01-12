import DB.MongoController;
import server.GestionCliente;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
//TODO: Hacer los log
//TODO: Hacer el cifrado SHA512
//TODO: Establecer el límite de retirada diaria
//TODO: Ligar con la base de datos mongoDB
public class Servidor {
    public static void main(String[] args) throws IOException {
        ServerSocket servidor = null;
        Socket cliente;
        int puerto = 12345;

        System.out.println("Arrancando el servicio de cajero automático");

        MongoController controller = MongoController.getInstance();

        try {
            servidor = new ServerSocket(puerto);
            while (true) {
                System.out.println("Esperando usuarios...");
                cliente = servidor.accept();

                System.out.println("Peticion de cliente -> " + cliente.getInetAddress() + " --- " + cliente.getPort());

                GestionCliente gc = new GestionCliente(cliente, controller);
                gc.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            servidor.close();
        }


    }
}
