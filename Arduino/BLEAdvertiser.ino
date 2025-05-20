#include <ArduinoBLE.h>

void setup()
{
  Serial.begin(9600); 

  if (Serial) {
    delay(100); // Give some time for Serial to stabilize
    Serial.println("Serial connected");
  }

  if (BLE.begin()) {
    if (Serial) {
      Serial.println("BLE device successfully initialized!");
      Serial.print("BLE device address is: ");
      Serial.println(BLE.address());
    }
  }  
  else {
    if (Serial) Serial.println("BLE device initialization failed!");
  }

  int N = 32;
  BLE.setAdvertisingInterval(N); // interval = 0.625 ms * N, min 20 ms (N=32)
  BLE.advertise();
}

void loop()
{
  BLE.poll();
}