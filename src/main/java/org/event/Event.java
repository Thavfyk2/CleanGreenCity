package org.event;

import org.users.Organizer;

import java.util.Date;
import java.util.List;

public interface Event {

    void addOrganizer(Organizer organisateur);
    void removeOrganizer(Organizer organisateur);
    List<Organizer> getOrganizer();
    void setPlace(String place);
    String getPlace();
    void setDescription(String description);
    String getDescription();
    void setBeginDate(Date date);
    Date getBeginDate();
    Date setEndDate(Date date);
    Date getEndDate();
}
