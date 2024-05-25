package tugaslab;

import java.sql.DriverManager;

public class Koneksi {
    private static java.sql.Connection koneksi;
    
    public static java.sql.Connection getKoneksi() {
        if (koneksi == null) {
            try{
                String url = "jdbc:mysql://localhost/tugaslab";
                String user = "root";
                String pass = "";
                DriverManager.registerDriver(new com.mysql.jdbc.Driver());
                koneksi = DriverManager.getConnection(url, user, pass);
                System.out.println("Connection Successfully");
            }catch (Exception e) {
                System.out.println("Error b");
            }
        }
        return koneksi;
    }
    public static void main(String[] args) {
        getKoneksi();
    }
    
}
