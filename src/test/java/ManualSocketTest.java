import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class ManualSocketTest {
    private final static int SERVER_PORT = 1234;
    private ServerSocket serverSocket;

    private static class ClientHandler extends Thread {
        private Socket clientSocket;
        private InputStream in;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try {
                in = clientSocket.getInputStream();
                int i;
                byte[] buf = new byte[2000];
                while (in.read(buf) > 0) {}
                ByteBuffer byteBuffer = ByteBuffer.allocate(4);
                byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
                byteBuffer.put(Arrays.copyOfRange(buf, 4, 8));
                byteBuffer.flip();
                int jsonLength = byteBuffer.getInt();
                byte[] json = new byte[jsonLength];
                System.arraycopy(buf, 8, json, 0, jsonLength);
                System.out.println(new String(json, "UTF_16LE"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void start() throws IOException {
        serverSocket = new ServerSocket(SERVER_PORT);
        while (true) {
            new ClientHandler(serverSocket.accept()).start();
        }
    }

    public void stop() throws IOException {
        serverSocket.close();
    }

    public static void main(String[] args) throws IOException {
        ManualSocketTest server = new ManualSocketTest();
        server.start();
    }
}
