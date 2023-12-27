package com.aidata.springboard02.controller;

import com.aidata.springboard02.dto.BoardFileDto;
import com.aidata.springboard02.dto.ReplyDto;
import com.aidata.springboard02.service.BoardService;
import com.aidata.springboard02.service.GuestService;
import com.aidata.springboard02.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
public class BoardRestController {
    @Autowired
    private MemberService mServ;
    @Autowired
    private BoardService bServ;
    //아이디 확인 처리 메소드
    @GetMapping("idcheck")
    public String idcheck(String mid) {
        log.info("idcheck()");
        String res = mServ.idcheck(mid);
        return res;
    }

    @PostMapping("delFile")
    public List<BoardFileDto> delFile(BoardFileDto bFile,
                                      HttpSession session){
        log.info("delFile()");
        List<BoardFileDto> fList = bServ.delFile(bFile, session);
        return fList;
    }

    //댓글 처리 메소드
    @PostMapping("replyInsert")
    public ReplyDto replyInsert(ReplyDto reply){
        log.info("replyInsert()");
        reply = bServ.replyInsert(reply);
        return reply;
    }
}
