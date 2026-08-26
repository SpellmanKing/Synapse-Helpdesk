package com.synapse.helpdesk.config;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.huggingface.HuggingFaceChatModel;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.model.output.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.List;

@Configuration
public class LangChain4jConfig {

    private static final Logger log = LoggerFactory.getLogger(LangChain4jConfig.class);

    @Value("${langchain4j.hugging-face.chat-model.api-key:}")
    private String apiKey;

    @Value("${langchain4j.hugging-face.chat-model.model-id}")
    private String modelId;

    @Value("${langchain4j.hugging-face.chat-model.timeout-seconds:60}")
    private int timeoutSeconds;

    @Bean
    public ChatLanguageModel chatLanguageModel() {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            log.warn("HUGGING_FACE_API_KEY nao configurada! Usando um modelo dummy para fins de desenvolvimento/teste sem API.");
            return new ChatLanguageModel() {
                @Override
                public Response<AiMessage> generate(List<ChatMessage> messages) {
                    String lastMessage = "";
                    if (!messages.isEmpty()) {
                        lastMessage = messages.get(messages.size() - 1).text();
                    }
                    
                    String reply;
                    if (lastMessage.contains("categoria") || lastMessage.contains("prioridade")) {
                        // Triagem
                        if (lastMessage.contains("VirtualBox") || lastMessage.contains("maquina virtual")) {
                            reply = "{\"categoria\": \"Infraestrutura\", \"prioridade\": \"Média\"}";
                        } else if (lastMessage.contains("firewall") || lastMessage.contains("porta")) {
                            reply = "{\"categoria\": \"Redes\", \"prioridade\": \"Crítica\"}";
                        } else {
                            reply = "{\"categoria\": \"Software\", \"prioridade\": \"Média\"}";
                        }
                    } else if (lastMessage.contains("Artigos de Solução") || lastMessage.contains("Artigos")) {
                        // RAG
                        if (lastMessage.contains("VirtualBox") || lastMessage.contains("virtual") || lastMessage.contains("BIOS")) {
                            reply = "Sugestão baseada na base de conhecimento: Para resolver problemas de maquina virtual no VirtualBox, ative a virtualizacao VT-x na BIOS.";
                        } else {
                            reply = "Sugestão baseada na base de conhecimento: Para realizar a reserva de notebooks e equipamentos de forma correta, você deve seguir as regras de uso em dias de alta demanda, que exigem solicitação formal via chamado com no mínimo 48 horas de antecedência.";
                        }
                    } else {
                        // Resumo ou chat geral
                        reply = "Resumo das interações: O cliente solicita auxílio técnico com prioridade. Foram analisados os detalhes do chamado.";
                    }
                    
                    return Response.from(AiMessage.from(reply));
                }
            };
        }

        log.info("Inicializando HuggingFaceChatModel com o modelo: {}", modelId);
        return HuggingFaceChatModel.builder()
                .accessToken(apiKey)
                .modelId(modelId)
                .timeout(Duration.ofSeconds(timeoutSeconds))
                .build();
    }
}