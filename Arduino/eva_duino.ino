
#define led 5
//.............

#include <Arduino.h>
#include <SoftwareSerial.h>
#define LENG 31   //0x42 + 31 bytes equal to 32 bytes
unsigned char buf[LENG];
int PM01Value=0;          //define PM1.0 value of the air detector module
int PM2_5Value=0;         //define PM2.5 value of the air detector module
int PM10Value=0;         //define PM10 value of the air detector module
int count=0;
char command;
String string;
boolean ledon = false;
SoftwareSerial BT(10, 11); 
// creates a "virtual" serial port/UART
// connect BT module TX to D10
// connect BT module RX to D11
// connect BT Vcc to 5V, GND to GND


void setup()
{
  BT.begin(9600);
  Serial.begin(9600);   //use serial0
  Serial.setTimeout(1500);    //set the Timeout to 1500ms, longer than the data transmission periodic time of the sensor
  pinMode(led, OUTPUT);
}

void loop()
{
  if(Serial.find(0x42)){    //start to read when detect 0x42
    Serial.readBytes(buf,LENG);

    if(buf[0] == 0x4d){
      if(checkValue(buf,LENG)){
        PM01Value=transmitPM01(buf); //count PM1.0 value of the air detector module
        PM2_5Value=transmitPM2_5(buf);//count PM2.5 value of the air detector module
        PM10Value=transmitPM10(buf); //count PM10 value of the air detector module 
      }           
    } 
  }

if (BT.available() > 0)
{string = "";}

  while(BT.available() > 0)
  {
    command = ((byte)BT.read()) ;
    if(command == ':')
    {
    break;
    }
    else
    {
    string += command;
    }
    delay(1);
  }
    if(string == "TO")
    {
    ledOn();
    ledon = true;
    }
    if(string =="TF")
    {
    ledOff();
    ledon = false;
    }
    if(string=="ttt")
        readSensor();

      static unsigned long OledTimer=millis();  
    if (millis() - OledTimer >=1000) 
    {
      OledTimer=millis(); 
      
      Serial.print("PM1.0: ");  
      Serial.print(PM01Value);
      Serial.println("  ug/m3");            
    
      Serial.print("PM2.5: ");  
      Serial.print(PM2_5Value);
      Serial.println("  ug/m3");     
      
      Serial.print("PM1 0: ");  
      Serial.print(PM10Value);
      Serial.println("  ug/m3");   
      Serial.println();

      for(int i=0;i<31;i++)
      {
        Serial.print(buf[i]);
        Serial.print(" ");
      } 
      Serial.println();
    }
           
}
  
    
  
  

void readSensor(){

 //     BT.print("PM1.0: ");  
 
      BT.print(PM01Value);
      BT.print(",");
  //   BT.println("  ug/m3\n");            
    
  //    BT.print("PM2.5: ");  
      BT.print(PM2_5Value);
      BT.print(",");
  //    BT.println("  ug/m3\n");     
      
   //   BT.print("PM1 0: ");  
      BT.print(PM10Value);
      BT.println(",");
      

  
}
char checkValue(unsigned char *thebuf, char leng)
{  
  char receiveflag=0;
  int receiveSum=0;

  for(int i=0; i<(leng-2); i++){
  receiveSum=receiveSum+thebuf[i];
  }
  receiveSum=receiveSum + 0x42;
 
  if(receiveSum == ((thebuf[leng-2]<<8)+thebuf[leng-1]))  //check the serial data 
  {
    receiveSum = 0;
    receiveflag = 1;
  }
  return receiveflag;
}

int transmitPM01(unsigned char *thebuf)
{
  int PM01Val;
  PM01Val=((thebuf[3]<<8) + thebuf[4]); //count PM1.0 value of the air detector module
  return PM01Val;
}

//transmit PM Value to PC
int transmitPM2_5(unsigned char *thebuf)
{
  int PM2_5Val;
  PM2_5Val=((thebuf[5]<<8) + thebuf[6]);//count PM2.5 value of the air detector module
  return PM2_5Val;
  }

//transmit PM Value to PC
int transmitPM10(unsigned char *thebuf)
{
  int PM10Val;
  PM10Val=((thebuf[7]<<8) + thebuf[8]); //count PM10 value of the air detector module  
  return PM10Val;
}




void ledOn()
{
analogWrite(led, 255);
delay(10);
}
void ledOff()
{
analogWrite(led, 0);
delay(10);
}
