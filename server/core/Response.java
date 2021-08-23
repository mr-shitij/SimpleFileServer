package core;

import java.io.Serializable;

public class Response implements Serializable {
    public static final long serialVersionUID = 7129821;
    byte[] dataBuffer;
    public enum ResponseCode {
        RESPONSE_CODE_GET_END,
        RESPONSE_CODE_POST_END,
        RESPONSE_CODE_GET_RESULT,
        RESPONSE_CODE_POST_RESULT,
        RESPONSE_CODE_GET_ERROR,
        RESPONSE_CODE_ERROR,
        RESPONSE_CODE_POST_ERROR
    };
    String message;
    ResponseCode responseCode;

    public Response(ResponseCode responseCode, String message, byte[] dataBuffer) {
        this.dataBuffer = dataBuffer;
        this.message = message;
        this.responseCode = responseCode;
    }

    public byte[] getDataBuffer() {
        return dataBuffer;
    }

    public void setDataBuffer(byte[] dataBuffer) {
        this.dataBuffer = dataBuffer;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ResponseCode getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(ResponseCode responseCode) {
        this.responseCode = responseCode;
    }
}
