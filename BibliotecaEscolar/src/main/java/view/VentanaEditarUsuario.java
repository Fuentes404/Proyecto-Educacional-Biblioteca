package view;

import controller.GestorUsuarios;
import model.Estudiante;
import model.Usuario;

import javax.swing.*;
import java.awt.*;

public class VentanaEditarUsuario extends JDialog {

    // - Atributos
    private JPanel panelCampos;
    private JPanel panelBotones;
    private JTextField txtId;
    private JTextField txtRol;
    private JTextField txtNombre;
    private JTextField txtRut;
    private JTextField txtCorreo;
    private JTextField txtCurso;
    private JButton btnGuardar;
    private JButton btnCancelar;

    // - Dependencias
    private final Usuario usuario;
    private final GestorUsuarios gestorUsuarios;

    // - Constructor
    public VentanaEditarUsuario(Window padre, Usuario usuario, GestorUsuarios gestorUsuarios) {
        super(padre, "Editar Usuario", ModalityType.APPLICATION_MODAL);
        this.usuario = usuario;
        this.gestorUsuarios = gestorUsuarios;

        configurarVentana();
        inicializarComponentes();
        configurarDiseno();
        configurarEventos();
        cargarDatos();
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
        txtId = new JTextField();
        txtRol = new JTextField();
        txtNombre = new JTextField();
        txtRut = new JTextField();
        txtCorreo = new JTextField();
        txtCurso = new JTextField();
        btnGuardar = new JButton("Guardar");
        btnCancelar = new JButton("Cancelar");

        // El ID y el rol identifican al usuario: no se pueden editar
        txtId.setEditable(false);
        txtRol.setEditable(false);
        // El curso solo aplica a estudiantes
        txtCurso.setEnabled(usuario instanceof Estudiante);
    }

    private void configurarDiseno() {
        panelCampos.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));
        panelCampos.add(new JLabel("ID: "));
        panelCampos.add(txtId);
        panelCampos.add(new JLabel("Rol: "));
        panelCampos.add(txtRol);
        panelCampos.add(new JLabel("Nombre: "));
        panelCampos.add(txtNombre);
        panelCampos.add(new JLabel("RUT: "));
        panelCampos.add(txtRut);
        panelCampos.add(new JLabel("Correo: "));
        panelCampos.add(txtCorreo);
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
    }

    // - Controladores
    private void cargarDatos() {
        txtId.setText(String.valueOf(usuario.getIdUsuario()));
        txtRol.setText(String.valueOf(usuario.getRol()));
        txtNombre.setText(usuario.getNombre());
        txtRut.setText(usuario.getRut());
        txtCorreo.setText(usuario.getCorreo());
        if (usuario instanceof Estudiante) {
            txtCurso.setText(((Estudiante) usuario).getCurso());
        }
    }

    private void guardar() {
        try {
            gestorUsuarios.editarUsuario(
                    usuario.getIdUsuario(),
                    txtNombre.getText(),
                    txtRut.getText(),
                    txtCorreo.getText(),
                    txtCurso.getText()
            );
            mostrarMensaje("Usuario actualizado correctamente");
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
}