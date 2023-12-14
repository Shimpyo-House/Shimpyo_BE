package com.fc.shimpyo_be.global.exception;

public class InvalidParameterException extends ApplicationException{

    public InvalidParameterException() {
        super(ErrorCode.INVALID_PARM);
    }
}
