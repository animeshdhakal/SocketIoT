#pragma once
#include <ESP8266WiFi.h>
#include <string.h>

struct HomeAtionHeader
{
    uint16_t msg_type;
    uint16_t msg_len;
};

typedef void (*HomeAtionCallback)(HomeAtionHeader &, const String &);

static HomeAtionCallback HomeAtionHandlers[20] = {NULL};

uint8_t registerHomeAtionHandler(uint8_t pin, HomeAtionCallback cb)
{
    Serial.printf("Pin is %d \n", pin);
    HomeAtionHandlers[pin] = cb;
    return 0;
}

#define HomeAtionWrite(PIN) \
    void HomeAtion_Write_##PIN(HomeAtionHeader &header, const String &msg); \
    uint8_t temp##PIN = registerHomeAtionHandler(PIN, HomeAtion_Write_##PIN); \
    void HomeAtion_Write_##PIN(HomeAtionHeader &header, const String &msg)

#define HomeAtion_Write(PIN) HomeAtionWrite(PIN)

#define STR_16(a, b) (a | b << 8)

#define NW_16 STR_16('n', 'w')
#define NR_16 STR_16('n', 'r')

enum
{
    MSG_AUTH = 1,
    MSG_PING = 2,
    MSG_RW = 3,
    MSG_READ = 4,
    MSG_DISCONNECT = 5
};

class HomeAtion
{
    const char *_token;
    const char *_host;
    uint16_t _port;
    WiFiClient client;

public:
    void begin(const char *token, const char *server, const uint16_t &port)
    {
        this->_token = token;
        this->_host = server;
        this->_port = port;
        this->connect();
    }

    void authenticate()
    {
        uint16_t msg_type = 1;
        uint16_t msg_len = strlen(this->_token);
        char msg[msg_len + 4];
        memcpy(msg, &msg_type, 2);
        memcpy(msg + 2, &msg_len, 2);
        memcpy(msg + 4, this->_token, msg_len);
        client.write(msg, msg_len + 4);
    }

    void processCmd(HomeAtionHeader& header, char* buff)
    {
        const uint8_t pin = atoi(buff + strlen(buff) + 1);
        uint16_t cmd;
        memcpy(&cmd, buff, 2);

        switch(cmd){
            case NW_16:
                Serial.println("Normal Write");
                if(HomeAtionHandlers[pin]){
                    buff += strlen(buff) + 1;
                    HomeAtionHandlers[pin](header, String(buff));
                }
                break;
            case NR_16:
                Serial.println("Normal Read");
                break;
            default:
                Serial.println("Unknown command");
        }
    }

    void connect()
    {
        client.stop();
        while (!client.connect(this->_host, this->_port))
        {
            delay(1000);
        }
        Serial.println("Connected");
        authenticate();
    }

    void run()
    {
        if (client.connected())
        {
            HomeAtionHeader buffer;
            int rlen = client.read((uint8_t *)&buffer, 4);
            if (rlen == sizeof(HomeAtionHeader))
            {
                char msg[buffer.msg_len];
                client.read(msg, buffer.msg_len);

                switch (buffer.msg_type)
                {
                case MSG_AUTH:
                    if (msg[0] == 'y'){
                        Serial.println("Authenticated");
                    }else if(msg[0] == 'n'){
                        Serial.println("Authentication failed");
                    }
                    break;
                case MSG_RW:
                    processCmd(buffer, msg);
                    break;
                default:
                    Serial.printf("Unknown message type is %d \n", buffer.msg_type);
                    break;
                }
            }
            // Disconnect from server
            // client.stop();
        }
        else
        {
            Serial.println("Disconnected");
            connect();
        }
        yield();
    }
};

HomeAtion homeAtion;
extern HomeAtion homeAtion;
