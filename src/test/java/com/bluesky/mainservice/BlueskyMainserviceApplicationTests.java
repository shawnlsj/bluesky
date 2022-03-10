package com.bluesky.mainservice;

import com.bluesky.mainservice.entity.Hello;
import com.bluesky.mainservice.entity.QHello;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceUnitUtil;
import javax.transaction.Transactional;

@Transactional
@SpringBootTest
class BlueskyMainserviceApplicationTests {

	@Test
	void contextLoads() {

	}
}
