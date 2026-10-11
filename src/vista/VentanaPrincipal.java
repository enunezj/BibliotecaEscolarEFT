
package vista;

import model.Persona;
import model.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class VentanaPrincipal extends JFrame {

    private final List<JFrame> ventanasAbiertas = new ArrayList<>();

    public VentanaPrincipal(Usuario usuario) {

        boolean esBibliotecario =
                "bibliotecario".equalsIgnoreCase(usuario.getRol());

        setTitle("Sistema de Biblioteca Escolar");

        setSize(
                esBibliotecario ? 650 : 600,
                esBibliotecario ? 500 : 410
        );

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(15, 15));

        panel.setBorder(
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        );

        JLabel titulo = new JLabel(
                "SISTEMA DE GESTIÓN DE BIBLIOTECA",
                SwingConstants.CENTER
        );

        titulo.setFont(new Font("Arial", Font.BOLD, 22));

        JLabel bienvenida = crearBienvenida(usuario);

        JLabel rol = new JLabel(
                "Rol: " + usuario.getRol(),
                SwingConstants.CENTER
        );

        JPanel centro = new JPanel(
                new GridLayout(0, 1, 10, 10)
        );

        centro.add(bienvenida);
        centro.add(rol);

        if (esBibliotecario) {

            JButton botonCategorias = new JButton(
                    "Gestionar categorías"
            );

            botonCategorias.addActionListener(evento ->
                    abrirVentana(new VentanaCategorias(usuario))
            );

            centro.add(botonCategorias);

            JButton botonEstudiantes = new JButton(
                    "Gestionar estudiantes"
            );

            botonEstudiantes.addActionListener(evento ->
                    abrirVentana(new VentanaEstudiantes(usuario))
            );

            centro.add(botonEstudiantes);
        }

        JButton botonLibros = new JButton(
                esBibliotecario
                        ? "Gestionar libros"
                        : "Consultar libros"
        );

        botonLibros.addActionListener(evento ->
                abrirVentana(new VentanaLibros(usuario))
        );

        centro.add(botonLibros);

        JButton botonPrestamos = new JButton(
                "Préstamos y devoluciones"
        );

        botonPrestamos.addActionListener(evento ->
                abrirVentana(new VentanaPrestamos(usuario))
        );

        centro.add(botonPrestamos);

        JButton botonReportes = new JButton(
                esBibliotecario
                        ? "Reportes"
                        : "Mi historial"
        );

        botonReportes.addActionListener(evento ->
                abrirVentana(new VentanaReportes(usuario))
        );

        centro.add(botonReportes);

        JButton botonSalir = new JButton("Cerrar sesión");

        botonSalir.addActionListener(evento -> cerrarSesion());

        panel.add(titulo, BorderLayout.NORTH);
        panel.add(centro, BorderLayout.CENTER);
        panel.add(botonSalir, BorderLayout.SOUTH);

        add(panel);
    }

    private void abrirVentana(JFrame ventana) {

        ventanasAbiertas.add(ventana);

        ventana.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosed(WindowEvent evento) {

                ventanasAbiertas.remove(ventana);
            }
        });

        ventana.setVisible(true);
    }

    private void cerrarSesion() {

        for (JFrame ventana :
                new ArrayList<>(ventanasAbiertas)) {

            ventana.dispose();
        }

        ventanasAbiertas.clear();

        dispose();

        new VentanaLogin().setVisible(true);
    }

    private JLabel crearBienvenida(Persona persona) {

        return new JLabel(
                persona.obtenerDescripcion(),
                SwingConstants.CENTER
        );
    }
}
