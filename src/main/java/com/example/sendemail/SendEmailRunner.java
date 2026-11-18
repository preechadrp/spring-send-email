package com.example.sendemail;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * Usage:
 *   java -jar spring-send-email.jar --to=a@b.com --subject="Hello" --body="Hi there"
 * Optional: --from=me@example.com (defaults to app.mail.from / spring.mail.username)
 */
@Component
public class SendEmailRunner implements ApplicationRunner, ExitCodeGenerator {

    private static final Logger log = LoggerFactory.getLogger(SendEmailRunner.class);

    private final JavaMailSender mailSender;
    private final String defaultFrom;
    private int exitCode = 0;

    public SendEmailRunner(JavaMailSender mailSender,
                           @Value("${app.mail.from:${spring.mail.username:}}") String defaultFrom) {
        this.mailSender = mailSender;
        this.defaultFrom = defaultFrom;
    }

    @Override
    public void run(ApplicationArguments args) {
        String to = option(args, "to");
        String subject = option(args, "subject");
        String body = option(args, "body");
        String from = option(args, "from");
        if (from == null || from.isBlank()) {
            from = defaultFrom;
        }

        if (to == null || subject == null || body == null) {
            System.err.println("""
                    Usage: --to=<email[,email2]> --subject=<text> --body=<text> [--from=<email>]
                    SMTP settings via env: MAIL_HOST, MAIL_PORT, MAIL_USERNAME, MAIL_PASSWORD""");
            exitCode = 2;
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            if (!from.isBlank()) {
                message.setFrom(from);
            }
            message.setTo(to.split("\\s*,\\s*"));
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("Email sent to {}", to);
        } catch (Exception e) {
            log.error("Failed to send email: {}", e.getMessage());
            exitCode = 1;
        }
    }

    private static String option(ApplicationArguments args, String name) {
        List<String> values = args.getOptionValues(name);
        return (values == null || values.isEmpty()) ? null : values.getFirst();
    }

    @Override
    public int getExitCode() {
        return exitCode;
    }
}
