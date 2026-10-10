
package vista;

import model.Persona;
import model.Usuario;

import javax.swing.*;
import java.awt.*;

public class VentanaPrincipal extends JFrame {

    public VentanaPrincipal(Usuario usuario) {

        setTitle("Sistema de Biblioteca Escolar");
        setSize(750, 550);
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

        JPanel centro = new JPanel(new GridLayout(0, 1, 10, 10));

        centro.add(bienvenida);
        centro.add(rol);

        boolean esBibliotecario =
                "bibliotecario".equalsIgnoreCase(usuario.getRol());

        if (esBibliotecario) {

            JButton botonCategorias = new JButton(
                    "Gestionar categorías"
            );

            botonCategorias.addActionListener(_ ->
                    new VentanaCategorias(usuario).setVisible(true)
            );

            centro.add(botonCategorias);

            JButton botonEstudiantes = new JButton(
                    "Gestionar estudiantes"
            );

            botonEstudiantes.addActionListener(_ ->
                    new VentanaEstudiantes(usuario).setVisible(true)
            );

            centro.add(botonEstudiantes);
        }

        JButton botonLibros = new JButton(
                esBibliotecario ? "Gestionar libros" : "Consultar libros"
        );

        botonLibros.addActionListener(_ ->
                new VentanaLibros(usuario).setVisible(true)
        );

        centro.add(botonLibros);

        JButton botonPrestamos = new JButton(
                "Préstamos y devoluciones"
        );

        botonPrestamos.addActionListener(_ ->
                new VentanaPrestamos(usuario).setVisible(true)
        );

        centro.add(botonPrestamos);

        JButton botonReportes = new JButton(
                esBibliotecario ? "Reportes" : "Mi historial"
        );

        botonReportes.addActionListener(_ ->
                new VentanaReportes(usuario).setVisible(true)
        );

        centro.add(botonReportes);

        JButton botonSalir = new JButton("Cerrar sesión");

        botonSalir.addActionListener(_ -> {
            dispose();
            new VentanaLogin().setVisible(true);
        });

        panel.add(titulo, BorderLayout.NORTH);
        panel.add(centro, BorderLayout.CENTER);
        panel.add(botonSalir, BorderLayout.SOUTH);

        add(panel);
    }

    private JLabel crearBienvenida(Persona persona) {

        return new JLabel(
                persona.obtenerDescripcion(),
                SwingConstants.CENTER
        );
    }
}
