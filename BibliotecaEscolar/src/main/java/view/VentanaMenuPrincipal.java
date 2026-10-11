package view;

import controller.GestorLibros;
import controller.GestorPrestamos;
import controller.GestorUsuarios;
import model.TipoRol;
import model.Usuario;

import javax.swing.*;
import java.awt.*;

public class VentanaMenuPrincipal extends JFrame {

    // - Atributos
    private JPanel panelMenu;
    private JPanel panelBotonesMenu;
    private JPanel panelCentral;
    private CardLayout cardLayout;

    private JButton btnInicio;
    private JButton btnLibros;
    private JButton btnEstudiantes;
    private JButton btnPrestamos;
    private JButton btnCerrarSesion;

    // - Paneles
    private PanelPerfil panelPerfil;
    private PanelLibros panelLibros;
    private PanelEstudiantes panelEstudiantes;   // solo existe para ADMIN
    private PanelPrestamos panelPrestamos;

    // - Dependencias
    private final Usuario usuario;
    private final GestorUsuarios gestorUsuarios;
    private final GestorLibros gestorLibros;
    private final GestorPrestamos gestorPrestamos;

    // - Constructor
    public VentanaMenuPrincipal(Usuario usuario, GestorUsuarios gestorUsuarios,
                                GestorLibros gestorLibros, GestorPrestamos gestorPrestamos) {
        this.usuario = usuario;
        this.gestorUsuarios = gestorUsuarios;
        this.gestorLibros = gestorLibros;
        this.gestorPrestamos = gestorPrestamos;

        configurarVentana();
        inicializarComponentes();
        configurarDiseno();
        configurarEventos();
    }

    private void configurarVentana() {
        setTitle("Biblioteca Escolar - Sistema de Gestion");
        setSize(1100, 600);
        setMinimumSize(new Dimension(800, 500));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    private void inicializarComponentes() {
        panelMenu = new JPanel(new BorderLayout());
        panelBotonesMenu = new JPanel(new GridLayout(0, 1, 0, 8));
        btnInicio = new JButton("Inicio");
        btnLibros = new JButton("Libros");
        btnEstudiantes = new JButton("Estudiantes");
        btnPrestamos = new JButton("Prestamos");
        btnCerrarSesion = new JButton("Cerrar Sesion");

        cardLayout = new CardLayout();
        panelCentral = new JPanel(cardLayout);

        panelPerfil = new PanelPerfil(gestorUsuarios);
        panelLibros = new PanelLibros(usuario, gestorLibros, gestorPrestamos);
        panelPrestamos = new PanelPrestamos(usuario, gestorPrestamos, gestorUsuarios, gestorLibros);
        if (esAdmin()) {
            panelEstudiantes = new PanelEstudiantes(usuario, gestorUsuarios, gestorPrestamos);
        }
    }

    private void configurarDiseno() {
        // Menu lateral
        panelMenu.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        panelMenu.setPreferredSize(new Dimension(180, 0));
        panelBotonesMenu.add(btnInicio);
        panelBotonesMenu.add(btnLibros);
        if (esAdmin()) {
            panelBotonesMenu.add(btnEstudiantes);
        }
        panelBotonesMenu.add(btnPrestamos);
        panelMenu.add(panelBotonesMenu, BorderLayout.NORTH);
        panelMenu.add(btnCerrarSesion, BorderLayout.SOUTH);

        // Paneles
        panelCentral.add(panelPerfil, "inicio");
        panelCentral.add(panelLibros, "libros");
        if (esAdmin()) {
            panelCentral.add(panelEstudiantes, "estudiantes");
        }
        panelCentral.add(panelPrestamos, "prestamos");

        // Pantalla inicial: perfil del usuario
        panelPerfil.mostrarUsuario(usuario);
        mostrar("inicio");

        // Ubicacion de los paneles
        add(panelMenu, BorderLayout.WEST);
        add(panelCentral, BorderLayout.CENTER);
    }

    private void configurarEventos() {
        btnInicio.addActionListener(e -> mostrar("inicio"));
        btnLibros.addActionListener(e -> {
            panelLibros.actualizar();
            mostrar("libros");
        });
        if (esAdmin()) {
            btnEstudiantes.addActionListener(e -> {
                panelEstudiantes.actualizar();
                mostrar("estudiantes");
            });
        }
        btnPrestamos.addActionListener(e -> {
            panelPrestamos.actualizar();
            mostrar("prestamos");
        });
        btnCerrarSesion.addActionListener(e -> cerrarSesion());
    }

    // - Navegacion
    private void mostrar(String nombrePanel) {
        cardLayout.show(panelCentral, nombrePanel);
    }

    // - Metodos Funcionales
    private void cerrarSesion() {
        int opcion = JOptionPane.showConfirmDialog(
                this,
                "¿Seguro que desea cerrar sesion?",
                "Cerrar sesion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        if (opcion != JOptionPane.YES_OPTION) {
            return;
        }
        dispose();
        new VentanaLogin(gestorUsuarios, gestorLibros, gestorPrestamos).setVisible(true);
    }

    // - Metodos Auxiliares
    private boolean esAdmin() {
        return usuario.getRol() == TipoRol.ADMIN;
    }
}