package com.beancontainer.domain.member.service;

import com.beancontainer.domain.member.dto.MemberDTO;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class MailService {
    @Autowired
    private final JavaMailSender javaMailSender;

    private String authNum; //인증번호

    //발송 메일 주소
    @Value("${spring.mail.username}")
    private String senderEmail;

    //인증코드 메일 전송 화면
    public MimeMessage createMessage(String to) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = javaMailSender.createMimeMessage();

        message.addRecipients(Message.RecipientType.TO, to); //보내는 대상
        message.setSubject("BeanContainer 회원가입 이메일 인증"); //메일 제목

        String msgg = "";
        msgg += "<div style='margin:100px;'>";
        msgg += "<h1> 안녕하세요</h1>";
        msgg += "<h1> 취향에 맞는 카페 탐색 사이트 BeanContainer 입니다</h1>";
        msgg += "<br>";
        msgg += "<p>아래 코드를 회원가입 창으로 돌아가 입력해주세요<p>";
        msgg += "<br>";
        msgg += "<p>아래 코드는 유출을 주의하세요<p>";
        msgg += "<br>";
        msgg += "<div align='center' style='border:1px solid black; font-family:verdana';>";
        msgg += "<h3 style='color:blue;'>회원가입 인증 코드입니다.</h3>";
        msgg += "<div style='font-size:130%'>";
        msgg += "CODE : <strong>";
        msgg += authNum + "</strong><div><br/> "; // 메일에 인증번호 넣기
        msgg += "</div>";
        //규격 맞춰주기
        message.setText(msgg, "utf-8", "html");// 내용, charset 타입, subtype
        // 보내는 사람의 이메일 주소, 보내는 사람 이름
        message.setFrom(new InternetAddress(senderEmail, "BeanContainer", "UTF-8"));

        return message;
    }

    // 랜덤 인증코드
    public String createCode() {
        Random random = new Random();
        StringBuffer key = new StringBuffer();

        for(int i = 0; i< 6; i++){	//인증 코드 6자리
            int index = random.nextInt(3);	//0~2까지 랜덤, 랜덤값으로 switch문 실행

            switch (index) {
                case 0 -> key.append((char) ((int) random.nextInt(26) + 97)); //소문자 알파벳 ASCII 코드 97('a') 부터 26개
                case 1 -> key.append((char) (int) random.nextInt(26) + 65); //대문자 알파벳 ASCII 코드 65('A') 부터 26개
                case 2 -> key.append(random.nextInt(6)); //숫자 0~5 랜덤
            }
        }
        return authNum = key.toString();
    }

    //메일 발송
    //등록해둔 javaMail 객체를 이용해서 이메일 전송함
    public String sendSimpleMessage(String sendEmail) throws Exception {
        authNum = createCode(); //랜덤 인증번호 생성

        MimeMessage message = createMessage(sendEmail); //메일 발송
        try {
            javaMailSender.send(message);
        } catch (MailException e) {
            e.printStackTrace();
            throw new IllegalArgumentException();
        }
        return authNum;
    }

    //저장된 인증 코드 반환
    public String getAuthNum() {
        return this.authNum;
    }
}
