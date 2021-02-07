package ru.netology.server.request;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Request {

    private final String method;
    private final String path;
    private final Map<String, String> headers;
    private final InputStream in;
    private final List<NameValuePair> queryParams;

    private Request(String method, String path, Map<String, String> headers, InputStream in, List<NameValuePair> queryParams) {
        this.method = method;
        this.path = path;
        this.headers = headers;
        this.in = in;
        this.queryParams = queryParams;

    }

    public List<NameValuePair> getQueryParams() {
        return queryParams;
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

    public static Request fromInputStream(InputStream inputStream) throws IOException, URISyntaxException {
        final BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

        final String requestLine = in.readLine();
        final String[] parts = requestLine.split(" ");

        //form like GET /path HTTP/1.1
        String method = parts[0];

        final int indexOfQuery = parts[1].indexOf("?");

        String path;
        String query = "";
        if (indexOfQuery == -1) {
            path = parts[1];
        } else {
            path = parts[1].substring(0, indexOfQuery);
            query = parts[1].substring(indexOfQuery);
        }

        final List<NameValuePair> queryParams = URLEncodedUtils.parse(new URI(query), StandardCharsets.UTF_8);

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
        return new Request(method, path, headers, inputStream, queryParams);
    }

    public String getQueryParam(String name) {
        for (NameValuePair queryParam : queryParams) {
            if(queryParam.getName().equals(name)) return queryParam.getValue();
        }
        return "value not found!";
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
