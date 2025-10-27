package com.mycompany.atm.cs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;

public class ClienteGUI extends JFrame {
    private Socket socket;
    private BufferedReader entrada;
    private PrintWriter salida;
    
    private JList<String> listaNotas;
    private DefaultListModel<String> modeloLista;
    private JTextArea areaContenido;
    private JButton btnCrear, btnVer, btnBorrar, btnActualizar;
    
    public ClienteGUI() {
        setTitle("Gestor de Notas - Cliente");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        if (!conectarServidor()) {
            return; 
        }
        
        inicializarComponentes();
        cargarNotas();
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                desconectar();
            }
        });
    }
    
    private void inicializarComponentes() {
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel panelIzquierdo = new JPanel(new BorderLayout(5, 5));
        panelIzquierdo.setBorder(BorderFactory.createTitledBorder("Notas"));
        
        modeloLista = new DefaultListModel<>();
        listaNotas = new JList<>(modeloLista);
        listaNotas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollLista = new JScrollPane(listaNotas);
        
        JPanel panelBotonesLista = new JPanel(new FlowLayout());
        btnActualizar = new JButton("Actualizar");
        btnVer = new JButton("Ver");
        btnBorrar = new JButton("Borrar");
        
        btnActualizar.addActionListener(e -> cargarNotas());
        btnVer.addActionListener(e -> verNota());
        btnBorrar.addActionListener(e -> borrarNota());
        
        panelBotonesLista.add(btnActualizar);
        panelBotonesLista.add(btnVer);
        panelBotonesLista.add(btnBorrar);
        
        panelIzquierdo.add(scrollLista, BorderLayout.CENTER);
        panelIzquierdo.add(panelBotonesLista, BorderLayout.SOUTH);
        
        JPanel panelDerecho = new JPanel(new BorderLayout(5, 5));
        panelDerecho.setBorder(BorderFactory.createTitledBorder("Contenido"));
        
        areaContenido = new JTextArea();
        areaContenido.setLineWrap(true);
        areaContenido.setWrapStyleWord(true);
        areaContenido.setEditable(false);
        JScrollPane scrollContenido = new JScrollPane(areaContenido);
        
        btnCrear = new JButton("Nueva Nota");
        btnCrear.addActionListener(e -> crearNota());
        
        panelDerecho.add(scrollContenido, BorderLayout.CENTER);
        panelDerecho.add(btnCrear, BorderLayout.SOUTH);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelIzquierdo, panelDerecho);
        splitPane.setDividerLocation(300);
        
        panelPrincipal.add(splitPane, BorderLayout.CENTER);
        add(panelPrincipal);
    }
    
    private boolean conectarServidor() {
        try {
            System.out.println("Intentando conectar con el servidor en localhost:3000...");
            socket = new Socket("localhost", 3000);
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            salida = new PrintWriter(socket.getOutputStream(), true);
            System.out.println("✓ Conexión exitosa con el servidor");
            return true;
        } catch (IOException e) {
            System.err.println("✗ ERROR CRÍTICO: No se puede conectar con el servidor");
            System.err.println("  - Asegúrate de que el servidor esté ejecutándose");
            System.err.println("  - Servidor esperado en: localhost:3000");
            System.err.println("  - Error: " + e.getMessage());
            
            JOptionPane.showMessageDialog(null, 
                "❌ ERROR CRÍTICO ❌\n\n" +
                "El cliente REQUIERE que el servidor esté ejecutándose.\n\n" +
                "INSTRUCCIONES:\n" +
                "1. Abre una terminal CMD o PowerShell\n" +
                "2. Ve al directorio del proyecto\n" +
                "3. Ejecuta: iniciar-servidor.bat\n" +
                "4. Espera el mensaje: 'Servidor iniciado en puerto 3000'\n" +
                "5. Luego ejecuta: iniciar-cliente.bat\n\n" +
                "Detalles del error: " + e.getMessage(),
                "ERROR: Servidor no disponible",
                JOptionPane.ERROR_MESSAGE);
            
            System.exit(1);
            return false;
        }
    }
    
    private void cargarNotas() {
        try {
            if (salida == null || entrada == null) {
                JOptionPane.showMessageDialog(this, "ERROR: No hay conexión con el servidor", "Error de Conexión", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            salida.println("LISTAR");
            String respuesta = entrada.readLine();
            
            if (respuesta == null) {
                JOptionPane.showMessageDialog(this, "ERROR: El servidor no responde. Verifique que esté ejecutándose.", "Error de Conexión", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (respuesta.startsWith("OK")) {
                String[] partes = respuesta.split("\\|");
                int cantidad = Integer.parseInt(partes[1]);
                
                modeloLista.clear();
                
                for (int i = 0; i < cantidad; i++) {
                    String lineaNota = entrada.readLine();
                    modeloLista.addElement(lineaNota);
                }
                
                areaContenido.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "ERROR del servidor: " + respuesta, "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "ERROR al cargar notas - El servidor no está disponible: " + e.getMessage(), "Error de Conexión", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ERROR al procesar respuesta del servidor: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void crearNota() {
        JTextField campoTitulo = new JTextField();
        JTextArea campoContenido = new JTextArea(5, 20);
        campoContenido.setLineWrap(true);
        campoContenido.setWrapStyleWord(true);
        
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.add(new JLabel("Título:"), BorderLayout.NORTH);
        panel.add(campoTitulo, BorderLayout.CENTER);
        panel.add(new JLabel("Contenido:"), BorderLayout.SOUTH);
        
        JPanel panelCompleto = new JPanel(new BorderLayout(5, 5));
        panelCompleto.add(panel, BorderLayout.NORTH);
        panelCompleto.add(new JScrollPane(campoContenido), BorderLayout.CENTER);
        
        int resultado = JOptionPane.showConfirmDialog(this, panelCompleto, "Nueva Nota", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (resultado == JOptionPane.OK_OPTION) {
            String titulo = campoTitulo.getText().trim();
            String contenido = campoContenido.getText().trim();
            
            if (titulo.isEmpty() || contenido.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El título y el contenido son obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                salida.println("CREAR");
                salida.println(titulo);
                salida.println(contenido);
                
                String respuesta = entrada.readLine();
                if (respuesta.startsWith("OK")) {
                    JOptionPane.showMessageDialog(this, respuesta.substring(3), "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    cargarNotas();
                } else {
                    JOptionPane.showMessageDialog(this, respuesta.substring(6), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error al crear nota: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void verNota() {
        String seleccion = listaNotas.getSelectedValue();
        if (seleccion == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una nota de la lista", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            String[] partes = seleccion.split("\\|");
            int id = Integer.parseInt(partes[0].trim());
            
            salida.println("VER");
            salida.println(id);
            
            String respuesta = entrada.readLine();
            if (respuesta.startsWith("OK")) {
                String[] datos = respuesta.substring(3).split("\\|");
                String titulo = datos[1];
                String contenido = datos[2];
                String fecha = datos[3];
                
                areaContenido.setText("Título: " + titulo + "\n");
                areaContenido.append("Fecha: " + fecha + "\n");
                areaContenido.append("═".repeat(50) + "\n\n");
                areaContenido.append(contenido);
            } else {
                JOptionPane.showMessageDialog(this, respuesta.substring(6), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException | NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error al ver nota: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void borrarNota() {
        String seleccion = listaNotas.getSelectedValue();
        if (seleccion == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una nota de la lista", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirmacion = JOptionPane.showConfirmDialog(this, "¿Está seguro de que desea borrar esta nota?", "Confirmar", JOptionPane.YES_NO_OPTION);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                String[] partes = seleccion.split("\\|");
                int id = Integer.parseInt(partes[0].trim());
                
                salida.println("BORRAR");
                salida.println(id);
                
                String respuesta = entrada.readLine();
                if (respuesta.startsWith("OK")) {
                    JOptionPane.showMessageDialog(this, respuesta.substring(3), "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    cargarNotas();
                    areaContenido.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, respuesta.substring(6), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException | NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Error al borrar nota: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void desconectar() {
        try {
            if (salida != null) {
                salida.println("DESCONECTAR");
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new ClienteGUI().setVisible(true);
        });
    }
}
