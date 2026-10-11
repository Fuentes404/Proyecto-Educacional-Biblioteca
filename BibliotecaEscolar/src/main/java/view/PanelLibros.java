package view;

import controller.GestorLibros;
import controller.GestorPrestamos;
import model.Libro;
import model.TipoRol;
import model.Usuario;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class PanelLibros extends JPanel {

    // - Atributos
    private JLabel lblTitulo;
    private JPanel panelBotones;
    private JPanel panelBusqueda;
    private JPanel panelVer;
    private JButton btnNuevo;
    private JButton btnEditar;
    private JButton btnEliminar;
    private JButton btnActualizar;
    private JLabel lblBuscar;
    private JTextField txtBuscar;
    private JLabel lblVer;
    private JComboBox<String> cmbVer;

    // - Tabla
    private JTable tablaLibros;
    private DefaultTableModel modeloTabla;
    private JScrollPane scrollTabla;

    // - Dependencias
    private final Usuario usuario;
    private final GestorLibros gestorLibros;
    private final GestorPrestamos gestorPrestamos;

    // - Constructor
    public PanelLibros(Usuario usuario, GestorLibros gestorLibros, GestorPrestamos gestorPrestamos) {
        this.usuario = usuario;
        this.gestorLibros = gestorLibros;
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
        lblTitulo = new JLabel("Gestion de Libros");
        panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelBusqueda = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelVer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnNuevo = new JButton("Nuevo");
        btnEditar = new JButton("Editar");
        btnEliminar = new JButton("Eliminar");
        btnActualizar = new JButton("Actualizar");
        lblBuscar = new JLabel("Buscar:", SwingConstants.RIGHT);
        txtBuscar = new JTextField(15);
        lblVer = new JLabel("Ver:", SwingConstants.RIGHT);
        cmbVer = new JComboBox<>(new String[]{"Todos", "Mas prestados", "Menos prestados"});

        // Solo se habilitan al seleccionar un libro
        btnEditar.setEnabled(false);
        btnEliminar.setEnabled(false);

        // Tabla (celdas no editables)
        String[] columnas = {"ISBN", "Titulo", "Autor", "Editorial", "Categoria", "Stock"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        tablaLibros = new JTable(modeloTabla);
        scrollTabla = new JScrollPane(tablaLibros);
    }

    private void configurarDiseno() {
        // Mismo tamaño para etiquetas y campos (quedan alineados)
        lblVer.setPreferredSize(lblBuscar.getPreferredSize());
        cmbVer.setPreferredSize(txtBuscar.getPreferredSize());

        // Fila 1: titulo a la izquierda, buscar a la derecha
        lblTitulo.setFont(lblTitulo.getFont().deriveFont(Font.BOLD, 24f));
        panelBusqueda.add(lblBuscar);
        panelBusqueda.add(txtBuscar);
        JPanel filaTitulo = new JPanel(new BorderLayout());
        filaTitulo.add(lblTitulo, BorderLayout.WEST);
        filaTitulo.add(panelBusqueda, BorderLayout.EAST);

        // Fila 2: botones a la izquierda (solo ADMIN), ver a la derecha
        if (esAdmin()) {
            panelBotones.add(btnNuevo);
            panelBotones.add(btnEditar);
            panelBotones.add(btnEliminar);
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

        // Tabla al centro
        tablaLibros.setRowHeight(25);
        tablaLibros.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaLibros.getTableHeader().setReorderingAllowed(false);
        add(scrollTabla, BorderLayout.CENTER);
    }

    private void configurarEventos() {
        btnNuevo.addActionListener(e -> nuevoLibro());
        btnEditar.addActionListener(e -> editarLibro());
        btnEliminar.addActionListener(e -> eliminarLibro());
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

        // Habilita Editar y Eliminar solo si hay una fila seleccionada
        tablaLibros.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                actualizarBotones();
            }
        });
    }

    // - Metodos Funcionales
    private void nuevoLibro() {
        Window padre = SwingUtilities.getWindowAncestor(this);
        new VentanaNuevoLibro(padre, gestorLibros).setVisible(true);
        cargarTabla();
    }

    private void editarLibro() {
        Libro libro = obtenerLibro();
        if (libro == null) {
            return;
        }
        Window padre = SwingUtilities.getWindowAncestor(this);
        new VentanaEditarLibro(padre, libro, gestorLibros).setVisible(true);
        cargarTabla();
    }

    private void eliminarLibro() {
        Libro libro = obtenerLibro();
        if (libro == null) {
            return;
        }

        // No se puede eliminar un libro que esta prestado
        if (gestorPrestamos.contarActivos(libro.getIsbn()) > 0) {
            JOptionPane.showMessageDialog(this, "No se puede eliminar un libro con prestamos activos", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int opcion = JOptionPane.showConfirmDialog(
                this,
                "¿Seguro que desea eliminar el libro \"" + libro.getTitulo() + "\"?",
                "Confirmar eliminacion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (opcion != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            gestorLibros.eliminarLibro(libro.getIsbn());
            JOptionPane.showMessageDialog(this, "Libro eliminado correctamente", "Libros",
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
        for (Libro l : obtenerLibrosAMostrar()) {
            modeloTabla.addRow(new Object[]{
                    l.getIsbn(),
                    l.getTitulo(),
                    l.getAutor(),
                    l.getEditorial(),
                    l.getCategoria().getNombre(),
                    l.getStock()
            });
        }
        actualizarBotones();
    }

    // - Metodos Auxiliares
    private boolean esAdmin() {
        return usuario.getRol() == TipoRol.ADMIN;
    }

    private ArrayList<Libro> obtenerLibrosAMostrar() {
        ArrayList<Libro> lista = gestorLibros.buscarLibros(txtBuscar.getText());

        int ver = cmbVer.getSelectedIndex();
        if (ver == 1) {
            lista.sort((a, b) -> Integer.compare(
                    gestorPrestamos.contarPrestamosLibro(b.getIsbn()),
                    gestorPrestamos.contarPrestamosLibro(a.getIsbn())));
        } else if (ver == 2) {
            lista.sort((a, b) -> Integer.compare(
                    gestorPrestamos.contarPrestamosLibro(a.getIsbn()),
                    gestorPrestamos.contarPrestamosLibro(b.getIsbn())));
        }
        return lista;
    }

    private void actualizarBotones() {
        boolean haySeleccion = tablaLibros.getSelectedRow() != -1;
        btnEditar.setEnabled(haySeleccion);
        btnEliminar.setEnabled(haySeleccion);
    }

    private Libro obtenerLibro() {
        int fila = tablaLibros.getSelectedRow();
        if (fila == -1) {
            return null;
        }
        String isbn = (String) modeloTabla.getValueAt(fila, 0);
        return gestorLibros.buscarPorIsbn(isbn);
    }
}