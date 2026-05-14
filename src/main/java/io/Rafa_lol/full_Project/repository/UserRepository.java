package io.Rafa_lol.full_Project.repository;

import io.Rafa_lol.full_Project.domain.User;
import jakarta.validation.constraints.NotEmpty;

import java.util.Collection;

public interface UserRepository<T extends User> {
    /* Operações CRUD */
    T create(T data);
    Collection<T> list(int page, int pageSize);
    T get(Long id);
    T update(T data);
    Boolean delete(Long id);


    /* operações mais complexas */


    User getUserByEmail(String email);

}
