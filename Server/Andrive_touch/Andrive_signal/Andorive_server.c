#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <string.h>
#include <sys/time.h>


void getConnect();
int getSensorValue();
int sendSignal();

int sock_num;
int sock0;

int pitch;
int accelerator;
int brake;
int gearNum;

int main(void){
	int count=0;
	struct timeval start_timeval, end_timeval;
	double sec_timeofday;

	//get connect to android
	getConnect();
	printf("get connect.\n");
	
	gettimeofday( &start_timeval, NULL );
	while(1){
		//printf("count: %d\n", count);
		//get value of sensor from android	
		if(getSensorValue() == -1)
			break;
		if(sendSignal() == -1)
			break;
		count++;
	}
	gettimeofday( &end_timeval, NULL );
	sec_timeofday = (end_timeval.tv_sec - start_timeval.tv_sec)
			+ (end_timeval.tv_usec - start_timeval.tv_usec) / 1000000.0;

	printf("%f\n",sec_timeofday);
	return 0;
}

void getConnect(void){
	struct sockaddr_in addr;
	struct sockaddr_in client;
	int len;
	int sock;
	int size;
	int yes = 1;

	sock0 = socket(AF_INET, SOCK_STREAM, 0);

	addr.sin_family = AF_INET;
	addr.sin_port = htons(12335); addr.sin_addr.s_addr = INADDR_ANY;
	//make it available immediately to connect
	setsockopt(sock0,SOL_SOCKET, SO_REUSEADDR, (const char *)&yes, sizeof(yes));
	bind(sock0, (struct sockaddr *)&addr, sizeof(addr));
	listen(sock0, 5);
	len = sizeof(client);
	sock = accept(sock0, (struct sockaddr *)&client, &len);
	if(sock == -1){
		printf("ERROR: cannot accept\n");
		return ;
	}
	sock_num = sock;
}

int getSensorValue(){
	int sensorInfo[5];
	if(recv(sock_num, &sensorInfo, 20, 0) == -1){
		printf("ERROR: can not recieve message\n");
		return -1;
	}

	printf("pitch: %d, accelerator: %d, brake %d, gearNum: %d, P/M: %d\n", sensorInfo[0], sensorInfo[1], sensorInfo[2], sensorInfo[3], sensorInfo[4]);

	/*
	 * ギアボックス
	 * B:0演じブレーキ強
	 * R:1後退
	 * N:2ニュートラル
	 * D:3ドライブ
	 */

	if(sensorInfo[1] == 999 && sensorInfo[2] == 999){
		printf("STOP Andorive!!!\n");
		if(close(sock0)<0){
			printf("ERROR: can not close sock0\n");
			return -1;
		}
		if(close(sock_num)<0){
			printf("ERROR: can not close sock_num\n");
			return -1;
		}
		return -1;
	}	

	return 0;
}

int sendSignal(){
	int signal = 0;

	if(send(sock_num, &signal, 4, 0) == -1){
		printf("ERROR: can not send signal\n");
		return -1;
	}
	return 0;
}
