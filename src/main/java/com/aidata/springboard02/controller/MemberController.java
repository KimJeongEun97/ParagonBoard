package com.aidata.springboard02.controller;

import com.aidata.springboard02.dto.MemberDto;
import com.aidata.springboard02.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Slf4j
public class MemberController {
    @Autowired
    private MemberService mServ;

    //메인페이지로 이동하는 메소드
    @GetMapping("/")
    public String home(){
        log.info("home()");
        return "index";
    }
    //회원가입으로 이동하는 메소드
    @GetMapping("joinForm")
    public String joinForm(){
        log.info("joinForm()");
        return "joinForm";
    }
    //회원가입을 처리하는 메소드
    @PostMapping("joinProc")
    public String joinProc(MemberDto member, RedirectAttributes rttr){
        log.info("joinProc()");
        String view = mServ.memberJoin(member, rttr);
        return view;
    }
    //로그인 화면으로 이동하는 메소드
    @GetMapping("loginForm")
    public String loginForm(){
        log.info("loginForm()");
        return "loginForm";
    }
    //로그인을 처리하는 메소드
    @PostMapping("loginProc")
    public String loginProc(MemberDto member, HttpSession session, RedirectAttributes rttr){
        log.info("loginProc()");
        String view = mServ.loginProc(member, session, rttr);
        return view;
    }
    //로그아웃을 처리하는 메소드
    @GetMapping("logout")
    public String logout(HttpSession session){
        log.info("logout()");
        mServ.logout(session);
        return "redirect:/";
    }
}
