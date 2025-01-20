package com.task.backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UserRoleDTO {
    private String id;
    private String name;
    private List<String> permissions;

    public UserRoleDTO(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
