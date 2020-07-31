#include <HID.h>
#include <Keyboard.h>

/*
 * This sketch is for use the Legado Hawking device
 * Created 2018/11/18 
 * By Rodolfo Navarro B.
 * E-Mail rnavarro@apcnet.co.cr
 * For use the ACAT Software in ANDROID phone, Raspberry or Windows
 */
 
const int sensorPin = A0; // select the input pin sensor
int sensorValue = 0; // variable to store the value coming from the sensor
int direction_Sensor =1;

const int buttonPinA1=A1; //select the input pin normal button
int buttonA1State=0;
int direction_BA1=1;

const int buttonPinA2=A2; //select the input pin button select normal button or sensor
int buttonA2State=0;
int direction_BA2=1;

bool Button_OR_Sensor; //Status for option between Button(true) or Sensor(false)

void setup ()
{
  pinMode(sensorPin,INPUT_PULLUP);
  pinMode(buttonPinA1,INPUT);
  pinMode(buttonPinA2,INPUT);
  Keyboard.begin();
  Button_OR_Sensor=true; 
}
 
void loop ()
{
    buttonA2State=digitalRead(buttonPinA2);
    if (buttonA2State==HIGH){ //if press button A2 change normal button or sensor
      direction_BA2 *= -1;
      if (direction_BA2==1){
        if (Button_OR_Sensor){
          Button_OR_Sensor=false;
        } else {
          Button_OR_Sensor=true;
        }
      }
      delay(200);
    }
    
    if (Button_OR_Sensor){
      buttonA1State=digitalRead(buttonPinA1);
      if (buttonA1State== HIGH){
        direction_BA1 *= -1;
        if (direction_BA1 == 1) {
          Keyboard.press(205);
          delay(300);
          Keyboard.releaseAll();
        }
        delay(200);
      }
    } else {
      sensorValue = analogRead (sensorPin);
      if(sensorValue <= 400)
       {
        direction_Sensor *=-1;
        if (direction_Sensor == 1) {
          Keyboard.press(205);
          delay(300);
          Keyboard.releaseAll();
        }
        delay(200);
       }
    }
}
