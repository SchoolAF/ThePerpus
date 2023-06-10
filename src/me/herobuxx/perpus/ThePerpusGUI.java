package me.herobuxx.perpus;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class ThePerpusGUI extends JFrame implements ActionListener {

    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost/AlgoDB";
    private static final String USER = "deez";
    private static final String PASS = "54321";

    private Connection conn;
    private Statement stmt;
    private ResultSet rs;

    private JButton insertButton, showButton, editButton, deleteButton;
    private JTextArea outputTextArea;

    public ThePerpusGUI() {
        setTitle("Perpus Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        insertButton = new JButton("Insert Data");
        showButton = new JButton("Show Data");
        editButton = new JButton("Edit Data");
        deleteButton = new JButton("Delete Data");
        outputTextArea = new JTextArea();

        insertButton.addActionListener(this);
        showButton.addActionListener(this);
        editButton.addActionListener(this);
        deleteButton.addActionListener(this);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 4));
        buttonPanel.add(insertButton);
        buttonPanel.add(showButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        JPanel outputPanel = new JPanel();
        outputPanel.setLayout(new BorderLayout());
        outputPanel.add(new JScrollPane(outputTextArea), BorderLayout.CENTER);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(buttonPanel, BorderLayout.NORTH);
        getContentPane().add(outputPanel, BorderLayout.CENTER);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == insertButton) {
            insertBuku();
        } else if (e.getSource() == showButton) {
            showData();
        } else if (e.getSource() == editButton) {
            showData();
            updateBuku();
        } else if (e.getSource() == deleteButton) {
            deleteBuku();
        }
    }

    private void showData() {
        outputTextArea.setText("");
        String sql = "SELECT * FROM buku";
        try {
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                int idBuku = rs.getInt("id_buku");
                String judul = rs.getString("judul");
                String pengarang = rs.getString("pengarang");

                outputTextArea.append(String.format("%d. %s -- (%s)\n", idBuku, judul, pengarang));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertBuku() {
        String judul = JOptionPane.showInputDialog(this, "Judul:");
        String pengarang = JOptionPane.showInputDialog(this, "Pengarang:");

        String sql = "INSERT INTO buku (judul, pengarang) VALUES (?, ?)";
        try {
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, judul);
            statement.setString(2, pengarang);
            statement.executeUpdate();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateBuku() {
        int idBuku = Integer.parseInt(JOptionPane.showInputDialog(this, "ID yang mau diedit:"));
        String judul = JOptionPane.showInputDialog(this, "Judul:");
        String pengarang = JOptionPane.showInputDialog(this, "Pengarang:");

        String sql = "UPDATE buku SET judul=?, pengarang=? WHERE id_buku=?";
        try {
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, judul);
            statement.setString(2, pengarang);
            statement.setInt(3, idBuku);
            statement.executeUpdate();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteBuku() {
        int idBuku = Integer.parseInt(JOptionPane.showInputDialog(this, "ID yang mau dihapus:"));
        String sql = "DELETE FROM buku WHERE id_buku=?";
        try {
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setInt(1, idBuku);
            statement.executeUpdate();
            statement.close();
            JOptionPane.showMessageDialog(this, "Data telah terhapus...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void connectToDatabase() {
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            stmt = conn.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void disconnectFromDatabase() {
        try {
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ThePerpusGUI perpusGUI = new ThePerpusGUI();
                perpusGUI.connectToDatabase();
                perpusGUI.setVisible(true);
            }
        });
    }
}
