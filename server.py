import os
import socket
import threading
import tkinter as tk
from tkinter import scrolledtext, messagebox

# Configuration for server connection
SERVER_HOST = 'localhost'
SERVER_PORT = 44444
BUFFER_SIZE = 4096

# Create UDP socket
server_socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
server_socket.bind((SERVER_HOST, SERVER_PORT))

# Create accounts directory if it doesn't exist
ACCOUNTS_DIR = 'accounts'
if not os.path.exists(ACCOUNTS_DIR):
    os.makedirs(ACCOUNTS_DIR)

print(f"Server is running on {SERVER_HOST}:{SERVER_PORT}...")

# Function to log messages to the UI
def log(message):
    log_area.config(state=tk.NORMAL)
    log_area.insert(tk.END, message + "\n")
    log_area.config(state=tk.DISABLED)
    log_area.see(tk.END)

def handle_client(message, client_address):
    command_parts = message.decode().split('|')
    command = command_parts[0]

    if command == "CREATE_ACCOUNT":
        account_name = command_parts[1]
        account_path = os.path.join(ACCOUNTS_DIR, account_name)
        if not os.path.exists(account_path):
            os.makedirs(account_path)
            with open(os.path.join(account_path, "new_email.txt"), 'w') as f:
                f.write("Thank you for using this service. We hope that you will feel comfortable using it.")
            server_socket.sendto(f"Account '{account_name}' created successfully.".encode(), client_address)
            log(f"Account '{account_name}' created.")
        else:
            server_socket.sendto(f"Error: Account '{account_name}' already exists.".encode(), client_address)
            log(f"Error: Account '{account_name}' already exists.")

    elif command == "SEND_EMAIL":
        to_account = command_parts[1]
        email_content = command_parts[2]
        account_path = os.path.join(ACCOUNTS_DIR, to_account)
        if os.path.exists(account_path):
            email_filename = os.path.join(account_path, f"{to_account}_send.txt")
            with open(email_filename, 'w', encoding='utf-8') as f:
                f.write(email_content)
            server_socket.sendto(f"Email sent to '{to_account}'.".encode(), client_address)
            log(f"Email sent to '{to_account}'.")
        else:
            server_socket.sendto(f"Error: Account '{to_account}' does not exist.".encode(), client_address)
            log(f"Error: Account '{to_account}' does not exist.")

    elif command == "LIST_FILES":
        account_name = command_parts[1]
        account_path = os.path.join(ACCOUNTS_DIR, account_name)
        if os.path.exists(account_path):
            files = os.listdir(account_path)
            if files:
                response = "Files: " + "|".join(files)
            else:
                response = "No files found."
        else:
            response = f"Error: Account '{account_name}' does not exist."
        server_socket.sendto(response.encode(), client_address)
        log(response)

    elif command == "LOGIN":
        account_name = command_parts[1]
        account_path = os.path.join(ACCOUNTS_DIR, account_name)
        if os.path.exists(account_path):
            files = os.listdir(account_path)
            if files:
                response = "Emails: " + "|".join(files)
            else:
                response = "No emails found."
            server_socket.sendto(response.encode(), client_address)
            log(f"User '{account_name}' logged in. Files sent.")
        else:
            response = f"Error: Account '{account_name}' does not exist."
            server_socket.sendto(response.encode(), client_address)
            log(f"Error: Account '{account_name}' does not exist.")

# Listening loop for client requests
def listen_for_clients():
    while True:
        message, client_address = server_socket.recvfrom(BUFFER_SIZE)
        threading.Thread(target=handle_client, args=(message, client_address)).start()

# Function to start the server
def start_server():
    log("Starting server...")
    threading.Thread(target=listen_for_clients, daemon=True).start()
    log("Server is now listening for clients...")

# Function to stop the server
def stop_server():
    server_socket.close()
    log("Server has been stopped.")

# Create Tkinter interface
root = tk.Tk()
root.title("UDP Mail Server")
root.geometry("600x400")
root.config(bg="#f0f8ff")

# Frame for controls
control_frame = tk.Frame(root, bg="#f0f8ff")
control_frame.pack(pady=10)

# Start server button
start_button = tk.Button(control_frame, text="Start Server", command=start_server, bg="#4CAF50", fg="white")
start_button.pack(side=tk.LEFT, padx=5)

# Stop server button
stop_button = tk.Button(control_frame, text="Stop Server", command=stop_server, bg="#f44336", fg="white")
stop_button.pack(side=tk.LEFT, padx=5)

# Log area
log_area = scrolledtext.ScrolledText(root, wrap=tk.WORD, state=tk.DISABLED, bg="#ffffff", height=15)
log_area.pack(expand=True, fill='both', padx=10, pady=10)

# Run the interface
root.mainloop()
