package com.roombooking.proxy;

import com.roombooking.model.EntradaHistorico;
import com.roombooking.model.Usuario;
import java.util.*;

public class HistoricoReservasProxy implements HistoricoReservas {

    private final HistoricoReservasReal real = new HistoricoReservasReal();

    // Cache simples: id do alvo → lista cacheada
    private final Map<String, List<EntradaHistorico>> cache = new HashMap<>();

    @Override
    public void registrar(EntradaHistorico entrada) {
        real.registrar(entrada);
        // Invalida cache do usuário ao registrar nova entrada
        cache.remove(entrada.getUsuario().getId());
    }

    @Override
    public List<EntradaHistorico> buscarHistorico(Usuario solicitante, Usuario alvo) {
        // Controle de acesso: só o próprio usuário ou admin pode ver
        if (!solicitante.getId().equals(alvo.getId()) && !solicitante.isAdmin()) {
            System.out.println("[PROXY] Acesso negado: " + solicitante.getNome()
                + " não pode ver histórico de " + alvo.getNome());
            return Collections.emptyList();
        }

        // Cache hit
        if (cache.containsKey(alvo.getId())) {
            System.out.println("[PROXY] Cache hit para: " + alvo.getNome());
            return cache.get(alvo.getId());
        }

        // Cache miss → delega ao objeto real
        List<EntradaHistorico> resultado = real.buscarHistorico(solicitante, alvo);
        cache.put(alvo.getId(), resultado);
        return resultado;
    }
}