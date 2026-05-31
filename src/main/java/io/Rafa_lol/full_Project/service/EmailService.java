package io.Rafa_lol.full_Project.service;

import io.Rafa_lol.full_Project.enumeration.VerificationType;

public interface EmailService {


    void sendVerificationEmail(String firstName, String email, String verificationUrl, VerificationType verificationType);





}
