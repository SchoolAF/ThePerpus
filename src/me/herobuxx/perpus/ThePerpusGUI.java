package me.herobuxx.perpus;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.sql.*;

public class ThePerpusGUI extends JFrame implements ActionListener {

    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost/AlgoDB";
    private static final String USER = "deez";
    private static final String PASS = "54321";

    private Connection conn;
    private Statement stmt;
    private ResultSet rs;

    private Label appTitle;
    private JButton insertButton, editButton, deleteButton;
    private JTable bookTable;
    private DefaultTableModel tableModel;

    public ThePerpusGUI() {
        setTitle("ThePerpus");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        ImageIcon addIcon = new ImageIcon("res/icons/add.png"); // Replace with your icon file path
        ImageIcon editIcon = new ImageIcon("res/icons/edit.png"); // Replace with your icon file path
        ImageIcon deleteIcon = new ImageIcon("res/icons/delete.png"); // Replace with your icon file path

        insertButton = new JButton(addIcon);
        editButton = new JButton(editIcon);
        deleteButton = new JButton(deleteIcon);

        insertButton.addActionListener(this);
        editButton.addActionListener(this);
        deleteButton.addActionListener(this);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 10)); // Set alignment to right for buttons
        buttonPanel.add(insertButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        tableModel = new DefaultTableModel();
        bookTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(bookTable);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(buttonPanel, BorderLayout.NORTH);
        getContentPane().add(tableScrollPane, BorderLayout.CENTER);

        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                int width = getContentPane().getWidth();
                int height = getContentPane().getHeight();
                tableScrollPane.setPreferredSize(new Dimension(width, height - buttonPanel.getHeight()));
                revalidate();
            }
        });

        connectToDatabase();
        showData(); // Show the table by default
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == insertButton) {
            insertBuku();
        } else if (e.getSource() == editButton) {
            updateBuku();
        } else if (e.getSource() == deleteButton) {
            deleteBuku();
        }
    }

    private void showData() {
        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);
        String sql = "SELECT * FROM buku";
        try {
            rs = stmt.executeQuery(sql);
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                tableModel.addColumn(metaData.getColumnName(i));
            }
            while (rs.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i - 1] = rs.getObject(i);
                }
                tableModel.addRow(row);
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
            showData(); // Refresh table after insertion
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
            showData(); // Refresh table after update
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
            showData(); // Refresh table after deletion
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
                perpusGUI.setVisible(true);
            }
        });
    }
}
