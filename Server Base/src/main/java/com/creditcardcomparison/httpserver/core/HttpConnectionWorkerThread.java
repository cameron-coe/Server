package com.creditcardcomparison.httpserver.core;

import com.creditcardcomparison.http.*;
import com.creditcardcomparison.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;

public class HttpConnectionWorkerThread extends Thread{

    private final static Logger LOGGER = LoggerFactory.getLogger(HttpServer.class);
    private Socket socket;

    public HttpConnectionWorkerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        InputStream inputStream = null;
        BufferedReader reader = null;

        OutputStream outputStream = null;
        PrintWriter writer = null;

        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream));
            writer = new PrintWriter(outputStream, true);

            String responseBody = "";

            String httpVersionLiteral = HttpVersion.HTTP_1_1.LITERAL;
            String httpStatusCode = "" + HttpStatusCode.SERVER_ERROR_500_INTERNAL_SERVER_ERROR.STATUS_CODE;
            String httpStatusMessage = HttpStatusCode.SERVER_ERROR_500_INTERNAL_SERVER_ERROR.MESSAGE;


            try {
                HttpRequest request = HttpParser.parseHttpRequest(inputStream);
                HttpResponse response = new HttpResponse(request);

                httpVersionLiteral = response.getHttpVersionLiteral();
                httpStatusCode = "" + response.getHttpStatusCode();
                httpStatusMessage = response.getHttpStatusMessage();
                responseBody = response.getResponseBody();
            } catch (HttpParsingException e) {
                httpStatusCode = "" + e.getErrorCode().STATUS_CODE;
                httpStatusMessage = e.getErrorCode().MESSAGE;
            }

            String responseLine = httpVersionLiteral + " " + httpStatusCode + " " + httpStatusMessage;
            final String CRLF = "\r\n";

            String response =
                    responseLine + CRLF +
                            "Content-Length: " + responseBody.getBytes().length + CRLF +
                            CRLF +
                            responseBody +
                            CRLF + CRLF;

            writer.println(response);
            writer.flush();

            LOGGER.info("Connection Processing Finished");
        } catch (IOException e) {
            LOGGER.error("Problem with communication", e);
            e.printStackTrace();
        } finally {
            // Close resources
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {}
            }
            if (writer != null) {
                writer.close();
            }

            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {}
            }

            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {}
            }

            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {}
            }

        }
    }
}
