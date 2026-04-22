package com.yumefusaka.yuelivingapi.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yumefusaka.yuelivingapi.service.QwenClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Service
public class QwenClientImpl implements QwenClient {

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private final String baseUrl;
    private final String apiKey;

    public QwenClientImpl(@Value("${ai.base-url:https://dashscope.aliyuncs.com/compatible-mode/v1}") String baseUrl,
                          @Value("${ai.api-key:}") String apiKey) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
    }

    @Override
    public String chat(String model, String systemPrompt, String userPrompt) {
        if (apiKey == null || apiKey.isBlank() || "your-api-key".equals(apiKey)) {
            throw new IllegalStateException("AI 模型未配置可用的 API Key");
        }

        JSONObject payload = new JSONObject();
        payload.put("model", model);
        JSONArray messages = new JSONArray();
        messages.add(createMessage("system", systemPrompt));
        messages.add(createMessage("user", userPrompt));
        payload.put("messages", messages);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/chat/completions"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(payload.toJSONString(), StandardCharsets.UTF_8))
                .timeout(Duration.ofSeconds(30))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() >= 400) {
                throw new IllegalStateException("AI 服务调用失败，状态码：" + response.statusCode());
            }
            return parseAnswer(response.body());
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("AI 服务调用失败：" + e.getMessage(), e);
        }
    }

    private JSONObject createMessage(String role, String content) {
        JSONObject message = new JSONObject();
        message.put("role", role);
        message.put("content", content);
        return message;
    }

    private String parseAnswer(String body) {
        JSONObject root = JSON.parseObject(body);
        JSONArray choices = root.getJSONArray("choices");
        if (choices == null || choices.isEmpty()) {
            throw new IllegalStateException("AI 服务未返回有效内容");
        }
        JSONObject message = choices.getJSONObject(0).getJSONObject("message");
        String content = message == null ? null : message.getString("content");
        if (content == null || content.isBlank()) {
            throw new IllegalStateException("AI 服务未返回有效内容");
        }
        return content.trim();
    }
}
