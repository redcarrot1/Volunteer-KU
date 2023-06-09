package kr.ac.konkuk.demo.global.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import kr.ac.konkuk.demo.global.email.exception.EmailSendFailException;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendMessage(String target, String title, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();

            message.addRecipients(MimeMessage.RecipientType.TO, target);
            message.setSubject(title);
            message.setText(content, "UTF-8", "HTML");
            message.setFrom(new InternetAddress("volunteerku0@gmail.com", "봉사하자KU"));

            mailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new EmailSendFailException();
        }
    }

}
