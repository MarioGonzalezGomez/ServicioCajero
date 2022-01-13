import DB.MongoController;
import log4j.HandlerLog4j;
import server.GestionCliente;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

//TODO: Utilizar los log
//TODO: Ligar con la base de datos mongoDB
//TODO: Poner como servicio
public class Servidor {
    public static void main(String[] args) throws IOException {
        ServerSocket servidor = null;
        Socket cliente;
        int puerto = 12345;
        HandlerLog4j.registrarInfo(Servidor.class, HandlerLog4j.TipoLog.INFO, "Arrancando el servicio de cajero automático");
        System.out.println("Arrancando el servicio de cajero automático");


        MongoController controller = MongoController.getInstance();

        try {
            servidor = new ServerSocket(puerto);
            while (true) {
                HandlerLog4j.registrarInfo(Servidor.class, HandlerLog4j.TipoLog.INFO, "Esperando usuarios...");
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
