package com.example.reviewadm;

public class Review {
    public String name, film, review, date, rating, uid, status;

    public Review(String name, String film, String review, String date, String rating, String uid, String status) {
        this.name = name;
        this.film = film;
        this.review = review;
        this.date = date;
        this.rating = rating;
        this.uid = uid;
        this.status = status;
    }

    public Review() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFilm() {
        return film;
    }

    public void setFilm(String film) {
        this.film = film;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}
