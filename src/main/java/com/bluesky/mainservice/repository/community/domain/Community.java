package com.bluesky.mainservice.repository.community.domain;

import com.bluesky.mainservice.repository.BaseTimeEntity;
import com.bluesky.mainservice.repository.community.constant.CommunityType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter
@Entity
@Table(name = "community")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Community extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "community_id")
    private Integer id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "community_type", unique = true)
    private CommunityType communityType;

    public Community(Integer id) {
        this.id = id;
    }
}
