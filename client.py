import socket
import tkinter as tk
from tkinter import messagebox, simpledialog

# Client configuration
SERVER_HOST = 'localhost'
SERVER_PORT = 44444
BUFFER_SIZE = 4096

# Create UDP client
client_socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

# Function to send command to server and receive response
def send_command(command):
    client_socket.sendto(command.encode(), (SERVER_HOST, SERVER_PORT))
    response, _ = client_socket.recvfrom(BUFFER_SIZE)
    return response.decode()

# Function to create a new account
def create_account():
    account_name = simpledialog.askstring("Create Account", "Enter account name:")
    if account_name:
        command = f"CREATE_ACCOUNT|{account_name}"
        response = send_command(command)
        messagebox.showinfo("Create Account", response)

# Function to login to an account
def login_account():
    account_name = simpledialog.askstring("Login", "Enter account name:")
    if account_name:
        command = f"LOGIN|{account_name}"
        response = send_command(command)
        messagebox.showinfo("Login", response)
        open_file_interface(account_name)

# Function to send an email
def send_email():
    to_account = simpledialog.askstring("Send Email", "Send to account:")
    content = simpledialog.askstring("Send Email", "Enter email content:")
    if to_account and content:
        command = f"SEND_EMAIL|{to_account}|{content}"
        response = send_command(command)
        messagebox.showinfo("Send Email", response)

# Function to open file interface
def open_file_interface(account_name):
    file_window = tk.Toplevel(root)
    file_window.title(f"Files for {account_name}")
    file_window.geometry("400x300")
    file_window.config(bg="#e0f7fa")

    title_label = tk.Label(file_window, text=f"Files for {account_name}", font=("Arial", 14, "bold"), bg="#e0f7fa")
    title_label.pack(pady=10)

    # Send command to get file list
    command = f"LIST_FILES|{account_name}"
    response = send_command(command)
    if response.startswith("Files:"):
        files = response.split(': ')[1].split('|')
        for file in files:
            file_label = tk.Label(file_window, text=file, bg="#e0f7fa")
            file_label.pack(anchor='w', padx=10)
    else:
        messagebox.showerror("Error", response)

    send_email_button = tk.Button(file_window, text="Send Email", font=("Arial", 12), bg="#2196F3", fg="white", command=send_email)
    send_email_button.pack(pady=10)

    exit_button = tk.Button(file_window, text="Exit", font=("Arial", 12), bg="#f44336", fg="white", command=file_window.destroy)
    exit_button.pack(pady=10)

# Setup the Tkinter interface
root = tk.Tk()
root.title("UDP Mail Client")
root.geometry("400x400")
root.config(bg="#f0f4c3")  # A light greenish background

# Title
title_label = tk.Label(root, text="Mail Server Client", font=("Arial", 20), bg="#f0f4c3", fg="#333")
title_label.pack(pady=20)

# Create a frame for buttons
button_frame = tk.Frame(root, bg="#f0f4c3")
button_frame.pack(pady=10)

# Create Account button
create_account_button = tk.Button(button_frame, text="Create Account", font=("Arial", 14), command=create_account, bg="#4CAF50", fg="white", padx=10, pady=5)
create_account_button.grid(row=0, column=0, padx=10)

# Login button
login_button = tk.Button(button_frame, text="Login", font=("Arial", 14), command=login_account, bg="#8BC34A", fg="white", padx=10, pady=5)
login_button.grid(row=0, column=1, padx=10)

# Exit button
exit_button = tk.Button(button_frame, text="Exit", font=("Arial", 14), command=root.quit, bg="#f44336", fg="white", padx=10, pady=5)
exit_button.grid(row=0, column=2, padx=10)

# Run the interface
root.mainloop()
