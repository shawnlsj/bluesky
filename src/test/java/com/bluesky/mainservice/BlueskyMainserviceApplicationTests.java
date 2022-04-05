package com.bluesky.mainservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

@Transactional
@SpringBootTest
class BlueskyMainserviceApplicationTests {

	@Autowired
	EntityManager em;

	@Test
	@Rollback(value = false)
	void contextLoads() {
//		Team teamA = new Team();
//		teamA.name = "teamA";
//		System.out.println("============================");
//
//		em.persist(teamA);
//		System.out.println("teamA.id = " + teamA.id);
//
//		Team teamB = new Team();
//		em.persist(teamB);
//		System.out.println("teamB.id = " + teamB.id);
//
//		Team teamC = new Team();
//		em.persist(teamC);
//		System.out.println("teamC.id = " + teamC.id);


//		for (int i = 0; i < 5; i++) {
//			Team team = new Team();
//			em.persist(team);
//
//			Member member = new Member();
//			member.setTeam(team);
//			em.persist(member);
//			//List<Member> resultList = em.createQuery("select m from Member m", Member.class).getResultList();
//		}
		em.persist(new Member());
		System.out.println("flush 전");
		em.flush();
		em.clear();

		System.out.println("flush 후");
		em.find(Member.class, 1L);
	//	List<Team> teamList = em.createQuery("select t from Team t", Team.class).getResultList();
	//	List<Member> memberList = teamList.get(3).memberList;
	//	Member member = memberList.get(0);

//		for (Team team : teamList) {
//			for (Member member : team.memberList) {
//				System.out.println("member id = " + member.id);
//			}
//		}
	}
}
