/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Admin
 */
public class RMIServer1 {
    private static final int port = 2021;
    private static final String serverName = "Sync2021";
    
    public static void main(String args[]){
        try {
            Registry registry = LocateRegistry.createRegistry(port); // đăng kí port
            registry.rebind(serverName, new SyncServer(serverName)); // đăng kí tên của server và thằng server đó là class nào
            
            System.out.println(serverName + " is running ...");
        } catch (RemoteException ex) {
            Logger.getLogger(RMIServer1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
