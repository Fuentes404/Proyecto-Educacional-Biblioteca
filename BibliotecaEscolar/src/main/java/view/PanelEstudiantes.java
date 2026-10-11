package view;

import controller.GestorPrestamos;
import controller.GestorUsuarios;
import model.Estudiante;
import model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PanelEstudiantes extends JPanel {

    // - Atributos
    private JLabel lblTitulo;
    private JPanel panelSuperior;
    private JPanel panelBotones;
    private JPanel panelBusqueda;
    private JButton btnNuevo;
    private JButton btnEditar;
    private JButton btnEliminar;
    private JButton btnActualizar;
    private JButton btnVerHistorial;
    private JLabel lblBuscar;
    private JTextField txtBuscar;

    // - Tabla
    private JTable tablaUsuarios;
    private DefaultTableModel modeloTabla;
    private JScrollPane scrollTabla;

    // - Dependencias
    private final Usuario usuario;
    private final GestorUsuarios gestorUsuarios;
    private final GestorPrestamos gestorPrestamos;

    // - Constructor
    public PanelEstudiantes(Usuario usuario, GestorUsuarios gestorUsuarios, GestorPrestamos gestorPrestamos) {
        this.usuario = usuario;
        this.gestorUsuarios = gestorUsuarios;
        this.gestorPrestamos = gestorPrestamos;

        configurarPanel();
        inicializarComponentes();
        configurarDiseno();
        configurarEventos();
        cargarTabla();
    }

    private void configurarPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
    }

    private void inicializarComponentes() {
        lblTitulo = new JLabel("Gestion de Estudiantes");
        panelSuperior = new JPanel(new BorderLayout());
        panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelBusqueda = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnNuevo = new JButton("Nuevo");
        btnEditar = new JButton("Editar");
        btnEliminar = new JButton("Eliminar");
        btnActualizar = new JButton("Actualizar");
        btnVerHistorial = new JButton("Ver historial");
        lblBuscar = new JLabel("Buscar:");
        txtBuscar = new JTextField(15);

        // Solo se habilitan al seleccionar un usuario
        btnEditar.setEnabled(false);
        btnEliminar.setEnabled(false);
        btnVerHistorial.setEnabled(false);

        // Tabla celdas no editables
        String[] columnas = {"ID Usuario", "Nombre", "RUT", "Correo", "Rol", "Curso"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        tablaUsuarios = new JTable(modeloTabla);
        scrollTabla = new JScrollPane(tablaUsuarios);
    }

    private void configurarDiseno() {
        // Titulo arriba
        lblTitulo.setFont(lblTitulo.getFont().deriveFont(Font.BOLD, 24f));
        JPanel contenedorSuperior = new JPanel(new BorderLayout());
        contenedorSuperior.add(lblTitulo, BorderLayout.NORTH);

        panelBotones.add(btnNuevo);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnVerHistorial);
        panelBusqueda.add(lblBuscar);
        panelBusqueda.add(txtBuscar);

        panelSuperior.setBorder(BorderFactory.createEmptyBorder(20, 0, 15, 0));
        panelSuperior.add(panelBotones, BorderLayout.WEST);
        panelSuperior.add(panelBusqueda, BorderLayout.EAST);
        contenedorSuperior.add(panelSuperior, BorderLayout.CENTER);

        add(contenedorSuperior, BorderLayout.NORTH);

        // Tabla al centro
        tablaUsuarios.setRowHeight(25);
        tablaUsuarios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaUsuarios.getTableHeader().setReorderingAllowed(false);
        add(scrollTabla, BorderLayout.CENTER);
    }

    private void configurarEventos() {
        btnNuevo.addActionListener(e -> nuevoUsuario());
        btnEditar.addActionListener(e -> editarUsuario());
        btnEliminar.addActionListener(e -> eliminarUsuario());
        btnActualizar.addActionListener(e -> cargarTabla());
        btnVerHistorial.addActionListener(e -> verHistorial());

        // Habilita los botones solo si hay una fila seleccionada
        tablaUsuarios.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                actualizarBotones();
            }
        });
    }

    // - Metodos Funcionales
    private void nuevoUsuario() {
        Window padre = SwingUtilities.getWindowAncestor(this);
        new VentanaNuevoUsuario(padre, gestorUsuarios).setVisible(true);
        cargarTabla();
    }

    private void editarUsuario() {
        Usuario seleccionado = obtenerUsuario();
        if (seleccionado == null) {
            return;
        }
        Window padre = SwingUtilities.getWindowAncestor(this);
        new VentanaEditarUsuario(padre, seleccionado, gestorUsuarios).setVisible(true);
        cargarTabla();
    }

    private void verHistorial() {
        Usuario seleccionado = obtenerUsuario();
        if (seleccionado == null) {
            return;
        }
        Window padre = SwingUtilities.getWindowAncestor(this);
        new VentanaHistorial(padre, seleccionado, gestorPrestamos).setVisible(true);
    }

    private void eliminarUsuario() {
        Usuario seleccionado = obtenerUsuario();
        if (seleccionado == null) {
            return;
        }

        // No se puede eliminar la cuenta con la que se inicio sesion
        if (seleccionado.getIdUsuario() == usuario.getIdUsuario()) {
            JOptionPane.showMessageDialog(this, "No puede eliminar su propio usuario", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // No se puede eliminar un estudiante con libros sin devolver
        if (gestorPrestamos.contarActivos(seleccionado.getIdUsuario()) > 0) {
            JOptionPane.showMessageDialog(this, "No se puede eliminar un usuario con prestamos activos", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int opcion = JOptionPane.showConfirmDialog(
                this,
                "¿Seguro que desea eliminar al usuario \"" + seleccionado.getNombre() + "\"?",
                "Confirmar eliminacion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (opcion != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            gestorUsuarios.eliminarUsuario(seleccionado.getIdUsuario());
            JOptionPane.showMessageDialog(this, "Usuario eliminado correctamente", "Usuarios",
                    JOptionPane.INFORMATION_MESSAGE);
            cargarTabla();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void actualizar() {
        cargarTabla();
    }

    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        for (Usuario u : gestorUsuarios.obtenerUsuarios()) {
            String curso = (u instanceof Estudiante) ? ((Estudiante) u).getCurso() : "-";
            modeloTabla.addRow(new Object[]{
                    u.getIdUsuario(),
                    u.getNombre(),
                    u.getRut(),
                    u.getCorreo(),
                    u.getRol(),
                    curso
            });
        }
        actualizarBotones();
    }

    // - Metodos Auxiliares
    private void actualizarBotones() {
        boolean haySeleccion = tablaUsuarios.getSelectedRow() != -1;
        btnEditar.setEnabled(haySeleccion);
        btnEliminar.setEnabled(haySeleccion);
        btnVerHistorial.setEnabled(haySeleccion);
    }

    private Usuario obtenerUsuario() {
        int fila = tablaUsuarios.getSelectedRow();
        if (fila == -1) {
            return null;
        }
        int id = (int) modeloTabla.getValueAt(fila, 0);
        return gestorUsuarios.buscarPorId(id);
    }
}