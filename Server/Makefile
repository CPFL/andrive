TARGET = PriusController_server
CC = gcc

$(TARGET) : PriusController_server.o 
	$(CC) -o $@ PriusController_server.o 

server.o : PriusController_server.c
	$(CC) -c $? 

clean:;	rm -f PriusController_server.o PriusController_server
