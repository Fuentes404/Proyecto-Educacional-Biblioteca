package view;

import controller.GestorUsuarios;
import model.Estudiante;
import model.Usuario;

import javax.swing.*;
import java.awt.*;

public class PanelPerfil extends JPanel {

    // - Atributos
    private JLabel lblTitulo;
    private JPanel panelDatos;
    private JPanel panelFoto;
    private JLabel lblId;
    private JLabel lblNombre;
    private JLabel lblRut;
    private JLabel lblCorreo;
    private JLabel lblRol;
    private JLabel lblTextoCurso;
    private JLabel lblCurso;
    private JButton btnEditar;
    private JButton btnCambiarContrasena;

    // - Dependencias
    private final GestorUsuarios gestorUsuarios;
    private Usuario usuarioActual;

    // - Constructor
    public PanelPerfil(GestorUsuarios gestorUsuarios) {
        this.gestorUsuarios = gestorUsuarios;

        configurarPanel();
        inicializarComponentes();
        configurarDiseno();
        configurarEventos();
    }

    private void configurarPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
    }

    private void inicializarComponentes() {
        lblTitulo = new JLabel("Mi Perfil");
        panelDatos = new JPanel(new GridLayout(0, 2, 10, 15));
        panelFoto = new JPanel();
        lblId = new JLabel();
        lblNombre = new JLabel();
        lblRut = new JLabel();
        lblCorreo = new JLabel();
        lblRol = new JLabel();
        lblTextoCurso = new JLabel("Curso:");
        lblCurso = new JLabel();
        btnEditar = new JButton("Editar datos");
        btnCambiarContrasena = new JButton("Cambiar Contraseña");
    }

    private void configurarDiseno() {

        lblTitulo.setFont(lblTitulo.getFont().deriveFont(Font.BOLD, 24f));
        add(lblTitulo, BorderLayout.NORTH);

        panelDatos.add(new JLabel("ID Usuario:"));
        panelDatos.add(lblId);
        panelDatos.add(new JLabel("Nombre:"));
        panelDatos.add(lblNombre);
        panelDatos.add(new JLabel("RUT:"));
        panelDatos.add(lblRut);
        panelDatos.add(new JLabel("Correo:"));
        panelDatos.add(lblCorreo);
        panelDatos.add(new JLabel("Rol:"));
        panelDatos.add(lblRol);
        panelDatos.add(lblTextoCurso);
        panelDatos.add(lblCurso);
        panelDatos.setPreferredSize(new Dimension(350, panelDatos.getPreferredSize().height));

        JPanel contenedorDatos = new JPanel(new BorderLayout());
        contenedorDatos.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));
        contenedorDatos.add(panelDatos, BorderLayout.NORTH);

        panelFoto.setPreferredSize(new Dimension(200, 200));
        panelFoto.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JPanel contenedorFoto = new JPanel(new BorderLayout());
        contenedorFoto.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));
        contenedorFoto.add(panelFoto, BorderLayout.NORTH);

        JPanel panelCentro = new JPanel(new BorderLayout());
        panelCentro.add(contenedorDatos, BorderLayout.WEST);
        panelCentro.add(contenedorFoto, BorderLayout.EAST);
        add(panelCentro, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBotones.add(btnEditar);
        panelBotones.add(btnCambiarContrasena);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private void configurarEventos() {
        btnEditar.addActionListener(e -> proximamente());
        btnCambiarContrasena.addActionListener(e -> cambiarContrasena());
    }

    // - Metodos Funcionales
    private void cambiarContrasena() {
        Window padre = SwingUtilities.getWindowAncestor(this);
        new VentanaEditarContrasena(padre, usuarioActual, gestorUsuarios).setVisible(true);
    }

    private void proximamente() {
        JOptionPane.showOptionDialog(
                this,
                "Proximamente",
                "Aviso",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                new Object[]{"Volver"},
                "Volver"
        );
    }

    public void mostrarUsuario(Usuario usuario) {
        this.usuarioActual = usuario;

        lblId.setText(String.valueOf(usuario.getIdUsuario()));
        lblNombre.setText(usuario.getNombre());
        lblRut.setText(usuario.getRut());
        lblCorreo.setText(usuario.getCorreo());
        lblRol.setText(String.valueOf(usuario.getRol()));

        // El curso solo existe en Estudiante
        boolean esEstudiante = usuario instanceof Estudiante;
        lblTextoCurso.setVisible(esEstudiante);
        lblCurso.setVisible(esEstudiante);
        if (esEstudiante) {
            lblCurso.setText(((Estudiante) usuario).getCurso());
        }
    }
}