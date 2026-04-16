JAVAC = javac
JAVA = java

all:
	javac MathServer.java MathClient.java MathThread.java LogManager.java

server: all
	java MathServer

client: all
	java MathClient

clean:
	rm -f *.class