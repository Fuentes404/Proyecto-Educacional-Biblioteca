package view;

import controller.GestorLibros;
import model.Categoria;
import model.Libro;

import javax.swing.*;
import java.awt.*;

public class VentanaNuevoLibro extends JDialog {

    // - Atributos
    private JPanel panelCampos;
    private JPanel panelBotones;
    private JTextField txtIsbn;
    private JTextField txtTitulo;
    private JTextField txtAutor;
    private JTextField txtEditorial;
    private JComboBox<Categoria> cmbCategoria;
    private JTextField txtStock;
    private JButton btnGuardar;
    private JButton btnCancelar;

    // - Dependencias
    private final GestorLibros gestorLibros;

    // - Constructor
    public VentanaNuevoLibro(Window padre, GestorLibros gestorLibros) {
        super(padre, "Nuevo Libro", ModalityType.APPLICATION_MODAL);
        this.gestorLibros = gestorLibros;

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
        txtIsbn = new JTextField();
        txtTitulo = new JTextField();
        txtAutor = new JTextField();
        txtEditorial = new JTextField();
        cmbCategoria = new JComboBox<>(gestorLibros.obtenerCategorias().toArray(new Categoria[0]));
        txtStock = new JTextField();
        btnGuardar = new JButton("Guardar");
        btnCancelar = new JButton("Cancelar");

        // Muestra solo el nombre de la categoria
        cmbCategoria.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Categoria) {
                    setText(((Categoria) value).getNombre());
                }
                return this;
            }
        });
    }

    private void configurarDiseno() {
        panelCampos.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));
        panelCampos.add(new JLabel("ISBN: "));
        panelCampos.add(txtIsbn);
        panelCampos.add(new JLabel("Titulo: "));
        panelCampos.add(txtTitulo);
        panelCampos.add(new JLabel("Autor: "));
        panelCampos.add(txtAutor);
        panelCampos.add(new JLabel("Editorial: "));
        panelCampos.add(txtEditorial);
        panelCampos.add(new JLabel("Categoria: "));
        panelCampos.add(cmbCategoria);
        panelCampos.add(new JLabel("Stock: "));
        panelCampos.add(txtStock);

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
    private void guardar() {
        int stock;
        try {
            stock = Integer.parseInt(txtStock.getText().trim());
        } catch (NumberFormatException ex) {
            mostrarError("El stock debe ser un numero entero");
            return;
        }

        try {
            Libro libro = new Libro(
                    txtIsbn.getText().trim(),
                    txtTitulo.getText().trim(),
                    txtAutor.getText().trim(),
                    txtEditorial.getText().trim(),
                    (Categoria) cmbCategoria.getSelectedItem(),
                    stock
            );
            gestorLibros.crearLibro(libro);
            mostrarMensaje("Libro creado correctamente");
            dispose();
        } catch (IllegalArgumentException ex) {
            mostrarError(ex.getMessage());
        }
    }

    // - Metodos Funcionales
    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Libros", JOptionPane.INFORMATION_MESSAGE);
    }

    public void mostrarError(String mensajeError) {
        JOptionPane.showMessageDialog(this, mensajeError, "Error", JOptionPane.ERROR_MESSAGE);
    }
}