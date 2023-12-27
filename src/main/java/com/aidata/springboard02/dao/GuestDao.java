package com.aidata.springboard02.dao;

import com.aidata.springboard02.dto.GuestDto;
import com.aidata.springboard02.dto.SearchDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GuestDao {
    List<GuestDto> selectGuestList(SearchDto sdto);

    int selectGuestCnt(SearchDto sdto);

    void insertGuest(GuestDto guest);

    //방명록 하나만 가져오는 메소드
    GuestDto selectGuest(int g_num);

    //방명록 번호에 해당하는 삭제 메소드
    void deleteGuestBook(int g_num);

    //방명록 수정 메소드
    void updateGuest(GuestDto guest);
}