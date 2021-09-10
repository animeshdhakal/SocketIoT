#include <ESP8266WiFi.h>
#include "HomeAtion.h"


// HomeAtion_Connected(){
//   Serial.println("Connected to Server");
// }

// HomeAtion_Disconnected(){
//   Serial.println("Disconnected from Server");
// }

HomeAtion_Write(0){
  Serial.println("Write 0");
  Serial.println(msg);
}

void setup(){
  Serial.begin(115200);

  WiFi.begin("unique nepal", "PIPEHUME");
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.println("Connecting to WiFi..");
  }
  Serial.println("Connected to the WiFi network");
  
  homeAtion.begin("123", "192.168.100.12", 3000);

  // homeAtion.writeHandler(0, [](HomeAtionHeader& header, const String& data){
  //   Serial.println("Received data");
  // });


}

void loop(){
  homeAtion.run();
}