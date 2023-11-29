package com.inflearn.practical_testing.spring.api.service.mail;

import com.inflearn.practical_testing.spring.client.mail.MailSendClient;
import com.inflearn.practical_testing.spring.domain.history.mail.MailSendHistory;
import com.inflearn.practical_testing.spring.domain.history.mail.MailSendHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Mail을 보내는 것은 따로 있고,
 * 이 서비스는 Mail을 한번 보냈을 때 히스토리를 남기는 서비스
 */
@RequiredArgsConstructor
@Service
public class MailService {

    private final MailSendClient mailSendClient;
    private final MailSendHistoryRepository mailSendHistoryRepository;

    public boolean sendMail(String fromEmail, String toEmail, String subject, String content) {
        boolean result = mailSendClient.sendEmail(fromEmail, toEmail, subject, content);
        if (result) {
            mailSendHistoryRepository.save(MailSendHistory.builder()
                    .fromEmail(fromEmail)
                    .toEmail(toEmail)
                    .subject(subject)
                    .content(content)
                    .build()
            );

            mailSendClient.a();
            mailSendClient.b();
            mailSendClient.c();
            
            return true;
        }

        return false;
    }

}