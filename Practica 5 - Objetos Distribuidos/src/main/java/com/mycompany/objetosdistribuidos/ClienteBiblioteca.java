package com.mycompany.objetosdistribuidos;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class ClienteBiblioteca {
    public static void main(String[] args) throws RemoteException, NotBoundException {
        Scanner scan = new Scanner(System.in);

        Registry registry = LocateRegistry.getRegistry("localhost");
        IBibliotecaAutenticacion auth = (IBibliotecaAutenticacion) registry.lookup("BibliotecaAuthService");
        IBibliotecaInventario inv = (IBibliotecaInventario) registry.lookup("InventarioService");
        IBibliotecaOperaciones ops = (IBibliotecaOperaciones) registry.lookup("OperacionesBibliotecaService");

    System.out.print("Ingrese su usuario: ");
    String usuario = scan.nextLine();
    System.out.print("Ingrese su PIN: ");
    int pin = scan.nextInt();
    scan.nextLine();

        String resp = auth.autenticarUsuario(usuario, pin);
        System.out.println(resp);
        if (!resp.startsWith("Autenticacion Exitosa")) return;

        while (true) {
            System.out.println("\nSeleccione la opcion deseada:");
            System.out.println("1. Consultar disponibilidad");
            System.out.println("2. Prestar libro");
            System.out.println("3. Devolver libro");
            System.out.println("4. Transferir copias entre sucursales");
            System.out.println("5. Salir");

            int opc = scan.nextInt();
            scan.nextLine();

            switch (opc) {
                case 1:
                    // Consultar disponibilidad con selección numerada
                    String[] sucursales = inv.listarSucursales();
                    if (sucursales.length == 0) {
                        System.out.println("No hay sucursales registradas.");
                        break;
                    }
                    System.out.println("Sucursales:");
                    for (int i = 0; i < sucursales.length; i++) System.out.println((i+1)+". "+sucursales[i]);
                    System.out.print("Seleccione sucursal (número): ");
                    int sidx = scan.nextInt(); scan.nextLine();
                    if (sidx < 1 || sidx > sucursales.length) { System.out.println("Selección inválida."); break; }
                    String suc = sucursales[sidx-1];
                    String[] libros = inv.listarLibros(suc);
                    if (libros.length == 0) { System.out.println("No hay libros en la sucursal seleccionada."); break; }
                    System.out.println("Libros en " + suc + ":");
                    for (int i = 0; i < libros.length; i++) System.out.println((i+1)+". "+libros[i]);
                    System.out.print("Seleccione libro (número): ");
                    int bidx = scan.nextInt(); scan.nextLine();
                    if (bidx < 1 || bidx > libros.length) { System.out.println("Selección inválida."); break; }
                    String id = libros[bidx-1];
                    int disp = inv.consultarDisponibilidad(suc, id);
                    System.out.println("Disponibilidad: " + disp);
                    break;
                case 2:
                    // Prestar: seleccionar sucursal y libro por número
                    sucursales = inv.listarSucursales();
                    if (sucursales.length == 0) { System.out.println("No hay sucursales."); break; }
                    System.out.println("Sucursales:"); for (int i=0;i<sucursales.length;i++) System.out.println((i+1)+". "+sucursales[i]);
                    System.out.print("Seleccione sucursal (número): "); sidx = scan.nextInt(); scan.nextLine();
                    if (sidx < 1 || sidx > sucursales.length) { System.out.println("Selección inválida."); break; }
                    suc = sucursales[sidx-1];
                    libros = inv.listarLibros(suc);
                    if (libros.length == 0) { System.out.println("No hay libros en la sucursal seleccionada."); break; }
                    System.out.println("Libros:"); for (int i=0;i<libros.length;i++) System.out.println((i+1)+". "+libros[i]);
                    System.out.print("Seleccione libro (número): "); bidx = scan.nextInt(); scan.nextLine();
                    if (bidx < 1 || bidx > libros.length) { System.out.println("Selección inválida."); break; }
                    id = libros[bidx-1];
                    resp = ops.prestar(usuario, suc, id);
                    System.out.println(resp);
                    break;
                case 3:
                    // Devolver: seleccionar sucursal y libro por número
                    sucursales = inv.listarSucursales();
                    if (sucursales.length == 0) { System.out.println("No hay sucursales."); break; }
                    System.out.println("Sucursales:"); for (int i=0;i<sucursales.length;i++) System.out.println((i+1)+". "+sucursales[i]);
                    System.out.print("Seleccione sucursal (número): "); sidx = scan.nextInt(); scan.nextLine();
                    if (sidx < 1 || sidx > sucursales.length) { System.out.println("Selección inválida."); break; }
                    suc = sucursales[sidx-1];
                    libros = inv.listarLibros(suc);
                    if (libros.length == 0) { System.out.println("No hay libros en la sucursal seleccionada."); break; }
                    System.out.println("Libros:"); for (int i=0;i<libros.length;i++) System.out.println((i+1)+". "+libros[i]);
                    System.out.print("Seleccione libro (número): "); bidx = scan.nextInt(); scan.nextLine();
                    if (bidx < 1 || bidx > libros.length) { System.out.println("Selección inválida."); break; }
                    id = libros[bidx-1];
                    resp = ops.devolver(usuario, suc, id);
                    System.out.println(resp);
                    break;
                case 4:
                    // Transferir: seleccionar origen (sucursal+libro), destino sucursal y cantidad
                    sucursales = inv.listarSucursales();
                    if (sucursales.length < 2) { System.out.println("Se requieren al menos 2 sucursales para transferir."); break; }
                    System.out.println("Sucursales (origen):"); for (int i=0;i<sucursales.length;i++) System.out.println((i+1)+". "+sucursales[i]);
                    System.out.print("Seleccione sucursal origen (número): "); sidx = scan.nextInt(); scan.nextLine();
                    if (sidx < 1 || sidx > sucursales.length) { System.out.println("Selección inválida."); break; }
                    String origen = sucursales[sidx-1];
                    libros = inv.listarLibros(origen);
                    if (libros.length == 0) { System.out.println("No hay libros en la sucursal origen."); break; }
                    System.out.println("Libros en origen:"); for (int i=0;i<libros.length;i++) System.out.println((i+1)+". "+libros[i]);
                    System.out.print("Seleccione libro (número): "); bidx = scan.nextInt(); scan.nextLine();
                    if (bidx < 1 || bidx > libros.length) { System.out.println("Selección inválida."); break; }
                    id = libros[bidx-1];
                    System.out.println("Sucursales (destino):"); for (int i=0;i<sucursales.length;i++) System.out.println((i+1)+". "+sucursales[i]);
                    System.out.print("Seleccione sucursal destino (número): "); int didx = scan.nextInt(); scan.nextLine();
                    if (didx < 1 || didx > sucursales.length) { System.out.println("Selección inválida."); break; }
                    String destino = sucursales[didx-1];
                    System.out.print("Cantidad a transferir: "); int cant = scan.nextInt(); scan.nextLine();
                    resp = ops.transferir(origen, destino, id, cant);
                    System.out.println(resp);
                    break;
                case 5:
                    System.out.println("Sesion finalizada.");
                    return;
                default:
                    System.out.println("Opción no válida.");
            }
        }
    }
}
