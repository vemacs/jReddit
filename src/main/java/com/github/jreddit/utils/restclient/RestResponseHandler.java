package com.github.jreddit.utils.restclient;

import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;

import java.io.IOException;
import java.io.InputStream;

public class RestResponseHandler implements ResponseHandler<Response> {

    private final JsonParser jsonParser;

    public RestResponseHandler() {
        this.jsonParser = new JsonParser();
    }

    public RestResponseHandler(JsonParser jsonParser) {
        this.jsonParser = jsonParser;
    }

    @Override
    public Response handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
        try {
            return parse(response);
        }
        catch (JsonParseException e) {
            System.err.println("Error parsing response from Reddit");
        }
        return null;
    }

    private Response parse(HttpResponse httpResponse) throws IOException, JsonParseException {
        InputStream responseStream = httpResponse.getEntity().getContent();
        String content = IOUtils.toString(responseStream, "UTF-8");
        Object responseObject = jsonParser.parse(content);
        return new RestResponse(content, responseObject, httpResponse);
    }
}
