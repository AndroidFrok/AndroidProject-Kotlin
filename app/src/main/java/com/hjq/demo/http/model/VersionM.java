package com.hjq.demo.http.model;

import com.google.gson.annotations.SerializedName;

public class VersionM {

    /**
     * code
     */
    @SerializedName("code")
    private Integer code;
    /**
     * msg
     */
    @SerializedName("msg")
    private String msg;
    /**
     * time
     */
    @SerializedName("time")
    private String time;
    /**
     * data
     */
    @SerializedName("data")
    private DataDTO data;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public DataDTO getData() {
        return data;
    }

    public void setData(DataDTO data) {
        this.data = data;
    }

    public static class DataDTO {
        /**
         * id
         */
        @SerializedName("id")
        private Integer id;
        /**
         * version
         */
        @SerializedName("version")
        private Integer version;
        /**
         * file
         */
        @SerializedName("file")
        private String file;
        /**
         * content
         */
        @SerializedName("content")
        private String content;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getVersion() {
            return version;
        }

        public void setVersion(Integer version) {
            this.version = version;
        }

        public String getFile() {
            return file;
        }

        public void setFile(String file) {
            this.file = file;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
