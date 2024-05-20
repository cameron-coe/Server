package com.creditcardcomparison.http;

import com.creditcardcomparison.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class HttpParser {

    private final static Logger LOGGER = LoggerFactory.getLogger(HttpServer.class);

    private static final int SPACE = 0x20; // 32
    private static final int CARRIAGE_RETURN = 0x0D; // 13
    private static final int LINE_FEED = 0x0A; // 10

    public static HttpRequest parseHttpRequest(InputStream inputStream) throws HttpParsingException {
        InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.US_ASCII);

        HttpRequest request = new HttpRequest();

        // Parse Request Line
        try {
            parseRequestLine(reader, request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        parseHeaders(reader, request);

        // Parse Body
        try {
            parseBody(reader, request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return request;
    }

    private static void parseRequestLine(InputStreamReader reader, HttpRequest request) throws IOException, HttpParsingException {
        StringBuilder processingDataBuffer = new StringBuilder();

        boolean methodParsed = false;
        boolean requestTargetParsed = false;

        int currentByte = 0;
        while (reader.ready()) {
            currentByte = reader.read();
            if(currentByte == CARRIAGE_RETURN) {
                currentByte = reader.read();
                if(currentByte == LINE_FEED) {
                    LOGGER.debug("Request Line VERSION to Process: {}", processingDataBuffer.toString());
                    try {
                        request.setHttpVersion(processingDataBuffer.toString());
                    } catch (BadHttpVersionException e) {
                        throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                    }

                    // Create an error if there are LESS than 3 items in the request line
                    if(!methodParsed || !requestTargetParsed) {
                        throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                    }

                    return;
                } else {
                    // When next character after Carriage Return is not a Line Feed
                    throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                }
            }

            if (currentByte == SPACE) {
                if (!methodParsed) {
                    LOGGER.debug("Request Line METHOD to Process: {}", processingDataBuffer.toString());
                    request.setMethod(processingDataBuffer.toString());
                    methodParsed = true;
                } else if (!requestTargetParsed) {
                    LOGGER.debug("Request Line REQUEST TARGET to Process: {}", processingDataBuffer.toString());
                    request.setRequestTarget(processingDataBuffer.toString());
                    requestTargetParsed = true;
                } else {
                    // Create an error if there are MORE than 3 items in the request line
                    throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                }
                processingDataBuffer.delete(0, processingDataBuffer.length()); // Clear Buffer
            } else {
                processingDataBuffer.append((char)currentByte);

                // Limit the length of the method
                if (!methodParsed) {
                    if (processingDataBuffer.length() > HttpMethod.MAX_LENGTH) {
                        throw new HttpParsingException(HttpStatusCode.SERVER_ERROR_501_NOT_IMPLEMENTED);
                    }
                }
            }
        }
    }

    private static void parseHeaders(InputStreamReader reader, HttpRequest request) {
        //request.setMethod();
    }

    private static void parseBody(InputStreamReader reader, HttpRequest request) throws IOException, HttpParsingException {
        StringBuilder bodyBuilder = new StringBuilder();

        boolean readingBody = false; // Variable to track if we are currently reading the body

        // Read characters from the reader until end of stream
        int currentByte = 0;
        while (reader.ready()) {
            currentByte = reader.read();

            // Looks for 2 CRLF's to check for the body
            if (currentByte == CARRIAGE_RETURN && reader.ready()) {
                currentByte = reader.read();
                if (currentByte == LINE_FEED && reader.ready()) {
                    currentByte = reader.read();
                    if (currentByte == CARRIAGE_RETURN && reader.ready()) {
                        currentByte = reader.read();
                        if (currentByte == LINE_FEED && reader.ready()) {
                            currentByte = reader.read();

                            // Start Reading the body
                            if (!readingBody) {
                                readingBody = true;
                            }

                        }
                    }
                }
            }

            // Append the character to the bodyBuilder when reading the body
            if (readingBody) {
                bodyBuilder.append((char) currentByte);
            }
        }

        request.setRequestBody(bodyBuilder.toString());


    }

}
