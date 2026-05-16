package com.roombooking.proxy;

import com.roombooking.model.EntradaHistorico;
import com.roombooking.model.Usuario;
import java.util.*;

public class HistoricoReservasReal implements HistoricoReservas {

    // Mapa: id do usuário → lista de entradas
    private final Map<String, List<EntradaHistorico>> dados = new HashMap<>();

    @Override
    public void registrar(EntradaHistorico entrada) {
        String id = entrada.getUsuario().getId();
        dados.computeIfAbsent(id, k -> new ArrayList<>()).add(entrada);
    }

    @Override
    public List<EntradaHistorico> buscarHistorico(Usuario solicitante, Usuario alvo) {
        System.out.println("[REAL] Consultando repositório para: " + alvo.getNome());
        return dados.getOrDefault(alvo.getId(), Collections.emptyList());
    }
}