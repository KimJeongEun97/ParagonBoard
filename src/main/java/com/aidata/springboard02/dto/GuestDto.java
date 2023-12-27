package com.aidata.springboard02.dto;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class GuestDto {
    private int g_num;
    private String g_contents;
    private String m_id;
    private Timestamp g_date;
}
