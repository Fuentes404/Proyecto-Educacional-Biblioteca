package view;

import controller.GestorLibros;
import controller.GestorPrestamos;
import controller.GestorUsuarios;
import model.Prestamo;
import model.TipoRol;
import model.Usuario;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class PanelPrestamos extends JPanel {

    // - Atributos
    private JLabel lblTitulo;
    private JPanel panelBotones;
    private JPanel panelBusqueda;
    private JPanel panelVer;
    private JButton btnNuevo;
    private JButton btnEditar;
    private JButton btnEliminar;
    private JButton btnDevolucion;
    private JButton btnActualizar;
    private JLabel lblBuscar;
    private JTextField txtBuscar;
    private JLabel lblVer;
    private JComboBox<String> cmbVer;

    // - Tabla
    private JTable tablaPrestamos;
    private DefaultTableModel modeloTabla;
    private JScrollPane scrollTabla;

    // - Dependencias
    private final Usuario usuario;
    private final GestorPrestamos gestorPrestamos;
    private final GestorUsuarios gestorUsuarios;
    private final GestorLibros gestorLibros;

    // - Constructor
    public PanelPrestamos(Usuario usuario, GestorPrestamos gestorPrestamos,
                          GestorUsuarios gestorUsuarios, GestorLibros gestorLibros) {
        this.usuario = usuario;
        this.gestorPrestamos = gestorPrestamos;
        this.gestorUsuarios = gestorUsuarios;
        this.gestorLibros = gestorLibros;

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
        lblTitulo = new JLabel(esAdmin() ? "Gestion de Prestamos" : "Mis Prestamos");
        panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelBusqueda = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelVer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnNuevo = new JButton("Nuevo");
        btnEditar = new JButton("Editar");
        btnEliminar = new JButton("Eliminar");
        btnDevolucion = new JButton("Registrar devolucion");
        btnActualizar = new JButton("Actualizar");
        lblBuscar = new JLabel("Buscar:", SwingConstants.RIGHT);
        txtBuscar = new JTextField(15);
        lblVer = new JLabel("Ver:", SwingConstants.RIGHT);
        cmbVer = new JComboBox<>(new String[]{"Todos", "En prestamo actualmente"});

        btnEditar.setEnabled(false);
        btnEliminar.setEnabled(false);
        btnDevolucion.setEnabled(false);

        // Tabla
        String[] columnas = {"ID Prestamo", "Libro", "Estudiante", "Fecha Prestamo", "Fecha Devolucion", "Devuelto"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        tablaPrestamos = new JTable(modeloTabla);
        scrollTabla = new JScrollPane(tablaPrestamos);
    }

    private void configurarDiseno() {

        lblVer.setPreferredSize(lblBuscar.getPreferredSize());
        cmbVer.setPreferredSize(txtBuscar.getPreferredSize());

        lblTitulo.setFont(lblTitulo.getFont().deriveFont(Font.BOLD, 24f));
        panelBusqueda.add(lblBuscar);
        panelBusqueda.add(txtBuscar);
        JPanel filaTitulo = new JPanel(new BorderLayout());
        filaTitulo.add(lblTitulo, BorderLayout.WEST);
        filaTitulo.add(panelBusqueda, BorderLayout.EAST);

        if (esAdmin()) {
            panelBotones.add(btnNuevo);
            panelBotones.add(btnEditar);
            panelBotones.add(btnEliminar);
            panelBotones.add(btnDevolucion);
            panelBotones.add(btnActualizar);
        }
        panelVer.add(lblVer);
        panelVer.add(cmbVer);
        JPanel filaBotones = new JPanel(new BorderLayout());
        filaBotones.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        filaBotones.add(panelBotones, BorderLayout.WEST);
        filaBotones.add(panelVer, BorderLayout.EAST);

        JPanel contenedorSuperior = new JPanel(new BorderLayout());
        contenedorSuperior.add(filaTitulo, BorderLayout.NORTH);
        contenedorSuperior.add(filaBotones, BorderLayout.CENTER);
        add(contenedorSuperior, BorderLayout.NORTH);

        tablaPrestamos.setRowHeight(25);
        tablaPrestamos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaPrestamos.getTableHeader().setReorderingAllowed(false);

        tablaPrestamos.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tabla, Object valor, boolean seleccionada,
                                                           boolean foco, int fila, int columna) {
                super.getTableCellRendererComponent(tabla, valor, seleccionada, foco, fila, columna);
                boolean atrasado = "No".equals(modeloTabla.getValueAt(fila, 5))
                        && valor instanceof LocalDate
                        && ((LocalDate) valor).isBefore(LocalDate.now());
                setForeground(atrasado ? Color.RED : tabla.getForeground());
                return this;
            }
        });

        add(scrollTabla, BorderLayout.CENTER);
    }

    private void configurarEventos() {
        btnNuevo.addActionListener(e -> nuevoPrestamo());
        btnEditar.addActionListener(e -> editarPrestamo());
        btnEliminar.addActionListener(e -> eliminarPrestamo());
        btnDevolucion.addActionListener(e -> registrarDevolucion());
        btnActualizar.addActionListener(e -> cargarTabla());
        cmbVer.addActionListener(e -> cargarTabla());

        // Busqueda en vivo: filtra con cada letra escrita
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                cargarTabla();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                cargarTabla();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                cargarTabla();
            }
        });

        // Habilita los botones solo si hay una fila seleccionada
        tablaPrestamos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                actualizarBotones();
            }
        });
    }

    // - Metodos Funcionales
    private void nuevoPrestamo() {
        if (!gestorPrestamos.puedeGestionarPrestamos()) {
            JOptionPane.showMessageDialog(this, "Debe existir al menos un estudiante y un libro", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        Window padre = SwingUtilities.getWindowAncestor(this);
        new VentanaNuevoPrestamo(padre, gestorPrestamos, gestorUsuarios, gestorLibros).setVisible(true);
        cargarTabla();
    }

    private void editarPrestamo() {
        Prestamo prestamo = obtenerPrestamo();
        if (prestamo == null) {
            return;
        }
        Window padre = SwingUtilities.getWindowAncestor(this);
        new VentanaEditarPrestamo(padre, prestamo, gestorPrestamos).setVisible(true);
        cargarTabla();
    }

    private void eliminarPrestamo() {
        Prestamo prestamo = obtenerPrestamo();
        if (prestamo == null) {
            return;
        }

        int opcion = JOptionPane.showConfirmDialog(
                this,
                "¿Seguro que desea eliminar el prestamo " + prestamo.getIdPrestamo() + "?",
                "Confirmar eliminacion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (opcion != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            gestorPrestamos.eliminarPrestamo(prestamo.getIdPrestamo());
            JOptionPane.showMessageDialog(this, "Prestamo eliminado correctamente", "Prestamos",
                    JOptionPane.INFORMATION_MESSAGE);
            cargarTabla();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void registrarDevolucion() {
        Prestamo prestamo = obtenerPrestamo();
        if (prestamo == null) {
            return;
        }

        int opcion = JOptionPane.showConfirmDialog(
                this,
                "¿Registrar la devolucion de \"" + prestamo.getLibro().getTitulo() + "\"?",
                "Confirmar devolucion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        if (opcion != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            gestorPrestamos.registrarDevolucion(prestamo.getIdPrestamo());
            JOptionPane.showMessageDialog(this, "Devolucion registrada correctamente", "Prestamos",
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
        for (Prestamo p : obtenerPrestamosAMostrar()) {
            modeloTabla.addRow(new Object[]{
                    p.getIdPrestamo(),
                    p.getLibro().getTitulo(),
                    p.getEstudiante().getNombre(),
                    p.getFechaPrestamo(),
                    p.getFechaDevolucion(),
                    p.isDevuelto() ? "Si" : "No"
            });
        }
        actualizarBotones();
    }

    // - Metodos Auxiliares
    private boolean esAdmin() {
        return usuario.getRol() == TipoRol.ADMIN;
    }

    private ArrayList<Prestamo> obtenerPrestamosAMostrar() {
        boolean soloActivos = cmbVer.getSelectedIndex() == 1;
        ArrayList<Prestamo> lista = esAdmin()
                ? gestorPrestamos.obtenerPrestamos()
                : gestorPrestamos.obtenerPrestamosEstudiante(usuario.getIdUsuario());

        if (soloActivos) {
            lista.removeIf(Prestamo::isDevuelto);
        }
        return gestorPrestamos.buscarPrestamos(lista, txtBuscar.getText());
    }

    private void actualizarBotones() {
        Prestamo seleccionado = obtenerPrestamo();
        boolean haySeleccion = seleccionado != null;
        btnEditar.setEnabled(haySeleccion);
        btnEliminar.setEnabled(haySeleccion);
        // Solo se puede devolver un prestamo que sigue activo
        btnDevolucion.setEnabled(haySeleccion && !seleccionado.isDevuelto());
    }

    private Prestamo obtenerPrestamo() {
        int fila = tablaPrestamos.getSelectedRow();
        if (fila == -1) {
            return null;
        }
        int id = (int) modeloTabla.getValueAt(fila, 0);
        return gestorPrestamos.buscarPorId(id);
    }

}