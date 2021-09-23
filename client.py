import socket
import struct

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

s.connect((socket.gethostname(), 3030))

def sendMsg(socket, msgtype, *args):
    msg = ('\0'.join([str(curr_arg) for curr_arg in args])).encode('utf-8')
    msglen = len(msg)
    socket.send(struct.pack("!HH", msglen, msgtype) + msg)

    print(msg)

def auth(token):
    sendMsg(s, 1, token)
    msg = s.recv(4)
    msglen, msgtype = struct.unpack("!HH", msg)
    data = b""
    while len(data) < msglen:
        msg = s.recv(msglen - len(data))
        if not msg:
            break
        data += msg


    # print(data)

auth("ktv5oyfgnajn8hmouib")

sendMsg(s, 2, 1, 14440)

msg = s.recv(4)
msglen, msgtype = struct.unpack("!HH", msg)
data = b""
while len(data) < msglen:
    msg = s.recv(msglen - len(data))
    if not msg:
        break
    data += msg

parsedMsg = data.decode('utf-8').split('\0')

print(parsedMsg)