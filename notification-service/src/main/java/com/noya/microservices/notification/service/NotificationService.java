package com.noya.microservices.notification.service;

import com.noya.microservices.order.event.OrderPlacedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final JavaMailSender javaMailSender;

    @KafkaListener(topics = "order-placed")
    public void listen(OrderPlacedEvent orderPlacedEvent){
        log.info("Got message from order-placed topic {}", orderPlacedEvent);
        MimeMessagePreparator mimeMessagePreparator = mimeMessage -> {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
            mimeMessageHelper.setFrom("springshop@email.com");
            mimeMessageHelper.setTo(orderPlacedEvent.getEmail().toString());
            mimeMessageHelper.setSubject(String.format("Your order with order number %s is placed successfully", orderPlacedEvent.getOrderNumber()));
            mimeMessageHelper.setText(String.format("""
                    Hi %s,%s
                    
                    Your order with order number %s is now placed successfully.
                    
                    Best Regards
                    Spring shop
                    """,
                    orderPlacedEvent.getFirsName().toString(),
                    orderPlacedEvent.getLastName().toString()
                    , orderPlacedEvent.getOrderNumber()

            ));
        };
        try {
            javaMailSender.send(mimeMessagePreparator);
            log.info("Order notification email sent!!");
        }
        catch (MailException e){
            log.error("Exception occured when sending mail", e);
            throw new RuntimeException("Exception occured when sending mail to springshop@email.com", e);
        }

    }
}
