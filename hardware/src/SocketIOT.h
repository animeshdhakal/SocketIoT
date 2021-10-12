#pragma once
#include <ESP8266WiFi.h>
#include <string.h>
#include <stddef.h>

struct SocketIOTHeader
{
    uint16_t msg_len;
    uint16_t msg_type;
};

#define HEARTBEAT 10000

#if __BYTE_ORDER__ == __ORDER_LITTLE_ENDIAN__
#define htons(x) (((x) << 8) | (((x) >> 8) & 0xFF))
#define htonl(x) (((x) << 24 & 0xFF000000UL) | \
                  ((x) << 8 & 0x00FF0000UL) |  \
                  ((x) >> 8 & 0x0000FF00UL) |  \
                  ((x) >> 24 & 0x000000FFUL))
#define ntohs(x) htons(x)
#define ntohl(x) htonl(x)
#elif __BYTE_ORDER__ == __ORDER_BIG_ENDIAN__
#define htons(x) (x)
#define htonl(x) (x)
#define ntohs(x) (x)
#define ntohl(x) (x)
#else
#error byte order problem
#endif

class SocketIOTData
{
public:
    char *buff;
    size_t buff_len;

    SocketIOTData(char *buff, size_t buff_len)
    {
        this->buff = buff;
        this->buff_len = buff_len;
    }

    bool is_valid()
    {
        return buff != NULL && buff < (buff + buff_len);
    }

    SocketIOTData &operator++()
    {
        if (is_valid())
        {
            buff += strlen(buff) + 1;
        }
        return *this;
    }
    SocketIOTData &operator--()
    {
        if (is_valid())
        {
            buff += strlen(buff) + 1;
        }
        return *this;
    }

    const char *toString()
    {
        return buff;
    }

    const char *toStr()
    {
        return buff;
    }

    int toInt()
    {
        if (is_valid())
        {
            return atoi(buff);
        }
        return 0;
    }

    float toFloat()
    {
        if (is_valid())
        {
            return atof(buff);
        }
        return 0;
    }

    const char *end()
    {
        return buff + buff_len;
    }

    size_t length()
    {
        return buff_len;
    }

    bool operator<(const char *data) const
    {
        return buff < data;
    }
    bool operator>=(const char *data) const
    {
        return buff >= data;
    }
};

typedef void (*SocketIOTCallback)(SocketIOTData &);

static SocketIOTCallback SocketIOTHandlers[20] = {NULL};

uint8_t registerSocketIOTCallback(uint8_t pin, SocketIOTCallback cb)
{
    SocketIOTHandlers[pin] = cb;
    return 0;
}

#define SocketIOTWrite(PIN)                                                     \
    void SocketIOT_Write_##PIN(SocketIOTData &);                                \
    uint8_t tempW##PIN = registerSocketIOTCallback(PIN, SocketIOT_Write_##PIN); \
    void SocketIOT_Write_##PIN(SocketIOTData &data)

#define SocketIOT_Write(PIN) \
    SocketIOTWrite(PIN)

enum SockeIOTMsgType
{
    AUTH = 1,
    WRITE = 2,
    PING = 3,
};

class SocketIOTWiFi
{
    const char *_token;
    const char *_host;
    uint16_t _port;
    WiFiClient client;
    int last_ping_time = 0;


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
        SocketIOTHeader hdr;
        hdr.msg_len = htons(strlen(this->_token));
        hdr.msg_type = htons(AUTH);
        uint8_t buff[sizeof(SocketIOTHeader) + strlen(this->_token)];
        memcpy(buff, &hdr, sizeof(SocketIOTHeader));
        memcpy(buff + sizeof(SocketIOTHeader), this->_token, strlen(this->_token));
        sendMsg(buff, sizeof(buff));
    }

    void sendMsg(uint8_t* buf, size_t size){
        this->client.write(buf, size);
    }

    void sendPing()
    {
        SocketIOTHeader hdr;
        hdr.msg_len = htons(0);
        hdr.msg_type = htons(3);
        sendMsg((uint8_t *)&hdr, sizeof(hdr));
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

    void processWrite(SocketIOTData &data)
    {
        const uint8_t pin = data.toInt();

        if (++data >= data.end())
        {
            return;
        }

        if (SocketIOTHandlers[pin])
        {
            SocketIOTHandlers[pin](data);
        }
    }

    void run()
    {
        if (client.connected())
        {
            SocketIOTHeader hdr;
            int rlen = client.read((uint8_t *)&hdr, sizeof(SocketIOTHeader));
            if (rlen == sizeof(SocketIOTHeader))
            {
                hdr.msg_len = ntohs(hdr.msg_len);
                hdr.msg_type = ntohs(hdr.msg_type);

                char buffer[hdr.msg_len + 1];
                client.read((uint8_t *)buffer, hdr.msg_len);


                buffer[hdr.msg_len] = 0;
                SocketIOTData data(buffer, hdr.msg_len);

                Serial.printf("Got Message : %s \n", data.toString());

                switch (hdr.msg_type)
                {
                case AUTH:
                    if (buffer[0] - '0' == 1)
                    {
                        Serial.println("Authenticated");
                    }
                    else
                    {
                        Serial.println("Authentication failed");
                    }
                    break;

                case WRITE:
                    processWrite(data);
                    break;

                case PING:
                    sendPing();
                    break;

                default:
                    Serial.println("Unknown");
                }
            }
            if((millis() - last_ping_time) >= HEARTBEAT){
                sendPing();
                last_ping_time = millis();
            }
        }
        else
        {
            connect();
        }


        yield();
    }
};

SocketIOTWiFi SocketIOT;
extern SocketIOTWiFi SocketIOT;