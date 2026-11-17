package com.example.demo.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final String apiKey;
    private final String remetenteEmail;
    private final String remetenteNome;
    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10)).build();

    public EmailService(@Value("${email.brevo-api-key:}") String apiKey,
                        @Value("${email.remetente-email:}") String remetenteEmail,
                        @Value("${email.remetente-nome:Cupkat}") String remetenteNome) {
        this.apiKey = apiKey;
        this.remetenteEmail = remetenteEmail;
        this.remetenteNome = remetenteNome;
    }

    /** Nunca lança exceção: a resposta da API não pode revelar se o e-mail existe. */
    public void enviarRecuperacaoSenha(String para, String nome, String link) {
        if (apiKey.isBlank() || remetenteEmail.isBlank()) {
            log.warn("E-mail não configurado. Link de recuperação para {}: {}", para, link);
            return;
        }

        String html = "<p>Olá, " + escaparHtml(nome) + "!</p>"
                + "<p>Recebemos um pedido para redefinir a sua senha. "
                + "Clique no link abaixo (válido por 30 minutos):</p>"
                + "<p><a href=\"" + link + "\">Redefinir minha senha</a></p>"
                + "<p>Se não foi você, ignore este e-mail.</p>";

        String json = "{\"sender\":{\"name\":" + aspas(remetenteNome)
                + ",\"email\":" + aspas(remetenteEmail) + "},"
                + "\"to\":[{\"email\":" + aspas(para) + ",\"name\":" + aspas(nome) + "}],"
                + "\"subject\":\"Redefinição de senha - Cupkat\","
                + "\"htmlContent\":" + aspas(html) + "}";

        HttpRequest req = HttpRequest.newBuilder(URI.create("https://api.brevo.com/v3/smtp/email"))
                .timeout(Duration.ofSeconds(15))
                .header("accept", "application/json")
                .header("content-type", "application/json")
                .header("api-key", apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        try {
            HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
            if (res.statusCode() >= 300) {
                log.error("Falha ao enviar e-mail (HTTP {}): {}", res.statusCode(), res.body());
            }
        } catch (IOException e) {
            log.error("Falha ao enviar e-mail: {}", e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Envio de e-mail interrompido.");
        }
    }

    private static String escaparHtml(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }

    private static String aspas(String s) {
        StringBuilder b = new StringBuilder("\"");
        for (char c : s.toCharArray()) {
            switch (c) {
                case '"' -> b.append("\\\"");
                case '\\' -> b.append("\\\\");
                case '\n' -> b.append("\\n");
                case '\r' -> b.append("\\r");
                case '\t' -> b.append("\\t");
                default -> {
                    if (c < 0x20) b.append(String.format("\\u%04x", (int) c));
                    else b.append(c);
                }
            }
        }
        return b.append('"').toString();
    }
}