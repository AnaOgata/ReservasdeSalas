package com.roombooking;

import com.roombooking.decorator.ComLimpeza;
import com.roombooking.decorator.ComMultimidia;
import com.roombooking.factory.*;
import com.roombooking.model.*;
import com.roombooking.observer.*;
import com.roombooking.proxy.HistoricoReservas;
import com.roombooking.proxy.HistoricoReservasProxy;
import com.roombooking.singleton.ConfiguracaoSistema;
import com.roombooking.strategy.PrimeiroAReservar;
import com.roombooking.strategy.PrioridadeDocente;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

// Commit 20 - Ana Clara
public class Main {
    public static void main(String[] args) {

        // ── Configuração do sistema (Singleton) ──────────────────
        ConfiguracaoSistema config = ConfiguracaoSistema.getInstance();
        System.out.println("🏫 Sistema: " + config.getNomeCampus());
        System.out.println("==============================================\n");

        // ── Criação de salas via Factory Method ──────────────────
        SalaFactory factoryIndividual = new SalaIndividualFactory();
        SalaFactory factoryGrupo = new SalaGrupoFactory();
        SalaFactory factoryLab = new SalaLaboratorioFactory("Computação");

        Sala salaIndividual = factoryIndividual.criarSala("S01", "Cabine 01", 1, "Bloco A");
        Sala salaGrupo = factoryGrupo.criarSala("S02", "Sala de Grupo 02", 8, "Bloco B");
        Sala salaLab = factoryLab.criarSala("S03", "Lab de Computação", 20, "Bloco C");

        List<Sala> todasSalas = Arrays.asList(salaIndividual, salaGrupo, salaLab);

        System.out.println("🏠 Salas criadas:");
        todasSalas.forEach(s -> System.out.println("  • " + s));
        System.out.println();

        // ── Criação de usuários ───────────────────────────────────
        Usuario estudante1 = new Estudante("U01", "Ana Clara", "ana@email.com", "2023001");
        Usuario estudante2 = new Estudante("U02", "Graziele", "grazi@email.com", "2023002");
        Usuario professor1 = new Professor("U03", "Prof. Silva", "silva@email.com", "Computação");

        // ── Gerenciador com Strategy: Primeiro a Reservar ────────
        GerenciadorReservas gerenciador = new GerenciadorReservas(new PrimeiroAReservar());

        // ── Observers ────────────────────────────────────────────
        gerenciador.adicionarObservador(new NotificadorEmail());
        gerenciador.adicionarObservador(new NotificadorSistema());
        ServicoRelatorio relatorio = new ServicoRelatorio();
        gerenciador.adicionarObservador(relatorio);

        System.out.println("==============================================");
        System.out.println("📌 RF-01: Salas disponíveis das 10h às 12h:");
        LocalDateTime inicio = LocalDateTime.now().withHour(10).withMinute(0);
        LocalDateTime fim = LocalDateTime.now().withHour(12).withMinute(0);
        gerenciador.salasDisponiveis(todasSalas, inicio, fim)
                .forEach(s -> System.out.println("  • " + s));

        System.out.println("\n==============================================");
        System.out.println("📌 RF-02 e RF-03: Criando reservas e detectando colisões:\n");

        // Reserva 1 — sucesso
        Reserva r1 = gerenciador.criarReserva(salaGrupo, estudante1, inicio, fim);

        // Reserva 2 — conflito com r1 (mesma sala e horário)
        System.out.println();
        Reserva r2 = gerenciador.criarReserva(salaGrupo, estudante2, inicio, fim);

        // Reserva 3 — sem conflito (sala diferente)
        System.out.println();
        Reserva r3 = gerenciador.criarReserva(salaIndividual, estudante2, inicio, fim);

        System.out.println("\n==============================================");
        System.out.println("📌 RF-02: Modificando reserva r3:\n");
        LocalDateTime novoFim = fim.plusHours(1);
        gerenciador.modificarReserva(r3, inicio, novoFim);

        System.out.println("\n==============================================");
        System.out.println("📌 Trocando política para Prioridade Docente (Strategy em runtime):\n");
        gerenciador.setPolitica(new PrioridadeDocente());

        // Professor tenta reservar mesma sala que estudante1 — deve sobrepor
        System.out.println();
        Reserva r4 = gerenciador.criarReserva(salaGrupo, professor1, inicio, fim);

        System.out.println("\n==============================================");
        System.out.println("📌 Bônus — Decorator:\n");
        if (r4 != null) {
            ComMultimidia comMultimidia = new ComMultimidia(r4);
            ComLimpeza comLimpeza = new ComLimpeza(r4);
            System.out.println("  " + comMultimidia);
            System.out.println("  " + comLimpeza);
        }

        System.out.println("\n==============================================");
        System.out.println("📌 RF-05: Relatório diário (pull):\n");
        gerenciador.gerarRelatorioDiario();

        // --- Demonstração do Proxy de Histórico ---
        HistoricoReservas historico = new HistoricoReservasProxy();
            
        // Registra entradas
        historico.registrar(new EntradaHistorico(estudante, sala1,
            LocalDateTime.now().minusDays(2), "CONFIRMADA"));
        historico.registrar(new EntradaHistorico(estudante, sala2,
            LocalDateTime.now().minusDays(1), "CANCELADA"));
        
        // Acesso legítimo (próprio usuário)
        System.out.println("\n-- Histórico próprio --");
        historico.buscarHistorico(estudante, estudante)
                 .forEach(System.out::println);
        
        // Acesso via cache (segunda chamada)
        System.out.println("\n-- Segunda consulta (cache) --");
        historico.buscarHistorico(estudante, estudante)
                 .forEach(System.out::println);
        
        // Acesso negado (outro usuário sem admin)
        System.out.println("\n-- Tentativa de acesso não autorizado --");
        historico.buscarHistorico(outroEstudante, estudante)
                 .forEach(System.out::println);
        
        // Acesso autorizado por admin
        System.out.println("\n-- Acesso do admin --");
        historico.buscarHistorico(admin, estudante)
                 .forEach(System.out::println);
    }
}