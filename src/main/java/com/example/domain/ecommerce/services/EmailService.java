package com.example.domain.ecommerce.services;
import java.util.*;

import com.example.domain.ecommerce.models.entities.Cliente;
import com.example.domain.ecommerce.models.entities.Detalle_venta;
import com.example.domain.ecommerce.models.entities.Email;
import com.example.domain.ecommerce.models.entities.PedidoUsuario;
import com.example.domain.ecommerce.models.entities.Producto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    TemplateEngine templateEngine;

    // @Value("${mail.urlFront}")
    // private String urlFront;

    // @Value("${mail.urlF}")
    // private String urlF;

    // public void sendEmailTemplate(Email email, Long id){
    //     MimeMessage message = javaMailSender.createMimeMessage();
    //     try {
    //         MimeMessageHelper helper = new MimeMessageHelper(message,true);
    //         Context context = new Context();
    //         Map<String, Object> model = new HashMap<>();

    //         model.put("username", email.getUserName());
    //         model.put("url", urlFront+"/"+id);

    //         context.setVariables(model);

    //         String htmlText = templateEngine.process("commerce/email_password", context);
    //         helper.setFrom(email.getMailFrom());
    //         helper.setTo(email.getMailTo());
    //         helper.setSubject(email.getSubject());
    //         helper.setText(htmlText, true);

    //         javaMailSender.send(message);
    //     } catch (MessagingException e) {
    //         e.printStackTrace();
    //     }
    // }

    // public void sendEmailVenta(Email email, List<Detalle_venta> detalle_ventas, double total, List<Producto> productos){
    //     MimeMessage message = javaMailSender.createMimeMessage();
    //     try {
    //         MimeMessageHelper helper = new MimeMessageHelper(message,true);
    //         Context context = new Context();
    //         Map<String, Object> model = new HashMap<>();

    //         model.put("username", email.getUserName());
    //         model.put("url", urlFront);
    //         model.put("lista", detalle_ventas);
    //         model.put("total", total);
    //         model.put("productos", productos);


    //         context.setVariables(model);

    //         String htmlText = templateEngine.process("commerce/mail_detalle_pedido", context);
    //         helper.setFrom(email.getMailFrom());
    //         helper.setTo(email.getMailTo());
    //         helper.setSubject(email.getSubject());
    //         helper.setText(htmlText, true);

    //         javaMailSender.send(message);
    //     } catch (MessagingException e) {
    //         e.printStackTrace();
    //     }
    // }


    // public void sendEmailRegistrar(Email email, Long id){
    //     MimeMessage message = javaMailSender.createMimeMessage();
    //     try {
    //         MimeMessageHelper helper = new MimeMessageHelper(message,true);
    //         Context context = new Context();
    //         Map<String, Object> model = new HashMap<>();

    //         model.put("username", email.getUserName());
    //         model.put("url", urlF+"/"+id);

    //         context.setVariables(model);

    //         String htmlText = templateEngine.process("commerce/email_registrar", context);
    //         helper.setFrom(email.getMailFrom());
    //         helper.setTo(email.getMailTo());
    //         helper.setSubject(email.getSubject());
    //         helper.setText(htmlText, true);

    //         javaMailSender.send(message);
    //     } catch (MessagingException e) {
    //         e.printStackTrace();
    //     }
    // }

    // public void sendEmailPedido(Email email, PedidoUsuario pedidoUsuario, 
    // // VentaEcommerce ventaEcommerce,
    //  Cliente cliente){
    //     MimeMessage message = javaMailSender.createMimeMessage();
    //     try{
    //         MimeMessageHelper helper = new MimeMessageHelper(message, true);
    //         Context context = new Context();
    //         Map<String, Object> model = new HashMap<>();

    //         model.put("pedido", pedidoUsuario);
    //         model.put("costoEnvio", pedidoUsuario.getEnvio());
    //         model.put("estado", pedidoUsuario.getEstado());
    //         model.put("cliente", cliente);

    //         context.setVariables(model);

    //         String htmlText = templateEngine.process("venta/correoEstado", context);
    //         helper.setFrom(email.getMailFrom());
    //         helper.setTo(email.getMailTo());
    //         helper.setSubject(email.getSubject());
    //         helper.setText(htmlText, true);

    //         javaMailSender.send(message);
    //     }catch(MessagingException e){
    //         e.printStackTrace();
    //     }
    // }
}