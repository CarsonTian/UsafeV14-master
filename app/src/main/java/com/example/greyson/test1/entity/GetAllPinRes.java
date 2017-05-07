package com.example.greyson.test1.entity;

import java.util.List;

/**
 * Created by greyson on 6/5/17.
 */

public class GetAllPinRes {

    /**
     * status : 200
     * Message : Allcrime
     * results : [{"id":19,"deviceid":"123456789101211:10:03","crime":"Stalking","crimedesc":"Creepy old man stalking me for 15mins","latitude":"35.123456","longitude":"-144.123456"},{"id":20,"deviceid":"0000000000000001494067493","crime":"test","crimedesc":"test","latitude":"31.009000","longitude":"-37.890000"},{"id":21,"deviceid":"000000000000000 1494067579","crime":"test","crimedesc":"test","latitude":"31.009000","longitude":"-37.890000"},{"id":22,"deviceid":"3582400531584571494068779","crime":"Assault","crimedesc":"test 2","latitude":"-37.877663","longitude":"145.043337"},{"id":23,"deviceid":"3582400531584571494068865","crime":"Assault","crimedesc":"test8","latitude":"-37.877663","longitude":"145.043337"},{"id":24,"deviceid":"3582400531584571494068957","crime":"Assault","crimedesc":"test8","latitude":"-37.877663","longitude":"145.043337"}]
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
         * id : 19
         * deviceid : 123456789101211:10:03
         * crime : Stalking
         * crimedesc : Creepy old man stalking me for 15mins
         * latitude : 35.123456
         * longitude : -144.123456
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
