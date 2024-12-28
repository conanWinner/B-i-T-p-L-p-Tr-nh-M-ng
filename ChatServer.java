package lab4_chatapp;

import javax.sound.midi.Soundbank;
import javax.swing.*;
import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class ChatServer {
    private static final Set<Socket> clientSockets = new HashSet<>();
    private static JTextArea chatArea;

    public static void main(String[] args) {

        JFrame frame = new JFrame("Server");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Tạo JTextArea cho phần chat
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("Arial", Font.PLAIN, 14));
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Tạo JTextField và các JButton
        JTextField inputField = new JTextField();
        inputField.setFont(new Font("Arial", Font.PLAIN, 14));
        inputField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JButton sendButton = new JButton("Send");
        sendButton.setFont(new Font("Arial", Font.BOLD, 14));
        sendButton.setPreferredSize(new Dimension(100, 30));

        JButton fileButton = new JButton("Send File");
        fileButton.setFont(new Font("Arial", Font.BOLD, 14));
        fileButton.setPreferredSize(new Dimension(120, 30));

        // Tạo JPanel để chứa các nút
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        buttonPanel.add(Box.createHorizontalGlue()); // Tạo khoảng cách phía bên trái
        buttonPanel.add(fileButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0))); // Khoảng cách giữa các nút
        buttonPanel.add(sendButton);

        // Tạo JPanel chứa inputField và buttonPanel
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(buttonPanel, BorderLayout.EAST);

        // Tạo danh sách người dùng đang hoạt động
        String[] activeUsers = {"User1", "User2", "User3", "User4"}; // Danh sách giả định
        JList<String> userList = new JList<>(activeUsers);
        userList.setFont(new Font("Arial", Font.PLAIN, 14));
        userList.setBorder(BorderFactory.createTitledBorder("Active Users"));
        userList.setFixedCellHeight(30);

        // Tạo JScrollPane để chứa danh sách người dùng
        JScrollPane userScrollPane = new JScrollPane(userList);
        userScrollPane.setPreferredSize(new Dimension(150, 0)); // Đặt chiều rộng cố định

        // Tạo JLabel để hiển thị số lượng người dùng đang hoạt động
        JLabel userCountLabel = new JLabel("Active Users Count: " + clientSockets.size());
        userCountLabel.setFont(new Font("Arial", Font.BOLD, 14));
        userCountLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // Tạo JPanel chứa danh sách người dùng và thông tin thống kê
        JPanel userPanel = new JPanel();
        userPanel.setLayout(new BorderLayout());
        userPanel.add(userScrollPane, BorderLayout.CENTER);
        userPanel.add(userCountLabel, BorderLayout.SOUTH);

        // Tạo JFrame và thiết lập bố cục
        frame.setLayout(new BorderLayout());
        frame.add(new JScrollPane(chatArea), BorderLayout.CENTER);
        frame.add(userPanel, BorderLayout.EAST); // Đặt danh sách người dùng và thống kê bên phải
        frame.add(inputPanel, BorderLayout.SOUTH);

        // Thiết lập kích thước và hiển thị JFrame
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null); // Đặt JFrame ở giữa màn hình
        frame.setVisible(true);


        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            System.out.println("Server is listening on port 12345");

            while (true) {
                Socket socket = serverSocket.accept();
                clientSockets.add(socket);
                System.out.println("New client connected");

                // Tạo một thread mới cho mỗi client
                new ClientHandler(socket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Lớp xử lý mỗi client
    private static class ClientHandler extends Thread {
        private final Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                DataInputStream input = new DataInputStream(socket.getInputStream());
                DataOutputStream output = new DataOutputStream(socket.getOutputStream());

                while (true) {
                    // Nhận loại dữ liệu: "MESSAGE" hoặc "FILE"
                    String dataType = input.readUTF();

                    if (dataType.equals("MESSAGE")) {
                        // Nhận tin nhắn văn bản
                        String message = input.readUTF();
                        chatArea.append(message + "\n");
                        System.out.println("Received: " + message);
                        broadcastMessage("MESSAGE", message);
                    } else if (dataType.equals("FILE")) {
                        // Nhận tên file
                        String fileName = input.readUTF();
                        long fileSize = input.readLong();

                        // Nhận dữ liệu file
                        byte[] fileData = new byte[(int) fileSize];
                        input.readFully(fileData);

                        // Gửi file tới tất cả client
                        broadcastFile(fileName, fileData);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                    clientSockets.remove(socket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // Phát tin nhắn tới tất cả client
        private void broadcastMessage(String dataType, String message) {
            for (Socket clientSocket : clientSockets) {

                try {
                    System.out.println("=============    " + clientSocket.toString());
                    DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream());
                    output.writeUTF(dataType); // Loại dữ liệu
                    output.writeUTF(message);  // Nội dung tin nhắn
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // Phát file tới tất cả client
        private void broadcastFile(String fileName, byte[] fileData) {


            for (Socket clientSocket : clientSockets) {
                try {
                    DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream());
                    output.writeUTF("FILE");  // Loại dữ liệu
                    output.writeUTF(fileName); // Tên file
                    output.writeLong(fileData.length); // Kích thước file
                    output.write(fileData); // Dữ liệu file
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
