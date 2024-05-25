package tugaslab;

import javax.swing.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class MahasiswaGUI extends JFrame {
    private JTextField tfNamaMK, tfSKS, tfNilai;
    private JTextField tfNama, tfNIM, tfProdi;
    private JButton btnAdd, btnUpdate, btnDelete, btnView;
    private JTable table;
    private DefaultTableModel tableModel;

    public MahasiswaGUI() {
        setTitle("Data Mahasiswa");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        JLabel lblNama = new JLabel("Nama:");
        lblNama.setBounds(20, 20, 100, 25);
        add(lblNama);

        tfNama = new JTextField();
        tfNama.setBounds(120, 20, 200, 25);
        add(tfNama);

        JLabel lblNIM = new JLabel("NIM:");
        lblNIM.setBounds(20, 60, 100, 25);
        add(lblNIM);

        tfNIM = new JTextField();
        tfNIM.setBounds(120, 60, 200, 25);
        add(tfNIM);

        JLabel lblNamaMK = new JLabel("Nama Mata Kuliah:");
        lblNamaMK.setBounds(20, 100, 150, 25);
        add(lblNamaMK);

        tfNamaMK = new JTextField();
        tfNamaMK.setBounds(170, 100, 200, 25);
        add(tfNamaMK);

        JLabel lblSKS = new JLabel("SKS:");
        lblSKS.setBounds(20, 140, 100, 25);
        add(lblSKS);

        tfSKS = new JTextField();
        tfSKS.setBounds(170, 140, 200, 25);
        add(tfSKS);

        JLabel lblProdi = new JLabel("Prodi:");
        lblProdi.setBounds(20, 180, 100, 25);
        add(lblProdi);

        tfProdi = new JTextField();
        tfProdi.setBounds(120, 180, 200, 25);
        add(tfProdi);

        JLabel lblNilai = new JLabel("Nilai:");
        lblNilai.setBounds(20, 220, 100, 25);
        add(lblNilai);

        tfNilai = new JTextField();
        tfNilai.setBounds(120, 220, 200, 25);
        add(tfNilai);

        btnAdd = new JButton("Add");
        btnAdd.setBounds(20, 260, 80, 25);
        add(btnAdd);

        btnUpdate = new JButton("Update");
        btnUpdate.setBounds(110, 260, 80, 25);
        add(btnUpdate);

        btnDelete = new JButton("Delete");
        btnDelete.setBounds(200, 260, 80, 25);
        add(btnDelete);

        btnView = new JButton("View");
        btnView.setBounds(290, 260, 80, 25);
        add(btnView);

        tableModel = new DefaultTableModel(new String[]{"ID", "Nama", "NIM", "Prodi", "Nama Mata Kuliah", "SKS", "Nilai"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(20, 300, 740, 250);
        add(scrollPane);

        btnAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    Connection con = Koneksi.getKoneksi();
                    
                    String sqlMahasiswa = "INSERT INTO mahasiswa (nama, nim, prodi) VALUES (?, ?, ?)";
                    PreparedStatement psMahasiswa = con.prepareStatement(sqlMahasiswa, Statement.RETURN_GENERATED_KEYS);
                    psMahasiswa.setString(1, tfNama.getText());
                    psMahasiswa.setString(2, tfNIM.getText());
                    psMahasiswa.setString(3, tfProdi.getText());
                    psMahasiswa.executeUpdate();
                    
                    ResultSet rs = psMahasiswa.getGeneratedKeys();
                    int mahasiswaId = -1;
                    if (rs.next()) {
                        mahasiswaId = rs.getInt(1);
                    }
                    
                    String sqlMatakuliah = "INSERT INTO matakuliah (nama_mk, sks) VALUES (?, ?)";
                    PreparedStatement psMatakuliah = con.prepareStatement(sqlMatakuliah, Statement.RETURN_GENERATED_KEYS);
                    psMatakuliah.setString(1, tfNamaMK.getText());
                    psMatakuliah.setString(2, tfSKS.getText());
                    psMatakuliah.executeUpdate();
                    
                    rs = psMatakuliah.getGeneratedKeys();
                    int matakuliahId = -1;
                    if (rs.next()) {
                        matakuliahId = rs.getInt(1);
                    }
                    
                    String sqlNilai = "INSERT INTO nilai (mahasiswa_id, matakuliah_id, nilai) VALUES (?, ?, ?)";
                    PreparedStatement psNilai = con.prepareStatement(sqlNilai);
                    psNilai.setInt(1, mahasiswaId);
                    psNilai.setInt(2, matakuliahId);
                    psNilai.setInt(3, Integer.parseInt(tfNilai.getText()));
                    psNilai.executeUpdate();
                    
                    JOptionPane.showMessageDialog(null, "Data Berhasil Ditambahkan");
                    viewData();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
                    System.out.println(ex.getMessage());
                }
            }
        });

        btnUpdate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    int row = table.getSelectedRow();
                    if (row == -1) {
                        JOptionPane.showMessageDialog(null, "Pilih Data yang Akan Diupdate");
                        return;
                    }
                    int id = (int) tableModel.getValueAt(row, 0);
                    Connection con = Koneksi.getKoneksi();
                    
                    String sqlMahasiswa = "UPDATE mahasiswa SET nama = ?, nim = ?, prodi = ? WHERE id = ?";
                    PreparedStatement psMahasiswa = con.prepareStatement(sqlMahasiswa);
                    psMahasiswa.setString(1, tfNama.getText());
                    psMahasiswa.setString(2, tfNIM.getText());
                    psMahasiswa.setString(3, tfProdi.getText());
                    psMahasiswa.setInt(4, id);
                    psMahasiswa.executeUpdate();
                    
                    String sqlMatakuliah = "UPDATE matakuliah SET nama_mk = ?, sks = ? WHERE id = (SELECT matakuliah_id FROM nilai WHERE mahasiswa_id = ?)";
                    PreparedStatement psMatakuliah = con.prepareStatement(sqlMatakuliah);
                    psMatakuliah.setString(1, tfNamaMK.getText());
                    psMatakuliah.setString(2, tfSKS.getText());
                    psMatakuliah.setInt(3, id);
                    psMatakuliah.executeUpdate();
                    
                    String sqlNilai = "UPDATE nilai SET nilai = ? WHERE mahasiswa_id = ?";
                    PreparedStatement psNilai = con.prepareStatement(sqlNilai);
                    psNilai.setInt(1, Integer.parseInt(tfNilai.getText()));
                    psNilai.setInt(2, id);
                    psNilai.executeUpdate();
                    
                    JOptionPane.showMessageDialog(null, "Data Berhasil Diupdate");
                    viewData();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
                }
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    int row = table.getSelectedRow();
                    if (row == -1) {
                        JOptionPane.showMessageDialog(null, "Pilih Data yang Akan Dihapus");
                        return;
                    }
                    int id = (int) tableModel.getValueAt(row, 0);
                    Connection con = Koneksi.getKoneksi();
                    
                    String sqlNilai = "DELETE FROM nilai WHERE mahasiswa_id = ?";
                    PreparedStatement psNilai = con.prepareStatement(sqlNilai);
                    psNilai.setInt(1, id);
                    psNilai.executeUpdate();
                    
                    String sqlMatakuliah = "DELETE FROM matakuliah WHERE id = (SELECT matakuliah_id FROM nilai WHERE mahasiswa_id = ?)";
                    PreparedStatement psMatakuliah = con.prepareStatement(sqlMatakuliah);
                    psMatakuliah.setInt(1, id);
                    psMatakuliah.executeUpdate();
                    
                    String sqlMahasiswa = "DELETE FROM mahasiswa WHERE id = ?";
                    PreparedStatement psMahasiswa = con.prepareStatement(sqlMahasiswa);
                    psMahasiswa.setInt(1, id);
                    psMahasiswa.executeUpdate();
                    
                    JOptionPane.showMessageDialog(null, "Data Berhasil Dihapus");
                    viewData();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
                }
            }
        });

        btnView.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                viewData();
            }
        });
    }

    private void viewData() {
        try {
            Connection con = Koneksi.getKoneksi();
            Statement stmt = con.createStatement();
            String sql = "SELECT m.id, m.nama, m.nim, m.prodi, mk.nama_mk, mk.sks, n.nilai " +
                         "FROM mahasiswa m " +
                         "JOIN nilai n ON m.id = n.mahasiswa_id " +
                         "JOIN matakuliah mk ON n.matakuliah_id = mk.id";
            ResultSet rs = stmt.executeQuery(sql);
            tableModel.setRowCount(0);
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("nama"),
                    rs.getString("nim"),
                    rs.getString("prodi"),
                    rs.getString("nama_mk"),
                    rs.getString("sks"),
                    rs.getInt("nilai")
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new MahasiswaGUI().setVisible(true);
            }
        });
    }
}
