package org.zerock.web;

import org.zerock.dao.MovieDAO;
import org.zerock.domain.MovieVO;
import org.zerock.domain.PageDTO;
import org.zerock.domain.PageMaker;
import org.zerock.web.util.Converter;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet(urlPatterns = "/movie/*")
public class MovieController extends AbstractController {

    private MovieDAO dao = new MovieDAO();
    boolean updateable = false;

    public String writeGET(HttpServletRequest req, HttpServletResponse resp)throws Exception{

        return "write";
    }

    public String writePOST(HttpServletRequest req, HttpServletResponse resp)throws Exception{
        req.setCharacterEncoding("UTF-8");
        MovieDAO dao = new MovieDAO();

        String mtitle = req.getParameter("mtitle");
        String userid = req.getParameter("userid");
        String rtitle = req.getParameter("rtitle");
        String cmt = req.getParameter("cmt");
        String mimg = req.getParameter("mimg");
        MovieVO vo = new MovieVO();
        vo.setCmt(cmt);
        vo.setMimg(mimg);
        vo.setUserid(userid);
        vo.setRtitle(rtitle);
        vo.setMtitle(mtitle);
        dao.addReview(vo);

        return "redirect/list";

    }

    public String listGET(HttpServletRequest req, HttpServletResponse resp)throws Exception{
        System.out.println("listGET.......................");

        PageDTO dto = PageDTO.of()
                .setPage(Converter.getInt(req.getParameter("page"),1))
                .setSize(Converter.getInt(req.getParameter("size"),10));

        PageMaker pageMaker = new PageMaker(dao.getMaxRno(),dto);
        req.setAttribute("pageMaker",pageMaker);
        req.setAttribute("list",dao.getList(dto));

        return "list";
    }

    public String readGET(HttpServletRequest req, HttpServletResponse resp)throws Exception{
        System.out.println("readGET.......................");

        String mnoStr = req.getParameter("mno");
        int mno = Converter.getInt(mnoStr, -1);

        if(mno == -1){ throw new Exception(("Invalid data")); }

        Cookie[] cookies = req.getCookies();  //쿠키확인
        Cookie viewCookie = null;
        for(Cookie ck:cookies){
            if(ck.getName().equals("views")){
                viewCookie = ck;
                break;
            }
        }
        //쿠키가 있다면


        //쿠키가 없다면
        if(viewCookie == null){
            Cookie newCookie = new Cookie("views",mnoStr+"%");
            newCookie.setMaxAge(60*60*24);  //유통기한
            viewCookie = newCookie;
            updateable = true;
        }else{
            String cookieValue = viewCookie.getValue();
            System.out.println("cookieValue"+cookieValue);
            updateable = cookieValue.contains(mnoStr+'%')==false;

            if(updateable){
                cookieValue += mnoStr + "%";
                viewCookie.setValue(cookieValue);  //갱신한 값을 다시 넣어줘야함
            }
            System.out.println("cookieValue after: " +cookieValue);

        }
        System.out.println("-------------------------------------");
        System.out.println(viewCookie);
        resp.addCookie(viewCookie);

        MovieVO vo = dao.getReview(mno,updateable);

        req.setAttribute("movie",vo);


        return "read";
    }

    public String modifyGET(HttpServletRequest req, HttpServletResponse resp)throws Exception{

        int mno = Integer.parseInt(req.getParameter("mno"));
        MovieVO vo = dao.getReview(mno,updateable);
        req.setAttribute("movie", vo);

        return "modify";
    }

    public String modifyPOST(HttpServletRequest req, HttpServletResponse resp)throws Exception{

        req.setCharacterEncoding("UTF-8");

        String mnoStr = req.getParameter("mno");
        String mtitle = req.getParameter("mtitle");
        String userid = req.getParameter("userid");
        String rtitle = req.getParameter("rtitle");
        String cmt = req.getParameter("cmt");
        String mimg = req.getParameter("mimg");
        int page = Integer.parseInt(req.getParameter("page"));
        int mno = Integer.parseInt(mnoStr);

//        MovieVO vo = new MovieVO();
        MovieVO vo = dao.getReview(mno,updateable);
        vo.setCmt(cmt);
        vo.setMimg(mimg);
        vo.setUserid(userid);
        vo.setRtitle(rtitle);
        vo.setMtitle(mtitle);
        dao.updateReview(mno,vo);
        return "redirect/list?page="+page;
    }

    public String removePOST(HttpServletRequest req, HttpServletResponse resp)throws Exception{
        int mno = Integer.parseInt(req.getParameter("mno"));
        dao.deleteReview(mno);           //수정해야함
        return "redirect/list";
    }

    public String getBasic() {
        return "/movie/";
    }
}