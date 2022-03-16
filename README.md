
# SocketIoT

SocketIoT is a IoT Platform based on Netty which sends message from one device to another device.

There are Several Features of SocketIoT Server like Email, Push Notification, Automatic Let's Encrypt Certificate Generation and many more.



## Building Jar File

Maven Should be Installed to build the Jar File.

`make build` - Building the Jar File\
`make run` - Building and Running the Jar File

## Protocol
#### SocketIoT uses Custom Protocol for Hardware and WebSocket for web and mobile.


| Message Length | Message Type     | Body                |
| :-------- | :------- | :------------------------- |
| `2 Bytes` | `2 Bytes` | `Variable` |

## Libraries
- [SocketIoTLib for C++](https://github.com/animeshdhakal/SocketIoTLib)
- [SocketIoTPy for Python](https://github.com/animeshdhakal/SocketIoTPy)
    
## Authors

- [@animeshdhakal](https://www.github.com/animeshdhakal)
