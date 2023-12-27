package com.aidata.springboard02.service;

import com.aidata.springboard02.dao.GuestDao;
import com.aidata.springboard02.dao.MemberDao;
import com.aidata.springboard02.dto.GuestDto;
import com.aidata.springboard02.dto.MemberDto;
import com.aidata.springboard02.dto.SearchDto;
import com.aidata.springboard02.utill.PagingUtil;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Service
@Slf4j
public class GuestService {
    @Autowired
    private GuestDao gDao;
    @Autowired
    private MemberDao mDao;
    @Autowired
    private PlatformTransactionManager manager;
    @Autowired
    private TransactionDefinition definition;
    private int lcnt = 10;

    public ModelAndView getGuestList(SearchDto sdto, HttpSession session) {
        log.info("getGuestList()");
        ModelAndView mv = new ModelAndView();
        if (session.getAttribute("member")==null){
            mv.setViewName("redirect:loginForm");
            return mv;
        }
        int num = sdto.getPageNum();
        if(num == 0) num = 1;
        if (sdto.getListCnt() == 0){
            sdto.setListCnt(lcnt);
        }
        sdto.setPageNum((num - 1) * sdto.getListCnt());
        List<GuestDto> gList = gDao.selectGuestList(sdto);
        mv.addObject("gList", gList);

        sdto.setPageNum(num);
        String pageHtml = getPaging(sdto);
        mv.addObject("paging", pageHtml);

        session.setAttribute("pageNum", num);
        if (sdto.getColname() != null){
            session.setAttribute("sdto", sdto);
        }else {
            session.removeAttribute("sdto");
        }
        session.setAttribute("pageNum",num);

        mv.setViewName("GuestList");
        return mv;
    }

    private String getPaging(SearchDto sdto) {
        String pageHtml = null;
        int maxNum = gDao.selectGuestCnt(sdto);
        int pageCnt = 5;
        String listName = "guestList?";
        if (sdto.getColname() != null){
            listName += "colname=" + sdto.getColname() + "&keyword=" + sdto.getKeyword() + "&";
        }

        PagingUtil paging = new PagingUtil(
                maxNum,
                sdto.getPageNum(),
                sdto.getListCnt(),
                pageCnt,
                listName);

        pageHtml = paging.makePaging();

        return pageHtml;
    }

    public String guestJoin(GuestDto guest,
                            RedirectAttributes rttr,
                            HttpSession session) {
        log.info("guestJoin()");
        //작성 성공시 방명록 목록으로...
        String view = null;
        String msg = null; //경고창으로 띄울 메시지 저장 변수

        try {
            log.info("방명록 번호 : " + guest.getG_num());
            gDao.insertGuest(guest);
            log.info("방명록 번호 : " + guest.getG_num());

            //작성자 point 수정
            MemberDto member = (MemberDto) session.getAttribute("member");
            int point = member.getM_point() + 1;
            if (point > 100){
                point = 100;
            }
            member.setM_point(point);
            mDao.updateMemberPoint(member);
            //세션에 새 정보 저장
            member = mDao.selectMember(member.getM_id());
            session.setAttribute("member",member);
            //세션에 같은 이름으로 set을 하면 덮어쓰기가 된다

            msg = "방명록 생성!";
            view = "redirect:guestList?pageNum=1";
        }catch (Exception e){
            e.printStackTrace();
            view = "redirect:guestForm";
        }

        rttr.addFlashAttribute("msg", msg);
        return view;
    }

    public ModelAndView getGuest(int g_num){
        log.info("getGuest()");
        ModelAndView mv = new ModelAndView();

        GuestDto guest = gDao.selectGuest(g_num);

        mv.addObject("guest", guest);
        mv.setViewName("guestDetail");

        return mv;
    }

    public String guestDelete(int g_num, RedirectAttributes rttr) {
        log.info("deleteGuest()");
        String view = null;
        String msg = null;

        try {
            gDao.deleteGuestBook(g_num);

            view = "redirect:guestList?pageNum=1";
            msg = "삭제 성공";
        }catch (Exception e){
            e.printStackTrace();
            view = "redirect:guestDetail?g_num=" + g_num;
            msg = "삭제 실패";
        }
        rttr.addFlashAttribute("msg", msg);
        return view;
    }

    //수정을 누르면 수정페이지로 이동 후 작성한 글이 표시됨
    public ModelAndView updateGuest(int g_num) {
        log.info("updateGuest()");
        ModelAndView mv = new ModelAndView();
        //방명록 내용 가져오기
        GuestDto guest = gDao.selectGuest(g_num);
        //mv에 담기
        mv.addObject("guest", guest);
        //템플릿 지정
        mv.setViewName("guestUpdate");
        return mv;
    }

    public String updateGuest(GuestDto guest, RedirectAttributes rttr){
        log.info("guestUpdate()");

        TransactionStatus status = manager.getTransaction(definition);

        String view = null;
        String msg = null;

        try {
            gDao.updateGuest(guest);

            manager.commit(status);
            view="redirect:guestDetail?g_num="+guest.getG_num();
            msg = "수정 성공";
        }catch (Exception e){
            e.printStackTrace();
            manager.rollback(status);
            view = "redirect:guestUpdate:g_num="+guest.getG_num();
            msg = "수정 실패";
        }
        rttr.addFlashAttribute("msg", msg);
        return view;
    }
}
