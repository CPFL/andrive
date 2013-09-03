TARGET = Andorive_server
CC = gcc

$(TARGET) : Andorive_server.o 
	$(CC) -o $@ Andorive_server.o 

server.o : Andorive_server.c
	$(CC) -c $? 

clean:;	rm -f Andorive_server.o Andorive_server
