#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <string.h>
#include <time.h>
#include <signal.h>
#include <errno.h>
#include <assert.h>

#define FILE_NAME "/home/gpu/Andrive/Andrive_picture/temp.jpg"

int sock;

int getConnect(int port_number);
int sendPicture();


int main(void){
	int answer;

	//get connect to android
	getConnect(12336);
	printf("get connect.\n");

	while(1){
		printf("send picture\n");
		if(sendPicture() == -1)
			break;
		
		//answer = 11;
		//send(sock, &answer, sizeof(answer), 0);
		printf("ans\n");
		recv(sock, &answer, sizeof(answer), 0);
		printf("answer: %d\n", answer);
		sleep(1);
	}

	return 0;

}


int getConnect(int port_number){
	struct sockaddr_in addr;
	struct sockaddr_in client;
	int len;
	int sock0;
	int size;
	int yes = 1;

	sock0 = socket(AF_INET, SOCK_STREAM, 0);

	addr.sin_family = AF_INET;
	addr.sin_port = htons(port_number); addr.sin_addr.s_addr = INADDR_ANY;
	//make it available immediately to connect
	setsockopt(sock0,SOL_SOCKET, SO_REUSEADDR, (const char *)&yes, sizeof(yes));
	bind(sock0, (struct sockaddr *)&addr, sizeof(addr));
	listen(sock0, 5);
	len = sizeof(client);
	sock = accept(sock0, (struct sockaddr *)&client, &len);
	if(sock == -1){
		printf("ERROR: cannot accept\n");
		return -1;
	}
	return 0;
}


// send image data to andrid
int sendPicture(){
        //TCP
        char buf[32];
        char size_str[20];
        int n,yes=1;

        FILE *fpr;
        unsigned char temp[500000];

        int size = 0, re = 0;

        //File path
        //if you want to send the image data, you shoud set the this file path.
        if((fpr = fopen( "temp.jpg", "rb" )) == NULL ){
                printf("ERROR: can not open.\n");
                return -1;
        }

        //search size of image file 
        size = fread( temp, sizeof( unsigned char ), 500000, fpr);
        sprintf(size_str,"%d",size);
	printf("file_Size: %s\n", size_str);
	//send size of image file to android
        re = send(sock, &size, sizeof(size), 0);
        //send image data to android
	re = send(sock, &temp, size, 0);
        if( re == -1 ){
                switch(errno){
                        case EPIPE:
                                break;
                }
        }
	

        //fflush buffer
        fflush(stdout);
        fclose(fpr);
	return 0;
}
