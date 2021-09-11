#ifndef HomeAtion_H
#define HomeAtion_H

#include <ESP8266WiFi.h>
#include <string.h>
#include <stdlib.h>

#define HA_DEBUG(x) Serial.println(x)

struct HomeAtionHeader
{
    uint16_t msg_type;
    uint16_t msg_len;
};


#define HomeAtionWrite(PIN) \
    void HomeAtion_Write_##PIN(HomeAtionHeader &header, HomeAtionData &data); \
    uint8_t tempW##PIN = registerHomeAtionHandler(PIN, HomeAtion_Write_##PIN); \
    void HomeAtion_Write_##PIN(HomeAtionHeader &header, HomeAtionData &data)

#define HomeAtion_Connected() \
    void HomeAtionOnConnected()

#define HomeAtion_Disconnected() \
    void HomeAtionOnDisconnected()


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


class HomeAtionDataIterator{
    const char* begin;
    const char* end;
    public:
    HomeAtionDataIterator(const char* begin, const char* end):begin(begin),end(end){}
    bool isValid() const { return begin != NULL && begin < end; }
    operator const char* () const   { return asStr(); }
    operator int () const           { return asInt(); }
    const char* asStr() const       { return begin; }
    const char* asString() const    { return begin; }
    int asInt() const { if(!isValid()) return 0; return atoi(begin); }
    long asLong() const { if(!isValid()) return 0; return atol(begin); }

    HomeAtionDataIterator& operator++() { 
        if (isValid()){
            begin += strlen(begin) + 1;
        }
        return *this;
    }

    HomeAtionDataIterator& operator--() { 
        if (isValid()){
            begin -= strlen(begin) - 1;
        }
        return *this;
    }


    bool operator <  (const HomeAtionDataIterator& it) const { return begin < it.begin; }
    bool operator >= (const HomeAtionDataIterator& it) const { return begin >= it.begin; }
};


class HomeAtionData{
    char* buffer;
    size_t len;
    size_t buffer_size;

    public:
    using iterator = HomeAtionDataIterator;
    HomeAtionData(const void* addr, size_t length):
        buffer((char*)addr), len(length){}

    HomeAtionData(const void* addr, size_t length, size_t buffer_size):
        buffer((char*)addr), len(length), buffer_size(buffer_size){}

    const char* asStr() {return buffer;}
    int asInt() {return atoi(buffer);}
    float asFloat() {return atof(buffer);}

    void add(){

    }

    iterator begin() const { return iterator(buffer, buffer + len); }
    iterator end() const { return iterator(buffer + len, buffer + len); }
};


typedef void (*HomeAtionCallback)(HomeAtionHeader &, HomeAtionData &);

static HomeAtionCallback HomeAtionHandlers[20] = {NULL};

uint8_t registerHomeAtionHandler(uint8_t pin, HomeAtionCallback cb);

class HomeAtion
{
    const char *_token;
    const char *_host;
    uint16_t _port;
    WiFiClient client;

public:
    void begin(const char *token, const char *server, const uint16_t &port);

    void authenticate();

    void processCmd(HomeAtionHeader& header, char* buff);

    void connect();

    void run();
};

extern HomeAtion homeAtion;

#endif