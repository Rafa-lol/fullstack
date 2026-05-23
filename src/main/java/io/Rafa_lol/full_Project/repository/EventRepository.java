package io.Rafa_lol.full_Project.repository;

import io.Rafa_lol.full_Project.domain.UserEvent;
import io.Rafa_lol.full_Project.enumeration.EventType;

import java.util.Collection;

public interface EventRepository {

    Collection<UserEvent> getEventsByUserId(Long userId);
    void addUserEvent(String email, EventType eventType, String device, String ipAddress);
    void addUserEvent(Long userId, EventType eventType, String device, String ipAddress);


}
