package io.Rafa_lol.full_Project.repository.implementation;

import io.Rafa_lol.full_Project.domain.Role;
import io.Rafa_lol.full_Project.domain.User;
import io.Rafa_lol.full_Project.domain.UserPrincipal;
import io.Rafa_lol.full_Project.dto.UserDTO;
import io.Rafa_lol.full_Project.exception.ApiException;
import io.Rafa_lol.full_Project.repository.RoleRepository;
import io.Rafa_lol.full_Project.repository.UserRepository;
import io.Rafa_lol.full_Project.rowmapper.UserRowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


import java.util.*;

import static io.Rafa_lol.full_Project.enumeration.RoleType.ROLE_USER;
import static io.Rafa_lol.full_Project.enumeration.VerificationType.ACCOUNT;
import static io.Rafa_lol.full_Project.query.UserQuery.*;

import static io.Rafa_lol.full_Project.utils.SmsUtils.sendSMS;
import static java.util.Map.*;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.time.DateFormatUtils.*;
import static org.apache.commons.lang3.time.DateUtils.addDays;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserRepositoryImpl implements UserRepository<User>, UserDetailsService {

    private static final String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";
    private final NamedParameterJdbcTemplate jdbc; /// Spring abre e feche as conecções com a base de dados automaticamente
    private final RoleRepository<Role> roleRepository;
    private final BCryptPasswordEncoder encoder;

    @Override
    public User create(User user) {
        // check the email is unique
            //procura se já existe um email igual no user
        if(getEmailCount(user.getEmail().trim().toLowerCase()) > 0) throw new ApiException("Email already in use. Please use a different email and try again.");
        // save new user
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder(); //id do user gerado para a Base de dados
            SqlParameterSource parameters =  getSqlParameterSource(user);
            //salvar user na base de dados
            jdbc.update(INSERT_USER_QUERY, parameters, keyHolder);
            user.setId(requireNonNull(keyHolder.getKey()).longValue());

            // add role to the user
            roleRepository.addRoleToUser(user.getId(), ROLE_USER.name());

            // send verification URL
            // é gerado um link de verificação (token unico) para o utilizador e guardado na base de dados
            String verificationUrl = getVerificationUrl(UUID.randomUUID().toString(), ACCOUNT.getType());

            // save URL in verification table
            jdbc.update(INSERT_ACCOUNT_VERIFICATION_URL_QUERY, of("userId", user.getId(), "url", verificationUrl));

            // send email to user with verification URL
            //emailService.sendVerificationUrl(user.getFirstName(), user.getEmail(), verificationUrl, ACCOUNT);
            user.setEnabled(true);     //estado inicial da conta, desativada(precisa de ser ativada) e não bloqueada
            user.setNotLocked(true);
            // return the newly created user
            return user;
            // if any errors, throw exception with proper message

        }catch (Exception e){
            //e.printStackTrace();
            //throw new RuntimeException(e);
            log.error(e.getMessage());
            throw new ApiException("An error occured. Please try again.");
        }
    }


    @Override
    public Collection list(int page, int pageSize) {
        return List.of();
    }

    @Override
    public User get(Long id) {
        return null;
    }

    @Override
    public User update(User data) {
        return null;
    }

    @Override
    public Boolean delete(Long id) {
        return null;
    }



    private Integer getEmailCount(String email) {
        // procura na base de dados quantos emails temos com o email dado
        return jdbc.queryForObject(COUNT_USER_EMAIL_QUERY, of("email", email), Integer.class);
    }

    private SqlParameterSource getSqlParameterSource(User user) {
        return new MapSqlParameterSource()
                .addValue("firstname", user.getFirstName())
                .addValue("lastname", user.getLastName())
                .addValue("email", user.getEmail())
                /// não revelar password e vir encriptada
                .addValue("password", encoder.encode(user.getPassword()));
    }

    private String getVerificationUrl(String key, String type) {
        /// Url criada pelo server
         return ServletUriComponentsBuilder.fromCurrentRequest().path("/user/verify/" + type + "/" + key).toUriString();

    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = getUserByEmail(email);
        if(user == null){
            log.error("User not found in the database");
            throw new UsernameNotFoundException("User not found in the database");
        }else{
            log.info("User found in the database: {}", email);
            return new UserPrincipal(user, roleRepository.getRoleByUserId(user.getId()).getPermission());
        }
    }

    @Override
    public User getUserByEmail(String email) {
        try {
            User user  = jdbc.queryForObject(SELECT_USER_BY_EMAIL_QUERY, of("email", email), new UserRowMapper());
            return user;

        }catch (EmptyResultDataAccessException exception){
            throw new ApiException("No User found by email: "+ email);
        }catch (Exception e){
            log.error(e.getMessage());
            throw new ApiException("An error occured. Please try again.");
        }
    }

    @Override
    public void sendVerificationCode(UserDTO user) {
        String expirationDate = format(addDays(new Date(), 1), DATE_FORMAT);
        String verificationCode = randomAlphabetic(8).toUpperCase();

        try {
            jdbc.update(DELETE_VERIFICATION_CODE_BY_USER_ID, of("id", user.getId()));
            jdbc.update(INSERT_VERIFICATION_CODE_QUERY, of("userId", user.getId(), "code", verificationCode, "expirationDate", expirationDate));
            //sendSMS(user.getPhone(), "From: Rafa \nVerification code \n" + verificationCode);
        }catch (Exception e){
            log.error(e.getMessage());
            throw new ApiException("An error occured. Please try again.");
        }

    }


}
