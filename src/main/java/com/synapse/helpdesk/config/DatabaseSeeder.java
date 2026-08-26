package com.synapse.helpdesk.config;

import com.synapse.helpdesk.model.*;
import com.synapse.helpdesk.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseSeeder.class);

    private final UsuarioRepository usuarioRepository;
    private final ArtigoSolucaoRepository artigoSolucaoRepository;

    public DatabaseSeeder(UsuarioRepository usuarioRepository, ArtigoSolucaoRepository artigoSolucaoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.artigoSolucaoRepository = artigoSolucaoRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Verificando necessidade de carga de dados iniciais...");

        if (usuarioRepository.count() == 0) {
            log.info("Populando tabela de usuarios...");
            Usuario cliente = Usuario.builder()
                    .nome("Calebe Carvalho")
                    .email("calebe.carvalho@synapse.com")
                    .tipo(TipoUsuario.CLIENTE)
                    .build();

            Usuario tecnico1 = Usuario.builder()
                    .nome("Maria Silva")
                    .email("maria.silva@synapse.com")
                    .tipo(TipoUsuario.TECNICO)
                    .build();

            Usuario tecnico2 = Usuario.builder()
                    .nome("Pedro Alvares")
                    .email("pedro.alvares@synapse.com")
                    .tipo(TipoUsuario.TECNICO)
                    .build();

            usuarioRepository.saveAll(Arrays.asList(cliente, tecnico1, tecnico2));
        }

        if (artigoSolucaoRepository.count() == 0) {
            log.info("Populando base de conhecimento RAG com artigos estruturados...");
            
            ArtigoSolucao art1 = ArtigoSolucao.builder()
                    .titulo("Erro de Virtualização VT-x / AMD-V no VirtualBox")
                    .conteudo("O erro de inicialização de máquinas virtuais no VirtualBox geralmente ocorre quando a virtualização de hardware (Intel VT-x ou SVM Mode) está desabilitada na BIOS/UEFI do computador. Para resolver:\n1. Reinicie o computador e pressione repetidamente a tecla de acesso à BIOS (geralmente F2, F12, Del ou Esc).\n2. Navegue até o menu 'Advanced', 'CPU Configuration' ou 'Virtualization'.\n3. Localize a opção 'Intel Virtualization Technology' ou 'SVM Mode' e mude para 'Enabled'.\n4. Salve as alterações (geralmente F10) e reinicie o sistema.\n5. Abra o VirtualBox e tente iniciar a máquina virtual novamente.")
                    .palavrasChave("VirtualBox, maquina, virtual, BIOS, virtualizacao, VT-x, AMD-V, UEFI")
                    .build();

            ArtigoSolucao art2 = ArtigoSolucao.builder()
                    .titulo("Procedimento para Reserva de Equipamentos e Notebooks")
                    .conteudo("A política corporativa de reserva de equipamentos de informática (laptops, projetores, adaptadores) exige solicitação formal:\n1. Abertura do chamado: O chamado deve ser aberto com antecedência mínima de 48 horas úteis.\n2. Priorização: Em dias de alta demanda, a prioridade da fila segue: (a) Reuniões presenciais com clientes externos; (b) Apresentações em eventos corporativos; (c) Uso diário interno.\n3. Devolução: Todos os equipamentos devem ser devolvidos limpos e com cabos em até 24 horas após o término.")
                    .palavrasChave("reserva, notebook, notebooks, equipamento, equipamentos, laptop, laptops, reserva de equipamentos")
                    .build();

            ArtigoSolucao art3 = ArtigoSolucao.builder()
                    .titulo("Instruções para Redefinição de Senha de Rede e Acessos")
                    .conteudo("Caso tenha esquecido sua senha corporativa ou esteja com o acesso bloqueado:\n1. Use o portal de autoatendimento em selfservice.synapse.com para redefinir via SMS ou e-mail secundário.\n2. Se o portal não estiver acessível, abra um chamado com a categoria 'Acessos'.\n3. Um técnico de suporte realizará a redefinição e fornecerá uma senha temporária.\n4. No primeiro login, o sistema exigirá a criação de uma nova senha forte (mínimo de 8 caracteres, contendo maiúsculas, minúsculas, números e caracteres especiais).")
                    .palavrasChave("senha, senhas, acesso, acessos, redefinicao, redefinir, esqueci, bloqueado, login")
                    .build();

            ArtigoSolucao art4 = ArtigoSolucao.builder()
                    .titulo("Resolução de Instabilidade de Rede e Bloqueios de Firewall")
                    .conteudo("Se o sistema apresentar quedas de conexão ou se certas portas de rede estiverem bloqueadas:\n1. Verifique se o seu cabo de rede está conectado corretamente ou se a rede Wi-Fi corporativa está ativa.\n2. Verifique se há regras de firewall de borda bloqueando as portas necessárias (como as portas HTTP 80/443 ou SSH 22).\n3. Reinicie o roteador ou switch local, caso esteja em home office.\n4. Caso persista, o técnico de suporte analisará as regras de firewall no console central Fortinet e liberará o tráfego da sua sub-rede.")
                    .palavrasChave("rede, firewall, porta, portas, conexao, internet, instabilidade, bloqueio")
                    .build();

            artigoSolucaoRepository.saveAll(Arrays.asList(art1, art2, art3, art4));
        }

        log.info("Inicialização de dados concluída!");
    }
}