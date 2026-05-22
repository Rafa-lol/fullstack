package io.Rafa_lol.full_Project.service;

import io.Rafa_lol.full_Project.domain.Role;

import java.util.Collection;

public interface RoleService {

    Role getRoleByUserId(Long id);
    Collection<Role> getRoles();
}
