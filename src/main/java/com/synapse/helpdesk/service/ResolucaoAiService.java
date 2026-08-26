package com.synapse.helpdesk.service;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface ResolucaoAiService {
    @SystemMessage("Você é um assistente de suporte de TI nível 1. Use os artigos fornecidos como contexto para formular uma resposta útil e amigável para o problema do usuário. Caso os artigos não resolvam o problema, explique que um técnico humano analisará o caso em breve.")
    @UserMessage("Artigos de Solução:\n{{contexto}}\n\nProblema do Usuário:\n{{descricao}}")
    String sugerirSolucao(@V("contexto") String contexto, @V("descricao") String descricao);
}
