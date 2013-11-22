#include <jni.h>
#include <android/log.h>

#include <stdio.h>
#include <stdlib.h>

#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>

//Global Functions
int sock1, sock2;

void setPicture(unsigned char *buf, int sum);

JNIEXPORT void JNICALL Java_com_andrive_GetSensorNative_connectServer(JNIEnv * env, jobject obj, jstring address, jint port_number ){
	struct sockaddr_in server;
	int sock;

	const char *address_number = (*env)->GetStringUTFChars(env, address, 0);

	/* create socket */
	sock = socket(AF_INET, SOCK_STREAM, 0);

	/* Preparation of the structure for the specified destination */
	server.sin_family = AF_INET;
	// default: 12335
	server.sin_port = htons(port_number);
	server.sin_addr.s_addr = inet_addr(address_number);

	/* connect to server */
	connect(sock, (struct sockaddr *)&server, sizeof(server));

	if(port_number == 12335)
		sock1 = sock;
	else if(port_number == 12336)
		sock2 = sock;
}

JNIEXPORT void JNICALL Java_com_andrive_GetSensorNative_closeConnect(JNIEnv * env){
	if(close(sock1)<0){
		printf("error_socket\n");
	}
	if(close(sock2)<0){
		printf("error_socket\n");
	}
}


JNIEXPORT void JNICALL Java_com_andrive_GetSensorNative_sendSensorValue(JNIEnv * env,jobject thiz, float pitch, float accelerator, float brake, jint gearNum, jint pmFlag){

	int sensorInfo[5];

	sensorInfo[0] = (int)pitch;
	sensorInfo[1] = (int)accelerator;
	sensorInfo[2] = (int)brake;
	sensorInfo[3] = gearNum;
	sensorInfo[4] = pmFlag;

    send(sock1, &sensorInfo, sizeof(pitch)*5, 0);

}

JNIEXPORT void JNICALL Java_com_andrive_GetSensorNative_getSignal(JNIEnv * env){
	int signal;
	recv(sock1, &signal, 4, 0);

}

JNIEXPORT jstring JNICALL Java_com_andrive_GetSensorNative_getPhoto(JNIEnv * env){
	int size = 0;
	int file_size = 555555;
	int sum = 0;
	int image_size;
	int answer = 1;
	char log[128];

	//save image data
	static unsigned char buf[500000];

	memset(buf, 0, sizeof(buf));
	size = recv(sock2, &image_size, sizeof(image_size), 0);

	while(1){
		//recieve image data
		size = recv(sock2, buf+sum, image_size-sum, 0);
		sum += size;

		if(sum == image_size){
			setPicture(buf,sum);
			send(sock2, &answer, sizeof(answer), 0);
			return (*env)->NewStringUTF(env, "HELLo");
		}
	}
}

void setPicture(unsigned char *buf, int sum){
	FILE *fpw;
	char *fname_w = "/mnt/sdcard/";
	int i, size;
	char file_name[32];

	//sprintf(file_name,"%s%02d:%02d:%02d:%3d.jpg",fname_w, tmptr->tm_hour, tmptr->tm_min, tmptr->tm_sec, tv.tv_usec/1000);
	sprintf(file_name,"%s%s.jpg",fname_w, "recieve");

	fpw = fopen( file_name, "wb" );
	if( fpw == NULL ){
		printf( "書込用 %sファイルが開けません\n", fname_w );
		return;
	}

	fwrite( buf, sizeof( unsigned char ), sum, fpw );

	fclose( fpw );
}
