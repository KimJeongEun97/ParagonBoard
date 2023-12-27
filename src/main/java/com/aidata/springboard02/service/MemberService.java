package com.aidata.springboard02.service;

import com.aidata.springboard02.dao.MemberDao;
import com.aidata.springboard02.dto.MemberDto;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Service
@Slf4j
public class MemberService {
    //Dao 사용(스프링이 자동으로 처리)
    @Autowired
    private MemberDao mDao;
    private BCryptPasswordEncoder bEncoder = new BCryptPasswordEncoder();

    public String idcheck(String mid){
        log.info("idcheck()");
        String result = "";
        int mcnt = mDao.selectId(mid);
        if (mcnt == 0){
            result= "ok";
        }else {
            result = "fail";
        }
        return result;
    }

    public String memberJoin(MemberDto member, RedirectAttributes rttr){
        log.info("memberJoin()");
        //가입 성공 시 첫페이지로, 실패 시 다시 가입 페이지로.
        String view = null;
        String msg = null;//경고창으로 띄울 메시지 저장 변수
        //비밀번호 암호화 처리
        String encpwd = bEncoder.encode(member.getM_pwd());
        log.info(encpwd);
        //평문 비밀번호를 암호화된 비밀번호로 덮어쓰기.
        member.setM_pwd(encpwd);

        //insert 작업
        try {
            mDao.insertMember(member);
            msg = "가입 성공!";
            view = "redirect:/";

        }catch (Exception e){
            e.printStackTrace();
            msg = "가입 실패!";
            view = "redirect:joinForm";
        }
        rttr.addFlashAttribute("msg", msg);
        return view;
    }

    public String loginProc(MemberDto member, HttpSession session, RedirectAttributes rttr) {
        log.info("loginProc()");
        String view = null;
        String msg = null;
        String encPwd = mDao.selectPwd(member.getM_id());
        if (encPwd != null){
            if (bEncoder.matches(member.getM_pwd(), encPwd)){
                member = mDao.selectMember(member.getM_id());
                session.setAttribute("member", member);
                view = "redirect:/";
            }else {
                view = "redirect:loginForm";
                msg = "비밀번호가 틀립니다.";
            }
        }else {
            view = "redirect:loginForm";
            msg = "아이디가 존재하지 않습니다.";
        }
        rttr.addFlashAttribute("msg", msg);
        return view;
    }

    @GetMapping("logout")
    public String logout(HttpSession session) {
        log.info("logout()");
        session.invalidate();
        return "redirect:/";
    }
}
