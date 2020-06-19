start java -jar iot-env-discoveryServer\target\iot-env-discoveryServer.war --server.port=8761

PING localhost -n 4 > NUL
start java -jar iot-env-authServer\target\iot-env-authServer.war --server.port=8080
start java -jar iot-env-filterServer\target\iot-env-filterServer.war --server.port=8081
start java -jar iot-env-windowActuator\target\iot-env-windowActuator.war --server.port=8082
start java -jar iot-env-windowSensor\target\iot-env-windowSensor.war --server.port=8083
start java -jar iot-env-thermometerSensor\target\iot-env-thermometerSensor.war --server.port=8084
start java -jar iot-env-heatingServer\target\iot-env-heatingServer.war --server.port=8085