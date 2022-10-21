package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TeamRepository teamRepository;

    @PersistenceContext
    EntityManager em;

    @Autowired
    MemberQueryRepository memberQueryRepository;

    @Test
    public void testMember() {
        System.out.println("memberRepository = " + memberRepository.getClass());
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(savedMember.getId()).get();

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    public void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        //단건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();

        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        //동일성 유지(JPA) ---> '==' 를 써도 동일성이 보장
        assertThat(findMember1).isSameAs(member1);
        assertThat(findMember2).isSameAs(member2);

        //update(변경 감지 | dirty checking)
//        findMember1.setUsername("member!!!!!");

        //리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);
        assertThat(all.size()).isSameAs(2);

        //카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);
        assertThat(count).isSameAs(2L);

        //삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deletedCnt = memberRepository.count();
        assertThat(deletedCnt).isEqualTo(0);
        assertThat(deletedCnt).isSameAs(0L);
    }

    @Test
    public void findByUsernameAndAgeGreaterThen() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void findHelloBy() {
        List<Member> helloBy = memberRepository.findTop3HelloBy();
    }

    @Test
    public void testNamedQuery() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsername("AAA");
        Member findMember = result.get(0);
        assertThat(findMember).isEqualTo(m1);
    }

    @Test
    public void testQuery() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("AAA", 10);
        assertThat(result.get(0)).isEqualTo(m1);
    }

    @Test
    public void findUsernameList() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> usernameList = memberRepository.findUsernameList();
        for (String s : usernameList) {
            System.out.println("s = " + s);
        }
    }

    @Test
    public void findMemberDto() {

        Team team = new Team("teamA");
        teamRepository.save(team);

        Member m1 = new Member("AAA", 10);
        m1.setTeam(team);
        memberRepository.save(m1);

        List<MemberDto> memberDto = memberRepository.findMemberDto();

        for (MemberDto dto : memberDto) {
            System.out.println("dto = " + dto);
        }
    }

    @Test
    public void findByNames() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        // select member0_.member_id as member_i1_0_, member0_.age as age2_0_, member0_.team_id as team_id4_0_, member0_.username as username3_0_ from member member0_ where member0_.username in ('AAA' , 'BBB');
        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));

        for (Member member : result) {
            System.out.println("member = " + member);
        }
    }

    @Test
    public void returnType() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

//        List<Member> findMembers = memberRepository.findListByUsername("AAA"); //컬렉션
//        Member findMember = memberRepository.findMemberByUsername("AAA");//단 건 조회
//        Optional<Member> findOptional = memberRepository.findOptionalByUsername("AAA"); // 단 건 Optional 조회
//        Member findMember = findOptional.orElseGet(() -> new Member("null", 0));
//        System.out.println("findMember = " + findMember);

        //주의사항
        //컬렉션
//        List<Member> result = memberRepository.findListByUsername("asasdasd");
//        System.out.println("result = " + result.size()); //result = 0 --> xxx != null 할 필요가 없고, 무조건 받으면 된다.

        //단건 조회
//        Member findMember = memberRepository.findMemberByUsername("asdsada");
//        System.out.println("findMember = " + findMember); //findMember = null ---> 결과가 null 반환

        //Optional
        Optional<Member> findMember = memberRepository.findOptionalByUsername("adasdada"); // 조회하는 값이 없을 경우
//        Optional<Member> findMember = memberRepository.findOptionalByUsername("AAA"); // 하나를 출력해야하는데 여러 개인 경우 --->(jpa)NonUniqueResultException --> (Spring)IncorrectResultSizeDataAccessException
        System.out.println("findMember = " + findMember); //findMember = Optional.empty
        System.out.println("findMember.orElse = " + findMember.orElse(new Member("default", 10)));
    }


    @Test
    public void paging() {

        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));


        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.Direction.DESC, "username"); // page index = 0부터 시작

        //when
        Page<Member> page = memberRepository.findByAge(age, pageRequest);
//        List<Member> findTop3Member = memberRepository.findTop3ByAge(age);

        Page<MemberDto> toMap = page.map(m -> new MemberDto(m.getId(), m.getUsername(), null));


        //then
        List<Member> content = page.getContent();
        long totalElements = page.getTotalElements();

//        for (Member member : content) {
//            System.out.println("member = " + member);
//        }
//        System.out.println("totalElements = " + totalElements);

        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();
    }

    @Test
    public void slice() {

        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));


        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.Direction.DESC, "username"); // slice는 size + 1를 가지고 온다.

        //when
        //select member0_.member_id as member_i1_0_, member0_.age as age2_0_, member0_.team_id as team_id4_0_, member0_.username as username3_0_ from member member0_ where member0_.age=10 order by member0_.username desc limit 4;
        Slice<Member> page = memberRepository.findSliceByAge(age, pageRequest);
//        List<Member> page = memberRepository.findListByAge(age, pageRequest); // 딱 자료만 다음이나 이전없이 딱 해당 리스트크기만큼만 데이터를 가져올 때

        //then
        List<Member> content = page.getContent();

        assertThat(content.size()).isEqualTo(3);
//        assertThat(page.getTotalPages()).isEqualTo(2); //slice에는 totalElements가 없다.
        assertThat(page.getNumber()).isEqualTo(0);
//        assertThat(page.getTotalPages()).isEqualTo(2); //slice에는 totalPages가 존재하지 않는다.
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();
    }

    @Test
    public void bulkUpdate() {

        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

//        em.flush(); // jpql 쿼리 실행 전에 자동으로 flush()를 진행한다.
        //when
        int resultCount = memberRepository.bulkAgePlus(20); //bulk update
//        em.flush();
//        em.clear();

        List<Member> result = memberRepository.findByUsername("member5"); //member5 = Member(id=5, username=member5, age=40) | 영속성 컨텍스트를 초기화하지 않는 경우
        Member member5 = result.get(0);
        System.out.println("member5 = " + member5); //member5 = Member(id=5, username=member5, age=40) | 영속성 컨텍스트를 초기화하지 않는 경우

        //then
        assertThat(resultCount).isEqualTo(3);
    }

    @Test
    public void findMemberLazy() {

        //given
        //member1 -> teamA
        //member2 -> teamB

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);

        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        //when N(결과) + 1
        //select Member Member : 1
//        List<Member> members = memberRepository.findAll();
//        List<Member> members = memberRepository.findMemberFetchJoin();
        List<Member> members = memberRepository.findEntityGraphByUsername("member1");

        //then
        for (Member member : members) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member.teamClass = " + member.getTeam().getClass());
            System.out.println("member.teamName = " + member.getTeam().getName());
        }
    }

    @Test
    public void queryHint() {

        //given
        Member member1 = memberRepository.save(new Member("member1", 10));
        memberRepository.save(member1);
        em.flush();
        em.clear();

        //when
        Member findMember = memberRepository.findReadOnlyByUsername("member1");
        findMember.setUsername("member2");

        em.flush();
    }

    @Test
    public void lock() {

        //given
        Member member1 = memberRepository.save(new Member("member1", 10));
        memberRepository.save(member1);
        em.flush();
        em.clear();

        //when
        List<Member> result = memberRepository.findLockByUsername("member1");
    }

    @Test
    public void callCustom() {
        List<Member> result = memberRepository.findMebmerCustom();
    }

}