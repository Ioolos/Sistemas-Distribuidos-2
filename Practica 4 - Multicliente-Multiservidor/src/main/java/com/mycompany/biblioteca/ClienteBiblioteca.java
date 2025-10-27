package com.mycompany.biblioteca;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClienteBiblioteca {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        try {
            Socket sAuth = new Socket("localhost", 7000);
            BufferedReader inAuth = new BufferedReader(new InputStreamReader(sAuth.getInputStream()));
            PrintWriter outAuth = new PrintWriter(sAuth.getOutputStream(), true);

            System.out.print("Usuario: ");
            String usuario = scan.nextLine();
            System.out.print("PIN: ");
            String pin = scan.nextLine();

            outAuth.println(usuario);
            outAuth.println(pin);
            outAuth.flush();

            String resp = inAuth.readLine();
            System.out.println("\n" + resp);
            if (!resp.startsWith("OK")) {
                sAuth.close();
                return;
            }
            sAuth.close();

            int opc = 0;
            while (opc != 4) {
                System.out.println("\n1. Consultar disponibilidad");
                System.out.println("2. Prestar libro");
                System.out.println("3. Devolver libro");
                System.out.println("4. Salir");
                opc = Integer.parseInt(scan.nextLine());
                switch (opc) {
                    case 1:
                        System.out.print("ID del libro: ");
                        String idC = scan.nextLine();
                        try (Socket sCat = new Socket("localhost", 8000);
                             BufferedReader inCat = new BufferedReader(new InputStreamReader(sCat.getInputStream()));
                             PrintWriter outCat = new PrintWriter(sCat.getOutputStream(), true)) {
                            outCat.println("C");
                            outCat.println(idC);
                            outCat.flush();
                            String r = inCat.readLine();
                            System.out.println("Disponibilidad: " + r);
                        } catch (IOException e) {
                            System.out.println("Error al consultar catálogo");
                        }
                        break;
                    case 2:
                        System.out.print("ID del libro a prestar: ");
                        String idP = scan.nextLine();
                        try (Socket sTx = new Socket("localhost", 9000);
                             BufferedReader inTx = new BufferedReader(new InputStreamReader(sTx.getInputStream()));
                             PrintWriter outTx = new PrintWriter(sTx.getOutputStream(), true)) {
                            outTx.println("B");
                            outTx.println(usuario);
                            outTx.println(idP);
                            outTx.flush();
                            String r = inTx.readLine();
                            System.out.println(r);
                        } catch (IOException e) {
                            System.out.println("Error en préstamo");
                        }
                        break;
                    case 3:
                        System.out.print("ID del libro a devolver: ");
                        String idR = scan.nextLine();
                        try (Socket sTx2 = new Socket("localhost", 9000);
                             BufferedReader inTx2 = new BufferedReader(new InputStreamReader(sTx2.getInputStream()));
                             PrintWriter outTx2 = new PrintWriter(sTx2.getOutputStream(), true)) {
                            outTx2.println("R");
                            outTx2.println(usuario);
                            outTx2.println(idR);
                            outTx2.flush();
                            String r2 = inTx2.readLine();
                            System.out.println(r2);
                        } catch (IOException e) {
                            System.out.println("Error en devolución");
                        }
                        break;
                    case 4:
                        System.out.println("Sesion terminada");
                        break;
                    default:
                        System.out.println("Opcion invalida");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
