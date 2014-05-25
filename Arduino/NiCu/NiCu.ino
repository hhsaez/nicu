#include "Ultrasonic.h"
#include <Servo.h>

#define LEFT_SERVO_PIN 6
#define LEFT_SERVO_STOP 87

#define RIGHT_SERVO_PIN 7
#define RIGHT_SERVO_STOP 89

Servo leftServo;
int leftServoSpeed = LEFT_SERVO_STOP;
Servo rightServo;
int rightServoSpeed = LEFT_SERVO_STOP;

// Sensors
int SENSOR_WAIT_MS = 50;
Ultrasonic leftSensor( 8, 9 ); // (Trig PIN,Echo PIN)
Ultrasonic centerSensor( 11, 10 ); // (Trig PIN,Echo PIN)
Ultrasonic rightSensor( 13, 12 ); // (Trig PIN,Echo PIN)

// utility
unsigned long prevMillis;

void rangeSweep() {
  Serial.print( leftSensor.Ranging( CM ) ); // CM or INC
  Serial.print( " " );
  delay( 50 );
  Serial.print( centerSensor.Ranging( CM ) ); // CM or INC
  Serial.print( " " );
  delay( 50 );
  Serial.print( rightSensor.Ranging( CM ) ); // CM or INC
  Serial.println( "" );
  delay( 50 );
}

void setupBT()
{
  digitalWrite( 13, HIGH );
  delay( 5000 );
  digitalWrite( 13, LOW );
  Serial.print( "AT" );
  delay( 1000 );
  Serial.print( "AT+NAME" );
  Serial.print( "ninodecobre" );
  delay( 1000 );
  Serial.print( "AT+BAUD" );
  Serial.print( "4" );
  delay( 1000 );
  Serial.print( "AT+PIN" );
  Serial.print( "9999" );
  delay( 1000 );
  Serial.print( "Setup completed" );
  digitalWrite( 13, HIGH );
  delay( 1000 );
}

void setupMotors()
{
  leftServo.attach(LEFT_SERVO_PIN);
  rightServo.attach(RIGHT_SERVO_PIN);
}

void stopMotors()
{
  leftServo.write(LEFT_SERVO_STOP);
  rightServo.write(RIGHT_SERVO_STOP);
}

void setLeftMotorSpeed(float value)
{
  int spd = (int)(LEFT_SERVO_STOP + 90 * (value / 100));
  leftServoSpeed = constrain(spd, 0, 180);
}

void setRightMotorSpeed(float value)
{
  int spd = (int)(RIGHT_SERVO_STOP - 90 * (value / 100));
  rightServoSpeed = constrain(spd, 0, 180);
}

void setup() 
{
  pinMode( 13, OUTPUT );
  Serial.begin( 9600 );
  Serial.setTimeout( 100 );
  
  setupBT();
  setupMotors();
  
  prevMillis = millis();
  stopMotors();
}

void loop() 
{
  int currMillis = millis();
  if ( currMillis - prevMillis >= 1000 ) {
    rangeSweep();
    prevMillis = currMillis;
  }
  
  while ( Serial.available() > 0 ) {
    char c = Serial.read();
    switch ( c ) {
      case 'L': {
        int spd = Serial.parseInt();
        setLeftMotorSpeed( spd );
        leftServo.write(leftServoSpeed);
        break;
      }
      case 'R': {
        int spd = Serial.parseInt();
        setRightMotorSpeed( spd );
        rightServo.write(rightServoSpeed);
        break;
      }
      case 'S': {
        int t = Serial.parseInt();
        delay( t );
        stopMotors();
        break;
      }
      case 'W': {
        digitalWrite( 13, HIGH );
        delay( 1000 );
        break;
      }
      case 'E': {
        digitalWrite( 13, LOW );
        delay( 1000 );
        break;
      }
      default: {
        break;
      }
    }
  }
}


