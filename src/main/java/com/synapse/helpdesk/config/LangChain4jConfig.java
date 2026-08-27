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
                    String systemMsg = "";
                    String userMsg = "";
                    
                    for (ChatMessage m : messages) {
                        if (m.type() == dev.langchain4j.data.message.ChatMessageType.SYSTEM) {
                            systemMsg = m.text();
                        } else if (m.type() == dev.langchain4j.data.message.ChatMessageType.USER) {
                            userMsg = m.text();
                        }
                    }
                    
                    String sys = systemMsg.toLowerCase();
                    String usr = userMsg.toLowerCase();
                    String reply;
                    
                    if (sys.contains("triagem")) {
                        // Triage routing
                        if (usr.contains("virtualbox") || usr.contains("virtual") || usr.contains("vm")) {
                            reply = "{\"categoria\": \"Infraestrutura\", \"prioridade\": \"Média\"}";
                        } else if (usr.contains("firewall") || usr.contains("porta") || usr.contains("rede")) {
                            reply = "{\"categoria\": \"Redes\", \"prioridade\": \"Crítica\"}";
                        } else if (usr.contains("senha") || usr.contains("acesso") || usr.contains("bloque")) {
                            reply = "{\"categoria\": \"Acessos\", \"prioridade\": \"Média\"}";
                        } else {
                            reply = "{\"categoria\": \"Software\", \"prioridade\": \"Média\"}";
                        }
                    } else if (sys.contains("resumo") || sys.contains("conversações")) {
                        // Summary routing: dynamic generation based on ticket description and timeline keywords
                        String title = "incidente técnico";
                        if (usr.contains("ticket #")) {
                            int startIdx = usr.indexOf(":", usr.indexOf("ticket #")) + 1;
                            int endIdx = usr.indexOf("\n", startIdx);
                            if (startIdx > 0 && endIdx > startIdx) {
                                title = usr.substring(startIdx, endIdx).trim();
                            }
                        }
                        
                        String desc = "";
                        if (usr.contains("descrição original:")) {
                            int startIdx = usr.indexOf("descrição original:") + "descrição original:".length();
                            int endIdx = usr.indexOf("\n", startIdx);
                            if (startIdx > 0 && endIdx > startIdx) {
                                desc = usr.substring(startIdx, endIdx).trim();
                            }
                        }
                        
                        String diagn = "O cliente relata um problema específico: \\\"" + title + "\\\". " + 
                                       (desc.isEmpty() ? "" : "Os sintomas descritos são: \\\"" + desc + "\\\".");
                        
                        int count = 0;
                        int pos = 0;
                        while ((pos = usr.indexOf("- ", pos)) != -1) {
                            count++;
                            pos += 2;
                        }
                        
                        String hist;
                        if (count <= 1) {
                            hist = "O chamado foi recém-aberto e encontra-se aguardando análise primária por um técnico. O suporte virtual nível 1 sugeriu tratativas iniciais.";
                        } else {
                            hist = "Existem " + count + " interações registradas na linha do tempo. Foram sugeridas orientações técnicas, e o ticket encontra-se em progresso.";
                        }
                        
                        reply = "1. **Descrição Precisa do Problema**: " + diagn + "\\n\\n" +
                                "2. **Resumo do Atendimento**: " + hist;
                    } else {
                        // RAG routing
                        if (usr.contains("virtualbox") || usr.contains("virtual") || usr.contains("vm")) {
                            reply = "--- SUPORTE VIRTUAL DE NÍVEL 1 ---\\n" +
                                    "**Causa Provável**: Desativação da tecnologia de virtualização de hardware na BIOS/UEFI.\\n\\n" +
                                    "**Resolução Sugerida**:\\n" +
                                    "1. Para resolver problemas de maquina virtual no VirtualBox, ative a virtualizacao VT-x na BIOS.\\n" +
                                    "2. Reinicie e aperte F2 ou Del para abrir a BIOS.\\n" +
                                    "3. Mude a opção Intel Virtualization Technology ou SVM Mode para Enabled e salve (F10).\\n\\n" +
                                    "**Próximos Passos**: Caso o procedimento acima não resolva, por favor descreva o resultado obtido. Um técnico humano analisará o caso em breve.";
                        } else {
                            reply = "--- SUPORTE VIRTUAL DE NÍVEL 1 ---\\n" +
                                    "**Causa Provável**: Configuração básica incorreta de permissões ou credenciais.\\n\\n" +
                                    "**Resolução Sugerida**:\\n" +
                                    "1. Consulte as diretrizes oficiais da base de conhecimento da Synapse.\\n" +
                                    "2. Siga o passo a passo detalhado: execute as verificações básicas, reajuste as credenciais ou reinicie a máquina se necessário.\\n" +
                                    "3. Caso persista, aguarde o atendimento técnico humano.\\n\\n" +
                                    "**Próximos Passos**: Caso o procedimento acima não resolva, por favor descreva o resultado obtido. Um técnico humano analisará o caso em breve.";
                        }
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