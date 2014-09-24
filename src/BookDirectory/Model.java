package BookDirectory;

import com.sun.istack.internal.Nullable;

import java.io.*;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.Properties;
import java.util.logging.*;

/**
 * Класс "Модель" - источник данных (модель MVC).
 * Паттерн Singleton.
 *
 * @author dyakonov
 * @version 1.3
 */
public final class Model {
    /**
     * Значение этого поля устанавливает в каком режиме работает программа.
     * Если true - режим разработки (удобно в IDE), если false - рабочая система.
     */
    private boolean developmentMode;

    /**
     * Строка - драйвер JDBC.
     */
    private String DRIVER;

    /**
     * Url JDBC.
     */
    private String URL;

    /**
     * Директория, в которой находится база данных.
     */
    private String DB_NAME;

    /**
     * Максимальная длина isbn книги.
     */
    private Integer BOOK_ISBN_SIZE;

    /**
     * Максимальная длина названия книги.
     */
    private Integer BOOK_TITLE_SIZE;

    /**
     * Максимальная длина поля автор книги.
     */
    private Integer BOOK_AUTHOR_SIZE;

    /**
     * Максимальная длина поля id иллюстрации.
     */
    private Integer ILLUSTRATION_ID_SIZE;

    /**
     * Максимальная длина поля название иллюстрации.
     */
    private Integer ILLUSTRATION_NAME_SIZE;

    /**
     * Максимальная длина поля автор иллюстрации.
     */
    private Integer ILLUSTRATION_AUTHOR_SIZE;

    /**
     * Уровень для записей логгера в приложении.
     */
    private Level LOG_LEVEL;

    /**
     * Статическая переменная класса для хранения единственного экземпляра класса
     */
    private static Model instance;

    /**
     * Конструктор класса с закрытым доступом (private).
     */
    private Model() {
    }

    /**
     * "Геттер" для получения экземляра класса
     *
     * @return Единственный экземпляр класса Model.
     */
    public static Model getInstance() {
        if (instance == null) {
            instance = new Model();
        }
        return instance;
    }

    /**
     * Символ-обозначение переноса строки в системе.
     */
    private final String NR = System.getProperty("line.separator");

    /**
     * Кодировка система, в которой запущено приложение.
     */
    private final String ENCODING_VALUE = encoding();

    /**
     * "Логгер" класса.
     */
    private final Logger LOGGER = Logger.getLogger(Model.class.getName());

    /**
     * Метод для получения кодировки в системе.
     *
     * @return String - формат кодировки в системе.
     */
    public String encoding() {
        String encoding = "";
        try {
            final Class<Console> clazz = Console.class;
            final Method method = clazz.getDeclaredMethod("encoding", new Class[0]);
            method.setAccessible(true);
            encoding = (String) method.invoke(null);
        } catch (NoSuchMethodException e) {
            View.getInstance().printErrorText(20);
            LOGGER.log(Level.SEVERE, "Exception: NoSuchMethod" + e.toString() + "");
        } catch (Exception e) {
            View.getInstance().printErrorText(0);
            LOGGER.log(Level.SEVERE, "Application Exception:" + e.toString() + "");
        }
        return encoding;
    }

    /**
     * "Геттер" для получения формата кодировки в системе.
     *
     * @return String - формат кодировки в системе.
     */
    protected String getEncoding() {
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(Level.FINE, "System encoding is: ", ENCODING_VALUE);
        }
        return ENCODING_VALUE;
    }

    /**
     * Метод определяет в какой системе запущено приложение и если это Windows - меняет кодировку всех сообщений.
     *
     * @return Boolean: true - если система Windows, иначе - false.
     */
    protected boolean setLocaleWindows() {
        boolean result = false;
        if (!isDevelopmentMode()) {
            try {
                if (System.getProperty("os.name").startsWith("Windows")) {
                    System.setOut(new PrintStream(System.out, true, getEncoding()));
                    result = true;
                }
                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.log(Level.FINE, "System is: ", System.getProperty("os.name") + ", arch: " + System.getProperty("os.arch"));
                }
            } catch (UnsupportedEncodingException e) {
                View.getInstance().printErrorText(21);
                LOGGER.log(Level.SEVERE, "Exception UnsupportedEncoding: " + e.toString() + "");
            }
        }
        return result;
    }

    /**
     * Метод для ввода данных с клавиатуры
     *
     * @return String - строка, введённая с клавиатуры
     */
    protected String keyboardInput() {
        String input = "";
        BufferedReader reader;
        try {
            if (setLocaleWindows()) {
                reader = new BufferedReader(new InputStreamReader(System.in, getEncoding()));
            } else {
                reader = new BufferedReader(new InputStreamReader(System.in));
            }
            input = reader.readLine();
        } catch (IOException e) {
            View.getInstance().printErrorText(3);
            LOGGER.log(Level.SEVERE, "IOException: " + e.toString() + "", input);
        }
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(Level.FINE, "Input is: ", input);
        }
        return input;
    }

    /**
     * Метод для получения всех книг (и иллюстраций) в каталоге.
     *
     * @return ArrayList<Book> - коллекция объектов Book (книга).
     * @see BookDirectory.Book
     * @see BookDirectory.DerbyDBManager
     */
    protected LinkedList<Book> getBooks() {
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine("The call of Model.getBooks()");
        }
        long startTime = System.currentTimeMillis();
        LinkedList<Book> books = new LinkedList<Book>();
        String SQL = "SELECT * FROM books";
        try {
            DerbyDBManager db = new DerbyDBManager();
            ResultSet booksArray = db.executeQuery(SQL);
            while (booksArray.next()) {
                String isbn = booksArray.getString(1);
                ArrayList<String[]> illustrationsValues = getIllustrationsValues(isbn);
                try {
                    if (illustrationsValues.size() > 0) {
                        Book book = Book.getBook(isbn, booksArray.getString(2), booksArray.getString(3));
                        for (String[] illustrationValues : illustrationsValues) {
                            book.addIllustration(illustrationValues[0], illustrationValues[1], illustrationValues[2]);
                            //if(LOGGER.isLoggable(Level.FINE)){LOGGER.log(Level.FINE, "Illustration is added to catalog (id, isbn): ("+illustrationValues[0]+","+isbn+")");}
                        }
                        books.add(book);
                    } else {
                        books.add(Book.getBook(isbn, booksArray.getString(2), booksArray.getString(3)));
                        //if(LOGGER.isLoggable(Level.INFO)){LOGGER.log(Level.INFO, "Book is added to catalog(isbn): "+isbn+"");}
                    }
                } catch (Exception e) {
                    View.getInstance().printErrorText(0);
                    LOGGER.log(Level.SEVERE, "Application Exception:" + e.toString() + "");
                }
            }
            long endTime = System.currentTimeMillis();
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.log(Level.FINE, "Time spent on the output the catalog (in ms):", endTime - startTime);
            }
            return books;
        } catch (SQLException e) {
            View.getInstance().printErrorText(2);
            LOGGER.log(Level.SEVERE, "SQLException: " + e.toString() + "", SQL);
        }
        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.log(Level.INFO, "Books in catalog: " + books.size() + "");
        }
        return books;
    }

    /**
     * Находит иллюстрации для определённой книги
     *
     * @param isbn String - isbn книги, иллюстрации которой нужно найти
     * @return ArrayList<String> - коллекцию массивов String[3]. Каждый массив это 3 параметра иллюстрации (id, название, автор).
     * @see BookDirectory.DerbyDBManager
     */
    protected ArrayList<String[]> getIllustrationsValues(String isbn) {
        ArrayList<String[]> arrayList = new ArrayList<String[]>();
        try {
            DerbyDBManager db = new DerbyDBManager();
            arrayList = db.searchIllustrationsQuery(isbn);
        } catch (SQLException e) {
            View.getInstance().printErrorText(2);
            LOGGER.log(Level.SEVERE, "SQLException: " + e.toString() + "");
        }
        return arrayList;
    }

    /**
     * Находит объект Book (книга)
     *
     * @param isbn String - ISBN книги, которую нужно найти.
     * @return Book - объект Book (если книга есть в каталоге), иначе - null.
     * @see BookDirectory.Book
     */
    @Nullable
    protected Book getBookByISbn(String isbn) {
        Book book = null;
        for (Book bookInCatalog : getBooks()) {
            if (bookInCatalog.getIsbn().equals(isbn)) {
                book = bookInCatalog;
            }
        }
        if (book == null) {
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.info("Book is null!");
            }
        }
        return book;
    }

    /**
     * Метод для добавления новой книги в базу данных.
     *
     * @param isbn   String - Isbn книги
     * @param title  String - Название книги
     * @param author String - Автор Книги
     * @see BookDirectory.DerbyDBManager
     */
    protected void addBook(String isbn, String title, String author) {
        String SQL = "INSERT INTO books (isbn, title, author) VALUES('" + isbn + "' , '" + title + "' , '" + author + "')";
        try {
            DerbyDBManager db = new DerbyDBManager();
            db.executeUpdate(SQL);
            View.getInstance().printMessage(0);
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.info("Book added in db!");
            }
        } catch (NullPointerException e) {
            View.getInstance().printErrorText(2);
            LOGGER.log(Level.SEVERE, "Exception: NullPointer");
        } catch (SQLIntegrityConstraintViolationException e) {
            View.getInstance().printErrorText(4);
            LOGGER.log(Level.SEVERE, "Exception: duplicate isbn in database", isbn);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Exception_SQL:" + e.toString() + "", SQL);
            View.getInstance().printErrorText(2);
        }
    }

    /**
     * Метод для добавления новой иллюстрации в б.д.
     * При добавлении проверяет, чтобы у одной книги не было двух иллюстраций с одинаковым id.
     *
     * @param isbn    String - Isbn книги, к которой добавляем иллюстрацию.
     * @param imageId String - Id добавляемой иллюстрации.
     * @param name    String - Название добавляемой иллюстрации.
     * @param author  String - Автор добавляемой иллюстрации.
     * @see BookDirectory.DerbyDBManager
     */
    protected void addIllustration(String isbn, String imageId, String name, String author) {
        String SQL = "INSERT INTO illustrations (isbn, imageId, name, author) VALUES('" + isbn + "' , '" + imageId + "' ,  '" + name + "' , '" + author + "')";
        try {
            Book book = getBookByISbn(isbn);
            if (book != null) {
                if (book.getIllustrations().size() > 0) {
                    for (Book.Illustration illustration : book.getIllustrations()) {
                        if (!illustration.getId().equals(imageId)) {
                            DerbyDBManager db = new DerbyDBManager();
                            db.executeUpdate(SQL);
                            View.getInstance().printMessage(2);
                            break;
                        } else {
                            View.getInstance().printErrorText(6);
                        }
                    }
                } else {
                    DerbyDBManager db = new DerbyDBManager();
                    db.executeUpdate(SQL);
                    View.getInstance().printMessage(2);
                }
                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.log(Level.FINE, "Illustration is added!");
                }
            }
        } catch (SQLException e) {
            View.getInstance().printErrorText(2);
            LOGGER.log(Level.SEVERE, "SQLException: " + e.toString() + "", SQL);
        }
    }

    /**
     * Метод для удаления книги из б.д.
     * Если у книги есть иллюстрации - удаляет и их.
     *
     * @param isbn Isbn книги, которая подлежит удалению.
     * @return Boolean, показывающий была ли удалена книга из б.д.
     * @see BookDirectory.DerbyDBManager
     */
    protected boolean deleteBook(String isbn) {
        boolean success = false;
        try {
            Book book = getBookByISbn(isbn);
            if (book != null) {
                if (book.getIllustrations().size() > 0) {
                    for (Book.Illustration illustration : book.getIllustrations()) {
                        deleteIllustrationById(illustration.getId());
                    }
                    View.getInstance().printMessage(3);
                } else {
                    View.getInstance().printMessage(4);
                }
                DerbyDBManager db = new DerbyDBManager();
                db.deleteQuery(isbn);
                success = true;
            }
        } catch (SQLException eSQL) {
            View.getInstance().printErrorText(2);
            LOGGER.log(Level.SEVERE, "SQLException: " + eSQL.toString() + "");
        }
        return success;
    }

    /**
     * Метод для удаления иллюстрации из б.д.
     * Удаляет иллюстрацию из всех книг.
     *
     * @param id String - Id иллюстрации, подлежащей удалению.
     * @see BookDirectory.DerbyDBManager
     */
    public void deleteIllustrationById(String id) {
        try {
            DerbyDBManager db = new DerbyDBManager();
            db.deleteIllustration(id);
        } catch (SQLException eSQL) {
            View.getInstance().printErrorText(2);
            LOGGER.log(Level.SEVERE, "SQLException: " + eSQL.toString() + "");
        }
    }

    /**
     * Метод проверяет, есть ли книги в каталоге.
     *
     * @return Boolean - true, если книги есть, иначе - false.
     * @see #getBooks()
     */
    public boolean checkAvailabilityBooks() {
        boolean result;
        LinkedList<Book> linkedList = getBooks();
        if (linkedList.isEmpty()) {
            View.getInstance().printMessage(5);
            result = false;
        } else {
            result = true;
        }
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(Level.FINE, "In the directory there are books?", result);
        }
        return result;
    }

    /**
     * Метод для экспорта одной книги (и её иллюстраций) в текстовый файл.
     *
     * @param book Book - объект Book
     * @return Boolean - результат экспорта. Если успешно - true, иначе - false;
     * @see BookDirectory.Book
     * @see BookDirectory.FileInOut#writeFile(String, String, String)
     */
    public boolean writeBookExport(Book book) {
        boolean result = false;
        String textExport = "[Книга]" + NR + book.getIsbn() + NR;
        textExport += book.getName() + NR;
        textExport += book.getAuthor() + NR + NR;
        if (book.getIllustrations().size() > 0) {
            for (Book.Illustration illustration : book.getIllustrations()) {
                textExport += "[Иллюстрация]" + NR + illustration.getId() + NR;
                textExport += illustration.getIsbn() + NR;
                textExport += illustration.getName() + NR;
                textExport += illustration.getAuthor() + NR + NR;
            }
        }
        if (FileInOut.getInstance().writeFile("export_" + book.getIsbn() + ".txt", "export", textExport)) {
            result = true;
        }
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(Level.FINE, "Export of the book was complete successfully?", result);
        }
        return result;
    }

    /**
     * Метод для экспорта всего каталога (всех книг и всех иллюстраций) в текстовый файл.
     *
     * @param fileName String - имя текстового файла, куда будет сохранён каталог.
     * @see BookDirectory.FileInOut#writeFile(String, String, String)
     */
    public void writeCatalogExport(String fileName) {
        String textExport = "";
        for (Book book : getBooks()) {
            textExport += "[Книга]" + NR + book.getIsbn() + NR;
            textExport += book.getName() + NR;
            textExport += book.getAuthor() + NR + NR;
            if (book.getIllustrations().size() > 0) {
                for (Book.Illustration illustration : book.getIllustrations()) {
                    textExport += "[Иллюстрация]" + NR + illustration.getId() + NR;
                    textExport += illustration.getIsbn() + NR;
                    textExport += illustration.getName() + NR;
                    textExport += illustration.getAuthor() + NR + NR;
                }
            }
        }
        if (FileInOut.getInstance().writeFile(fileName, "export", textExport)) {
            View.getInstance().printMessage(6);
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.log(Level.FINE, "Export of the catalog was successful!");
            }
        }
    }

    /**
     * Служебный метод для форматирования даты и времени.
     *
     * @param milliseconds Количество миллисекунд. Обычно для указания этого параметра используется getMillis()
     * @return String - дата и время в формате "сен 18,2014 10:34"
     */
    private static String calcDate(long milliseconds) {
        SimpleDateFormat date_format = new SimpleDateFormat("MMM dd,yyyy HH:mm");
        Date resultDate = new Date(milliseconds);
        return date_format.format(resultDate);
    }

    /**
     * Метод для инициализации java LOGGER и указания ему двух файлов для записи: текстовый и html.
     *
     * @return boolean: true, если LOGGER запустился без ошибок, иначе - false.
     */
    protected boolean startLogger(String logsDirectory) {
        boolean result = false;
        try {
            LOGGER.setUseParentHandlers(false);
            LOGGER.setLevel(LOG_LEVEL);

            File file = new File(logsDirectory);
            if (!file.exists()) {
                FileInOut.getInstance().createNewDir(logsDirectory);
            }

            FileHandler fileHTML = new FileHandler(logsDirectory + "//" + "BookDirectoryLog.html");
            Formatter formatterHTML = new Model.HtmlFormatter();
            fileHTML.setFormatter(formatterHTML);
            LOGGER.addHandler(fileHTML);

            FileHandler fileHandler = new FileHandler(logsDirectory + "//" + "BookDirectoryLog.txt");
            fileHandler.setEncoding("UTF-8");
            Formatter formatTXT = new Model.TxtFormatter();
            fileHandler.setFormatter(formatTXT);
            LOGGER.addHandler(fileHandler);

            result = true;
        } catch (SecurityException e) {
            View.getInstance().printErrorText(23);
            LOGGER.log(Level.SEVERE, "Start Logger, SecurityException: " + e.toString() + "");
        } catch (IOException e) {
            View.getInstance().printErrorText(22);
            LOGGER.log(Level.SEVERE, "Start Logger, IOException: " + e.toString() + "");
        }
        return result;
    }

    /**
     * Служебный метод для перевода байты в МегаБайты
     *
     * @param bytes - количество Байт.
     * @return - Количество МегаБайт
     */
    public long bytesToMegabytes(long bytes) {
        long MEGABYTE = 1024L * 1024L;
        return bytes / MEGABYTE;
    }

    /**
     * Метод для загрузки конфигурационных параметров.
     *
     * @param fileName - имя файла(с расширением), откуда должны считываться параметры.
     */
    public void getProperties(String fileName) {
        Properties properties = new Properties();
        try {
            properties.load(new InputStreamReader(new FileInputStream(fileName), "UTF-8"));
            developmentMode = Boolean.parseBoolean(properties.getProperty("developmentMode"));
            DRIVER = properties.getProperty("dbDriver");
            URL = properties.getProperty("dbUrl");
            DB_NAME = properties.getProperty("dbName");
            BOOK_ISBN_SIZE = Integer.parseInt(properties.getProperty("bookIsbnSize"));
            BOOK_TITLE_SIZE = Integer.parseInt(properties.getProperty("bookTitleSize"));
            BOOK_AUTHOR_SIZE = Integer.parseInt(properties.getProperty("bookAuthorSize"));
            ILLUSTRATION_ID_SIZE = Integer.parseInt(properties.getProperty("illustrationIdSize"));
            ILLUSTRATION_NAME_SIZE = Integer.parseInt(properties.getProperty("illustrationNameSize"));
            ILLUSTRATION_AUTHOR_SIZE = Integer.parseInt(properties.getProperty("illustrationAuthorSize"));
            LOG_LEVEL = Level.parse(properties.getProperty("logLevel", "ALL"));
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.log(Level.FINE, "Properties have been loaded successfully");
            }
        } catch (FileNotFoundException e) {
            View.getInstance().printErrorText(14);
            LOGGER.log(Level.SEVERE, "File of properties not found: ", e.toString());
        } catch (UnsupportedEncodingException e) {
            View.getInstance().printErrorText(21);
            LOGGER.log(Level.SEVERE, "File of properties have unsupported encoding: ", e.toString());
        } catch (IOException e) {
            View.getInstance().printErrorText(22);
            LOGGER.log(Level.SEVERE, "When loading the configuration file error occurred input\\output", e.toString());
        }
    }

    /**
     * "Геттер" для получения значения максимальной длины поля isbn книги.
     *
     * @return Integer
     */
    public Integer getBookIsbnSize() {
        return BOOK_ISBN_SIZE;
    }

    /**
     * "Геттер" для получения значения максимальной длины поля название книги.
     *
     * @return Integer
     */
    public Integer getBookTitleSize() {
        return BOOK_TITLE_SIZE;
    }

    /**
     * "Геттер" для получения значения максимальной длины поля автора книги.
     *
     * @return Integer
     */
    public Integer getBookAuthorSize() {
        return BOOK_AUTHOR_SIZE;
    }

    /**
     * "Геттер" для получения значения максимальной длины поля id иллюстрации.
     *
     * @return Integer
     */
    public Integer getIllustrationIdSize() {
        return ILLUSTRATION_ID_SIZE;
    }

    /**
     * "Геттер" для получения значения максимальной длины поля название иллюстрации.
     *
     * @return Integer
     */
    public Integer getIllustrationNameSize() {
        return ILLUSTRATION_NAME_SIZE;
    }

    /**
     * "Геттер" для получения значения максимальной длины поля автор иллюстрации.
     *
     * @return Integer
     */
    public Integer getIllustrationAuthorSize() {
        return ILLUSTRATION_AUTHOR_SIZE;
    }

    /**
     * "Геттер" для получения режима, в котором работает приложение.
     *
     * @return true - если режим разработки, инача - false.
     */
    public boolean isDevelopmentMode() {
        return developmentMode;
    }

    /**
     * "Геттер" для получения драйвера JDBC.
     *
     * @return Строка драйвера.
     */
    public String getDRIVER() {
        return DRIVER;
    }

    /**
     * "Геттер" для получения url драйвера JDBC.
     *
     * @return Строка url драйвера JDBC.
     */
    public String getURL() {
        return URL;
    }

    /**
     * "Геттер" для получения названия директории, где находится бд.
     *
     * @return Строка - название директории.
     */
    public String getDB_NAME() {
        return DB_NAME;
    }

    /**
     * "Геттер" для получения уровня логирования в приложении.
     *
     * @return уровень логирования в приложении
     */
    public Level getLOG_LEVEL() {
        return LOG_LEVEL;
    }

    /**
     * Внутренний статичный класс для задания форматирования при выводе лог-записей в html-файл.
     *
     * @see java.util.logging.Formatter
     */
    protected static class HtmlFormatter extends Formatter {

        /**
         * Метод для задания формата вывода каждой записи лога.
         *
         * @param logRecord - запись лога.
         * @return String - отформатировання запись лога (html-код).
         * @see java.util.logging.Formatter#format(java.util.logging.LogRecord)
         */
        public String format(LogRecord logRecord) {
            StringBuilder buf = new StringBuilder();
            buf.append("<tr>\n");
            if (logRecord.getLevel().intValue() >= Level.WARNING.intValue()) {
                buf.append("\t<td style=\"color:red\">");
                buf.append("<b>");
                buf.append(logRecord.getLevel());
                buf.append("</b>");
            } else if (logRecord.getLevel().intValue() == Level.INFO.intValue()) {
                buf.append("\t<td style=\"color:green\">");
                buf.append("<b>");
                buf.append(logRecord.getLevel());
                buf.append("</b>");
            } else {
                buf.append("\t<td>");
                buf.append(logRecord.getLevel());
            }
            buf.append("</td>\n");
            buf.append("\t<td>");
            buf.append(calcDate(logRecord.getMillis()));
            buf.append("</td>\n");
            buf.append("\t<td>");
            buf.append(formatMessage(logRecord));
            buf.append("</td>\n");
            buf.append("\t<td>");
            if (logRecord.getParameters() != null) {
                for (Object parameter : logRecord.getParameters()) {
                    if (parameter.getClass() != Object.class) {
                        buf.append(parameter);
                    }
                }
            }
            buf.append("</td>\n");
            buf.append("</tr>\n");
            return buf.toString();
        }

        /**
         * Метод указывает, что должно быть в начале лог файла.
         *
         * @param h Handler
         * @return String - html-код.
         * @see java.util.logging.Formatter#getHead(java.util.logging.Handler)
         */
        @Override
        public String getHead(Handler h) {
            return "<!DOCTYPE html>\n<head>\n<style"
                    + "type=\"text/css\">\n"
                    + "table { width: 100% }\n"
                    + "th { font:bold 10pt Tahoma; }\n"
                    + "td { font:normal 10pt Tahoma; }\n"
                    + "h1 {font:normal 11pt Tahoma;}\n"
                    + "</style>\n"
                    + "</head>\n"
                    + "<body>\n"
                    + "<h1>" + (new Date()) + "</h1>\n"
                    + "<table border=\"0\" cellpadding=\"5\" cellspacing=\"3\">\n"
                    + "<tr align=\"left\">\n"
                    + "\t<th style=\"width:10%\">LogLevel</th>\n"
                    + "\t<th style=\"width:15%\">Time</th>\n"
                    + "\t<th style=\"width:30%\">Log Message</th>\n"
                    + "\t<th style=\"width:45%\">Value</th>\n"
                    + "</tr>\n";
        }

        /**
         * Метод указывает, что должно быть в конце лог-файла.
         *
         * @param h Handler
         * @return String - html-код.
         * @see java.util.logging.Formatter#getTail(java.util.logging.Handler)
         */
        @Override
        public String getTail(Handler h) {
            return "</table>\n</body>\n</html>";
        }
    }

    /**
     * Внутренний статичный класс для задания форматирования при выводе лог-записей в txt-файл.
     *
     * @see java.util.logging.Formatter
     */
    protected static class TxtFormatter extends Formatter {

        /**
         * Метод для задания формата вывода каждой записи лога.
         *
         * @param logRecord - - запись лога.
         * @return String - отформатировання запись лога.
         * @see java.util.logging.Formatter#format(java.util.logging.LogRecord)
         */
        public String format(LogRecord logRecord) {
            StringBuilder b = new StringBuilder();
            b.append("[Date & Time] ");
            b.append(calcDate(logRecord.getMillis()));
            b.append(" {Class}");
            b.append(logRecord.getSourceClassName());
            b.append(" {method}");
            b.append(logRecord.getSourceMethodName());
            b.append(" [");
            b.append(logRecord.getLevel());
            b.append("] ");
            b.append(logRecord.getMessage());
            if (logRecord.getParameters() != null) {
                for (Object parameter : logRecord.getParameters()) {
                    if (parameter.getClass() != Object.class) {
                        b.append(parameter);
                    }
                }
            }
            b.append(System.getProperty("line.separator"));
            return b.toString();
        }
    }
}