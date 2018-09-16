package org.zerock.dao;




import org.zerock.domain.MovieVO;
import org.zerock.domain.PageDTO;

import java.util.ArrayList;
import java.util.List;


public class MovieDAO {

    private static final String LIST = "select *\n" +
            "from (select\n" +
            "             /*+INDEX_DESC(tbl_movie_review pk_movie_review) */\n" +
            "          ROWNUM rn, mno, mtitle, userid, rtitle,cmt, mimg, REGDATE,views  \n" +
            "      from TBL_movie_review\n" +
            "      where MNO > 0\n" +
            "        AND ROWNUM <= (? * ?))\n" +
            "where rn > ( ? -1) * ?";

    private static final String MAXRNO = "select count(*) from tbl_movie_review";

    private static final String UPDATE_HIT = "update tbl_movie_review set views = views +1 where mno = ?";

    private static final String READ = "select * from tbl_movie_review where mno = ?";

    public List<MovieVO> getList(final PageDTO pageDTO) throws Exception{

        final List<MovieVO> list = new ArrayList<>();
        new QueryExecutor() {
            public void doJob() throws Exception {
                stmt = con.prepareStatement(LIST);
                int i = 1;
                stmt.setInt(i++, pageDTO.getPage());
                stmt.setInt(i++, pageDTO.getSize());
                stmt.setInt(i++, pageDTO.getPage());
                stmt.setInt(i++, pageDTO.getSize());
                rs = stmt.executeQuery();

                while (rs.next()) {
                    MovieVO vo = new MovieVO();
                    int idx = 2;
                    vo.setMno(rs.getInt(idx++));
                    vo.setMtitle(rs.getString(idx++));
                    vo.setUserid(rs.getString(idx++));
                    vo.setRtitle(rs.getString(idx++));
                    vo.setCmt(rs.getString(idx++));
                    vo.setMimg(rs.getString(idx++));
                    vo.setRegdate(rs.getDate(idx++));
                    vo.setViews(rs.getInt(idx++));

                    list.add(vo);
                }
            }
        }.executeAll();
        return list;
    }

    public int getMaxRno(){
        final int[] maxRno = {0};
        new QueryExecutor() {
            @Override
            public void doJob() throws Exception {
                stmt = con.prepareStatement(MAXRNO);
                rs = stmt.executeQuery();
                rs.next();
                maxRno[0] = rs.getInt(1);

            }
        }.executeAll();

        return maxRno[0];
    }

    public void updateView(final int mno){

        final String sql = "update tbl_movie_review \n" +
                "set views=views+1\n" +
                "where mno=?";

        new QueryExecutor() {
            @Override
            public void doJob() throws Exception {
                stmt = con.prepareStatement(sql);
                stmt.setInt(1, mno);
                stmt.executeUpdate();
            }
        }.executeAll();
    }

    public MovieVO getReview(final int mno, final boolean updateable) {
        final MovieVO vo = new MovieVO();

        new QueryExecutor() {
            public void doJob() throws Exception {
                if(updateable) {
                    stmt = con.prepareStatement(UPDATE_HIT);
                    stmt.setInt(1, mno);
                    stmt.executeUpdate();
                    stmt.close();
                }
                stmt = con.prepareStatement(READ);
                stmt.setInt(1,mno);
                rs = stmt.executeQuery();
                while(rs.next()){
                    vo.setMno(rs.getInt("mno"));
                    vo.setMimg(rs.getString("mimg"));
                    vo.setCmt(rs.getString("cmt"));
                    vo.setMtitle(rs.getString("mtitle"));
                    vo.setUserid(rs.getString("userid"));
                    vo.setRtitle(rs.getString("rtitle"));
                    vo.setRegdate(rs.getDate("regdate"));
                }
            }
        }.executeAll();

        //code
        return vo;
    }

    public void addReview(final MovieVO vo) {
        final String sql = "insert into tbl_movie_review (mno, mtitle, userid, rtitle, cmt, mimg)\n" +
                "values ( seq_movie_review.nextval, ?, ?, ?, ?, ?)";
        //mnum, score, cmt
        new QueryExecutor() {
            public void doJob() throws Exception {
                stmt = con.prepareStatement(sql);
                stmt.setString(1,vo.getMtitle());
                stmt.setString(2,vo.getUserid());
                stmt.setString(3,vo.getRtitle());
                stmt.setString(4,vo.getCmt());
                stmt.setString(5,vo.getMimg());
                stmt.executeUpdate();
            }
        }.executeAll();
    }

    public void deleteReview(final int mno) {
        final String sql = "delete from tbl_movie_review where mno = ?";

        new QueryExecutor() {
            public void doJob() throws Exception {
                stmt = con.prepareStatement(sql);
                stmt.setInt(1,mno);
                stmt.executeUpdate();
            }
        }.executeAll();
    }

    public void updateReview(final int mno,final MovieVO vo) {
        final String sql = "update tbl_movie_review set mtitle = ?,userid = ?,rtitle = ?, cmt = ?, mimg = ?, regdate = sysdate where mno= ?";

        new QueryExecutor() {
            public void doJob() throws Exception {
                stmt = con.prepareStatement(sql);
                stmt.setString(1,vo.getMtitle());
                stmt.setString(2,vo.getUserid());
                stmt.setString(3,vo.getRtitle());
                stmt.setString(4,vo.getCmt());
                stmt.setString(5,vo.getMimg());
                stmt.setInt(6,mno);
                stmt.executeUpdate();
            }
        }.executeAll();
    }

}
