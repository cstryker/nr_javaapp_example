import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import com.newrelic.api.agent.Trace;

public class App {

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/test", new MyHandler());
        server.createContext("/other", new OtherHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    static class MyHandler implements HttpHandler {
        @Override
        @Trace (dispatcher=true)
        public void handle(HttpExchange t) throws IOException {
            String response = "This is a very nice response";
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    static class OtherHandler implements HttpHandler {
        @Override
        @Trace (dispatcher=true)
        public void handle(HttpExchange t) throws IOException {
            String response = "This is another very nice response";
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            this.doSomething();
            os.write(response.getBytes());
            os.close();
        }

        @Trace
        public void doSomething() {
        }
    }
}
