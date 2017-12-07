package com.zydcompany.management.common;

import com.zydcompany.management.exception.message.BaseExceptionMsg;

public class PlatformResponse {

    private Integer code;
    private String msg;
    private Object data;

    private PlatformResponse() {
    }

    /**
     * json转换需要get方法
     */
    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public Object getData() {
        return data;
    }


    public static Builder builder() {
        return new Builder();
    }

    private PlatformResponse(Builder builder) {
        this.code = builder.code;
        this.msg = builder.msg;
        this.data = builder.data;
    }

    public static class Builder {
        private Integer code = BaseExceptionMsg.SUCCESS_CODE;
        private String msg = BaseExceptionMsg.SUCCESS_MSG;
        private Object data;

        public Builder code(Integer code) {
            this.code = code;
            return this;
        }

        public Builder msg(String msg) {
            this.msg = msg;
            return this;
        }

        public Builder data(Object data) {
            this.data = data;
            return this;
        }

        public PlatformResponse build() {
            return new PlatformResponse(this);
        }
    }
}