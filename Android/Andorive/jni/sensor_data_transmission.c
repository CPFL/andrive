#include <jni.h>
#include <android/log.h>

#include <stdio.h>
#include <stdlib.h>

#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>

//Global Functions
int sock;

JNIEXPORT void JNICALL Java_com_andorive_GetSensorNative_connectServer(JNIEnv * env, jobject obj, jstring address, jint port_number ){
	  struct sockaddr_in server;

	  const char *address_number = (*env)->GetStringUTFChars(env, address, 0);

	  /* create socket */
	  sock = socket(AF_INET, SOCK_STREAM, 0);

	  /* Preparation of the structure for the specified destination */
	  server.sin_family = AF_INET;
	  // default: 12345
	  //server.sin_port = htons(12345);
	  server.sin_port = htons(port_number);

	  //cloudstack
	  //server.sin_addr.s_addr = inet_addr("192.168.129.88");
	  //home
	  //server.sin_addr.s_addr = inet_addr(buf);
	  //gpu
	  //server.sin_addr.s_addr = inet_addr("192.168.2.226");
	  server.sin_addr.s_addr = inet_addr(address_number);

	  /* connect to server */
	  connect(sock, (struct sockaddr *)&server, sizeof(server));
}

JNIEXPORT void JNICALL Java_com_andorive_GetSensorNative_closeConnect(JNIEnv * env){
	if(close(sock)<0){
		printf("error_socket\n");
	}
}


JNIEXPORT void JNICALL Java_com_andorive_GetSensorNative_sendSensorValue(JNIEnv * env,jobject thiz, jint pitch, jint accelerator, jint brake, jint gearNum){

	//float* values_data = (*env)->GetPrimitiveArrayCritical(env, values, NULL);

	int sensorInfo[4];

	sensorInfo[0] = pitch;
	sensorInfo[1] = accelerator;
	sensorInfo[2] = brake;
	sensorInfo[3] = gearNum;

	send(sock, &sensorInfo, 16, 0);

//	send(sock, &pitch, 4, 0);
//	send(sock, &accelerator, 4, 0);
//	send(sock, &brake, 4, 0);
//	send(sock, &gearNum, 4, 0);

	//(*env)->ReleasePrimitiveArrayCritical(env, values, values_data, 0);
}

JNIEXPORT void JNICALL Java_com_andorive_GetSensorNative_getSignal(JNIEnv * env){
	int signal;
	recv(sock, &signal, 4, 0);

}
