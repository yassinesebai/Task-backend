package com.task.backend.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class UserRoleDTO {
    private String name;
    private List<String> permissionIds;
}
