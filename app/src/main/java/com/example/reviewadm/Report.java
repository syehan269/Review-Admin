package com.example.reviewadm;

public class Report {
    public String reviewId, reason,date,name,review;

    public Report(String reviewId, String reason, String date, String name, String review) {
        this.reviewId = reviewId;
        this.reason = reason;
        this.date = date;
        this.name = name;
        this.review = review;
    }

    public Report() {
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
