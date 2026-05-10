package io.Rafa_lol.full_Project.query;

public class RoleQuery {

    public static final String INSERT_ROLE_TO_USER_QUERY =
            "INSERT INTO Roles (role_id, username) "
                    + " VALUES (:roleId, :username)";


    public static final String SELECT_ROLE_BY_NAME_QUERY =
            "SELECT * FROM Roles WHERE name = :name";

}
