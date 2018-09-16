import org.junit.Test;
import org.zerock.dao.MovieDAO;
import org.zerock.domain.PageDTO;

public class test {

    @Test
    public void test() throws Exception {
         MovieDAO dao = new MovieDAO();
        PageDTO dto = new PageDTO();
        dao.getList(dto);
//        System.out.println(dao.getList(dto));



    }
}
