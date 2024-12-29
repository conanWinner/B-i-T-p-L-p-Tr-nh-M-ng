/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import model.Transaction;

/**
 *
 * @author Admin
 */
public interface SyncInterface extends Remote {

    boolean login(String username, String password) throws RemoteException;
    void logout(String username, boolean checkMyself) throws RemoteException;

    List<Transaction> getAllTransaction(String username, List<Transaction> listTransaction) throws RemoteException;
    boolean insertDataIntoTransactionTable(String sender, String receiver, String amount, boolean checkMyself) throws RemoteException;

    void updateMySelf(String newLogin) throws RemoteException;
    void getUser(String username) throws RemoteException;
}
