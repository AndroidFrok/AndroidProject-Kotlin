package com.hjq.demo.http.model;

import com.google.gson.annotations.SerializedName;

public class AdResp {

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
         * ad
         */
        @SerializedName("ad")
        private AdDTO ad;
        /**
         * mp3
         */
        @SerializedName("mp3")
        private String mp3;

        public AdDTO getAd() {
            return ad;
        }

        public void setAd(AdDTO ad) {
            this.ad = ad;
        }

        public String getMp3() {
            return mp3;
        }

        public void setMp3(String mp3) {
            this.mp3 = mp3;
        }

        public static class AdDTO {
            /**
             * id
             */
            @SerializedName("id")
            private Integer id;
            /**
             * image
             */
            @SerializedName("image")
            private String image;
            /**
             * startTimeText
             */
            @SerializedName("start_time_text")
            private String startTimeText;
            /**
             * endTimeText
             */
            @SerializedName("end_time_text")
            private String endTimeText;
            /**
             * typeText
             */
            @SerializedName("type_text")
            private String typeText;

            public Integer getId() {
                return id;
            }

            public void setId(Integer id) {
                this.id = id;
            }

            public String getImage() {
                return image;
            }

            public void setImage(String image) {
                this.image = image;
            }

            public String getStartTimeText() {
                return startTimeText;
            }

            public void setStartTimeText(String startTimeText) {
                this.startTimeText = startTimeText;
            }

            public String getEndTimeText() {
                return endTimeText;
            }

            public void setEndTimeText(String endTimeText) {
                this.endTimeText = endTimeText;
            }

            public String getTypeText() {
                return typeText;
            }

            public void setTypeText(String typeText) {
                this.typeText = typeText;
            }
        }
    }
}
