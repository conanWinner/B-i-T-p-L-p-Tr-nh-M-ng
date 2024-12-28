package lab4_chatapp;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ChatClient {
    private static DataInputStream input;
    private static DataOutputStream output;
    private static boolean isSender = false; // Biến cục bộ để xác định client là người gửi
    private static JButton sendButton;
    private static JButton fileButton;
    private static JFrame frame;
    private static JTextArea chatArea;
    private static JTextField inputField;


    public static void main(String[] args) {
        //nhap ten
        JFrame ten = new JFrame("Nhập tên");
        ten.setSize(300, 300);
        JTextField ten_nhan = new JTextField();
        JButton guiten = new JButton("Gửi tên");

        JPanel panelTen = new JPanel(new BorderLayout());
        panelTen.add(ten_nhan, BorderLayout.CENTER);
        panelTen.add(guiten, BorderLayout.EAST);
        ten.add(panelTen, BorderLayout.SOUTH);
        ten.setVisible(true);


        // Action để ẩn frame khi nhấn nút Send
        guiten.addActionListener(ee -> {

            frame = new JFrame("Chat Client");
            chatArea = new JTextArea();
            chatArea.setEditable(false);
            inputField = new JTextField();
            sendButton = new JButton("Send");
            fileButton = new JButton("Send File");

            JPanel panel = new JPanel(new BorderLayout());
            panel.add(inputField, BorderLayout.CENTER);
            panel.add(sendButton, BorderLayout.EAST);
            panel.add(fileButton, BorderLayout.WEST);

            frame.setLayout(new BorderLayout());
            frame.add(new JScrollPane(chatArea), BorderLayout.CENTER);
            frame.add(panel, BorderLayout.SOUTH);

            frame.setSize(400, 400);
            frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
            frame.setVisible(true);

            ten.dispose();

            // ==========================================


            try {
                Socket socket = new Socket("localhost", 12345);


                input = new DataInputStream(socket.getInputStream());
                output = new DataOutputStream(socket.getOutputStream());

                // Nhận dữ liệu từ server
                output.writeUTF("MESSAGE");
                output.writeUTF(ten_nhan.getText() + " đã vào");

                // Thread để nhận tin nhắn và file từ server
                new Thread(() -> {
                    try {
                        while (true) {
                            String dataType = input.readUTF(); // Nhận loại dữ liệu

                            if (dataType.equals("MESSAGE")) {
                                String message = input.readUTF();
                                chatArea.append(message + "\n");
                            } else if (dataType.equals("FILE")) {
                                // Nhận file
                                String fileName = input.readUTF();
                                long fileSize = input.readLong();
                                byte[] fileData = new byte[(int) fileSize];
                                input.readFully(fileData);

                                // Lưu file vào hệ thống
                                File file = new File("received_" + fileName);
                                try (FileOutputStream fos = new FileOutputStream(file)) {
                                    fos.write(fileData);
                                }
                                chatArea.append("File received: " + file.getName() + "\n");

                                // Chỉ hỏi mở file nếu client không phải là người gửi
                                if (!isSender) {
                                    int response = JOptionPane.showConfirmDialog(null,
                                            "File " + fileName + " received. Do you want to open it?",
                                            "Open File",
                                            JOptionPane.YES_NO_OPTION);

                                    if (response == JOptionPane.YES_OPTION) {
                                        // Mở file nếu người dùng chọn Yes
                                        if (Desktop.isDesktopSupported()) {
                                            Desktop.getDesktop().open(file);
                                        } else {
                                            chatArea.append("Cannot open file: Desktop is not supported\n");
                                        }
                                    }
                                } else {
                                    // Reset lại biến isSender sau khi gửi file
                                    isSender = false;
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();

                // Gửi tin nhắn khi nhấn nút Send
                sendButton.addActionListener(e -> {
                    String message = inputField.getText();
                    try {
                        output.writeUTF("MESSAGE");
                        output.writeUTF(ten_nhan.getText() + " : " + message);
                        inputField.setText("");
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });

                // Gửi file khi nhấn nút Send File
                fileButton.addActionListener(e -> {
                    JFileChooser fileChooser = new JFileChooser();
                    int result = fileChooser.showOpenDialog(frame);
                    if (result == JFileChooser.APPROVE_OPTION) {
                        File file = fileChooser.getSelectedFile();
                        try {
                            byte[] fileData = Files.readAllBytes(Paths.get(file.getAbsolutePath()));

                            output.writeUTF("FILE");
                            output.writeUTF(file.getName());
                            output.writeLong(fileData.length);
                            output.write(fileData);
                            chatArea.append("File sent: " + file.getName() + "\n");

                            // Đánh dấu client này là người gửi
                            isSender = true;
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                });

                // Gửi tin nhắn khi nhấn Enter
                inputField.addActionListener(e -> sendButton.doClick());

            } catch (IOException e) {
                e.printStackTrace();
            }


        });


    }
}
