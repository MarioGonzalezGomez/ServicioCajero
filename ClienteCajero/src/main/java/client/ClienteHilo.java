package client;

import cifrador.Cifrador;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class ClienteHilo extends Thread {
    private Socket server;
    private DataOutputStream dos;
    private DataInputStream dis;
    private Scanner sc;

    public ClienteHilo(InetAddress direccion, int puerto) throws IOException {
        server = new Socket(direccion, puerto);
        dos = new DataOutputStream(server.getOutputStream());
        dis = new DataInputStream(server.getInputStream());
        sc = new Scanner(System.in);
    }

    @Override
    public void run() {
        String respuesta = "pruebe de nuevo";
        boolean salir = false;
        int option;
        Cifrador cifrador = new Cifrador();
        try {
            //Recibir mensaje de introducir email y enviarlo
            System.out.println(dis.readUTF());
            while (respuesta.contains("pruebe")) {
                dos.writeUTF(sc.nextLine());
                respuesta = dis.readUTF();
                System.out.println(respuesta);
            }
            respuesta = "pruebe de nuevo";
            System.out.println(dis.readUTF());
            while (respuesta.contains("pruebe")) {
                dos.writeUTF(cifrador.get_SHA_512_SecurePassword(sc.nextLine()));
                respuesta = dis.readUTF();
                System.out.println(respuesta);
            }

            while (!salir) {
                System.out.println(dis.readUTF());
                option = sc.nextInt();
                dos.writeInt(option);
                switch (option) {
                    case 1:
                    case 2:
                        System.out.println(dis.readUTF());
                        dos.writeDouble(sc.nextDouble());
                        System.out.println(dis.readUTF());
                        break;

                    case 3:
                        System.out.println(dis.readUTF());
                        break;

                    case 4:
                        System.out.println(dis.readUTF());
                        salir = true;
                        break;

                    default:
                        System.out.println(dis.readUTF());
                        break;
                }

            }


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
