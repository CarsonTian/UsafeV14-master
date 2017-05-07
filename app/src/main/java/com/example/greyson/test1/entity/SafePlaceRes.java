package com.example.greyson.test1.entity;

import java.util.List;

/**
 * Created by greyson on 1/4/17.
 */

public class SafePlaceRes {

    /**
     * status : 200
     * message : 2 KM
     * results : [{"id":479,"establishment":"Mcdonald'S","address":"100 Waverley Rd","suburb":"Malvern East","postcode":3145,"state":"VIC","type":"Restaurant","latitude":"-37.876095","longitude":"145.047812"},{"id":371,"establishment":"Station 24","address":"80 Waverley Road","suburb":" East Malvern","postcode":3145,"state":"VIC","type":"Firestation","latitude":"-37.876011","longitude":"145.047143"},{"id":532,"establishment":"7-Eleven","address":"15-17 Sir John Monash Dr","suburb":"Caulfield","postcode":3162,"state":"VIC","type":"Convenience Shop","latitude":"-37.876706","longitude":"145.042316"},{"id":517,"establishment":"Coles Express","address":"779/763 Dandenong Rd","suburb":"Malvern","postcode":3144,"state":"VIC","type":"Petrol Station","latitude":"-37.873054","longitude":"145.037864"}]
     */

    private int status;
    private String message;
    private List<ResultsBean> results;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ResultsBean> getResults() {
        return results;
    }

    public void setResults(List<ResultsBean> results) {
        this.results = results;
    }

    public static class ResultsBean {
        /**
         * id : 479
         * establishment : Mcdonald'S
         * address : 100 Waverley Rd
         * suburb : Malvern East
         * postcode : 3145
         * state : VIC
         * type : Restaurant
         * latitude : -37.876095
         * longitude : 145.047812
         */

        private int id;
        private String establishment;
        private String address;
        private String suburb;
        private int postcode;
        private String state;
        private String type;
        private String latitude;
        private String longitude;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getEstablishment() {
            return establishment;
        }

        public void setEstablishment(String establishment) {
            this.establishment = establishment;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getSuburb() {
            return suburb;
        }

        public void setSuburb(String suburb) {
            this.suburb = suburb;
        }

        public int getPostcode() {
            return postcode;
        }

        public void setPostcode(int postcode) {
            this.postcode = postcode;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
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
