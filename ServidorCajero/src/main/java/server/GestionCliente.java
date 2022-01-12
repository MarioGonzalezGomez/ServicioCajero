package server;

import DB.MongoController;
import com.mongodb.client.MongoCollection;
import model.Movimiento;
import model.Usuario;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class GestionCliente extends Thread {
    private Socket cliente;
    private Usuario user;
    private Movimiento movimiento;
    private DataOutputStream dos;
    private DataInputStream dis;
    private MongoController controller;

    public GestionCliente(Socket cliente, MongoController controller) throws IOException {
        this.cliente = cliente;
        this.controller = controller;
        dos = new DataOutputStream(cliente.getOutputStream());
        dis = new DataInputStream(cliente.getInputStream());
    }

    @Override
    public void run() {
        String email;
        int option;
        double cantidad;
        boolean emailCorrecto = false;
        boolean pinCorrecto = false;
        boolean salir = false;
        try {
            //no olvidar escribir en el log estos mensajes

            controller.open();
            MongoCollection<Usuario> userCollection = controller.getCollection("test", "usuarios", Usuario.class);
            MongoCollection<Movimiento> movimientoCollection = controller.getCollection("test", "movimientos", Movimiento.class);

            dos.writeUTF("Introduzca su email");
            email = dis.readUTF();
            while (!emailCorrecto) {
                emailCorrecto = comprobarEmail(dis.readUTF(), userCollection);
                if (!emailCorrecto) {
                    dos.writeUTF("El email no es correcto o no está registrado, pruebe de nuevo");
                }
            }
            dos.writeUTF("Introduzca su pin de 4 dígitos");
            while (!pinCorrecto) {
                pinCorrecto = comprobarPin(dis.readInt());
                if (!pinCorrecto) {
                    dos.writeUTF("El pin no es correcto, pruebe de nuevo");
                }
            }
            List<Movimiento> movimientos = movimientoCollection.find(eq("usuario", user.getEmail())).into(new ArrayList<>());
            while (!salir) {
                dos.writeUTF("¿Qué operación desea realizar:\n1.\tRetirar efectivo\n2.\tIngresar dinero\n3.\tConsultar saldo\n4.\tSalir");
                option = dis.readInt();
                switch (option) {
                    case 1:
                        dos.writeUTF("Qué cantidad desea retirar");
                        cantidad = dis.readDouble();
                        retirarEfectivo(cantidad, movimientos, movimientoCollection, dos);
                        break;
                    case 2:
                        dos.writeUTF("Qué cantidad desea ingresar");
                        cantidad = dis.readDouble();
                        ingresarDinero(cantidad, movimientos, movimientoCollection, dos);
                        break;
                    case 3:
                        dos.writeUTF("El usuario " + user.getEmail() + " tiene un saldo de: " + consultarSaldo(movimientos, movimientoCollection));
                        break;
                    case 4:
                        salir(movimientos, movimientoCollection);
                        dos.writeUTF("Gracias por utilizar nuestro servicio, adiós");
                        salir = true;
                        break;
                    default:
                        dos.writeUTF("La opción seleccionada no es correcta. Introduzca 1 para retirar, 2 para ingresar , 3 para consultar o 4 para salir");
                        break;

                }
            }

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


    private void ingresarDinero(double cantidad, List<Movimiento> movimientos, MongoCollection<Movimiento> movimientoCollection, DataOutputStream dos) {
        if (cantidad > 0) {
            user.setSaldo(user.getSaldo() + cantidad);
            movimiento = new Movimiento();
            movimiento.setCantidad(cantidad);
            movimiento.setFecha(LocalDateTime.now());
            movimiento.setTipo(Movimiento.tipoMovimiento.INGRESO);
            movimiento.setUsuario(user.getEmail());

            movimientos.add(movimiento);
            movimientoCollection.insertOne(movimiento);
        }
    }

    private void retirarEfectivo(double cantidad, List<Movimiento> movimientos, MongoCollection<Movimiento> movimientoCollection, DataOutputStream dos) {
        if (cantidad > 0 && user.getSaldo() - cantidad >= 0) {
            user.setSaldo(user.getSaldo() - cantidad);
            movimiento = new Movimiento();
            movimiento.setCantidad(cantidad);
            movimiento.setFecha(LocalDateTime.now());
            movimiento.setTipo(Movimiento.tipoMovimiento.RETIRADA);
            movimiento.setUsuario(user.getEmail());

            movimientos.add(movimiento);
            movimientoCollection.insertOne(movimiento);
        }
    }

    private double consultarSaldo(List<Movimiento> movimientos, MongoCollection<Movimiento> movimientoCollection) {
        movimiento = new Movimiento();
        // movimiento.setCantidad(cantidad);
        movimiento.setFecha(LocalDateTime.now());
        movimiento.setTipo(Movimiento.tipoMovimiento.CONSULTA);
        movimiento.setUsuario(user.getEmail());

        movimientos.add(movimiento);
        movimientoCollection.insertOne(movimiento);

        return user.getSaldo();
    }

    private void salir(List<Movimiento> movimientos, MongoCollection<Movimiento> movimientoCollection) {
        movimiento = new Movimiento();
        // movimiento.setCantidad(cantidad);
        movimiento.setFecha(LocalDateTime.now());
        movimiento.setTipo(Movimiento.tipoMovimiento.SALIR);
        movimiento.setUsuario(user.getEmail());

        movimientos.add(movimiento);
        movimientoCollection.insertOne(movimiento);
    }

    private boolean comprobarEmail(String readUTF, MongoCollection<Usuario> userCollection) {
        user = userCollection.find(eq("email", readUTF)).first();
        return user != null;
    }

    private boolean comprobarPin(int readUTF) {
        return user.getPin() == readUTF;
    }


}