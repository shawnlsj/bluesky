package com.bluesky.mainservice;

import javax.persistence.*;
import java.util.List;

@Entity
public class Team {

    @Id
    @GeneratedValue
    Long id;

    String name;

    @OneToMany(mappedBy = "team", fetch = FetchType.LAZY)
    List<Member> memberList;
}
