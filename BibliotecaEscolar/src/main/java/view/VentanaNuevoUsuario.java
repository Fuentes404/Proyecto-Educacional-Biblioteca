package view;

import controller.GestorUsuarios;
import model.Bibliotecario;
import model.Estudiante;
import model.TipoRol;
import model.Usuario;

import javax.swing.*;
import java.awt.*;

public class VentanaNuevoUsuario extends JDialog {

    // - Atributos
    private JPanel panelCampos;
    private JPanel panelBotones;
    private JComboBox<TipoRol> cmbRol;
    private JTextField txtNombre;
    private JTextField txtRut;
    private JTextField txtCorreo;
    private JPasswordField txtClave;
    private JTextField txtCurso;
    private JButton btnGuardar;
    private JButton btnCancelar;

    // - Dependencias
    private final GestorUsuarios gestorUsuarios;

    // - Constructor
    public VentanaNuevoUsuario(Window padre, GestorUsuarios gestorUsuarios) {
        super(padre, "Nuevo Usuario", ModalityType.APPLICATION_MODAL);
        this.gestorUsuarios = gestorUsuarios;

        configurarVentana();
        inicializarComponentes();
        configurarDiseno();
        configurarEventos();
    }

    private void configurarVentana() {
        setSize(450, 380);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(getOwner());
        setResizable(false);
    }

    private void inicializarComponentes() {
        panelCampos = new JPanel(new GridLayout(6, 2, 20, 15));
        panelBotones = new JPanel(new FlowLayout());
        cmbRol = new JComboBox<>(TipoRol.values());
        txtNombre = new JTextField();
        txtRut = new JTextField();
        txtCorreo = new JTextField();
        txtClave = new JPasswordField();
        txtCurso = new JTextField();
        btnGuardar = new JButton("Guardar");
        btnCancelar = new JButton("Cancelar");

        cmbRol.setSelectedItem(TipoRol.ESTUDIANTE);
    }

    private void configurarDiseno() {
        panelCampos.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));
        panelCampos.add(new JLabel("Rol: "));
        panelCampos.add(cmbRol);
        panelCampos.add(new JLabel("Nombre: "));
        panelCampos.add(txtNombre);
        panelCampos.add(new JLabel("RUT: "));
        panelCampos.add(txtRut);
        panelCampos.add(new JLabel("Correo: "));
        panelCampos.add(txtCorreo);
        panelCampos.add(new JLabel("Contraseña: "));
        panelCampos.add(txtClave);
        panelCampos.add(new JLabel("Curso: "));
        panelCampos.add(txtCurso);

        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);

        add(panelCampos, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private void configurarEventos() {
        btnGuardar.addActionListener(e -> guardar());
        btnCancelar.addActionListener(e -> dispose());
        cmbRol.addActionListener(e -> actualizarCurso());
    }

    // - Controladores
    private void guardar() {
        try {
            String nombre = txtNombre.getText().trim();
            String rut = txtRut.getText().trim();
            String correo = txtCorreo.getText().trim();
            String clave = new String(txtClave.getPassword());

            Usuario nuevo;
            if (cmbRol.getSelectedItem() == TipoRol.ESTUDIANTE) {
                nuevo = new Estudiante(0, nombre, rut, correo, clave, txtCurso.getText().trim());
            } else {
                nuevo = new Bibliotecario(0, nombre, rut, correo, clave);
            }

            gestorUsuarios.crearUsuario(nuevo);
            mostrarMensaje("Usuario creado correctamente");
            dispose();
        } catch (IllegalArgumentException ex) {
            mostrarError(ex.getMessage());
        }
    }

    // - Metodos Funcionales
    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Usuarios", JOptionPane.INFORMATION_MESSAGE);
    }

    public void mostrarError(String mensajeError) {
        JOptionPane.showMessageDialog(this, mensajeError, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // - Metodos Auxiliares
    private void actualizarCurso() {
        boolean esEstudiante = cmbRol.getSelectedItem() == TipoRol.ESTUDIANTE;
        txtCurso.setEnabled(esEstudiante);
        if (!esEstudiante) {
            txtCurso.setText("");
        }
    }
}