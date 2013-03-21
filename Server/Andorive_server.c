#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <string.h>

void getConnect();
int getSensorValue();

int sock_num;
int sock0;

int main(void){

	//get connect to android
	getConnect();
	printf("get connect.\n");
	while(1){
		//get value of sensor from android	
		if(getSensorValue() == -1)
			break;
	}

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
	addr.sin_port = htons(12345); addr.sin_addr.s_addr = INADDR_ANY;
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
	int pitch = 0;
	int roll = 0;

	if(recv(sock_num, &pitch, 4, 0) == -1){
		printf("ERROR: can not recieve pitch info\n");
		return -1;
	}

	if(recv(sock_num, &roll, 4, 0) == -1){
		printf("ERROR: can not recieve roll info\n");
		return -1;
	}
	printf("pitch: %d\nroll: %d\n", pitch, roll);
	
	if(pitch == 900 && roll == 900){
		printf("Program stop.\n");
		return -1;
	}
	
	return 0;
}
