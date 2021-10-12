#include <ESP8266WiFi.h>
#include "SocketIOT.h"


SocketIOTWrite(1){
    Serial.println("SocketIOTWrite");
    Serial.println(data.toInt());
    digitalWrite(LED_BUILTIN, data.toInt());
}


void setup()
{
    Serial.begin(115200);

    pinMode(LED_BUILTIN, OUTPUT);

    WiFi.begin("unique nepal", "BOLPATRA");

    while (WiFi.status() != WL_CONNECTED)
    {
        Serial.print(".");
        delay(500);
    }
    Serial.println("");
    Serial.print("Connected to ");
    Serial.println(WiFi.SSID());
    Serial.print("IP address: ");
    Serial.println(WiFi.localIP());

    SocketIOT.begin("animesh", "192.168.100.58", 1222);
}

void loop()
{
    SocketIOT.run();
}