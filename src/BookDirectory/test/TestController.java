package BookDirectory.test;

import BookDirectory.Controller;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Тестовый класс для проверки методов класса Controller.
 * @see BookDirectory.Controller
 */
public class TestController {
    Controller tester = BookDirectory.Controller.getInstance();

    @Test
    public void testBookValuesCheck_10Isbn10Title10Author_trueReturned(){
        //arrange
        String isbn = "test-isbn1";
        String title = "test-title";
        String author = "test-author";
        boolean expected = true;

        //act
        boolean result = tester.checkBookValues(isbn, title, author);

        //assert
        assertTrue("Wrong book values", expected==result);
    }

    @Test
    public void testIllustrationValuesCheck_10Id10Name10Author_trueReturned(){
        //arrange
        String id = "test-id123";
        String name = "test-title";
        String author = "test-author";
        boolean expected = true;

        //act
        boolean result = tester.checkIllustrationValues(id, name, author);

        //assert
        assertTrue("Wrong illustration values", result==expected);
    }
}