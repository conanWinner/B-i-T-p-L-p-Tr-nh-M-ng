package multicastChat;

import javax.swing.*;
import java.net.*;
import java.io.*;
import java.awt.event.*;
import java.util.Date;
import java.text.SimpleDateFormat;

public class MulticastFrame extends javax.swing.JFrame {

    private MulticastSocket multicastSocket;
    private InetAddress multicastGroup;
    private int port;
    private Thread receiveThread;
    private boolean joined = false;

    /**
     * Initializes the GUI components.
     */
    public MulticastFrame() {
        initComponents();
    }

    // Listens for and processes incoming multicast messages.
    private void receiveMessages() {
        byte[] buffer = new byte[1024]; // Buffer for incoming messages.
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        while (!Thread.currentThread().isInterrupted()) {
            try {
                multicastSocket.receive(packet); // Receives a message from the multicast group.
                // Formats the received message.
                String receivedMessage = new String(packet.getData(), 0, packet.getLength());
                String senderIP = packet.getAddress().getHostAddress();
                String timeStamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
                String formattedMessage = timeStamp + " " + senderIP + " : " + receivedMessage;

                // Updates the text area of the GUI safely on the Swing event thread.
                SwingUtilities.invokeLater(() -> messageTextArea.append(formattedMessage + "\n"));
            } catch (IOException e) {
                // If an error occurs while receiving, display an error message unless the socket has been closed.
                if (!multicastSocket.isClosed()) {
                    JOptionPane.showMessageDialog(this, "Error receiving message: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
                break;
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        ipTextField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        portTextField = new javax.swing.JTextField();
        leaveButton = new javax.swing.JButton();
        joinButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        messageTextArea = new javax.swing.JTextArea();
        jLabel4 = new javax.swing.JLabel();
        messageTextField = new javax.swing.JTextField();
        sendButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Multicast Chat");

        jLabel1.setText("IP :");

        jLabel2.setText("Name :");

        nameTextField.setToolTipText("IP de Conexão");
        nameTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nameTextFieldActionPerformed(evt);
            }
        });

        ipTextField.setToolTipText("IP de Conexão");
        ipTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ipTextFieldActionPerformed(evt);
            }
        });

        jLabel3.setText("Port : ");

        portTextField.setToolTipText("IP de Conexão");
        portTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                portTextFieldActionPerformed(evt);
            }
        });

        leaveButton.setText("Leave");
        leaveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                leaveButtonActionPerformed(evt);
            }
        });

        joinButton.setText("Join");
        joinButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                joinButtonActionPerformed(evt);
            }
        });

        messageTextArea.setEditable(false);
        messageTextArea.setColumns(20);
        messageTextArea.setRows(5);
        jScrollPane1.setViewportView(messageTextArea);

        jLabel4.setText("Mensagem : ");

        messageTextField.setToolTipText("IP de Conexão");
        messageTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                messageTextFieldActionPerformed(evt);
            }
        });

        sendButton.setText("Send");
        sendButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(18, 18, 18)
                        .addComponent(messageTextField)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(sendButton))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(18, 18, 18)
                                .addComponent(nameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(ipTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(61, 61, 61)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(portTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(joinButton)
                                .addGap(18, 18, 18)
                                .addComponent(leaveButton)))))
                .addGap(24, 24, 24))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ipTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(portTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(nameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(joinButton)
                    .addComponent(leaveButton))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(messageTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sendButton))
                .addContainerGap(28, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void nameTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nameTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_nameTextFieldActionPerformed

    private void ipTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ipTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ipTextFieldActionPerformed

    private void portTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_portTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_portTextFieldActionPerformed

    private void leaveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_leaveButtonActionPerformed
        try {
            // Checks if the user is connected to the multicast group.
            if (joined) {
                // Leaves the multicast group and closes the socket, thus ending the connection.
                multicastSocket.leaveGroup(multicastGroup);
                multicastSocket.close();

                // Marks the state as not connected and interrupts the message receiving thread.
                joined = false;
                receiveThread.interrupt();

                // Enables the fields for a new connection and adjusts the "Join" and "Leave" buttons.
                ipTextField.setEnabled(true);
                portTextField.setEnabled(true);
                nameTextField.setEnabled(true);
                joinButton.setEnabled(true);
                leaveButton.setEnabled(false);

                // Clears the text area.
                messageTextArea.setText("");
                messageTextArea.append("Left the chat session.\n");
            }
        } catch (IOException e) {
            // In case of an error when leaving the group, displays an error message.
            JOptionPane.showMessageDialog(this, "Error leaving the multicast group: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_leaveButtonActionPerformed

    private void joinButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_joinButtonActionPerformed
        try {
            int portVerification = Integer.parseInt(portTextField.getText());
            // Validates the port number.
            if (portVerification < 1024 || portVerification > 65535) {
                throw new IllegalArgumentException("Invalid port, must be between 1024 and 65535.");
            }

            // Checks if the IP address is valid.
            InetAddress address;
            try {
                address = InetAddress.getByName(ipTextField.getText());
            } catch (UnknownHostException e) {
                throw new IllegalArgumentException("Invalid IP address.");
            }

            // Checks if the name field is empty.
            String name = nameTextField.getText().trim();
            if (name.isEmpty()) {
                throw new IllegalArgumentException("Name is required.");
            }

            if (!joined) {
                multicastGroup = address;
                port = portVerification;
                multicastSocket = new MulticastSocket(port);
                multicastSocket.joinGroup(multicastGroup);
                joined = true;

                // Disables the IP, port, and name entry fields.
                ipTextField.setEnabled(false);
                portTextField.setEnabled(false);
                nameTextField.setEnabled(false);

                // Disables the "Join" button and enables the "Leave" button.
                joinButton.setEnabled(false);
                leaveButton.setEnabled(true);

                // Starts a new thread to listen to chat messages asynchronously.
                receiveThread = new Thread(this::receiveMessages);
                receiveThread.start();

                // Adds a message informing that the user has joined a chat session.
                messageTextArea.append("Joined the chat session!\n");
            }
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Error joining the multicast group: " + e.getMessage(), "Network Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error joining the multicast group: " + e.getMessage(), "Network Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_joinButtonActionPerformed

    private void messageTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_messageTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_messageTextFieldActionPerformed

    private void sendButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendButtonActionPerformed
        // Sends a message to the multicast group with the sender's name and message content.
        if (!messageTextField.getText().trim().isEmpty()) {
            try {
                String messageContent = nameTextField.getText().trim() + " : " + messageTextField.getText().trim();
                byte[] buffer = messageContent.getBytes();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, multicastGroup, port);
                multicastSocket.send(packet); // Sends the message to all members of the multicast group.
                messageTextField.setText(""); // Clears the text field after sending.
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error sending message: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Cannot send an empty message.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_sendButtonActionPerformed

    /**
     * Main method to run the application.
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MulticastFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MulticastFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MulticastFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MulticastFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MulticastFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField ipTextField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton joinButton;
    private javax.swing.JButton leaveButton;
    private javax.swing.JTextArea messageTextArea;
    private javax.swing.JTextField messageTextField;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JTextField portTextField;
    private javax.swing.JButton sendButton;
    // End of variables declaration//GEN-END:variables
}
