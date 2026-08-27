package com.synapse.helpdesk.service;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface ResumoAiService {
    @SystemMessage("Você é um assistente de IA especialista em suporte de TI. Sua tarefa é analisar o histórico de conversações de um chamado e produzir um resumo profissional com duas seções:\n\n1. **Descrição Precisa do Problema**: [Identifique e resuma o problema exato relatado pelo cliente]\n\n2. **Resumo do Atendimento**: [Sintetize o andamento da conversa entre o cliente e o técnico, listando o que já foi sugerido, tentado, resolvido ou o status atual da negociação]")
    @UserMessage("Histórico de Interações:\n{{interacoes}}")
    String resumirConversa(@V("interacoes") String interacoes);
}
