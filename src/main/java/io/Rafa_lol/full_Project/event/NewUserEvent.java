package io.Rafa_lol.full_Project.event;

import io.Rafa_lol.full_Project.domain.UserEvent;
import io.Rafa_lol.full_Project.enumeration.EventType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.time.Clock;

@Getter
@Setter
public class NewUserEvent extends ApplicationEvent {

    private EventType type;
    private String email;



    public NewUserEvent(String email, EventType type) {
        super(email);
        this.type = type;
        this.email = email;
    }
}
