package com.example.reviewadm;

import android.net.Uri;

public class Film {

    public String title, genre, rilis, sinopsis, alamat;

    public Film(String title, String genre, String rilis, String sinopsis, String alamat) {
        this.title = title;
        this.genre = genre;
        this.rilis = rilis;
        this.sinopsis = sinopsis;
        this.alamat = alamat;
    }

    public Film() {
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getRilis() {
        return rilis;
    }

    public void setRilis(String rilis) {
        this.rilis = rilis;
    }

    public String getSinopsis() {
        return sinopsis;
    }

    public void setSinopsis(String sinopsis) {
        this.sinopsis = sinopsis;
    }

}
