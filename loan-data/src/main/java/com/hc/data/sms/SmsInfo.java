package com.hc.data.sms;

import java.util.List;

public class SmsInfo {
    /**
     * appId : in.testCash
     * appName : testCash
     * phoneNo : 999999999
     * smsInfo : [{"address":"this is test addr","body":"this is test body","date":"1256539465022","id":"0","person":"testMan","protocol":"0SMS_RPOTO","read":"1","serviceCenter":"+8613800755500","status":"64pending","threadId":"0","type":"1"},{"address":"this is test addr","body":"this is test body","date":"1256539465022","id":"1","person":"testMan","protocol":"0SMS_RPOTO","read":"1","serviceCenter":"+8613800755500","status":"64pending","threadId":"1","type":"1"},{"address":"this is test addr","body":"this is test body","date":"1256539465022","id":"2","person":"testMan","protocol":"0SMS_RPOTO","read":"1","serviceCenter":"+8613800755500","status":"64pending","threadId":"2","type":"1"},{"address":"this is test addr","body":"this is test body","date":"1256539465022","id":"3","person":"testMan","protocol":"0SMS_RPOTO","read":"1","serviceCenter":"+8613800755500","status":"64pending","threadId":"3","type":"1"},{"address":"this is test addr","body":"this is test body","date":"1256539465022","id":"4","person":"testMan","protocol":"0SMS_RPOTO","read":"1","serviceCenter":"+8613800755500","status":"64pending","threadId":"4","type":"1"},{"address":"this is test addr","body":"this is test body","date":"1256539465022","id":"5","person":"testMan","protocol":"0SMS_RPOTO","read":"1","serviceCenter":"+8613800755500","status":"64pending","threadId":"5","type":"1"},{"address":"this is test addr","body":"this is test body","date":"1256539465022","id":"6","person":"testMan","protocol":"0SMS_RPOTO","read":"1","serviceCenter":"+8613800755500","status":"64pending","threadId":"6","type":"1"},{"address":"this is test addr","body":"this is test body","date":"1256539465022","id":"7","person":"testMan","protocol":"0SMS_RPOTO","read":"1","serviceCenter":"+8613800755500","status":"64pending","threadId":"7","type":"1"},{"address":"this is test addr","body":"this is test body","date":"1256539465022","id":"8","person":"testMan","protocol":"0SMS_RPOTO","read":"1","serviceCenter":"+8613800755500","status":"64pending","threadId":"8","type":"1"},{"address":"this is test addr","body":"this is test body","date":"1256539465022","id":"9","person":"testMan","protocol":"0SMS_RPOTO","read":"1","serviceCenter":"+8613800755500","status":"64pending","threadId":"9","type":"1"}]
     * userId : 123456
     */

    private String appId;
    private String appName;
    private String phoneNo;
    private String userId;
    private List<SmsInfoBean> smsInfo;
    private Long timeStamp;


    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<SmsInfoBean> getSmsInfo() {
        return smsInfo;
    }

    public void setSmsInfo(List<SmsInfoBean> smsInfo) {
        this.smsInfo = smsInfo;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }


    public static class SmsInfoBean {
        /**
         * address : this is test addr
         * body : this is test body
         * date : 1256539465022
         * id : 0
         * person : testMan
         * protocol : 0SMS_RPOTO
         * read : 1
         * serviceCenter : +8613800755500
         * status : 64pending
         * threadId : 0
         * type : 1
         */

        private String address = "";
        private String body = "";
        private String date = "";
        private String id = "";
        private String person = "";
        private String protocol = "";
        private String read = "";
        private String serviceCenter = "";
        private String status = "";
        private String threadId = "";
        private String type = "";

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setProtocol(String protocol) {
            this.protocol = protocol;
        }

        public String getRead() {
            return read;
        }

        public void setRead(String read) {
            this.read = read;
        }

        public String getServiceCenter() {
            return serviceCenter;
        }


        public String getPerson() {
            return person;
        }

        public void setPerson(String person) {
            this.person = person;
        }

        public String getProtocol() {
            return protocol;
        }


        public void setServiceCenter(String serviceCenter) {
            this.serviceCenter = serviceCenter;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getThreadId() {
            return threadId;
        }

        public void setThreadId(String threadId) {
            this.threadId = threadId;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
