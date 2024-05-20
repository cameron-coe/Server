package com.creditcardcomparison.http;

import com.creditcardcomparison.controller.MainController;

public class HttpResponse extends HttpMessage {

    HttpRequest request;

    private String responseBody;
    private MainController mainController = new MainController();

    /** --- Constructor --- **/
    public HttpResponse(HttpRequest request) {
        this.request = request;
        this.setHttpStatusCode(HttpStatusCode.SUCCESS_RESPONSE_200_OK);

        this.responseBody = mainController.getResponseBody(this);
    }

    /** --- Getters --- **/
    public HttpMethod getRequestMethod() {
        return request.getMethod();
    }

    public String getRequestTarget() {
        return request.getRequestTarget();
    }

    public String getHttpVersionLiteral() {
        return request.getHttpVersionLiteral();
    }

    public HttpVersion getBestCompatibleHttpVersion() {
        return request.getBestCompatibleHttpVersion();
    }

    public int getHttpStatusCode() {
        return request.getHttpStatusCode();
    }

    public String getHttpStatusMessage() {
        return request.getHttpStatusMessage();
    }

    public String getRequestBody() {
        return request.getRequestBody();
    }

    public String getResponseBody() {
        return responseBody;
    }


    /** --- Setters --- **/
    public void setHttpStatusCode(HttpStatusCode httpStatusCode) {
        request.setHttpStatusCode(httpStatusCode);
    }



    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }
}
