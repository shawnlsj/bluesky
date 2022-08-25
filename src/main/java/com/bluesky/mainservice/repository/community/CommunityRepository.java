package com.bluesky.mainservice.repository.community;

import com.bluesky.mainservice.repository.community.constant.CommunityType;
import com.bluesky.mainservice.repository.community.domain.Community;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommunityRepository extends JpaRepository<Community, Integer> {

    @Query("select c.id from Community c where c.communityType = :communityType")
    Integer findIdByCommunityType(@Param("communityType") CommunityType communityType);
}
