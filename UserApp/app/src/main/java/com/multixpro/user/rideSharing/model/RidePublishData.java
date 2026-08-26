package com.multixpro.user.rideSharing.model;

import androidx.annotation.Nullable;

import com.general.files.GeneralFunctions;

import org.json.JSONArray;

public class RidePublishData {

    @Nullable
    private LocationDetails locationDetails;

    @Nullable
    private String dateTime;

    @Nullable
    private String perSeat;

    @Nullable
    private String recommendedPrice;
    @Nullable
    private String passengerNo;

    @Nullable
    private String recommdedPriceText;
    @Nullable
    private String recommdedPriceRange;

    @Nullable
    private JSONArray dynamicDetailsArray;

    @Nullable
    private String startCity;

    @Nullable
    private String endCity;

    @Nullable
    private String documentIds;

    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Nullable
    public LocationDetails getLocationDetails() {
        return locationDetails;
    }

    public void setLocationDetails(@Nullable LocationDetails locationDetails) {
        this.locationDetails = locationDetails;
    }

    @Nullable
    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(@Nullable String dateTime) {
        this.dateTime = dateTime;
    }

    @Nullable
    public String getPerSeat() {
        return perSeat;
    }

    public void setPerSeat(@Nullable String perSeat) {
        this.perSeat = perSeat;
    }

    @Nullable
    public String getRecommendedPrice() {
        return GeneralFunctions.parseIntegerValue(0, recommendedPrice) >= 1 ? recommendedPrice : "";
    }

    public void setRecommendedPrice(@Nullable String recommendedPrice) {
        this.recommendedPrice = recommendedPrice;
    }

    @Nullable
    public String getPassengerNo() {
        return passengerNo;
    }

    public void setPassengerNo(@Nullable String passengerNo) {
        this.passengerNo = passengerNo;
    }

    @Nullable
    public String getRecommdedPriceText() {
        return recommdedPriceText;
    }

    public void setRecommdedPriceText(@Nullable String recommendedMessage) {
        this.recommdedPriceText = recommendedMessage;
    }

    @Nullable
    public String getRecommdedPriceRange() {
        return recommdedPriceRange;
    }

    public void setRecommdedPriceRange(@Nullable String recommendedMessage) {
        this.recommdedPriceRange = recommendedMessage;
    }

    @Nullable
    public JSONArray getDynamicDetailsArray() {
        return dynamicDetailsArray;
    }

    public void setDynamicDetailsArray(@Nullable JSONArray dynamicDetailsArray) {
        this.dynamicDetailsArray = dynamicDetailsArray;
    }

    @Nullable
    public String getStartCity() {
        return startCity;
    }

    public void setStartCity(@Nullable String startCity) {
        this.startCity = startCity;
    }

    @Nullable
    public String getEndCity() {
        return endCity;
    }

    public void setEndCity(@Nullable String endCity) {
        this.endCity = endCity;
    }

    @Nullable
    public String getDocumentIds() {
        return documentIds;
    }

    public void setDocumentIds(@Nullable String documentIds) {
        this.documentIds = documentIds;
    }
    /////////////////////////////////////////////////////////

    public static class LocationDetails {

        @Nullable
        private String fromAddress;

        @Nullable
        private String fromLatitude;

        @Nullable
        private String fromLongitude;

        @Nullable
        private String toAddress;

        @Nullable
        private String toLatitude;

        @Nullable
        private String toLongitude;

        @Nullable
        public String getFromAddress() {
            return fromAddress;
        }

        public void setFromAddress(@Nullable String fromAddress) {
            this.fromAddress = fromAddress;
        }

        @Nullable
        public String getFromLatitude() {
            return fromLatitude;
        }

        public void setFromLatitude(@Nullable String fromLatitude) {
            this.fromLatitude = fromLatitude;
        }

        @Nullable
        public String getFromLongitude() {
            return fromLongitude;
        }

        public void setFromLongitude(@Nullable String fromLongitude) {
            this.fromLongitude = fromLongitude;
        }

        @Nullable
        public String getToAddress() {
            return toAddress;
        }

        public void setToAddress(@Nullable String toAddress) {
            this.toAddress = toAddress;
        }

        @Nullable
        public String getToLatitude() {
            return toLatitude;
        }

        public void setToLatitude(@Nullable String toLatitude) {
            this.toLatitude = toLatitude;
        }

        @Nullable
        public String getToLongitude() {
            return toLongitude;
        }

        public void setToLongitude(@Nullable String toLongitude) {
            this.toLongitude = toLongitude;
        }
    }
}