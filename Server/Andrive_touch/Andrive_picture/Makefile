TARGET = Andrive_Pic
CC = gcc

$(TARGET) : Andrive_Pic.o
	$(CC) -g -o $@ Andrive_Pic.o 

server.o : Andrive_Pic.c
	$(CC) -g -c $? 

clean:
	rm -f Andrive_Pic.o Andrive_Pic
