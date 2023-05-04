import threading
import socket
from collections import Counter
import time
import string

alias = input('Choose an alias >>> ')
client = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
client.connect(('127.0.0.1', 59000))


def client_receive():
    while True:
        try:
            message = client.recv(1024).decode('utf-8')
            if message == "alias?":
                client.send(alias.encode('utf-8'))
            elif ":" in message:
                # if ":" in message:
                words=message.split(': ')[1].translate(str.maketrans('', '', string.punctuation)).split(' ')
                message_send=f'{alias}: '
                dic= Counter(words)
                for key in set(words):
                    if dic[key]>1:
                        message_send+=f'\"{key}\" repeated times: {dic[key]} \n'
                if message_send==f'{alias}: ':
                    message_send+=f'No repeated word'
                    client.send(message_send.encode('utf-8'))
                else:
                    client.send(message_send[:-1].encode('utf-8'))
                print(message)    
            else:
                print(message)
            
        except:
            print('Error!')
            client.close()
            break


receive_thread = threading.Thread(target=client_receive)
receive_thread.start()
