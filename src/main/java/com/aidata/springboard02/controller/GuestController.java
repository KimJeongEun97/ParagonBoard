package com.aidata.springboard02.controller;

import com.aidata.springboard02.dto.GuestDto;
import com.aidata.springboard02.dto.SearchDto;
import com.aidata.springboard02.service.GuestService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Slf4j
public class GuestController {
    @Autowired
    private GuestService gServ;

    //방명록 목록을 처리하는 메소드
    @GetMapping("guestList")
    public ModelAndView guestList(SearchDto sdto, HttpSession session){
        log.info("guestList()");
        ModelAndView mv = gServ.getGuestList(sdto, session);
        return mv;
    }
    //방명록으로 이동하는 메소드
    @GetMapping("guestForm")
    public String guestForm(){
        log.info("guestForm()");
        return "guestForm";
    }
    //방명록 작성을 처리하는 메소드
    @PostMapping("guestProc")
    public String guestProc(GuestDto guest, RedirectAttributes rttr, HttpSession session){
        log.info("guestProc()");

        String view = gServ.guestJoin(guest, rttr, session);

        return view;
    }
    //방명록 상세보기
    @GetMapping("guestDetail")
    public ModelAndView guestDetail(int g_num){
        log.info("guestDetail() : {}", g_num);
        ModelAndView mv = gServ.getGuest(g_num);
        return mv;
    }
    //방명록 삭제
    @GetMapping("guestDelete")
    public String guestDelete(int g_num, RedirectAttributes rttr){
        log.info("guestDelete()");
        String view = gServ.guestDelete(g_num, rttr);
        return view;
    }
    //수정 화면에 나오는 메소드
    @GetMapping("guestUpdate")
    public ModelAndView guestUpdate(int g_num){
        log.info("guestUpdate()");
        ModelAndView mv = gServ.updateGuest(g_num);
        return mv;
    }

    //방명록 수정 완료 후 이동할 메소드
    @PostMapping("gupdateProc")
    public String gupdateProc(GuestDto guest, RedirectAttributes rttr){
        log.info("gupdateProc()");
        String view = gServ.updateGuest(guest, rttr);
        return view;
    }

}
