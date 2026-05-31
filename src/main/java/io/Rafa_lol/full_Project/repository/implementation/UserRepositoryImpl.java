package io.Rafa_lol.full_Project.repository.implementation;

import io.Rafa_lol.full_Project.domain.Role;
import io.Rafa_lol.full_Project.domain.User;
import io.Rafa_lol.full_Project.domain.UserPrincipal;
import io.Rafa_lol.full_Project.dto.UserDTO;
import io.Rafa_lol.full_Project.enumeration.VerificationType;
import io.Rafa_lol.full_Project.exception.ApiException;
import io.Rafa_lol.full_Project.form.UpdateForm;
import io.Rafa_lol.full_Project.repository.RoleRepository;
import io.Rafa_lol.full_Project.repository.UserRepository;
import io.Rafa_lol.full_Project.rowmapper.UserRowMapper;
import io.Rafa_lol.full_Project.service.EmailService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static io.Rafa_lol.full_Project.enumeration.RoleType.ROLE_USER;
import static io.Rafa_lol.full_Project.enumeration.VerificationType.ACCOUNT;
import static io.Rafa_lol.full_Project.enumeration.VerificationType.PASSWORD;
import static io.Rafa_lol.full_Project.query.UserQuery.*;

import static io.Rafa_lol.full_Project.utils.SmsUtils.sendSMS;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.util.Map.*;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.time.DateFormatUtils.*;
import static org.apache.commons.lang3.time.DateUtils.addDays;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.*;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserRepositoryImpl implements UserRepository<User>, UserDetailsService {

    private static final String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";
    private final NamedParameterJdbcTemplate jdbc; /// Spring abre e feche as conecções com a base de dados automaticamente
    private final RoleRepository<Role> roleRepository;
    private final BCryptPasswordEncoder encoder;
    private final EmailService emailService;

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
            sendEmail(user.getFirstName(), user.getEmail(), verificationUrl, ACCOUNT);
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

    private void sendEmail(String firstName, String email, String verificationUrl, VerificationType verificationType) {
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            try{
                emailService.sendVerificationEmail(firstName, email, verificationUrl, verificationType);

            }catch (Exception exception){
                throw new ApiException("Unable to send email");
            }
        });
    }
    /*
    CompletableFuture<Void> future = CompletableFuture.runAsync(new Runnable(){
        @Override
        public void run() {
            try{
                emailService.sendVerificationEmail(firstName, email, verificationUrl, verificationType);

            }catch (Exception exception){
                throw new ApiException("Unable to send email");
            }
        }
    });*/




    @Override
    public Collection list(int page, int pageSize) {
        return List.of();
    }

    @Override
    public User get(Long id) {
        try {
            return jdbc.queryForObject(SELECT_USER_BY_ID_QUERY, of("id", id), new UserRowMapper());
        } catch (EmptyResultDataAccessException exception) {
            throw new ApiException("No User found by id: " + id);
            //return null;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ApiException("An error occured. Please try again.");
        }
    }

    @Override
    public User update(User data) {
        return null;
    }

    @Override
    public Boolean delete(Long id) {
        return null;
    }



    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = getUserByEmail(email);
        if(user == null){
            log.error("User not found in the database");
            throw new UsernameNotFoundException("User not found in the database");
        }else{
            log.info("User found in the database: {}", email);
            return new UserPrincipal(user, roleRepository.getRoleByUserId(user.getId()));
        }
    }

    @Override
    public User getUserByEmail(String email) {
        try {
            User user  = jdbc.queryForObject(SELECT_USER_BY_EMAIL_QUERY, of("email", email), new UserRowMapper());
            return user;

        }catch (EmptyResultDataAccessException exception){
            throw new ApiException("No User found by email: " + email);
            //return null;
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
            log.info("Verification code: {}", verificationCode);
        }catch (Exception e){
            log.error(e.getMessage());
            throw new ApiException("An error occured. Please try again.");
        }

    }

    @Override
    public User verifyCode(String email, String code) {
        if(isVerificationCodeExpired(code)) throw new ApiException("This code has expired. Please login again.");
        try {
            User userByCode = jdbc.queryForObject(SELECT_USER_CODE_QUERY, of("code", code), new UserRowMapper());
            User userByEmail = jdbc.queryForObject(SELECT_USER_BY_EMAIL_QUERY, of("email", email), new UserRowMapper());
            if(userByCode.getEmail().equalsIgnoreCase(userByEmail.getEmail())){
                jdbc.update(DELETE_CODE, of("code", code));
                return userByCode;
            }else{
                throw new ApiException("Code is invalid. Please try again.");
            }
        }catch (EmptyResultDataAccessException e){
            throw new ApiException("Could noy find record");
        }catch (Exception e) {
            throw new ApiException("An error occured. Please try again.");
        }
    }

    @Override
    public void resetPassword(String email) {
        log.info("Reset Password");
        if(getEmailCount(email.trim().toLowerCase()) <= 0) throw new ApiException("There is no account for this email address.");
        try {
            String expirationDate = format(addDays(new Date(), 1), DATE_FORMAT);
            User user = getUserByEmail(email);
            String verificationUrl = getVerificationUrl(UUID.randomUUID().toString(), PASSWORD.getType());
            jdbc.update(DELETE_PASSWORD_VERIFICATION_BY_USER_ID_QUERY, of("userId", user.getId()));
            jdbc.update(INSERT_PASSWORD_VERIFICATION_QUERY, of("userId", user.getId(), "url", verificationUrl, "expirationDate", expirationDate));
            // send email with url to user
            sendEmail(user.getFirstName(), email, verificationUrl, PASSWORD);
            log.info("Verification URL: {}", verificationUrl);
        }catch (Exception e) {
            throw new ApiException("An error occured. Please try again.");
        }

    }

    @Override
    public User verifyPasswordKey(String key) {
        if(isLinkExpired(key, PASSWORD)) throw new ApiException("This link has expired. Please reset your password again.");
        try {
            User user = jdbc.queryForObject(SELECT_USER_PASSWORD_URL_QUERY, of("url", getVerificationUrl(key, PASSWORD.getType())), new UserRowMapper());
            ///jdbc.update("DELETE_USER_FROM_PASSWORD_VERIFICATION_QUERY", of("userId", user.getId())); //depends
            return user;
        }catch (EmptyResultDataAccessException e){
            log.error(e.getMessage());
            throw new ApiException("This link is not valid. Please reset your password again.");
        }catch (Exception e) {
            log.error(e.getMessage());
            throw new ApiException("An error occured. Please try again.");
        }
    }

    @Override
    public void renewPassword(String key, String password, String confirmPassword) {
        if(!password.equals(confirmPassword)) throw new ApiException("Passwords dont match. Please try again.");
        try {
            jdbc.update(UPDATE_USER_PASSWORD_BY_URL_QUERY, of("password", encoder.encode(password), "url", getVerificationUrl(key, PASSWORD.getType())));
            jdbc.update(DELETE_VERIFICATION_BY_URL_QUERY, of("url", getVerificationUrl(key, PASSWORD.getType())));


        }catch (Exception e) {
            log.error(e.getMessage());
            throw new ApiException("An error occured. Please try again.");
        }
    }

    @Override
    public void renewPassword(Long userId, String password, String confirmPassword) {
        if(!password.equals(confirmPassword)) throw new ApiException("Passwords dont match. Please try again.");
        try {
            jdbc.update(UPDATE_USER_PASSWORD_BY_USER_ID_QUERY, of("id", userId, "password", encoder.encode(password)));
            //jdbc.update(DELETE_PASSWORD_VERIFICATION_BY_USER_ID_QUERY, of("userId", userId));


        }catch (Exception e) {
            log.error(e.getMessage());
            throw new ApiException("An error occured. Please try again.");
        }
    }

    @Override
    public User verifyAccountKey(String key) {
        try {
            User user = jdbc.queryForObject(SELECT_USER_BY_ACCOUNT_URL_QUERY, of("url", getVerificationUrl(key, ACCOUNT.getType())), new UserRowMapper());
            jdbc.update(UPDATE_USER_ENABLED_QUERY, of("enabled", true, "id", user.getId()));
            return user;
        }catch (EmptyResultDataAccessException e){
            throw new ApiException("This link is not valid.");
        }catch (Exception e) {
            throw new ApiException("An error occured. Please try again.");
        }
    }

    @Override
    public User updateUserDetails(UpdateForm user) {
        try {
            jdbc.update(UPDATE_USER_DETAILS_QUERY, getUserDetailsSqlParameterSource(user));
            return get(user.getId());
        }catch (EmptyResultDataAccessException e){
            throw new ApiException("No User found by email: " + user.getId());
        }catch (Exception e){
            log.error(e.getMessage());
            throw new ApiException("This link is not valid. Please reset your password again.");
        }
    }

    @Override
    public void updatePassword(Long id, String currentPassword, String newPassword, String confirmNewPassword) {
        if(!newPassword.equals(confirmNewPassword)){
            log.info("Passwords dont match. Please try again.");
            throw new ApiException("Passwords dont match. Please try again.");
        }
        User user = get(id);
        if(encoder.matches(currentPassword, user.getPassword())){
            try {
                jdbc.update(UPDATE_USER_PASSWORD_BY_ID_QUERY, of("userId", id, "password", encoder.encode(newPassword)));
                log.info("Passwords update");
            }catch (Exception e){
                log.error(e.getMessage());
                throw new ApiException("This link is not valid. Please reset your password again.");
            }
        }else{
            log.info("Incorrect password");
            throw new ApiException("Incorrect current password. Please try again.");
        }

    }

    @Override
    public void updateAccountSettings(Long userId, Boolean enabled, Boolean notLocked) {
        try {
            jdbc.update(UPDATE_USER_SETTINGS_QUERY, of("userId", userId, "enabled", enabled, "notLocked", notLocked));

        }catch (Exception e) {
            log.error(e.getMessage());
            throw new ApiException("An error occured. Please try again.");
        }
    }

    @Override
    public User toggleMfa(String email) {

        User user = getUserByEmail(email);
        if(isBlank(user.getPhone())){
            throw new ApiException("You need a phone number to change Multi-Factor Authentication");
        }
        user.setUsingMfa(!user.isUsingMfa());
        try {
            jdbc.update(TOGGLE_USER_MFA_QUERY, of("email", email, "isUsingMfa", user.isUsingMfa()));
            return user;
        }catch (Exception e) {
            log.error(e.getMessage());
            throw new ApiException("Unable to update Multi-Factor Authentication");
        }

    }

    @Override
    public void updateImage(UserDTO user, MultipartFile image) {
        String userImageUrl = setUserImageUrl(user.getEmail());
        user.setImageUrl(userImageUrl);
        saveImage(user.getEmail(), image);
        jdbc.update(UPDATE_USER_IMAGE_QUERY, Map.of("imageUrl", userImageUrl, "id", user.getId()));
    }

    private void saveImage(String email, MultipartFile image) {
        Path fileStorageLocation = Paths.get(System.getProperty("user.home") + "/Downloads/images/").toAbsolutePath().normalize();
        if(!Files.exists(fileStorageLocation)){
            try{
                Files.createDirectories(fileStorageLocation);
            }catch (Exception exception){
                log.error(exception.getMessage());
                throw new ApiException("Unable to create directory to save image.");
            }
            log.info("Created directories: {}", fileStorageLocation);
        }
        try{
            Files.copy(image.getInputStream(), fileStorageLocation.resolve(email + ".png"), REPLACE_EXISTING);
        }catch (IOException exception){
            log.error(exception.getMessage());
            throw new ApiException(exception.getMessage());
        }
        log.info("File saved in: {} folder" + fileStorageLocation);
    }

    private String setUserImageUrl(String email) {
        return fromCurrentContextPath()
                .path("/user/image/" + email + ".png")
                .toUriString();
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


    private SqlParameterSource getUserDetailsSqlParameterSource(UpdateForm user) {
        return new MapSqlParameterSource()
                .addValue("id", user.getId())
                .addValue("firstname", user.getFirstName())
                .addValue("lastname", user.getLastName())
                .addValue("email", user.getEmail())
                .addValue("phone", user.getPhone())
                .addValue("address", user.getAddress())
                .addValue("title", user.getTitle())
                .addValue("bio", user.getBio());
    }


    private String getVerificationUrl(String key, String type) {
        /// Url criada pelo server
        return fromCurrentContextPath()
                .path("/user/verify/" + type + "/" + key)
                .toUriString();
    }

    private boolean isLinkExpired(String key, VerificationType password) {
        try {
            return jdbc.queryForObject(SELECT_EXPIRATION_BY_URL, of("url", getVerificationUrl(key, password.getType())), Boolean.class);

        }catch (EmptyResultDataAccessException e){
            log.error(e.getMessage());
            throw new ApiException("This link is not valid. Please reset your password again.");
        }catch (Exception e) {
            log.error(e.getMessage());
            throw new ApiException("An error occured. Please try again.");
        }
    }

    private boolean isVerificationCodeExpired(String code) {
        try {
            return jdbc.queryForObject(SELECT_CODE_EXPIRATION_QUERY, of("code", code), Boolean.class);

        }catch (EmptyResultDataAccessException e){
            throw new ApiException("This code is not valid. Please login again");
        }catch (Exception e) {
            throw new ApiException("An error occured. Please try again.");
        }
    }


}
