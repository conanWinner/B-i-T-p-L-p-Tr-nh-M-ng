/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Server;

import Server.SyncInterface;
import connect.ConnectDB;
import java.sql.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Transaction;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;

/**
 *
 * @author Admin
 */
public class SyncServer extends UnicastRemoteObject implements SyncInterface {

    private final String serverName;
    private ConnectDB db;
    private List<String> listLoggedIn;

    String[] serverNames = {"Sync2021", "Sync2022"};
    int[] ports = {2021, 2022};
    String[] ip = {}; 

    public SyncServer(String serverName) throws RemoteException {
        super();
        this.serverName = serverName;
        this.db = connectDB(serverName);
        listLoggedIn = new ArrayList<>();
    }

    private ConnectDB connectDB(String serverName) {
        switch (serverName) {
            case "Sync2021":
                return new ConnectDB("sa", "0905640692t", "Server_RMI_1", "LAPTOP-3LGDO2CD\\SQLEXPRESS", 1433);
            case "Sync2022":
                return new ConnectDB("sa", "0905640692t", "Server_RMI_2", "LAPTOP-3LGDO2CD\\SQLEXPRESS", 1433);
        }
        return null;
    }

    @Override
    public synchronized boolean insertDataIntoTransactionTable(String sender, String receiver, String amount, boolean checkMyself) {
        String query = "INSERT INTO transactions (sender, receiver, amout, datetime) VALUES (?, ?, ?, ?)";
        try (Connection con = db.getConnect().getConnection(); PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(1, sender);
            pstmt.setString(2, receiver);
            pstmt.setString(3, amount);

            // Lấy thời gian hiện tại và định dạng theo yêu cầu
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            String formattedTimestamp = sdf.format(new java.util.Date());
            pstmt.setString(4, formattedTimestamp);

            // Thực hiện chèn dữ liệu
            int check = pstmt.executeUpdate();
            if (check == 1) {
                if (checkMyself == false) {
                    synchronizeTransaction(sender, receiver, amount);
                }
                System.out.println("Giao dịch đã được ghi nhận.");
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public synchronized void updateMySelf(String newLogin) {
        this.listLoggedIn.add(newLogin);
    }

    @Override
    public synchronized boolean login(String username, String password) throws RemoteException {
//        Kiểm tra ai đã đăng nhập
        for (String logger : listLoggedIn) {
            if (logger.equals(username)) {
                return false;
            }
        }

        String query = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection con = db.getConnect().getConnection(); PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Đăng nhập thành công
                listLoggedIn.add(username);
                synchronizeLogin(username);
                return true;
            } else {
                System.out.println("Tên người dùng hoặc mật khẩu không chính xác.");
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public synchronized void logout(String username, boolean checkMyself) {
        try {
            this.listLoggedIn.remove(username);
            String clientHost = RemoteServer.getClientHost();
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            String formattedTimestamp = sdf.format(new java.util.Date());
            System.out.println("Log out Client name: " + username + " - Client IP: " + clientHost + " - " + formattedTimestamp);
            if (checkMyself == false) {
                synchronizeLogout(username);
            }
        } catch (ServerNotActiveException ex) {
            Logger.getLogger(SyncServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public List<Transaction> getAllTransaction(String username, List<Transaction> listTransaction) {
        String query = "SELECT * FROM transactions WHERE sender = ? OR receiver = ?";
        try (Connection con = db.getConnect().getConnection(); PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, username);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String sender = rs.getString(1);
                String receiver = rs.getString(2);
                String amount = rs.getString(3);
                String datetime = rs.getString(4);

                listTransaction.add(new Transaction(sender, receiver, amount, datetime));
            }
        } catch (SQLException ex) {
        }
        return listTransaction;
    }

    @Override
    public void getUser(String username){
        try {
            String clientHost = RemoteServer.getClientHost();
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            String formattedTimestamp = sdf.format(new java.util.Date());
            System.out.println("Client name: " + username + " - Client IP: " + clientHost + " - " + formattedTimestamp);
        } catch (ServerNotActiveException ex) {
            Logger.getLogger(SyncServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void synchronizeTransaction(String sender, String receiver, String amount) {
        for (int i = 0; i < serverNames.length; i++) {
            if (!serverNames[i].equals(serverName)) {
                try {
                    Registry registry = LocateRegistry.getRegistry("localhost", ports[i]);
                    SyncInterface otherServer = (SyncInterface) registry.lookup("Sync" + ports[i]);
                    otherServer.insertDataIntoTransactionTable(sender, receiver, amount, true);

                } catch (RemoteException ex) {
                    Logger.getLogger(SyncServer.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NotBoundException ex) {
                    Logger.getLogger(SyncServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private void synchronizeLogin(String newLogIn) {

        for (int i = 0; i < serverNames.length; i++) {
            if (!serverNames[i].equals(serverName)) {
                try {
                    Registry registry = LocateRegistry.getRegistry("localhost", ports[i]);
                    SyncInterface otherServer = (SyncInterface) registry.lookup("Sync" + ports[i]);
                    otherServer.updateMySelf(newLogIn);

                } catch (RemoteException ex) {
                    Logger.getLogger(SyncServer.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NotBoundException ex) {
                    Logger.getLogger(SyncServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private void synchronizeLogout(String username) {
        for (int i = 0; i < serverNames.length; i++) {
            if (!serverNames[i].equals(serverName)) {
                try {
                    Registry registry = LocateRegistry.getRegistry("localhost", ports[i]);
                    SyncInterface otherServer = (SyncInterface) registry.lookup("Sync" + ports[i]);
                    otherServer.logout(username, true);

                } catch (RemoteException ex) {
                    Logger.getLogger(SyncServer.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NotBoundException ex) {
                    Logger.getLogger(SyncServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

}
