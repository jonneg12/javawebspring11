package ru.netology.server.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class Request {

    private final String method;
    private final String path;
    private final Map<String, String> headers;
    private final InputStream in;

    public Request(String method, String path, Map<String, String> headers, InputStream in) {
        this.method = method;
        this.path = path;
        this.headers = headers;
        this.in = in;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public InputStream getIn() {
        return in;
    }

    public static Request fromInputStream(InputStream inputStream) throws IOException {
        final BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

        final String requestLine = in.readLine();
        final String[] parts = requestLine.split(" ");

        //form like GET /path HTTP/1.1
        String method = parts[0];
        String path = parts[1];

        if (parts.length != 3) {
            throw new IOException("Invalid request!");
        }

        //parsing headers to map. it must be like header: value
        String line;
        HashMap<String, String> headers = new HashMap<>();
        while (!(line = in.readLine()).equals("")) {
            int indexOf = line.indexOf(":");
            String headerName = line.substring(0, indexOf);
            String headerValue = line.substring(indexOf + 2);
            headers.put(headerName, headerValue);
        }
        return new Request(method, path, headers, inputStream);
    }

    @Override
    public String toString() {
        return "Request{" +
                "method='" + method + '\'' +
                ", path='" + path + '\'' +
                ", headers=" + headers +
                '}';
    }


}
