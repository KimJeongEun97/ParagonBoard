package com.aidata.springboard02.controller;

import com.aidata.springboard02.dto.BoardDto;
import com.aidata.springboard02.dto.BoardFileDto;
import com.aidata.springboard02.dto.MemberDto;
import com.aidata.springboard02.dto.SearchDto;
import com.aidata.springboard02.service.BoardService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
@Slf4j
public class BoardController {
    @Autowired
    private BoardService bServ;

    //게시글 작성을 처리하는 메소드
    @PostMapping("boardProc")
    public String boardProc(@RequestPart List<MultipartFile> files,
                            BoardDto board,
                            RedirectAttributes rttr,
                            HttpSession session) {
        log.info("boardProc()");
        String view = bServ.boardJoin(files, board, session, rttr);
        return view;
    }

    //게시판 목록을 처리하는 메소드
    @GetMapping("boardList")
    public ModelAndView boardList(SearchDto sdto, HttpSession session) {
        log.info("boardList()");
        ModelAndView mv = bServ.getBoardList(sdto, session);
        return mv;
    }

    //게시글 작성 페이지 이동 메소드
    @GetMapping("boardForm")
    public String boardForm() {
        log.info("boardForm()");
        return "boardForm";
    }

    //글 상세보기
    @GetMapping("boardDetail")
    public ModelAndView boardDetail(int b_num, BoardDto bDto, HttpSession session){
        log.info("boardDetail() : {}", b_num);
        ModelAndView mv = bServ.getBoard(b_num, bDto, session);
        return mv;
    }

    //파일 다운로드
    @GetMapping("download")
    public ResponseEntity<Resource> fileDownload(BoardFileDto bfile,
                                                 HttpSession session) throws IOException {
        log.info("fileDownload()");
        ResponseEntity<Resource> resp = bServ.fileDownload(bfile,session);
        return resp;
    }

    //삭제 처리 메소드
    @GetMapping("boardDelete")
    public String boardDelete(int b_num, RedirectAttributes rttr, HttpSession session){
        log.info("boardDelete()");
        String view = bServ.deleteBoard(b_num, rttr, session);
        return view;
    }

    //수정 처리 메소드
    @GetMapping("updateForm")
    public ModelAndView updateForm(int b_num){
        log.info("updateForm()");
        ModelAndView mv = bServ.updateBoard(b_num);
        return mv;
    }

    //게시글 수정 완료 후 이동할 메소드
    @PostMapping("updateProc")
    public String updateProc(List<MultipartFile> files, BoardDto board, HttpSession session, RedirectAttributes rttr){
        log.info("updateProc()");
        String view = bServ.updateBoard(files, board, session, rttr);
        return view;
    }
}



















