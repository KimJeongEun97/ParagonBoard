package com.aidata.springboard02.dao;

import com.aidata.springboard02.dto.MemberDto;
import com.aidata.springboard02.dao.MemberDao;
import org.junit.jupiter.api.*;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import static org.junit.jupiter.api.Assertions.*;
@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestMethodOrder(MethodOrderer.DisplayName.class)
class MemberDaoTest {
    @Autowired
    private MemberDao mDao;

    @Test
    @DisplayName("1. MemberDao id check")
    @Order(1)
    public void selectIdTest() throws Exception{
        int cnt = mDao.selectId("test");
    }

    @Test
    @DisplayName("2. MemberDao insert")
    @Order(2)
    public void insertMemberTest() throws Exception{
        // 화면이 없기 때문에 직접 Dto를 생성하여 데이터를 넣고 테스트를 수행
        MemberDto member = new MemberDto();
        member.setM_id("user01"); //DB에 동일한게 있으면 error 테스트라 따로 저장은 안됨
        member.setM_pwd("1111");
        member.setM_name("사용자1");
        member.setM_birth("2002-01-01");
        member.setM_addr("인천시");
        member.setM_phone("010-1111-2222");

        mDao.insertMember(member);
    }
    @Test
    @DisplayName("3. Get password")
    @Order(3)
    public void selectPasswaordTest() throws Exception{
        String pwd = mDao.selectPwd("jek");
    }
}