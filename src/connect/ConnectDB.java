/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package connect;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;

/**
 *
 * @author Admin
 */
public class ConnectDB {
    private static SQLServerDataSource ds;

    
    
    public ConnectDB(String user, String password, String databaseName, String serverName, int portNumber) {
        ds = new SQLServerDataSource();
        ds.setUser(user);        
        ds.setPassword(password);
        ds.setDatabaseName(databaseName);
        ds.setServerName(serverName);
        ds.setPortNumber(portNumber);
        ds.setTrustServerCertificate(true);
    }
    
    public SQLServerDataSource getConnect() {
        return ds;
    }
    
    
}
