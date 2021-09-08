import socket
import struct


MSG_AUTH = 1
MSG_PING = 2
MSG_RW = 3
MSG_READ = 4
MSG_DISCONNECT = 5

class HomeAtionError(Exception):
    pass

class HomeAtion:
    SOCK_TIMEOUT = 0.05

    def __init__(self, token, server=socket.gethostname(), port=3000):
        self._server = server
        self._token = token
        self._port = port
        self._connected = False
        self._authenticated = False
        self._socket = None
    
    def connect(self):
        try:
            if self._socket:
                self.disconnect()
            self._socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            self._socket.connect((self._server, self._port))
        except Exception as e:
            pass
        self._socket.settimeout(self.SOCK_TIMEOUT)
        self.authenticate()
        self._connected = True

    def disconnect(self):
        self._socket.close()
        self._connected = False

    def sendmsg(self, msgtype, *args):
        data = ('\0'.join([str(curr_arg) for curr_arg in args])).encode('utf-8')
        self._socket.send(struct.pack("HH", msgtype, len(data)) + data)

    def recvmsg(self, bytes):
        dbuff = b''
        try:
            dbuff += self._socket.recv(bytes)
            return dbuff
        except Exception as e:
            if 'timed out' in str(e):
                return b''
            raise
    
    def checkConnection(self):
        try:
            self.sendmsg(MSG_PING)
            return True
        except Exception as e:
            return False

    def parse_resonse(self, buff):
        msg_type, msg_len = struct.unpack("HH", buff)
        data = b""
        while len(data) < msg_len:
            msg = self.recvmsg(msg_len - len(data))
            if not msg:
                break
            data += msg
        return msg_type, [i.decode("utf-8") for i in data.split(b"\0")]

    def authenticate(self):
        self.sendmsg(MSG_AUTH, self._token)
        msg = self.recvmsg(4)
        if msg:
            msg_type, msg_args = self.parse_resonse(msg)
            if msg_args[0] == "OK":
                self._authenticated = True
            else:
                raise HomeAtionError("Authentication failed")

    def process(self, msg_type, msg_args):
        if msg_type == MSG_RW:
            print("write: {}".format(msg_args))
        else:
            pass
            # print("Unknown message: {}".format(msg_type))
    
    def run(self):
        if not self.checkConnection():
            self.connect()
        else:
            msg = self.recvmsg(4)
            if msg:
                self.process(*self.parse_resonse(msg))
    
new = HomeAtion("123")

while True:
    new.run()