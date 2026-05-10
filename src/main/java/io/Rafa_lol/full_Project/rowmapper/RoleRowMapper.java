package io.Rafa_lol.full_Project.rowmapper;

import io.Rafa_lol.full_Project.domain.Role;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RoleRowMapper implements RowMapper<Role> {

    @Override
    public Role mapRow(ResultSet resultSet, int rowNum) throws SQLException {
            /// mesmo de fazer os setters e getters mas com o builder()
        return Role.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .permission(resultSet.getString("permission"))
                .build();
    }
}
