package com.xy.order.common;

public enum ResponseCode {

    SUCCESS(0,"SUCCESS"),
    ERROR(1,"ERROR"),
    //...
    ILLEGAL_ARGUMENT(101,"ILLEGAL ARGUMENT"),
    //...
    PRODUCT_NOT_EXISTS(701, "PRODUCT NOT EXISTS"),
    //..
    REPEAT_PURCHASE(801, "REPEAT PURCHASE"),
    //...
    OUT_OF_STOCK(999, "OUT OF STOCK")
    ;

    private int code;
    private String msg;

    ResponseCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static ResponseCode getResponseCode(int code){
        for (ResponseCode c:ResponseCode.values()){
            if (c.getCode()==code)
                return c;
        }
        return null;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
