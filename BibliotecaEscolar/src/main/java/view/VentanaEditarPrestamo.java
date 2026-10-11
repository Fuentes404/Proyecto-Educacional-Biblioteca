package view;

import controller.GestorPrestamos;
import model.Prestamo;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class VentanaEditarPrestamo extends JDialog {

    // - Atributos
    private JPanel panelCampos;
    private JPanel panelBotones;
    private JTextField txtId;
    private JTextField txtEstudiante;
    private JTextField txtLibro;
    private JTextField txtFechaPrestamo;
    private JTextField txtFechaDevolucion;
    private JTextField txtEstado;
    private JButton btnGuardar;
    private JButton btnCancelar;

    // - Dependencias
    private final Prestamo prestamo;
    private final GestorPrestamos gestorPrestamos;

    // - Constructor
    public VentanaEditarPrestamo(Window padre, Prestamo prestamo, GestorPrestamos gestorPrestamos) {
        super(padre, "Editar Prestamo", ModalityType.APPLICATION_MODAL);
        this.prestamo = prestamo;
        this.gestorPrestamos = gestorPrestamos;

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
        txtEstudiante = new JTextField();
        txtLibro = new JTextField();
        txtFechaPrestamo = new JTextField();
        txtFechaDevolucion = new JTextField();
        txtEstado = new JTextField();
        btnGuardar = new JButton("Guardar");
        btnCancelar = new JButton("Cancelar");

        txtId.setEditable(false);
        txtEstudiante.setEditable(false);
        txtLibro.setEditable(false);
        txtFechaPrestamo.setEditable(false);
        txtEstado.setEditable(false);

        boolean activo = !prestamo.isDevuelto();
        txtFechaDevolucion.setEnabled(activo);
        btnGuardar.setEnabled(activo);
    }

    private void configurarDiseno() {
        panelCampos.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));
        panelCampos.add(new JLabel("ID: "));
        panelCampos.add(txtId);
        panelCampos.add(new JLabel("Estudiante: "));
        panelCampos.add(txtEstudiante);
        panelCampos.add(new JLabel("Libro: "));
        panelCampos.add(txtLibro);
        panelCampos.add(new JLabel("Fecha prestamo: "));
        panelCampos.add(txtFechaPrestamo);
        panelCampos.add(new JLabel("Fecha devolucion (AAAA-MM-DD): "));
        panelCampos.add(txtFechaDevolucion);
        panelCampos.add(new JLabel("Estado: "));
        panelCampos.add(txtEstado);

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
        txtId.setText(String.valueOf(prestamo.getIdPrestamo()));
        txtEstudiante.setText(prestamo.getEstudiante().getNombre());
        txtLibro.setText(prestamo.getLibro().getTitulo());
        txtFechaPrestamo.setText(prestamo.getFechaPrestamo().toString());
        txtFechaDevolucion.setText(prestamo.getFechaDevolucion().toString());
        txtEstado.setText(prestamo.isDevuelto() ? "Devuelto" : "En prestamo");
    }

    private void guardar() {
        LocalDate fechaDevolucion;
        try {
            fechaDevolucion = LocalDate.parse(txtFechaDevolucion.getText().trim());
        } catch (DateTimeParseException ex) {
            mostrarError("La fecha de devolucion debe tener el formato AAAA-MM-DD");
            return;
        }

        try {
            gestorPrestamos.editarPrestamo(prestamo.getIdPrestamo(), fechaDevolucion);
            mostrarMensaje("Prestamo actualizado correctamente");
            dispose();
        } catch (IllegalArgumentException ex) {
            mostrarError(ex.getMessage());
        }
    }

    // - Metodos Funcionales
    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Prestamos", JOptionPane.INFORMATION_MESSAGE);
    }

    public void mostrarError(String mensajeError) {
        JOptionPane.showMessageDialog(this, mensajeError, "Error", JOptionPane.ERROR_MESSAGE);
    }
}