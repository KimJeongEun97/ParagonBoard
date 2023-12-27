package com.aidata.springboard02.dto;

import lombok.Data;


@Data
public class MemberDto {
    private String m_id;
    private String m_pwd;
    private String m_name;
    private String m_birth;
    private String m_addr;
    private String m_phone;
    private int m_point;
    private String g_name;
    //포인트 등급이 표기되는 g_name도 함께 작성.
}