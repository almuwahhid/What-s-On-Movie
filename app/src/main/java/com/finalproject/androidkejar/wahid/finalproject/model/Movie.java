package com.finalproject.androidkejar.wahid.finalproject.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by gueone on 5/4/2016.
 */
public class Movie {
    private String id;
    private String title, releaseDate, PosterUrl, synopsis, bgPath;
    private String vote_average;
    private Date date;
    private String formattedDate;


    public Movie(){

    }

    public String getVote_average() {
        return vote_average;
    }

    public void setBgPath(String bgPath) {
        this.bgPath = bgPath;
    }

    public String getBgPath() {

        return bgPath;
    }

    public void setVote_average(String vote_average) {
        this.vote_average = vote_average;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        if(releaseDate.length()<=10){
            try {
                //this.releaseDate = releaseDate.substring(0, 4);
                int month= Integer.parseInt(releaseDate.substring(5, 7));
                String monthString;
                switch (month) {
                    case 1:  monthString = "January";       break;
                    case 2:  monthString = "Februari";      break;
                    case 3:  monthString = "Maret";         break;
                    case 4:  monthString = "April";         break;
                    case 5:  monthString = "Mei";           break;
                    case 6:  monthString = "Juni";          break;
                    case 7:  monthString = "Juli";          break;
                    case 8:  monthString = "Agustus";        break;
                    case 9:  monthString = "September";     break;
                    case 10: monthString = "Oktober";       break;
                    case 11: monthString = "November";      break;
                    case 12: monthString = "Desember";      break;
                    default: monthString = "Invalid month"; break;
                }
                this.releaseDate = releaseDate.substring(8, 10)+" "+monthString+" "+releaseDate.substring(0, 4);
            } catch (java.lang.StringIndexOutOfBoundsException e) {
                this.releaseDate = null;
            }
        }else{
            this.releaseDate = releaseDate;
        }
    }

    public String getPosterUrl() {
        return PosterUrl;
    }

    public void setPosterUrl(String PosterUrl) {
        this.PosterUrl = PosterUrl;
    }

    public String getId() {
        return id;
    }
    public Date getDate() {
        return date;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }
    public void setFormattedDate(String stringDate) {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        try {

            Date unformatedDate = formatter.parse(stringDate);

            //We use setDate() method so we don't have to repeat code in filmography adapter for every film
            setDate(unformatedDate);

            formattedDate = formatter.format(unformatedDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    public void setDate(Date dateInString) {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        try {

            Date date = formatter.parse(String.valueOf(dateInString));

            this.date = date;

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}