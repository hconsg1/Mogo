package com.example.h.mogo;

/**
 * Created by H on 2015-08-02.
 */
import com.parse.*;

@ParseClassName("VideoObject")
public class VideoObject extends ParseObject {
    public VideoObject() {
        // A default constructor is required.
    }

    public String getDisplayName() {
        return getString("displayName");
    }
    public void setDisplayName(String displayName) {
        put("displayName", displayName);
    }

    public ParseUser getOwner() {
        return getParseUser("owner");
    }
    public void setOwner(ParseUser user) {
        put("owner", user);
    }

/*    public InstrumentType getType() {
        return InstrumentType.parse(getString("type"));
    }
    public void setType(InstrumentType type) {
        put("type", type.toString());
    }*/

    public void play() {
        // Ah, that takes me back!
    }
}