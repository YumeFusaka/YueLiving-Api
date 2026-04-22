package com.yumefusaka.yuelivingapi.service;

public interface QwenClient {
    String chat(String model, String systemPrompt, String userPrompt);
}
