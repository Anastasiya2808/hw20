import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

interface Command{
    void execute();

    static Command getCommand(String command, EchoServer server){
        if (command.equalsIgnoreCase("count")) {
            return new Count(server);
        }
        return null;
    }
}

class Count implements Command{
    private EchoServer server;

    public Count(EchoServer server) {
        this.server = server;
    }

    @Override
    public void execute() {
    }
}

public class EchoServer {


    private int port;
    private Connection connection;
    private int number = 0;

    public EchoServer(int port) {
        this.port = port;
    }

    public void start() throws IOException, ClassNotFoundException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started...");
            while (true) {
                Socket socket = serverSocket.accept();
                connection = new Connection(socket);

                number++;
                SimpleMessage message = connection.readMessage();
                printMessage(message);

                if (message.getText().equalsIgnoreCase("/help")) {
                    connection.sendMessage(SimpleMessage.getMessage("server", "Список доступных команд: /help - вывод команд; /count-количество подключений; /ping- время; /exit-выход"));

                } else if (message.getText().equalsIgnoreCase("/count")) {
                    connection.sendMessage(SimpleMessage.getMessage("server", Integer.toString(number)));

                } else if (message.getText().equalsIgnoreCase("/ping")) {
                    connection.sendMessage(SimpleMessage.getMessage("server", "Как определить время отправки?"));

                } else if (message.getText().equalsIgnoreCase("/exit")) {
                    connection.sendMessage(SimpleMessage.getMessage("server", "Как выйти?"));
                }
                Command command = Command.getCommand(connection.readMessage().getText(), this);
                command.execute();
                connection.sendMessage(SimpleMessage.getMessage("server", "сообщение получено"));
            }
        }
    }

    private void printMessage(SimpleMessage message) {
        System.out.println("получено сообщение: " + message);
    }

    public static void main(String[] args) {
        int port = 8090;
        EchoServer messageServer = new EchoServer(port);
        try {
            messageServer.start();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}