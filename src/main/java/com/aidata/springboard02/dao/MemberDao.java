package com.aidata.springboard02.dao;

import com.aidata.springboard02.dto.GuestDto;
import com.aidata.springboard02.dto.MemberDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberDao {
    //회원 아이디 조회
    int selectId(String mid);

    //회원 정보 저장
    void insertMember(MemberDto member);

    //로그인 비밀번호 가져오는 메소드
    String selectPwd(String mid);

    //로그인 성공 시 회원정보를 가져오는 메소드
    MemberDto selectMember(String mid);

    //회원 point 수정 메소드
    void updateMemberPoint(MemberDto mDto);

}
