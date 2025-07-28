package com.caracore.cso.service;

import com.caracore.cso.entity.Team;
import com.caracore.cso.repository.TeamRepository;
import java.util.List;

public class TeamService {
    private final TeamRepository teamRepository;

    public TeamService() {
        this.teamRepository = new TeamRepository();
    }

    public void save(Team team) {
        teamRepository.save(team);
    }

    public Team findById(Long id) {
        return teamRepository.findById(id);
    }

    public List<Team> findAll() {
        return teamRepository.findAll();
    }

    public void delete(Long id) {
        try {
            teamRepository.delete(id);
        } catch (Exception e) {
            if ((e.getMessage() != null && e.getMessage().contains("Referential integrity constraint violation")) ||
                (e.getCause() != null && e.getCause().getMessage() != null && e.getCause().getMessage().contains("Referential integrity constraint violation"))) {
                throw new com.caracore.cso.exception.ReferentialIntegrityException("Não é possível excluir o time pois existem registros vinculados.", e);
            }
            throw e;
        }
    }
}
