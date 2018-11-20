import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Optional;

public class HTTPServer {
    private static HashMap<String, Boolean> data = new HashMap<String, Boolean>();

    public static void main(String[] args) throws Exception {
        String portStr = args[0];
        initializeData();
        HttpServer server = HttpServer.create(new InetSocketAddress(Integer.parseInt(portStr)), 0);
        server.createContext("/get", new MyHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    private static void initializeData() {
        for (int i = 0; i < 56000; i += 3000) {
            data.put(String.valueOf(i), false);
        }
    }

    static class MyHandler implements HttpHandler {
        public synchronized void handle(HttpExchange t) throws IOException {
            String response = "This is the response";
            Optional<String> optFirstKey = data.keySet().stream().filter(s -> !data.get(s)).findFirst();
            response = optFirstKey.map(s -> {
                data.put(s, true);
                return s;
            }).orElse("-1");
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();

        }
    }
}
