package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import model.CrudException;

public class Conexao {
    
    String hostName = "localhost"; 
    String dbName = "trabalhopoo"; 
    String user = "guto";
    String senha = "guto"; 

    private static Conexao instancia;
    private Connection con;
    
    private Conexao() throws CrudException {
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new CrudException(e);
        }
    }
    public static Conexao getInstance() throws CrudException { 
        if (instancia == null) { 
            instancia = new Conexao();
        }
        return instancia;
    }
    public Connection getConnection() throws CrudException {
        try { 
            if (this.con == null || 
                this.con.isClosed() || 
                !this.con.isValid(5000)) { 
                    this.con = DriverManager.getConnection(String.format(
                        "jdbc:jtds:sqlserver://%s:57480;databaseName=%s;user=%s;password=%s;"
                        , hostName, dbName, user, senha)); //SQLServer
            }
        } catch (SQLException e) { 
            throw new CrudException(e);
        }
        return this.con;
    }
    
}
