package BookDirectory;

import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Класс используется как менеджер для работы с бд derby
 * @author dyakonov
 * @version 1.2
 */
public class DerbyDBManager{

    /**
     * Параметры б.д.: драйвер, расположение, название, размеры полей таблиц.
     */
    private static Connection con = null ;
    private static final String DRIVER = Model.getInstance().getDRIVER();
    private static final String URL = Model.getInstance().getURL();
    private static String dbName = Model.getInstance().getDB_NAME();
    private static final Integer BOOK_ISBN_SIZE = Model.getInstance().getBookIsbnSize();
    private static final Integer BOOK_TITLE_SIZE = Model.getInstance().getBookTitleSize();
    private static final Integer BOOK_AUTHOR_SIZE = Model.getInstance().getBookAuthorSize();
    private static final Integer ILLUSTRATION_ID_SIZE = Model.getInstance().getIllustrationIdSize();
    private static final Integer ILLUSTRATION_NAME_SIZE = Model.getInstance().getIllustrationNameSize();
    private static final Integer ILLUSTRATION_AUTHOR_SIZE = Model.getInstance().getBookAuthorSize();

    /**
     * "Логгер" класса.
     */
    private static final Logger LOGGER = Logger.getLogger(Model.class.getName());



    /**
     * Конструктор - менеджер для работы с б.д. derby.
     * Если бд не существует - создаёт новую б.д. и таблицы в ней.
     */
    public DerbyDBManager(){
            if(!dbExists())
            {
                String sqlCreateTableBook = "CREATE TABLE books(isbn VARCHAR("+ BOOK_ISBN_SIZE +") UNIQUE, title VARCHAR("+ BOOK_TITLE_SIZE +"), author VARCHAR("+ BOOK_AUTHOR_SIZE +"))";
                String sqlCreateTableIllustration = "CREATE TABLE illustrations(isbn VARCHAR("+ BOOK_ISBN_SIZE +"), imageId VARCHAR("+ ILLUSTRATION_ID_SIZE +"),  name VARCHAR("+ ILLUSTRATION_NAME_SIZE +"), author VARCHAR("+ ILLUSTRATION_AUTHOR_SIZE +") )";
                try
                {
                    Class.forName(DRIVER) ;
                    con = DriverManager.getConnection(URL + dbName + ";create=true");
                    executeUpdate(sqlCreateTableBook);
                    executeUpdate(sqlCreateTableIllustration);
                    View.getInstance().printMessage(11);
                }
                catch (ClassNotFoundException e)
                {
                    View.getInstance().printErrorText(0);
                    LOGGER.log(Level.SEVERE, "ClassNotFoundException: " + e.toString() + "");
                }
                catch (SQLException e)
                {
                    View.getInstance().printErrorText(2);
                    LOGGER.log(Level.SEVERE, "SQLException: " + e.toString() + "", sqlCreateTableBook);
                }
            }
    }

    /**
     * Служебный метод для проверки существования бд.
     * @return Boolean: true если б.д. существует, иначе - false;
     */
    private Boolean dbExists(){
        Boolean exists = false;
        try
        {
            Class.forName(DRIVER);
            con = DriverManager.getConnection(URL + dbName);
            exists = true ;
        }
        catch(Exception e)
        {
            View.getInstance().printMessage(14);
            if(LOGGER.isLoggable(Level.WARNING)){
                LOGGER.log(Level.WARNING, "Creation of a new database!");}

        }
        return(exists);
    }

    /**
     * Запрос на обновление базы данных  (INSERT, UPDATE, CREATE TABLE и т.д.)
     * @param sql SQL запрос
     * @throws SQLException Ошибки SQL
     */
    public void executeUpdate(String sql) throws SQLException{
        Statement stmt = con.createStatement();
        int count = stmt.executeUpdate(sql);
        if(LOGGER.isLoggable(Level.FINE)){
            LOGGER.log(Level.FINE, "The count of executeUpdate is: ", count);}
        stmt.close();
    }

    /**
     * Метод для выборки данных из б.д.
     * @param sql SQL запрос
     * @return ResultSet - Результат выборки
     * @throws SQLException - Ошибки SQL
     */
    public ResultSet executeQuery(String sql) throws SQLException{
            Statement stmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            return stmt.executeQuery(sql);
    }

    /**
     * Метод для удаления объекта "Книга" из б.д.
     * @param isbn String - Isbn книги для удаления.
     * @throws SQLException Ошибки SQL
     */
    public void deleteQuery(String isbn) throws SQLException{
        Statement sta = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
        ResultSet result = sta.executeQuery("SELECT * FROM books");
        if (result.getConcurrency() == ResultSet.CONCUR_READ_ONLY){
            View.getInstance().printErrorText(19);
            LOGGER.severe("The table book is blocked!");
        }
        else {
            while (result.next()){
                String isbnDB = result.getString(1);
                if (isbnDB.equals(isbn)){
                    result.deleteRow();
                    if(LOGGER.isLoggable(Level.FINE)){
                        LOGGER.log(Level.FINE, "The book was deleted from database!", isbn);}
                     break;
                }
            }
        }
        result.close();
        sta.close();
        con.close();
    }

    /**
     * Метод для удаления иллюстрации из б.д.
     * Примечание: иллюстрация будет удалена из всех книг, где она используется.
     * @param id String - Id иллюстрации для удаления
     * @throws SQLException - Ошибки SQL
     */
    public void deleteIllustration(String id) throws SQLException{
        Boolean success = false;
        Statement sta = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
        ResultSet result = sta.executeQuery("SELECT * FROM illustrations");
        if (result.getConcurrency() == ResultSet.CONCUR_READ_ONLY){
            View.getInstance().printErrorText(19);
            LOGGER.severe("The table illustrations is blocked!");
        }
        else {
            while (result.next()){
                String idDB = result.getString(2);
                if (idDB.equals(id)){
                    result.deleteRow();
                    success = true;
                    if(LOGGER.isLoggable(Level.FINE)){
                        LOGGER.log(Level.FINE, "The illustration was deleted from database!", id);}
                }
            }
            if(success){
                View.getInstance().printMessage(12);
            }
            else {View.getInstance().printErrorText(9);}
        }
        result.close();
        sta.close();
        con.close();
    }

    /**
     * Метод для поиска всех иллюстраций книги в б.д.
     * @param isbn String - Isbn книги, иллюстрации которой нужно найти.
     * @return ArrayList<String> - каждый элемент коллекции (ArrayList) - это параметры иллюстрации(String[3]).
     * Сколько у книги иллюстрации - столько и элементов в коллекции.
     * @throws SQLException - Ошибки SQL
     */
    public ArrayList<String[]> searchIllustrationsQuery(String isbn) throws SQLException{
        ArrayList<String[]> arrayList = new ArrayList<String[]>();
        Statement sta = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
        ResultSet resultIsbn = sta.executeQuery("SELECT * FROM illustrations");
        if (resultIsbn.getConcurrency() == ResultSet.CONCUR_READ_ONLY){
            View.getInstance().printErrorText(19);
            LOGGER.severe("The table illustrations is blocked!");
        }
        else {
            while (resultIsbn.next()){
                String isbnDB = resultIsbn.getString(1);
                if (isbnDB.equals(isbn)){
                    String[] row = new String[3];
                    for(int i = 2; i <= 4; i++){
                        row[i-2] = resultIsbn.getString(i);
                    }
                    arrayList.add(row);
                }
            }
        }
        resultIsbn.close();
        sta.close();
        con.close();
        return arrayList;
    }
}