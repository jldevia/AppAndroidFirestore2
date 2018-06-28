package unpsjb.pm.fi.ars;

import java.util.ArrayList;
import java.util.List;

public class Phrase {

    private String phrase;
    private String author;
    private String poster;
    private float rating;
    private List<RatingPhrase> ratings;

    public Phrase() {
        ratings = new ArrayList<>();
    }

    public Phrase(String phrase, String author, String poster, float rating, List<RatingPhrase> ratings) {
        this.phrase = phrase;
        this.author = author;
        this.poster = poster;
        this.rating = rating;
        this.ratings = ratings == null? new ArrayList<RatingPhrase>(): ratings;
    }

    public String getPhrase() {
        return phrase;
    }

    public void setPhrase(String phrase) {
        this.phrase = phrase;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public List<RatingPhrase> getRatings() {
        return ratings;
    }

    public void setRatings(List<RatingPhrase> ratings) {
        this.ratings = ratings;
    }
}
