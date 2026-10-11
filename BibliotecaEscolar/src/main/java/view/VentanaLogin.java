package view;

import controller.GestorLibros;
import controller.GestorPrestamos;
import controller.GestorUsuarios;
import model.Usuario;

import javax.swing.*;
import java.awt.*;

public class VentanaLogin extends JFrame {

    // - Atributos
    private JPanel panelCampos;
    private JPanel panelBotones;
    private JTextField txtCorreo;
    private JPasswordField txtClave;
    private JButton btnIngresar;
    private JButton btnSalir;

    // - Dependencias
    private final GestorUsuarios gestorUsuarios;
    private final GestorLibros gestorLibros;
    private final GestorPrestamos gestorPrestamos;

    // - Constructor
    public VentanaLogin(GestorUsuarios gestorUsuarios, GestorLibros gestorLibros, GestorPrestamos gestorPrestamos) {
        this.gestorUsuarios = gestorUsuarios;
        this.gestorLibros = gestorLibros;
        this.gestorPrestamos = gestorPrestamos;

        configurarVentana();
        inicializarComponentes();
        configurarDiseno();
        configurarEventos();
    }

    private void configurarVentana() {
        setTitle("Login");
        setSize(500, 180);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void inicializarComponentes() {
        panelCampos = new JPanel(new GridLayout(2, 2, 20, 20));
        panelBotones = new JPanel(new FlowLayout());
        txtCorreo = new JTextField();
        txtClave = new JPasswordField();
        btnIngresar = new JButton("Ingresar");
        btnSalir = new JButton("Salir");
    }

    private void configurarDiseno() {
        // Panel Superior
        panelCampos.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));
        panelCampos.add(new JLabel("Correo: "));
        panelCampos.add(txtCorreo);
        panelCampos.add(new JLabel("Contraseña: "));
        panelCampos.add(txtClave);
        // Panel Inferior
        panelBotones.add(btnIngresar);
        panelBotones.add(btnSalir);
        // ubicacion de los paneles
        add(panelCampos, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private void configurarEventos() {
        btnIngresar.addActionListener(e -> ingresar());
        btnSalir.addActionListener(e -> System.exit(0));
    }

    // - Controladores
    private void ingresar() {
        String correo = txtCorreo.getText().trim();
        String clave = new String(txtClave.getPassword());

        try {
            Usuario usuario = gestorUsuarios.autentificar(correo, clave);
            if (usuario != null) {
                mostrarMensaje("Bienvenido " + usuario.getNombre() + " - " + usuario.getRol());
                new VentanaMenuPrincipal(usuario, gestorUsuarios, gestorLibros, gestorPrestamos).setVisible(true);
                dispose();
            } else {
                mostrarError("Correo o contraseña incorrectos");
                limpiarClave();
            }
        } catch (Exception ex) {
            mostrarError(ex.getMessage());
        }
    }

    // - Metodos Funcionales
    public void mostrarMensaje(String mensajeEntrada) {
        JOptionPane.showMessageDialog(this, mensajeEntrada, "Login", JOptionPane.INFORMATION_MESSAGE);
    }

    public void mostrarError(String mensajeError) {
        JOptionPane.showMessageDialog(this, mensajeError, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // - Metodos Auxiliares
    public void limpiarClave() {
        txtClave.setText("");
        txtClave.requestFocus();
    }

}