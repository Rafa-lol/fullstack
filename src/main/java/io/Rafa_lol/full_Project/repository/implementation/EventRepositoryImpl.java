package io.Rafa_lol.full_Project.repository.implementation;


import io.Rafa_lol.full_Project.domain.UserEvent;
import io.Rafa_lol.full_Project.enumeration.EventType;
import io.Rafa_lol.full_Project.repository.EventRepository;
import io.Rafa_lol.full_Project.rowmapper.UserEventRowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static io.Rafa_lol.full_Project.query.EventQuery.INSERT_EVENT_BY_USER_EMAIL_QUERY;
import static io.Rafa_lol.full_Project.query.EventQuery.SELECT_EVENT_BY_USER_ID_QUERY;
import static java.util.Map.*;


@Repository
@RequiredArgsConstructor
@Slf4j
public class EventRepositoryImpl implements EventRepository {

    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public Collection<UserEvent> getEventsByUserId(Long userId) {
        return jdbc.query(SELECT_EVENT_BY_USER_ID_QUERY, of("id", userId), new UserEventRowMapper());
    }

    @Override
    public void addUserEvent(String email, EventType eventType, String device, String ipAddress) {
        System.out.println("EVENT TYPE: " + eventType.toString());
        jdbc.update(INSERT_EVENT_BY_USER_EMAIL_QUERY, of("email", email, "type", eventType.toString(), "device", device, "ipAddress", ipAddress));
    }

    @Override
    public void addUserEvent(Long userId, EventType eventType, String device, String ipAddress) {

    }
}
