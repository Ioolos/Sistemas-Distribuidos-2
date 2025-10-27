package com.mycompany.biblioteca;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClienteBibliotecaGUI {
    private JFrame frame;
    private JTextField userField;
    private JPasswordField pinField;
    private JButton loginButton;

    private JTextField bookIdField;
    private JButton consultButton;
    private JButton borrowButton;
    private JButton returnButton;
    private JTextArea outputArea;

    private String usuario;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClienteBibliotecaGUI().createAndShowGUI());
    }

    private void createAndShowGUI() {
        frame = new JFrame("Biblioteca GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        JPanel loginPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5,5,5,5);

        c.gridx = 0; c.gridy = 0; loginPanel.add(new JLabel("Usuario:"), c);
        userField = new JTextField(15); c.gridx = 1; loginPanel.add(userField, c);
        c.gridx = 0; c.gridy = 1; loginPanel.add(new JLabel("PIN:"), c);
        pinField = new JPasswordField(15); c.gridx = 1; loginPanel.add(pinField, c);
        loginButton = new JButton("Iniciar sesión"); c.gridx = 0; c.gridy = 2; c.gridwidth = 2; loginPanel.add(loginButton, c);

        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel controls = new JPanel();
        bookIdField = new JTextField(10);
        consultButton = new JButton("Consultar");
        borrowButton = new JButton("Prestar");
        returnButton = new JButton("Devolver");
        controls.add(new JLabel("ID libro:"));
        controls.add(bookIdField);
        controls.add(consultButton);
        controls.add(borrowButton);
        controls.add(returnButton);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        mainPanel.add(controls, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(outputArea), BorderLayout.CENTER);

        frame.getContentPane().setLayout(new CardLayout());
        frame.getContentPane().add(loginPanel, "LOGIN");
        frame.getContentPane().add(mainPanel, "MAIN");

        loginButton.addActionListener(this::onLogin);
        consultButton.addActionListener(e -> runBackground(this::onConsult));
        borrowButton.addActionListener(e -> runBackground(this::onBorrow));
        returnButton.addActionListener(e -> runBackground(this::onReturn));

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void onLogin(ActionEvent e) {
        runBackground(() -> {
            String u = userField.getText().trim();
            String p = new String(pinField.getPassword()).trim();
            if (u.isEmpty() || p.isEmpty()) {
                append("Ingrese usuario y PIN\n");
                return;
            }
            try (Socket s = new Socket("localhost", 7000);
                 BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                 PrintWriter out = new PrintWriter(s.getOutputStream(), true)) {
                out.println(u);
                out.println(p);
                out.flush();
                String resp = in.readLine();
                if (resp != null && resp.startsWith("OK")) {
                    usuario = u;
                    SwingUtilities.invokeLater(() -> switchToMain());
                    append("Autenticación exitosa\n");
                } else {
                    append("Error de autenticación\n");
                }
            } catch (Exception ex) {
                append("Error conectando al servidor de autenticación: " + ex.getMessage() + "\n");
            }
        });
    }

    private void onConsult() {
        String id = bookIdField.getText().trim();
        if (id.isEmpty()) { append("Ingrese ID de libro\n"); return; }
        try (Socket s = new Socket("localhost", 8000);
             BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
             PrintWriter out = new PrintWriter(s.getOutputStream(), true)) {
            out.println("C");
            out.println(id);
            out.flush();
            String r = in.readLine();
            append("Disponibilidad: " + r + "\n");
        } catch (Exception ex) {
            append("Error consultando catálogo: " + ex.getMessage() + "\n");
        }
    }

    private void onBorrow() {
        String id = bookIdField.getText().trim();
        if (id.isEmpty()) { append("Ingrese ID de libro\n"); return; }
        try (Socket s = new Socket("localhost", 9000);
             BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
             PrintWriter out = new PrintWriter(s.getOutputStream(), true)) {
            out.println("B");
            out.println(usuario);
            out.println(id);
            out.flush();
            String r = in.readLine();
            append(r + "\n");
        } catch (Exception ex) {
            append("Error en préstamo: " + ex.getMessage() + "\n");
        }
    }

    private void onReturn() {
        String id = bookIdField.getText().trim();
        if (id.isEmpty()) { append("Ingrese ID de libro\n"); return; }
        try (Socket s = new Socket("localhost", 9000);
             BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
             PrintWriter out = new PrintWriter(s.getOutputStream(), true)) {
            out.println("R");
            out.println(usuario);
            out.println(id);
            out.flush();
            String r = in.readLine();
            append(r + "\n");
        } catch (Exception ex) {
            append("Error en devolución: " + ex.getMessage() + "\n");
        }
    }

    private void switchToMain() {
        CardLayout cl = (CardLayout) frame.getContentPane().getLayout();
        cl.show(frame.getContentPane(), "MAIN");
    }

    private void append(String s) {
        SwingUtilities.invokeLater(() -> outputArea.append(s));
    }

    private void runBackground(Runnable r) {
        new Thread(r).start();
    }
}
