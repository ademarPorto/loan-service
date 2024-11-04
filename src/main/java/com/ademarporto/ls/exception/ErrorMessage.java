package com.ademarporto.ls.exception;

public class ErrorMessage {

    private ErrorMessage() {
    }

    public static final String NOT_READABLE_REQUEST_BODY_CODE = "ERROR_001";
    public static final String NOT_READABLE_REQUEST_BODY_MESSAGE =  "Malformed JSON request.";
    public static final String METHOD_ARGUMENT_NOT_VALID_CODE = "ERROR_002";
    public static final String METHOD_ARGUMENT_NOT_VALID_CODE_MESSAGE =
            "Invalid parameter: [ %s ], cause: field [ %s ].";

    public static final String CLIENT_NOT_FOUND_CODE = "ERROR_003";
    public static final String CLIENT_NOT_FOUND_MESSAGE = "Not found any client with id [ %s ]";

    public static final String LOAN_NOT_FOUND_CODE = "ERROR_004";
    public static final String LOAN_NOT_FOUND_MESSAGE = "The client [%s] does not have any loan with id [ %s ]";


}
