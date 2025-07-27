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
        teamRepository.delete(id);
    }
}
