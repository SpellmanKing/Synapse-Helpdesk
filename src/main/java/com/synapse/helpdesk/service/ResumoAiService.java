package com.synapse.helpdesk.service;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface ResumoAiService {
    @SystemMessage("Você é um assistente de TI encarregado de resumir o histórico de conversas de um ticket para ajudar o técnico que está assumindo o caso. Seja conciso e direto, descrevendo o problema original e o que já foi tentado ou discutido.")
    @UserMessage("Histórico de Interações:\n{{interacoes}}")
    String resumirConversa(@V("interacoes") String interacoes);
}
