package BookDirectory.test;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.BeforeClass;
import static org.junit.Assert.*;

/**
 * Тестовый класс для проверки методов класса FileInOut.
 * @see BookDirectory.FileInOut
 */
public class TestFileInOut {
    static String fileName;
    static String directory;
    static String text;
    BookDirectory.FileInOut tester = BookDirectory.FileInOut.getInstance();

    @BeforeClass
    public static void setUp(){
        fileName = "test.txt";
        directory = "test";
        text = "It's test!";
    }

    @Test
    /**
     * Тестовый метод для проверки возможности записи\создания нового файла.
     */
    public void testWriteFile_fileTestTxtWriteInDirTest_trueReturned() throws Exception{
        //arrange
        boolean expected = true;

        //act
        boolean result = tester.writeFile(fileName, directory, text);

        //assert
        assertTrue("Write file error!", expected == result);
    }

    @Test(timeout = 100000)
    @Ignore
    /**
     * Тестовый метод для проверки чтения текстовой строки (записанной методом ранее) из файла.
     */
    public void testReadFile_testTxt_trueReturned() throws Exception{
        //arrange
        boolean expected = true;

        //act
        String textInFile = tester.readFile(fileName, directory);
        boolean result = textInFile.contains(text);

        //assert
        assertTrue("Read file error!", expected == result);
    }
}