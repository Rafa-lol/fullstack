package io.Rafa_lol.full_Project.listener;

import io.Rafa_lol.full_Project.event.NewUserEvent;
import io.Rafa_lol.full_Project.service.EventService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import static io.Rafa_lol.full_Project.utils.RequestUtils.getDevice;
import static io.Rafa_lol.full_Project.utils.RequestUtils.getIpAddress;


@Component
@RequiredArgsConstructor
@Slf4j
public class NewUserEventListener {

    private final EventService eventService;

    private final HttpServletRequest request;

    @EventListener
    public void onNewUserEvent(NewUserEvent event) {
        log.info("NewUserEvent is fired");
        eventService.addUserEvent(event.getEmail(), event.getType(), getDevice(request), getIpAddress(request));
    }

}
