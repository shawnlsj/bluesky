package com.bluesky.mainservice;

import javax.persistence.*;

@Entity
public class Member {

    public Member() {

    }

    @Id
    @GeneratedValue
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    Team team;

    public void setTeam(Team team) {
        this.team = team;
    }
}
