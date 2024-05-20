package com.creditcardcomparison.controller;

import com.creditcardcomparison.http.HttpMethod;
import com.creditcardcomparison.http.HttpResponse;
import com.creditcardcomparison.http.HttpStatusCode;

public class MainController {

    private final HttpMethod GET = HttpMethod.GET;
    private final HttpMethod POST = HttpMethod.POST;

    private HttpResponse response;

    public String getResponseBody(HttpResponse response) {
        this.response = response;

        if (requestLineMatches(GET, "/")) {
            response.setHttpStatusCode(HttpStatusCode.SUCCESS_RESPONSE_200_OK);
            return homepage();
        }

        if (requestLineMatches(POST, "/")) {
            response.setHttpStatusCode(HttpStatusCode.SUCCESS_RESPONSE_200_OK);
            return examplePost() + response.getRequestBody();
        }

        // Default Web Page
        response.setHttpStatusCode(HttpStatusCode.CLIENT_ERROR_404_NOT_FOUND);
        return notFoundPage();
    }


    /** --- Helper Methods --- **/
    private boolean requestLineMatches(HttpMethod httpMethod, String path) {
        if (response != null) {
            if (this.response.getRequestMethod() == httpMethod) {
                if (this.response.getRequestTarget().equals(path)) {
                    return true;
                }
            }
        }
        return false;
    }


    /** --- Pages --- **/
    private String homepage() {
        return "<!DOCTYPE html>" +
                "<html lang=\"en\">" +
                "<body>" +
                "<p>Hello world, this is an example GET Request!</p>" +
                "</body>" +
                "</html>";
    }

    private String examplePost() {
        return "This is an example POST response.\n";
    }

    private String notFoundPage() {
        return "<!DOCTYPE html>" +
                "<html lang=\"en\">" +
                "<body>" +
                "<p>Sorry, this webpage does not exist.</p>" +
                "</body>" +
                "</html>";
    }




}
