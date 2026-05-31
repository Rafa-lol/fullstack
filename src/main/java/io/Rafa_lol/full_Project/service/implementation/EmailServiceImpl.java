package io.Rafa_lol.full_Project.service.implementation;

import io.Rafa_lol.full_Project.enumeration.VerificationType;
import io.Rafa_lol.full_Project.exception.ApiException;
import io.Rafa_lol.full_Project.service.EmailService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.Nullable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendVerificationEmail(String firstName, String email, String verificationUrl, VerificationType verificationType) {
        try{
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("");
            message.setTo(email);
            message.setText(getEmailMessage(firstName, verificationUrl, verificationType));
            message.setSubject(String.format("Full_stack - %s Verification Email", StringUtils.capitalize(verificationType.getType())));
            mailSender.send(message);
            log.info("Email Sent to {}", firstName);

        }catch (Exception exception){
            log.error(exception.getMessage());
        }
    }

    private String getEmailMessage(String firstName, String verificationUrl, VerificationType verificationType) {

        switch (verificationType) {
            case PASSWORD -> { return "Hello " + firstName + "\n\nReset password. Please click the link below to reset your password.\n\n" + verificationUrl + "\n\nThe suport Team"; }

            case ACCOUNT -> { return "Hello " + firstName + "\n\nYour new account has been created. Please click the link below to verify your account.\n\n" + verificationUrl + "\n\nThe suport Team"; }

            default -> throw new ApiException("Unable to send email. Email type unknown");

        }

    }
}
