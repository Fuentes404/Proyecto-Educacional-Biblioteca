package view;

import controller.GestorUsuarios;
import model.Usuario;

import javax.swing.*;
import java.awt.*;

public class VentanaEditarContrasena extends JDialog {

    // - Atributos
    private JPanel panelCampos;
    private JPanel panelBotones;
    private JPasswordField txtAntigua;
    private JPasswordField txtNueva;
    private JPasswordField txtConfirmar;
    private JButton btnGuardar;
    private JButton btnCancelar;

    // - Dependencias
    private final Usuario usuario;
    private final GestorUsuarios gestorUsuarios;

    // - Constructor
    public VentanaEditarContrasena(Window padre, Usuario usuario, GestorUsuarios gestorUsuarios) {
        super(padre, "Cambiar Contraseña", ModalityType.APPLICATION_MODAL);
        this.usuario = usuario;
        this.gestorUsuarios = gestorUsuarios;

        configurarVentana();
        inicializarComponentes();
        configurarDiseno();
        configurarEventos();
    }

    private void configurarVentana() {
        setSize(450, 230);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(getOwner());
        setResizable(false);
    }

    private void inicializarComponentes() {
        panelCampos = new JPanel(new GridLayout(3, 2, 20, 20));
        panelBotones = new JPanel(new FlowLayout());
        txtAntigua = new JPasswordField();
        txtNueva = new JPasswordField();
        txtConfirmar = new JPasswordField();
        btnGuardar = new JButton("Guardar");
        btnCancelar = new JButton("Cancelar");
    }

    private void configurarDiseno() {
        // Panel Superior
        panelCampos.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));
        panelCampos.add(new JLabel("Contraseña antigua: "));
        panelCampos.add(txtAntigua);
        panelCampos.add(new JLabel("Nueva contraseña: "));
        panelCampos.add(txtNueva);
        panelCampos.add(new JLabel("Confirme contraseña: "));
        panelCampos.add(txtConfirmar);
        // Panel Inferior
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);
        // ubicacion de los paneles
        add(panelCampos, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private void configurarEventos() {
        btnGuardar.addActionListener(e -> guardar());
        btnCancelar.addActionListener(e -> dispose());
    }

    // - Controladores
    private void guardar() {
        String antigua = new String(txtAntigua.getPassword());
        String nueva = new String(txtNueva.getPassword());
        String confirmacion = new String(txtConfirmar.getPassword());

        try {
            gestorUsuarios.cambiarContrasena(usuario, antigua, nueva, confirmacion);
            mostrarMensaje("Contraseña actualizada correctamente");
            dispose();
        } catch (IllegalArgumentException ex) {
            mostrarError(ex.getMessage());
        }
    }

    // - Metodos Funcionales
    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Contraseña", JOptionPane.INFORMATION_MESSAGE);
    }

    public void mostrarError(String mensajeError) {
        JOptionPane.showMessageDialog(this, mensajeError, "Error", JOptionPane.ERROR_MESSAGE);
    }
}