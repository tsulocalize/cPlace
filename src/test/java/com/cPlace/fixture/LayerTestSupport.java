package com.cPlace.fixture;

import com.cPlace.chzzk.respository.ChzzkMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class LayerTestSupport {

    @Autowired
    ChzzkMemberRepository chzzkMemberRepository;

    @BeforeEach
    void renewalMember() {
        Fixtures.saveMembers(chzzkMemberRepository);
    }
}
