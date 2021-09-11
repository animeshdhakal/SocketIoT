#include "HomeAtion.h"

extern "C"
{
    void HAlias()
    {
    }
}

HomeAtion_Connected() __attribute__((weak, alias("HAlias")));
HomeAtion_Disconnected() __attribute__((weak, alias("HAlias")));

uint8_t registerHomeAtionHandler(uint8_t pin, HomeAtionCallback cb)
{
    HomeAtionHandlers[pin] = cb;
    return 0;
}

void HomeAtion::begin(const char *token, const char *server, const uint16_t &port)
{
    this->_token = token;
    this->_host = server;
    this->_port = port;
    this->connect();
}

void HomeAtion::authenticate()
{
    HomeAtionHeader hdr = {1, strlen(this->_token) + sizeof(HomeAtionHeader)};
    char buff[strlen(this->_token) + sizeof(HomeAtionHeader)];
    memcpy(buff, &hdr, sizeof(HomeAtionHeader));
    memcpy(buff + sizeof(HomeAtionHeader), this->_token, strlen(this->_token));
    client.write(buff, strlen(this->_token) + sizeof(HomeAtionHeader));
}

void HomeAtion::processCmd(HomeAtionHeader &header, char *buff)
{
    buff[header.msg_len] = '\0';

    HomeAtionData resp(buff, header.msg_len);

    HomeAtionData::iterator it = resp.begin();

    uint16_t cmd;

    const char *cmd_str = it.asStr();

    memcpy(&cmd, cmd_str, sizeof(cmd));

    if (++it >= resp.end())
    {
        return;
    }

    const uint8_t pin = it.asInt();

    if (++it >= resp.end())
    {
        return;
    }

    switch (cmd)
    {
    case NW_16:
        HA_DEBUG("Normal Write");
        if (HomeAtionHandlers[pin])
        {
            char *begin = (char *)it.asStr();
            HomeAtionData data(begin, header.msg_len - (begin - buff));
            HomeAtionHandlers[pin](header, data);
        }
        break;
    case NR_16:
        Serial.println("Normal Read");
        break;
    default:
        Serial.println("Unknown command");
    }
}

void HomeAtion::connect()
{
    client.stop();
    while (!client.connect(this->_host, this->_port))
    {
        delay(1000);
    }
    HomeAtionOnConnected();
    authenticate();
}

void HomeAtion::run()
{
    if (client.connected())
    {
        HomeAtionHeader buffer;
        int rlen = client.read((uint8_t *)&buffer, 4);
        if (rlen == sizeof(HomeAtionHeader))
        {
            char msg[buffer.msg_len + 1];
            client.read((uint8_t *)msg, buffer.msg_len);

            switch (buffer.msg_type)
            {
            case MSG_AUTH:
                if (msg[0] == 'y')
                {
                    HA_DEBUG("Authenticated");
                }
                else if (msg[0] == 'n')
                {
                    HA_DEBUG("Invalid Auth Token");
                }
                break;
            case MSG_RW:
                processCmd(buffer, msg);
                break;
            default:
                HA_DEBUG("Unknown message type");
                break;
            }
        }
    }
    else
    {
        HomeAtionOnDisconnected();
        connect();
    }
    yield();
}

HomeAtion homeAtion;