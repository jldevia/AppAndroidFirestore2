package unpsjb.pm.fi.ars;

import java.util.Date;

public class RatingPhrase {

    private Date date_update;
    private String user;
    private Float value;

    public RatingPhrase() {
    }

    public RatingPhrase(Date date_update, String user, Float value) {
        this.date_update = date_update;
        this.user = user;
        this.value = value;
    }

    public Date getDate_update() {
        return date_update;
    }

    public void setDate_update(Date date_update) {
        this.date_update = date_update;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Float getValue() {
        return value;
    }

    public void setValue(Float value) {
        this.value = value;
    }
}
