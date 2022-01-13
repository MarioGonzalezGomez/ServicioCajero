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

            controller.open();
            MongoCollection<Usuario> userCollection = controller.getCollection("cajeroDB", "usuarios", Usuario.class);
            MongoCollection<Movimiento> movimientoCollection = controller.getCollection("cajeroDB", "movimientos", Movimiento.class);

            dos.writeUTF("Introduzca su email");
            email = dis.readUTF();
            System.out.println(email);
            while (!emailCorrecto) {
                emailCorrecto = comprobarEmail(email, userCollection);
                if (!emailCorrecto) {
                    dos.writeUTF("El email no es correcto o no está registrado, pruebe de nuevo");
                } else {
                    dos.writeUTF("Email introducido con éxito");
                }
            }
            dos.writeUTF("Introduzca su pin de 4 dígitos");
            while (!pinCorrecto) {
                pinCorrecto = comprobarPin(dis.readUTF());
                if (!pinCorrecto) {
                    dos.writeUTF("El pin no es correcto, pruebe de nuevo");
                } else {
                    dos.writeUTF("Pin reconocido. Bienvenido al servicio de cajero automático");
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


    private void ingresarDinero(double cantidad, List<Movimiento> movimientos, MongoCollection<Movimiento> movimientoCollection, DataOutputStream dos) throws IOException {
        if (cantidad > 0) {
            user.setSaldo(user.getSaldo() + cantidad);
            movimiento = new Movimiento();
            movimiento.setCantidad(cantidad);
            movimiento.setFecha(LocalDateTime.now());
            movimiento.setTipo(Movimiento.tipoMovimiento.INGRESO);
            movimiento.setUsuario(user.getEmail());

            movimientos.add(movimiento);
            movimientoCollection.insertOne(movimiento);
            dos.writeUTF("Se ha hecho correctamente el ingreso de: " + cantidad + "€\nSu saldo actualizado es de: " + user.getSaldo() + " €");
        } else {
            dos.writeUTF("La cantidad introducida no es válida");
        }
    }

    private void retirarEfectivo(double cantidad, List<Movimiento> movimientos, MongoCollection<Movimiento> movimientoCollection, DataOutputStream dos) throws IOException {
        if (cantidad > 0 && user.getSaldo() - cantidad >= 0 && user.getRetiradoHoy() + cantidad < user.getLimite()) {
            user.setSaldo(user.getSaldo() - cantidad);
            movimiento = new Movimiento();
            movimiento.setCantidad(cantidad);
            movimiento.setFecha(LocalDateTime.now());
            movimiento.setTipo(Movimiento.tipoMovimiento.RETIRADA);
            movimiento.setUsuario(user.getEmail());

            movimientos.add(movimiento);
            movimientoCollection.insertOne(movimiento);

            dos.writeUTF("Se ha hecho correctamente la retirada de: " + cantidad + "€\nSu saldo actualizado es de: " + user.getSaldo() + " €");
        }
        if (cantidad <= 0) {
            dos.writeUTF("Lo siento, " + cantidad + " no es una cantidad válida para la retirada");
        }
        if (user.getSaldo() - cantidad < 0) {
            dos.writeUTF("Lo siento, parece que no dispone de efectivo suficiente para la retirada");
        }
        if (user.getRetiradoHoy() + cantidad < user.getLimite()) {
            dos.writeUTF("Lo siento, parece que se ha superado el límite diario de: " + user.getLimite() + " € diarios");
        }
    }

    private double consultarSaldo(List<Movimiento> movimientos, MongoCollection<Movimiento> movimientoCollection) {
        movimiento = new Movimiento();
        movimiento.setFecha(LocalDateTime.now());
        movimiento.setTipo(Movimiento.tipoMovimiento.CONSULTA);
        movimiento.setUsuario(user.getEmail());

        movimientos.add(movimiento);
        movimientoCollection.insertOne(movimiento);

        return user.getSaldo();
    }

    private void salir(List<Movimiento> movimientos, MongoCollection<Movimiento> movimientoCollection) {
        movimiento = new Movimiento();
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

    private boolean comprobarPin(String readUTF) {
        return user.getPin().equals(readUTF);
    }


}
