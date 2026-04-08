package com.clickhouse.alnscodingexercise.domains.iamplatform.authn.models.dtos;

import jakarta.servlet.http.HttpSessionBindingEvent;
import jakarta.servlet.http.HttpSessionBindingListener;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

// @Component
@Getter
@Setter
public class LoggedUserDTO implements HttpSessionBindingListener, Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private ActiveUserStoreDTO activeUserStoreDTO;

    public LoggedUserDTO(String username, ActiveUserStoreDTO activeUserStore) {
        this.username = username;
        this.activeUserStoreDTO = activeUserStore;
    }

    @Override
    public void valueBound(HttpSessionBindingEvent event) {
        List<String> users = activeUserStoreDTO.getUsers();
        LoggedUserDTO user = (LoggedUserDTO) event.getValue();
        if (!users.contains(user.getUsername())) {
            users.add(user.getUsername());
        }
    }

    @Override
    public void valueUnbound(HttpSessionBindingEvent event) {
        List<String> users = activeUserStoreDTO.getUsers();
        LoggedUserDTO user = (LoggedUserDTO) event.getValue();
        users.remove(user.getUsername());
    }

}
