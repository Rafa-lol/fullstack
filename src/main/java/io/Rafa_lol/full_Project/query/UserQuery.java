package io.Rafa_lol.full_Project.query;

public class UserQuery {


    public static final String INSERT_USER_QUERY =
            "INSERT INTO Users (first_name, last_name, email, password) "
                    + " VALUES (:firstname, :lastname, :email, :password)";


    public static final String COUNT_USER_EMAIL_QUERY =
            "SELECT COUNT(*) FROM Users WHERE email = :email";

    public static final String INSERT_ACCOUNT_VERIFICATION_URL_QUERY =
            "INSERT INTO AccountVerifications (user_id, url) "
                    + " VALUES (:userId, :url)";

    public static final String SELECT_USER_BY_EMAIL_QUERY =
            "SELECT * FROM users WHERE email = :email";
}
