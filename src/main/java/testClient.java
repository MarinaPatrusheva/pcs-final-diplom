import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class testClient {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("127.0.0.1", 8989);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream());
            out.println("блокчейн технология");
            out.flush();
            String txt = "";
            while (txt != null) {
                txt = in.readLine();
                System.out.println(txt);
            }
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
