package com.inflearn.practical_testing.spring.api.service.mail;

import com.inflearn.practical_testing.spring.client.mail.MailSendClient;
import com.inflearn.practical_testing.spring.domain.history.mail.MailSendHistory;
import com.inflearn.practical_testing.spring.domain.history.mail.MailSendHistoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


/**
 * OrderStatisticsServiceTest 의 MockBean의 경우, Bean이기 때문에 스프링 컨텍스트가 떠야 효과가 있다.
 * 하지만 항상 스프링을 띄울 때만 Mockito를 사용하지는 않을 수도 있다.
 * 예를 들어, 통합 테스트가 아닌 단위 테스트해서 Mocking을 할 수도 있음
 * <p>
 * 따라서, 여기서는 순수한 Mockito Test 진행
 */
@ExtendWith(MockitoExtension.class) // 테스트가 시작될 때 Mockito를 사용해서 Mock 객체를 만들 것임을 알려준다.
class MailServiceTest {

    @Mock
//    @Spy
    private MailSendClient mailSendClient;

    @Mock
    private MailSendHistoryRepository mailSendHistoryRepository;

    @InjectMocks    // Mock객체로 선언된 것들을 Inject한다. DI와 동일한 기능을 수행한다.
    private MailService mailService;

    @DisplayName("메일 전송 테스트: v1")
    @Test
    void sendMail_v1() {
        // given
        MailSendClient mailSendClientV1 = Mockito.mock(MailSendClient.class);
        MailSendHistoryRepository mailSendHistoryRepositoryV1 = Mockito.mock(MailSendHistoryRepository.class);

        MailService mailService1 = new MailService(mailSendClientV1, mailSendHistoryRepositoryV1);

        when(mailSendClientV1.sendEmail(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(true);

        // when
        boolean result = mailService1.sendMail("", "", "", "");

        // then
        assertThat(result).isTrue();
        verify(mailSendHistoryRepositoryV1, times(1)).save(any(MailSendHistory.class));
    }

    @DisplayName("메일 전송 테스트: v1에서 리팩토링된 버전")
    @Test
    void sendMail() {
        // given
        // mock
//        Mockito.when(mailSendClient.sendEmail(anyString(), anyString(), anyString(), anyString()))
//                .thenReturn(true);

        // 위의 Mockito.when(...) 과 동작은 동일하나
        // BDD 스타일(given ~ when ~ then)로 명칭만 변경된 것임
        BDDMockito.given(mailSendClient.sendEmail(anyString(), anyString(), anyString(), anyString()))
                .willReturn(true);

        // spy
        // 한 객체에서 일부는 실제 로직(a(), b(), c())를 사용하고 나머지 일부만 stubbing을 하고 싶을 때 사용한다.
        // 실제로 사용 빈도는 많지 않은 것 같다.
        // 왜냐하면 하나의 객체의 기능이 엄청 많은데 이 중 일부만 쓰고 일부만 Mocking할 일이 그렇게 많지는 않아서
        // 보통 이제 @Mock을 주로 사용한다.
        /*doReturn(true)
                .when(mailSendClient)
                .sendEmail(anyString(), anyString(), anyString(), anyString());*/

        // when
        boolean result = mailService.sendMail("", "", "", "");

        // then
        assertThat(result).isTrue();
        verify(mailSendHistoryRepository, times(1)).save(any(MailSendHistory.class));
    }

}