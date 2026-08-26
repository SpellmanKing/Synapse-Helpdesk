package com.synapse.helpdesk.service;

import com.synapse.helpdesk.model.TriagemResult;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface TriadorAiService {
    @SystemMessage("Você é um assistente de triagem de TI. Analise o seguinte problema e retorne APENAS um JSON no formato {\"categoria\": \"X\", \"prioridade\": \"Y\"}. " +
            "Escolha a categoria entre: Hardware, Software, Redes, Infraestrutura, Acessos. " +
            "Escolha a prioridade entre: Baixa, Média, Alta, Crítica.")
    @UserMessage("Analise o problema: Título: {{titulo}} - Descrição: {{descricao}}")
    TriagemResult triar(@V("titulo") String titulo, @V("descricao") String descricao);
}
