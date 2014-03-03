// Motors
int MAX_SPEED = 255;
int MIN_SPEED = 150;
// rigth
int ENA = 5;
int IN1 = 2;
int IN2 = 3;
// left
int ENB = 6;
int IN3 = 4;
int IN4 = 7;

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
  // setup motors
  pinMode( ENA, OUTPUT );
  pinMode( ENB, OUTPUT );
  pinMode( IN1, OUTPUT );
  pinMode( IN2, OUTPUT );
  pinMode( IN3, OUTPUT );
  pinMode( IN4, OUTPUT );
  runMotors();
}

void stopMotors() 
{
  digitalWrite( ENA, LOW );
  digitalWrite( ENB, LOW );
}

void runMotors()
{
  digitalWrite( ENA, HIGH );
  digitalWrite( ENB, HIGH );
}

void setMotorRForward()
{
  digitalWrite(IN1, LOW);
  digitalWrite(IN2, HIGH);
}

void setMotorRBackward()
{
  digitalWrite(IN1, HIGH);
  digitalWrite(IN2, LOW);
}

void setMotorLForward()
{
  digitalWrite(IN3, HIGH);
  digitalWrite(IN4, LOW);
}

void setMotorLBackward()
{
  digitalWrite(IN3, LOW);
  digitalWrite(IN4, HIGH);
}

int computeEffectiveSpeed( int input )
{
  float spd = input / 100.0f;
  if ( spd < 0 ) {
    spd = -spd;
  }
  return (int)( spd * MAX_SPEED );
}

void setRightMotorSpeed(int spd)
{
  if (spd >= 0) {
    setMotorRForward();
    analogWrite( ENA, computeEffectiveSpeed( spd ) );
  }
  else {
    setMotorRBackward();
    analogWrite( ENA, computeEffectiveSpeed( -spd ) );
  }
}

void setLeftMotorSpeed(int spd)
{
  if (spd >= 0) {
    setMotorLForward();
    analogWrite( ENB, computeEffectiveSpeed( spd ) );
  }
  else {
    setMotorLBackward();
    analogWrite( ENB, computeEffectiveSpeed( -spd ) );
  }
}

void setup() 
{
  pinMode( 13, OUTPUT );
  Serial.begin( 9600 );
  
  setupBT();
  setupMotors();
}

void loop() 
{
  while ( Serial.available() > 0 ) {
    char c = Serial.read();
    switch ( c ) {
      case 'L': {
        int spd = Serial.parseInt();
        setLeftMotorSpeed( spd );
        break;
      }
      case 'R': {
        int spd = Serial.parseInt();
        setRightMotorSpeed( spd );
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
//        Serial.print( "Unknown: " );
//        Serial.println( c );
        break;
      }
    }
  }
}


