package io.Rafa_lol.full_Project.repository;

import io.Rafa_lol.full_Project.domain.Role;

import java.util.Collection;

public interface RoleRepository<T extends Role> {

    /* Operações CRUD */
    T create(T data);
    Collection<T> list();
    T get(Long id);
    T update(T data);
    Boolean delete(Long id);


    /* operações mais complexas */
    void addRoleToUser(Long userId, String roleName);
    Role getRoleByUserId(Long userId);
    Role getRoleByEmail(String email);

    void updateUserRole(Long userId, String roleName);
}
