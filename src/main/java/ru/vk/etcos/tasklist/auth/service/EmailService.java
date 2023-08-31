package ru.vk.etcos.tasklist.auth.service;

/*
Класс отправляет различные письма пользователю.

Методы отправки письма желательно делать в параллельном потоке, чтобы не задерживать пользователя.

Самый простой способ:
- @EnableAsync - разрешает асинхронный вызов методов (прописать в конфиг Spring)
- Async - запускает метод в параллельном потоке (указывается в нужного метода)
- Если метод возвращает какой-либо тип, его нужно обернуть в спец. объект AsyncResult

Сервис может вызываться из любых Spring компонентов, в том числе из контроллеров
 */

import java.util.concurrent.*;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import lombok.extern.java.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.mail.javamail.*;
import org.springframework.scheduling.annotation.*;
import org.springframework.stereotype.Service;
import ru.vk.etcos.tasklist.util.*;

@Service
@Log
public class EmailService {

    // клиентский URL, который будет подставляться в письма
    @Value("${client.url}")
    private String clientURL;

    // от кого будут приходить письма
    @Value("${email.from}")
    private String emailFrom;

    // готовый спринговый объект для отправки писем (настройки берутся из application.properties)
    private JavaMailSender sender;

    @Autowired
    public EmailService(JavaMailSender sender) {
        this.sender = sender;
    }

    // отправка письма активации аккаунта
    // прикрепляем uuid к URL как get-параметр
    // клиент при нажатии на ссылку из письма - получит этот uuid
    @Async // метод запуститься в параллельном потоке, поэтому должен возвращать спец. обертку Future
    public Future<Boolean> sendActivateEmail(String email, String username, String uuid) {
        try {
            // создаем HTML документ
            MimeMessage mimeMessage = sender.createMimeMessage();
            // контейнер для отправки письма
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, "utf-8");

            // ссылка на клиент, после перехода на которую произойдет активация аккаунта
            // эту ссылку клиент должен обработать и отправить соответствующий запрос на backend
            String url = clientURL + "/activate-account/" + uuid;

            // текст письма в формате HTML
            String htmlMsg = String.format(
                "Здравствуйте.<br/><br/>" +
                    "Вы создали аккаунт для веб приложения \"Планировщик дел\": %s<br/><br/>." +
                    "<a href='%s'>Для подтверждения регистрации нажмите на эту ссылку</a><br/><br/>", username, url
            );

            mimeMessage.setContent(htmlMsg, "text/html"); // тип письма
            messageHelper.setTo(email); // email получателя
            messageHelper.setFrom(emailFrom); // обратный адрес
            messageHelper.setSubject("Активация аккаунта"); // тема письма
            messageHelper.setText(htmlMsg, true); // указываем что это HTML письмо
            sender.send(mimeMessage); // отправка

            return new AsyncResult<>(true); // true - успешная отправка, оборачиваем результат в спец. объект
        } catch (MessagingException e) {
            CLogger.fatal("Ошибка отправки письма", e);
        }

        return new AsyncResult<>(false); // false - отправка не удалась, оборачиваем результат в спец. объект
    }

    // мы в письме не можем передать token с помощью кука, поэтому прикрепляем его к URL как get-параметр
    // клиент при нажатии на ссылку из письма - получит этот токен (для последующей авторизации запроса на сервер)
    @Async // метод запуститься в параллельном потоке, поэтому должен возвращать спец. обертку Future
    public Future<Boolean> sendResetPassword(String email, String token) {
        try {
            // создаем HTML документ
            MimeMessage mimeMessage = sender.createMimeMessage();
            // контейнер для отправки письма
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, "utf-8");

            // ссылка на клиент, после перехода на которую произойдет активация аккаунта
            // эту ссылку клиент должен обработать и отправить соответствующий запрос на backend
            String url = clientURL + "/update-password/" + token;

            // текст письма в формате HTML
            String htmlMsg = String.format(
                "Здравствуйте.</br></br>" +
                    "Запрос на сброс пароля для веб приложения \"Планировщик дел\"</br></br>" +
                    "<a href='%s'>Для сброса пароля нажмите на эту ссылку</a></br></br>", url
            );

            mimeMessage.setContent(htmlMsg, "text/html"); // тип письма
            messageHelper.setTo(email); // email получателя
            messageHelper.setFrom(emailFrom); // обратный адрес
            messageHelper.setSubject("Сброс пароля"); // тема письма
            messageHelper.setText(htmlMsg, true); // указываем что это HTML письмо
            sender.send(mimeMessage); // отправка

            return new AsyncResult<>(true); // true - успешная отправка, оборачиваем результат в спец. объект
        } catch (MessagingException e) {
            CLogger.fatal("Ошибка отправки письма", e);
        }

        return new AsyncResult<>(false); // false - отправка не удалась, оборачиваем результат в спец. объект
    }
}
