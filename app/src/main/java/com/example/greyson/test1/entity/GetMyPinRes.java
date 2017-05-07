package com.example.greyson.test1.entity;

import java.util.List;

/**
 * Created by greyson on 7/5/17.
 */

public class GetMyPinRes {

    /**
     * status : 200
     * Message : Filtered by id = 358240053158457
     * results : [{"id":27,"deviceid":"3582400531584571494080405","crime":"Assault","crimedesc":"","latitude":"-37.877985","longitude":"145.043444"},{"id":28,"deviceid":"3582400531584571494081103","crime":"Stalking","crimedesc":"delete 4","latitude":"-37.878279","longitude":"145.043908"},{"id":29,"deviceid":"3582400531584571494081143","crime":"Others","crimedesc":"delete 5","latitude":"-37.878249","longitude":"145.044369"}]
     */

    private int status;
    private String Message;
    private List<ResultsBean> results;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String Message) {
        this.Message = Message;
    }

    public List<ResultsBean> getResults() {
        return results;
    }

    public void setResults(List<ResultsBean> results) {
        this.results = results;
    }

    public static class ResultsBean {
        /**
         * id : 27
         * deviceid : 3582400531584571494080405
         * crime : Assault
         * crimedesc :
         * latitude : -37.877985
         * longitude : 145.043444
         */

        private int id;
        private String deviceid;
        private String crime;
        private String crimedesc;
        private String latitude;
        private String longitude;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getDeviceid() {
            return deviceid;
        }

        public void setDeviceid(String deviceid) {
            this.deviceid = deviceid;
        }

        public String getCrime() {
            return crime;
        }

        public void setCrime(String crime) {
            this.crime = crime;
        }

        public String getCrimedesc() {
            return crimedesc;
        }

        public void setCrimedesc(String crimedesc) {
            this.crimedesc = crimedesc;
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }
    }
}
