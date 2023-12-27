package com.aidata.springboard02.service;

import com.aidata.springboard02.dao.BoardDao;
import com.aidata.springboard02.dao.MemberDao;
import com.aidata.springboard02.dto.*;
import com.aidata.springboard02.utill.PagingUtil;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

@Service
@Slf4j
public class BoardService {
    @Autowired
    private BoardDao bDao;

    @Autowired
    private MemberDao mDao;

    //트랜젝션 관련 객체 선언
    @Autowired
    private PlatformTransactionManager manager;
    @Autowired
    private TransactionDefinition definition;

    private int lcnt = 10;

    public ModelAndView getBoardList(SearchDto sdto, HttpSession session) {
        log.info("getBoardList()");
        ModelAndView mv = new ModelAndView(); //객체 생성
        //세션에 member 속성이 없으면 로그인 폼으로 리다이렉트
        if (session.getAttribute("member") == null) {
            mv.setViewName("redirect:loginForm"); //로그인 폼으로 리다이렉트
            return mv; //메소드 종료
        }
        int num = sdto.getPageNum(); //페이지 번호를 가져옴
        if (num == 0) num = 1; //페이지 번호가 0인 경우 1로 설정
        if (sdto.getListCnt() == 0) {
            sdto.setListCnt(lcnt); //검색 조건으로 주어진 목록 개수가 0이면 lcnt로 설정
        }
        sdto.setPageNum((num - 1) * sdto.getListCnt()); //시작하는 항목 인덱스 설정
        //검색 조건에 따라 게시물 목록을 데이터베이스에서 가져옴
        List<BoardDto> bList = bDao.selectBoardList(sdto);
        //가져온 게시물 목록을 ModelAndView 객체에 추가
        mv.addObject("bList", bList);

        sdto.setPageNum(num); //페이지 번호를 원래 값으로 복원
        String pageHtml = getPaging(sdto); //페이지 번호에 따른 페이징 HTML을 생성
        mv.addObject("paging", pageHtml); //생성한 페이징 HTML을 ModelAndView 객체에 추가

        //페이지 번호와 검색 관련 내용을 세션에 저장
        if (sdto.getColname() != null) {
            session.setAttribute("sdto", sdto);
        } else {// 검색 조건이 없는 경우 세션에서 제거
            session.removeAttribute("sdto");
        }
        // 현재 페이지 번호를 세션에 저장
        session.setAttribute("pageNum", num);

        mv.setViewName("boardList");// 뷰 이름을 "boardList"로 설정
        return mv; // ModelAndView 객체 반환
    }

    private String getPaging(SearchDto sdto) {
        String pageHtml = null; // 페이징 HTML 문자열을 초기화
        int maxNum = bDao.selectBoardCnt(sdto); // 전체 게시물 수를 데이터베이스에서 가져옴
        int pageCnt = 5; // 페이징에 표시할 페이지 번호 개수를 설정
        String listName = "boardList?"; // 페이징 링크의 기본 URL
        if (sdto.getColname() != null) { // 검색 조건이 있는 경우 URL에 검색 조건을 추가
            listName += "colname=" + sdto.getColname() + "&keyword=" + sdto.getKeyword() + "&";
        }

        PagingUtil paging = new PagingUtil(  // PagingUtil 객체를 생성하고 필요한 정보를 전달하여 페이징을 계산
                maxNum,          // 전체 게시물 수
                sdto.getPageNum(), // 현재 페이지 번호
                sdto.getListCnt(), // 한 페이지에 표시할 게시물 수
                pageCnt,            // 페이징에 표시할 페이지 번호 개수
                listName);           // 페이징 링크의 기본 URL

        pageHtml = paging.makePaging(); // 생성한 페이징 HTML을 가져와 pageHtml에 저장

        return pageHtml;  // 페이징 HTML을 반환
    }

    public String boardJoin(List<MultipartFile> files,
                            BoardDto board,
                            HttpSession session,
                            RedirectAttributes rttr) {
        log.info("boardJoin()");

        TransactionStatus status = manager.getTransaction(definition);

        String view = null; // 리다이렉트로 페이지 이동 처리
        String msg = null; //경고창으로 띄울 메시지 저장 변수

        try {
            //글 내용 저장.
            bDao.insertBoard(board);
            //log.info("게시글 번호 : " + board.getB_num());

            //파일 저장(파일 정보 저장)
            fileUpload(files, session, board.getB_num());

            //작성자 point 수정
            MemberDto member = (MemberDto) session.getAttribute("member");
            //자료형을 특정짓기 위한 다운캐스팅
            int point = member.getM_point() + 3; //member에 포인트 3점을 정수값으로 더한다
            if (point > 100){//point가 100을 넘지 않도록 필터링.
                point = 100;//100으로 설정
            }

            member = mDao.selectMember(member.getM_id());
            session.setAttribute("member", member);
            //세션에 같은 이름으로 set을 하면 덮어쓰기가 된다.

            manager.commit(status); //최종 승인
            msg = "게시글 생성";
            view = "redirect:boardList?pageNum=1"; //작성 성공시 게시판 목록 1페이지로
        } catch (Exception e) {
            e.printStackTrace();
            manager.rollback(status); //취소
            msg = "작성 실패";
            view = "redirect:boardForm";
        }

        rttr.addFlashAttribute("msg", msg);

        return view;
    }

    private void fileUpload(List<MultipartFile> files,
                            HttpSession session,
                            int bNum) throws Exception {
        //이 메소드의 예외처리(파일 저장 실패, 파일 정보 저장 실패)를
        // 호출한 메소드에서 처리하도록 throws를 사용.
        log.info("fileUpload()");
        //파일 저장(폴더)
        //파일 저장 위치 처리: 세션에서 위치(경로) 정보를 구함.
        String realPath = session.getServletContext().getRealPath("/");
        log.info(realPath);
        realPath += "upload/";//파일 폴더용 업로드('/'를 무조건 넣어야 한다)
        //업로드용 폴더가 없으면 자동으로 생성하자.
        File folder = new File(realPath);
        if (folder.isDirectory() == false){
            //isDirectory() 폴더의 유무 확인 메소드
            //폴더가 있으면 true, 없거나 폴더가 아니면 false.
            folder.mkdir();//MaKe DIRectory(폴더)를 줄임
        }

        for (MultipartFile mf : files){
            //파일명(원래 이름) 추출
            String oriname = mf.getOriginalFilename();//파일이름을 추출해주는 메소드
            if (oriname.equals("")){//이름 비교 - 비어있다면
                return;//업로드할 파일 없음. 파일 저장 작업 종료.
            }

            BoardFileDto bfd = new BoardFileDto();
            bfd.setBf_bnum(bNum);//게시글 번호 저장.
            bfd.setBf_oriname(oriname);//원래 파일명 저장.
            String sysname = System.currentTimeMillis() + oriname.substring(oriname.lastIndexOf("."));
            //파일명을 변경 마지막에 '.'을 찍어준다
            bfd.setBf_sysname(sysname);

            //파일 저장(upload폴더에...)
            File file = new File(realPath + sysname);
            //........./.../.../webapp/upload/123123.jpg 처럼 저장할 수 있는 객체가 만들어 졌다
            mf.transferTo(file);//하드디스크에 저장.

            //파일 정보 저장(DB에...)
            bDao.insertFile(bfd);
        }
    }

    public ModelAndView getBoard(int b_num, BoardDto bDto, HttpSession session){ //controller에 작성한 타입에 맞춰 생성
        log.info("getBoard()");
        ModelAndView mv = new ModelAndView();//모델앤뷰객체생성

        MemberDto member = (MemberDto) session.getAttribute("member");
        int views = bDto.getB_views();
        if (member.getM_id() != bDto.getB_id()){
            views++;
            bDto.setB_views(views);
            bDao.updateViewPoint(bDto);
        }
        //조회수가 증가하게 세션에 저장
        bDto = bDao.selectBoard(bDto.getB_num());
        session.setAttribute("board", bDto);

        //게시글 번호로 선택한 게시글 가져오기
        BoardDto board = bDao.selectBoard(b_num);//board 객체생성
        mv.addObject("board", board);//mv에 board넣기

        //게시글의 파일목록 가져오기
        List<BoardFileDto> bfList = bDao.selectFileList(b_num);
        mv.addObject("bfList", bfList);

        //게시글의 댓글목록 가져오기
        List<ReplyDto> rList = bDao.selectReplyList(b_num);
        mv.addObject("rList", rList);

        mv.setViewName("boardDetail"); //html로 이동

        return mv;
    }

    public ResponseEntity<Resource> fileDownload(BoardFileDto bfile, HttpSession session)throws IOException {
        log.info("fileDownload()");
        //파일 저장 경로 및 경로와 파일명을 조합
        String realpath = session.getServletContext().getRealPath("/");
        realpath += "upload/" + bfile.getBf_sysname();

        //실제 파일이 저장된 하드디스크까지의 경로를 수립.//자원을 가져오기 위한 통로 객체
        InputStreamResource fResource = new InputStreamResource(new FileInputStream(realpath));

        //파일명이 한글인 경우의 처리(UTF-8로 인코딩)
        String fileName = URLEncoder.encode(bfile.getBf_oriname(), "UTF-8");

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .cacheControl(CacheControl.noCache())
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename="+fileName)
                .body(fResource);
    }

    //게시글 삭제(파일목록+파일, 댓글 목록 함께 삭제)
    public String deleteBoard(int b_num, RedirectAttributes rttr, HttpSession session){
        log.info("deleteBoard()");

        //트렌젝션
        TransactionStatus status = manager.getTransaction(definition);

        String view = null;
        String msg = null;

        try {
            //0. 파일 삭제 목록 구하기
            List<String> fList = bDao.selectFnameList(b_num);

            //1. 파일목록 삭제
            bDao.deleteFiles(b_num);
            //2. 댓글목록 삭제
            bDao.deleteReplys(b_num);
            //3. 게시글 삭제
            bDao.deleteBoard(b_num);

            //4. 파일 삭제 처리
            deleteFiles(fList,session);
            if (fList.size() != 0){
                deleteFiles(fList, session);
            }

            manager.commit(status);
            view = "redirect:boardList?pageNum=1";
            msg = "삭제 성공";
        }catch (Exception e){
            e.printStackTrace();

            manager.rollback(status);
            view ="redirect:boardDetail?b_num=" + b_num;
            msg = "삭제 실패";
        }
        rttr.addFlashAttribute("msg", msg);
        return view;
    }
    //파일 목록 안에있는 파일 지정 삭제
    private void deleteFiles(List<String> fList, HttpSession session) throws Exception {
        log.info("deleteFiles()");
        //파일 위치
        String realPath = session.getServletContext().getRealPath("/");
        realPath += "upload/";

        for (String sn : fList){
            File file = new File(realPath + sn);
            if (file.exists() == true){//파일 유무 확인
                file.delete(); //파일을 삭제
            }
        }
    }

    //게시글 수정 처리
    public ModelAndView updateBoard(int b_num) {
        log.info("updateBoard()");
        ModelAndView mv = new ModelAndView();
        //게시글 내용 가져오기
        BoardDto board = bDao.selectBoard(b_num);
        //파일 목록 가져오기
        List<BoardFileDto> fList = bDao.selectFileList(b_num);
        //mv에 담기
        mv.addObject("board", board);
        mv.addObject("fList", fList);
        //템플릿 지정.
        mv.setViewName("updateForm");
        return mv;
    }

    //게시글 수정시 파일삭제 처리
    public List<BoardFileDto> delFile(BoardFileDto bFile, HttpSession session){
        log.info("delFile()");
        List<BoardFileDto> fList = null;

        //파일 경로 설정
        String realPath = session.getServletContext().getRealPath("/");
        realPath += "upload/"+bFile.getBf_sysname();

        try {
            //파일 삭제
            File file = new File(realPath);
            if (file.exists()){
                if (file.delete()){
                    //해당 파일 정보 삭제(DB)
                    bDao.deleteFile(bFile.getBf_sysname());
                    //나머지 파일 목록 다시 가져오기
                    fList = bDao.selectFileList(bFile.getBf_bnum());
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return fList;
    }

    //게시글 수정 완료 후 이동할 페이지 처리 메소드
    public String updateBoard(List<MultipartFile> files, BoardDto board, HttpSession session, RedirectAttributes rttr){
        log.info("updateBoard()");

        TransactionStatus status = manager.getTransaction(definition);

        String view = null;
        String msg = null;

        try {
            bDao.updateBoard(board);
            fileUpload(files, session, board.getB_num());

            manager.commit(status);
            view = "redirect:boardDetail?b_num=" + board.getB_num();
            msg = "수정 성공";
        }catch (Exception e){
            e.printStackTrace();
            manager.rollback(status);
            view = "redirect:updateForm?b_num="+board.getB_num();
            msg = "수정 실패";
        }
        rttr.addFlashAttribute("msg", msg);
        return view;
    }

    //댓글 작성 처리
    public ReplyDto replyInsert(ReplyDto reply) {
        log.info("replyInsert()");

        try {
            bDao.insertReply(reply);
            reply = bDao.selectLastReply(reply.getR_num());
        }catch (Exception e){
            e.printStackTrace();
            reply = null;
        }
        return reply;
    }
}//class end































