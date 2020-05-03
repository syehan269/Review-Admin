package com.example.reviewadm;

public class Genre {
    public String genre, url;

    public Genre(String genre, String url) {
        this.genre = genre;
        this.url = url;
    }

    public Genre() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }
}
