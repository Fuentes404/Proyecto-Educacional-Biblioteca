package view;

import controller.GestorLibros;
import controller.GestorPrestamos;
import controller.GestorUsuarios;
import model.Estudiante;
import model.Libro;
import model.Prestamo;
import model.Usuario;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.concurrent.ExecutionException;

public class VentanaNuevoPrestamo extends JDialog {

    // - Atributos
    private JPanel panelCampos;
    private JPanel panelBotones;
    private JComboBox<Estudiante> cmbEstudiante;
    private JComboBox<Libro> cmbLibro;
    private JTextField txtFechaPrestamo;
    private JTextField txtFechaDevolucion;
    private JButton btnGuardar;
    private JButton btnCancelar;

    // - Dependencias
    private final GestorPrestamos gestorPrestamos;
    private final GestorUsuarios gestorUsuarios;
    private final GestorLibros gestorLibros;

    // - Constructor
    public VentanaNuevoPrestamo(Window padre, GestorPrestamos gestorPrestamos,
                                GestorUsuarios gestorUsuarios, GestorLibros gestorLibros) {
        super(padre, "Nuevo Prestamo", ModalityType.APPLICATION_MODAL);
        this.gestorPrestamos = gestorPrestamos;
        this.gestorUsuarios = gestorUsuarios;
        this.gestorLibros = gestorLibros;

        configurarVentana();
        inicializarComponentes();
        configurarDiseno();
        configurarEventos();
    }

    private void configurarVentana() {
        setSize(450, 290);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(getOwner());
        setResizable(false);
    }

    private void inicializarComponentes() {
        panelCampos = new JPanel(new GridLayout(4, 2, 20, 15));
        panelBotones = new JPanel(new FlowLayout());
        cmbEstudiante = new JComboBox<>(obtenerEstudiantes());
        cmbLibro = new JComboBox<>(gestorLibros.obtenerLibros().toArray(new Libro[0]));
        txtFechaPrestamo = new JTextField(LocalDate.now().toString());
        txtFechaDevolucion = new JTextField(LocalDate.now().plusDays(7).toString());
        btnGuardar = new JButton("Guardar");
        btnCancelar = new JButton("Cancelar");
        txtFechaPrestamo.setEditable(false);

        cmbEstudiante.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Estudiante) {
                    Estudiante e = (Estudiante) value;
                    setText(e.getNombre() + " (" + e.getCurso() + ")");
                }
                return this;
            }
        });

        cmbLibro.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Libro) {
                    Libro l = (Libro) value;
                    setText(l.getTitulo() + " (stock: " + l.getStock() + ")");
                }
                return this;
            }
        });
    }

    private void configurarDiseno() {
        panelCampos.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));
        panelCampos.add(new JLabel("Estudiante: "));
        panelCampos.add(cmbEstudiante);
        panelCampos.add(new JLabel("Libro: "));
        panelCampos.add(cmbLibro);
        panelCampos.add(new JLabel("Fecha prestamo: "));
        panelCampos.add(txtFechaPrestamo);
        panelCampos.add(new JLabel("Fecha devolucion (AAAA-MM-DD): "));
        panelCampos.add(txtFechaDevolucion);

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
        LocalDate fechaDevolucion;
        try {
            fechaDevolucion = LocalDate.parse(txtFechaDevolucion.getText().trim());
        } catch (DateTimeParseException ex) {
            mostrarError("La fecha de devolucion debe tener el formato AAAA-MM-DD");
            return;
        }

        Prestamo prestamo = new Prestamo(
                0,
                (Estudiante) cmbEstudiante.getSelectedItem(),
                (Libro) cmbLibro.getSelectedItem(),
                LocalDate.now(),
                fechaDevolucion,
                false
        );

        establecerOcupado(true);

        new SwingWorker<Void, Void>() {
            // Hilo en segundo plano: aqui NO se toca la interfaz
            @Override
            protected Void doInBackground() {
                gestorPrestamos.crearPrestamo(prestamo);
                return null;
            }

            // Vuelve al hilo de la interfaz cuando termina el trabajo
            @Override
            protected void done() {
                establecerOcupado(false);
                try {
                    get();
                    mostrarMensaje("Prestamo creado correctamente");
                    dispose();
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException ex) {
                    Throwable causa = ex.getCause();
                    mostrarError(causa.getMessage());
                }
            }
        }.execute();
    }

    // - Metodos Funcionales
    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Prestamos", JOptionPane.INFORMATION_MESSAGE);
    }

    public void mostrarError(String mensajeError) {
        JOptionPane.showMessageDialog(this, mensajeError, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // - Metodos Auxiliares
    private void establecerOcupado(boolean ocupado) {
        cmbEstudiante.setEnabled(!ocupado);
        cmbLibro.setEnabled(!ocupado);
        txtFechaDevolucion.setEnabled(!ocupado);
        btnGuardar.setEnabled(!ocupado);
        btnCancelar.setEnabled(!ocupado);
        btnGuardar.setText(ocupado ? "Guardando..." : "Guardar");
        setCursor(ocupado ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor());
        setDefaultCloseOperation(ocupado ? DO_NOTHING_ON_CLOSE : DISPOSE_ON_CLOSE);
    }

    private Estudiante[] obtenerEstudiantes() {
        java.util.ArrayList<Estudiante> estudiantes = new java.util.ArrayList<>();
        for (Usuario u : gestorUsuarios.obtenerUsuarios()) {
            if (u instanceof Estudiante) {
                estudiantes.add((Estudiante) u);
            }
        }
        return estudiantes.toArray(new Estudiante[0]);
    }

}