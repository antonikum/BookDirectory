package BookDirectory;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Контроллер - обеспечивает связь между пользователем и системой (модель MVC).
 * Использует модель и представление для реализации необходимой реакции.
 * Паттерн Singleton.
 * @author dyakonov
 * @version 1.2
 */
public final class Controller
{

    /**
     * Статическая переменная класса для хранения единственного экземпляра класса
     */
    private static Controller instance;

    /**
     * Конструктор класса с закрытым доступом (private).
     */
    private Controller(){}

    /**
     * "Логгер" класса.
     */
    private final Logger LOGGER = Logger.getLogger(Model.class.getName());

    public static Controller getInstance(){
        if(instance == null){
            instance = new Controller();
        }
        return instance;
    }

    /**
     * Метод для вывода главного меню программы.
     * Когда пользователь выбирает пункт главного меню для дальнейшей работы - вызывает метод menuDraw.
     * @see BookDirectory.Controller#menuDraw(Integer)
     */
    public void mainMenuDraw(){
        try {
            boolean exitProgram = false;
            while (!exitProgram)
            {
                View.getInstance().printMenu(0);
                View.getInstance().printMessage(15);
                String inputMenu = Model.getInstance().keyboardInput();
                if (inputMenu.isEmpty()) exitProgram = true;
                else{
                    if(checkMenuItem('1', '6', inputMenu)){
                        Integer choice = Integer.parseInt(inputMenu);
                        View.getInstance().printMenu(choice);
                        menuDraw(choice);
                    }
                    else{
                        View.getInstance().printErrorText(1);
                        if(LOGGER.isLoggable(Level.FINE)){
                            LOGGER.log(Level.FINE, "Error in a menu selection");}
                    }
                }
            }
        }
        catch (Exception e){
            View.getInstance().printErrorText(0);
            LOGGER.log(Level.SEVERE, "Application Error: " + e.toString() + "");
        }
    }

    /**
     * Метод для вывода пунктов меню, запросов к пользователю и вызова соответствующих методов действий (от 1 до 6).
     * @param choice Integer - выбор пункта главного меню.
     */
    public void menuDraw(Integer choice){
        try {
            if(LOGGER.isLoggable(Level.FINE)){
                LOGGER.log(Level.FINE, "The call of menuDraw");}
            boolean returnMainMenu = false;
            while (!returnMainMenu){
                switch (choice){
                    case 1:{
                        firstMenuEngine();
                        returnMainMenu = backToMainMenu();
                        break;
                    }
                    case 2:{
                        secondMenuEngine();
                        returnMainMenu = backToMainMenu();
                        break;
                    }
                    case 3: {
                        thirdMenuEngine();
                        returnMainMenu = backToMainMenu();
                        break;
                    }
                    case 4:{
                        returnMainMenu = fourthMenuEngine();
                        break;
                    }
                    case 5:{
                        returnMainMenu = fifthMenuEngine();
                        break;
                    }
                    case 6:{
                        returnMainMenu = sixthMenuEngine();
                        break;
                    }
                }
            }
        }
        catch (NullPointerException e){
            View.getInstance().printErrorText(2);
            LOGGER.log(Level.SEVERE, "NullPointerException: " + e.toString() + "");
        }
        catch (Exception e){
            View.getInstance().printErrorText(0);
            LOGGER.log(Level.SEVERE, "Application Error: " + e.toString() + "");
        }
    }

    /**
     * Метод первого пункта главного меню - вывод списка книг в каталоге.
     * Проверяет наличие книг в каталоге и выводит их список при помощи метода getBooks() Модели.
     * @see Model#getBooks()
     */
    private void firstMenuEngine(){
        if(LOGGER.isLoggable(Level.FINE)){
            LOGGER.fine("Menu select: list of books(1)");}
        if (Model.getInstance().checkAvailabilityBooks()){
            for(Book book : Model.getInstance().getBooks()){
                System.out.println(book);
            }
            if(LOGGER.isLoggable(Level.FINE)){
                LOGGER.fine("The end of the list of books in the catalog");}
        }
    }

    /**
     * Метод второго пункта главного меню - добавления новой книги в каталог.
     * Параметры новой книги получает при помощи метода printAddBookMenu() представления.
     * Проверяет введённые параметры на допустимые значения при помощи методов checkBookValues() и checkIsbnForbidden().
     * @see View#printAddBookMenu()
     * @see #checkBookValues(String, String, String)
     * @see #checkIsbnForbidden(String)
     */
    private void secondMenuEngine(){
        if(LOGGER.isLoggable(Level.FINE)){
            LOGGER.fine("Menu select: add book(2)");}
        String[] inputBookValues = View.getInstance().printAddBookMenu();
        if(checkBookValues(inputBookValues[0], inputBookValues[1], inputBookValues[2])){
            if(checkIsbnForbidden(inputBookValues[0])){
                Model.getInstance().addBook(inputBookValues[0], inputBookValues[1], inputBookValues[2]);
            }
        }
    }

    /**
     * Метод третьего пункта главного меню - удаления книги из каталога.
     * Удаляет книгу по введённому пользователем с клавиатуры isbn при помощи метода deleteBook Модели.
     * @see Model#keyboardInput()
     * @see BookDirectory.Model#deleteBook(String)
     */
    private void thirdMenuEngine(){
        if(LOGGER.isLoggable(Level.FINE)){
            LOGGER.fine("Menu select: delete book(3)");}
        String isbn = Model.getInstance().keyboardInput();
        if (Model.getInstance().deleteBook(isbn)) {
            View.getInstance().printMessage(9);
        }
        else{
            View.getInstance().printErrorText(5);
            System.out.println("(" + isbn + ").");
            if(LOGGER.isLoggable(Level.FINE)){
                LOGGER.fine("Book for removal was not found in the catalog");}
        }
    }

    /**
     * Метод четвёртого пункта главного меню - работа с иллюстрациями.
     * Подпункты: 1 - Добавить иллюстрацию; 2 - Удалить иллюстрацию; 3 - Поиск книг по иллюстрации; 4 - Список иллюстраций книги;
     *
     * При добавлении иллюсстрации: а) проверяет есть ли книги в каталоге; б) запрашивает количество книг и их isbn, к которым будет добавлена иллюстрация;
     * в) проверяет isbn книг на наличие в каталоге; г) проверяет введённые параметры иллюстрации на допустимые значения; д) проверяет, нет ли уже у введённых книг иллюстрации с таким id;
     * е) добавляет иллюстрацию.
     *
     * При удалении иллюстрации: а)проверяет введённый id иллюстрации на допустимое значение; б) вызывает метод deleteIllustrationById() Модели.
     *
     * При поиске книг по иллюстрации: а)проверяет введённый id иллюстрации на допустимое значение; б)получает список книг в каталоге и у каждой книги иющет иллюстрацию с таким id.
     * @see Model#checkAvailabilityBooks()
     * @see BookDirectory.Model#getBookByISbn(String)
     * @see Book#getIllustrations()
     * @see BookDirectory.Model#addIllustration(String, String, String, String)
     * @see BookDirectory.Model#deleteIllustrationById(String)
     * @see Model#getBooks()
     * @see BookDirectory.Book.Illustration#getId()
     * @return Boolean - флаг возврата в главное меню. Если true - возвращаемся в главное меню.
     */
    private boolean fourthMenuEngine(){
        if(LOGGER.isLoggable(Level.FINE)){
            LOGGER.fine("Menu select: Illustration(4)");}
        boolean returnMainMenu = false;
        Integer selectSub = checkSubMenu(4, 4);
        if(selectSub == 0){
            returnMainMenu = true;
        }
        else if(selectSub == 1){
            View.getInstance().printSubMenuText(2);
            if(LOGGER.isLoggable(Level.FINE)){
                LOGGER.fine("Menu select: add illustration(4-1)");}
            if(Model.getInstance().checkAvailabilityBooks()){
                try{
                    View.getInstance().printSubMenuText(3);
                    Integer bookCount = Integer.parseInt(Model.getInstance().keyboardInput());
                    if(LOGGER.isLoggable(Level.FINE)){
                        LOGGER.log(Level.FINE, "The number of books to which user want to add an illustration: ", bookCount);}
                    ArrayList<String> isbns = new ArrayList<String>();
                    for (int i = 0; i < bookCount; i++){
                        View.getInstance().printSubMenuText(1);
                        String isbn = Model.getInstance().keyboardInput();
                        Book book = Model.getInstance().getBookByISbn(isbn);
                        if(book != null){
                          isbns.add(isbn);
                        }
                        else{
                            View.getInstance().printErrorText(5);
                            System.out.println("(" + isbn + ").");
                        }
                    }
                    if (!(isbns.isEmpty())){
                        View.getInstance().printMessage(10);
                        for(String isbn : isbns){
                            System.out.println(Model.getInstance().getBookByISbn(isbn));
                        }
                        if(LOGGER.isLoggable(Level.FINE)){
                            LOGGER.log(Level.FINE, "Number of books, which will be added to illustration: ", isbns.size());}
                        String[] inputIllustrValues = View.getInstance().printAddIllustrationMenu();
                        if(checkIllustrationValues(inputIllustrValues[0], inputIllustrValues[1], inputIllustrValues[2])){
                            for(String isbn : isbns){
                                Book book = Model.getInstance().getBookByISbn(isbn);
                                if(book.getIllustrations().size() > 0){
                                    boolean addFlag = true;
                                    for(Book.Illustration illustration : book.getIllustrations()){
                                        if(illustration.getId().equals(inputIllustrValues[0])){
                                            System.out.println("\nОшибка, у книги(ISBN: "+book.getIsbn()+ ") уже есть иллюстрация с таким Id.\n");
                                            if(LOGGER.isLoggable(Level.WARNING)){
                                                LOGGER.log(Level.WARNING, "The book in the catalog already has illustration with such id");}
                                            addFlag = false;
                                        }
                                    }
                                    if(addFlag){Model.getInstance().addIllustration(isbn, inputIllustrValues[0], inputIllustrValues[1], inputIllustrValues[2]);}
                                }
                                else{Model.getInstance().addIllustration(isbn, inputIllustrValues[0], inputIllustrValues[1], inputIllustrValues[2]);}
                            }
                        }
                    }
                }
                catch (NumberFormatException e){View.getInstance().printErrorText(7);}
            }
            returnMainMenu = backToMainMenu();
        }
        else if(selectSub == 2){
            if(LOGGER.isLoggable(Level.FINE)){
                LOGGER.fine("Menu select: delete illustration(4-2)");}
            View.getInstance().printSubMenuText(4);
            String imageId = Model.getInstance().keyboardInput();
            if(imageId.length() < Model.getInstance().getIllustrationIdSize()){
                Model.getInstance().deleteIllustrationById(imageId);
            }
            else{View.getInstance().printErrorText(8);}
            returnMainMenu = backToMainMenu();
        }
        else if(selectSub == 3){
            if(LOGGER.isLoggable(Level.FINE)){
                LOGGER.fine("Menu select: Search Books by illustration(4-3)");}
            View.getInstance().printSubMenuText(5);
            String imageId = Model.getInstance().keyboardInput();
            boolean successSearchId = false;
            if(imageId.length() < Model.getInstance().getIllustrationIdSize()){
                try{
                    for(Book book : Model.getInstance().getBooks()){
                        ArrayList<Book.Illustration> illustrations = book.getIllustrations();
                        if(illustrations.size() > 0){
                            for(Book.Illustration illustration : illustrations){
                                if(illustration.getId().equals(imageId)){
                                    System.out.println(book);
                                    successSearchId = true;
                                }
                            }
                        }
                    }
                    if(!successSearchId){View.getInstance().printErrorText(9);}
                }
                catch (Exception e){
                    View.getInstance().printErrorText(0);
                    LOGGER.log(Level.SEVERE, "Application error: " + e.toString() + "");
                }
            }
            else{View.getInstance().printErrorText(8);}
            returnMainMenu = backToMainMenu();
        }

        else if(selectSub == 4){
            if(LOGGER.isLoggable(Level.FINE)){
                LOGGER.fine("Menu select: the list of illustrations of the book(4-4)");}
            try{
                View.getInstance().printSubMenuText(1);
                String isbn = Model.getInstance().keyboardInput();
                if(Model.getInstance().getBookByISbn(isbn) != null){
                    Book book = Model.getInstance().getBookByISbn(isbn);
                    if(book.getIllustrations().size() > 0){
                        for(Book.Illustration illustration : book.getIllustrations()){
                            System.out.println(illustration);
                        }
                    }
                }
                else{
                    View.getInstance().printErrorText(5);
                    if(LOGGER.isLoggable(Level.FINE)){
                        LOGGER.log(Level.FINE, "The book with this isbn is not found in the catalog: ", isbn);}
                }
            }
            catch (Exception e){
               View.getInstance().printErrorText(0);
                LOGGER.log(Level.SEVERE, "Application error: " + e.toString() + "");
            }
            returnMainMenu = backToMainMenu();
        }

    return returnMainMenu;
    }

    /**
     * Метод пятого пункта главного меню - экспорт книги и каталога.
     * При экспорте книги проверяет существование книги с введённым isbn в каталоге.
     * При экспорте книги или каталога целиком создаёт новый текстовый файл в директории "export" программы.
     * @see Model#getIllustrationIdSize()
     * @see Model#keyboardInput()
     * @see Model#getBooks()
     * @see BookDirectory.Model#writeBookExport(Book)
     * @see BookDirectory.Model#writeCatalogExport(String)
     * @return Boolean - флаг возврата в главное меню. Если true - возвращаемся в главное меню.
     */
    private boolean fifthMenuEngine(){
        if(LOGGER.isLoggable(Level.FINE)){
            LOGGER.fine("Menu select: Export(5)");}
        boolean returnMainMenu;
        Integer selectSub = checkSubMenu(2, 5);
        if(selectSub == 0){
            returnMainMenu = true;
        }
        else if(selectSub == 1){
            if(LOGGER.isLoggable(Level.FINE)){
                LOGGER.fine("Menu select: Export book(5-1)");}
            View.getInstance().printSubMenuText(1);
            String isbn = Model.getInstance().keyboardInput();
            if(isbn.length() <= Model.getInstance().getBookIsbnSize() && !(isbn.isEmpty())){
                boolean searchSuccess = false;
                for(Book book : Model.getInstance().getBooks()){
                    if(book.getIsbn().equals(isbn)){
                        if(Model.getInstance().writeBookExport(book)){
                            View.getInstance().printMessage(7);
                            searchSuccess = true;
                        }
                    }
                }
                if(!searchSuccess){View.getInstance().printErrorText(5);}
            }
            else View.getInstance().printMessage(8);
            returnMainMenu = backToMainMenu();
        }
        else if(selectSub == 2){
            if(LOGGER.isLoggable(Level.FINE)){
                LOGGER.fine("Menu select: Export catalog(5-2)");}
            if (Model.getInstance().checkAvailabilityBooks()){
                Model.getInstance().writeCatalogExport("catalog_export.txt");
            }
            returnMainMenu = backToMainMenu();
        }
        else{returnMainMenu = backToMainMenu();}
        return returnMainMenu;
    }

    /**
     * Метод шестого пункта главного меню - импорта отдельной книги или каталога.
     * При импорте проверяет: существование файла импорта; форматирование файла импорта;
     * при обнаружении книг или иллюстраций - наличие дублей в каталоге и соответствие их параметров допустимым значениям.
     * @return Boolean - флаг возврата в главное меню. Если true - возвращаемся в главное меню.
     */
    private boolean sixthMenuEngine(){
        if(LOGGER.isLoggable(Level.FINE)){
            LOGGER.fine("Menu select: Import(6)");}
        boolean returnMainMenu;
        Integer selectSub = checkSubMenu(2, 6);
        if(selectSub == 0){
            returnMainMenu = true;
        }
        else if(selectSub == 1){
            if(LOGGER.isLoggable(Level.FINE)){
                LOGGER.fine("Menu select: Import book(6-1)");}
            View.getInstance().printSubMenuText(6);
            String fileText = FileInOut.getInstance().readFile(Model.getInstance().keyboardInput() + ".txt", "import");
            int textSize = fileText.length();
            try {
                if(LOGGER.isLoggable(Level.INFO)){
                    LOGGER.info("Search for book in the import file");}
                fileText = fileText.replace("\n\r", "\n");
                if (fileText.length() > 0) { //проверка, что файл существует
                    /** поиск книги и попытка добавить её в каталог **/
                    String[] bookValues = findValuesBook(fileText);
                    if(bookValues[0] != null){
                        if(Model.getInstance().getBookByISbn(bookValues[0]) == null){
                            if(checkBookValues(bookValues[0], bookValues[1], bookValues[2])){
                                if(checkIsbnForbidden(bookValues[0])){
                                    Model.getInstance().addBook(bookValues[0], bookValues[1], bookValues[2]);
                                }
                            }
                        }
                        else{
                            View.getInstance().printErrorText(15);
                            System.out.println(bookValues[0]);
                            if(LOGGER.isLoggable(Level.WARNING)){
                                LOGGER.log(Level.WARNING, "Found the book already exists in the directory: ", bookValues[0]);}
                        }
                    }
                    else{View.getInstance().printErrorText(16);}
                    /** Поиск всех иллюстраций в файле импорта и попытка добавить их в каталог **/
                    View.getInstance().printMessage(13);
                    if(LOGGER.isLoggable(Level.INFO)){
                        LOGGER.info("Search for illustrations in the import file");}
                    String[] testValues = findValuesIllustration(fileText);
                    if(testValues[4] != null){ //проверка, что в тексте присутствует тег [Иллюстрация]
                        int offset = 0;
                        while (offset < textSize-2){
                            String buffer = fileText.substring(offset, textSize);
                            String[] illustrationValues = findValuesIllustration(buffer);
                            if(illustrationValues[4] == null){break;} //проверка дальнейшей целесообразности просмотра текста
                            Book book = Model.getInstance().getBookByISbn(illustrationValues[1]);
                            if(book != null){ //проверка, что книга с таким isbn присутствует в каталоге
                                if(book.getIllustrations().size() > 0){
                                    boolean illustrationDuplicate = false;
                                    for(Book.Illustration illustration : book.getIllustrations()){
                                        if(illustration.getId().equals(illustrationValues[0])){
                                            System.out.println("Ошибка! В каталоге у книги с Isbn=" + illustrationValues[1] + " уже есть иллюстрация с Id=" + illustrationValues[0]);
                                            if(LOGGER.isLoggable(Level.WARNING)){
                                                LOGGER.log(Level.WARNING, "illustration found (with id= "+illustrationValues[0]+") already exists in the catalog and added to the book with isbn: ", illustrationValues[1]);}
                                            illustrationDuplicate = true;
                                            break;
                                        }
                                    }
                                    if(!illustrationDuplicate){
                                        if(checkIllustrationValues(illustrationValues[0], illustrationValues[2], illustrationValues[3])){
                                            Model.getInstance().addIllustration(illustrationValues[1], illustrationValues[0], illustrationValues[2], illustrationValues[3]);
                                        }
                                    }
                                }
                                else {
                                    if(checkIllustrationValues(illustrationValues[0], illustrationValues[2], illustrationValues[3])){
                                        Model.getInstance().addIllustration(illustrationValues[1], illustrationValues[0], illustrationValues[2], illustrationValues[3]);
                                    }
                                }
                            }
                            else {
                                System.out.println("Ошибка! Не получилось добавить в каталог иллюстрацию с id=" + illustrationValues[0]+"\nВ каталоге отсутствует книга с ISBN=" + illustrationValues[1]+"\n");
                                if(LOGGER.isLoggable(Level.WARNING)){
                                    LOGGER.log(Level.WARNING, "illustration found (with ID= "+illustrationValues[0]+") can not be added to the catalog, there is no book with isbn: ", illustrationValues[1]);}
                            }
                            offset += Integer.parseInt(illustrationValues[4]);
                        }
                    }
                }
            }
            catch (Exception e){View.getInstance().printErrorText(17);}
            returnMainMenu = backToMainMenu();
        }
        else if(selectSub == 2){
            if(LOGGER.isLoggable(Level.FINE)){
                LOGGER.fine("Menu select: Import catalog(6-1)");}
            /** импорт каталога из файла **/
            View.getInstance().printSubMenuText(6);
            String fileText = FileInOut.getInstance().readFile(Model.getInstance().keyboardInput() + ".txt", "import");
            int textSize = fileText.length();
            try {
                fileText = fileText.replace("\n\r", "\n");
                if (fileText.length() > 0) {
                    /** поиск книг и попытка добавить их в каталог **/
                    try {
                        if(LOGGER.isLoggable(Level.INFO)){
                            LOGGER.info("Search for books in the import file");}
                        if (findValuesBook(fileText)[3] != null) {//проверка, что в тексте присутствует тег [Книга]
                            long startTime = System.currentTimeMillis();
                            int offset = 0;
                            while (offset < textSize - 2) {
                                String buffer = fileText.substring(offset, textSize);
                                String[] bookValues = findValuesBook(buffer);
                                if (bookValues[3] == null) {
                                    break;
                                }
                                if (Model.getInstance().getBookByISbn(bookValues[0]) == null) {
                                    if(checkBookValues(bookValues[0], bookValues[1], bookValues[2])){
                                        if(checkIsbnForbidden(bookValues[0])){
                                            Model.getInstance().addBook(bookValues[0], bookValues[1], bookValues[2]);
                                        }
                                    }
                                } else {
                                    View.getInstance().printErrorText(15);
                                    System.out.println(bookValues[0]);
                                    if(LOGGER.isLoggable(Level.WARNING)){
                                        LOGGER.log(Level.WARNING, "Found the book already exists in the directory: ", bookValues[0]);}
                                }
                                offset += Integer.parseInt(bookValues[3]);
                            }
                            long stopTime = System.currentTimeMillis();
                            long elapsedTime = stopTime - startTime;
                            if(LOGGER.isLoggable(Level.FINE)){LOGGER.log(Level.FINE, "The time spent on the import of all books (in ms):", elapsedTime);}
                        }
                        else {View.getInstance().printErrorText(17);}
                    }
                    catch (Exception e){
                        View.getInstance().printErrorText(0);
                        LOGGER.log(Level.SEVERE, "Application error: " + e.toString() + "");
                    }

                    /** Поиск всех иллюстраций в файле импорта и попытка добавить их в каталог **/
                    View.getInstance().printMessage(13);
                    if(LOGGER.isLoggable(Level.INFO)){
                        LOGGER.info("Search for illustrations in the import file");}
                    if(findValuesIllustration(fileText)[4] != null){ //проверка, что в тексте присутствует тег [Иллюстрация]
                        int offset = 0;
                        while (offset < textSize-2){
                            String buffer = fileText.substring(offset, textSize);
                            String[] illustrationValues = findValuesIllustration(buffer);
                            if(illustrationValues[4] == null){break;} //проверка дальнейшей целесообразности просмотра текста
                            Book book = Model.getInstance().getBookByISbn(illustrationValues[1]);
                            if(book != null){ //проверка, что книга с таким isbn присутствует в каталоге
                                if(book.getIllustrations().size() > 0){
                                    boolean illustrationDuplicate = false;
                                    for(Book.Illustration illustration : book.getIllustrations()){
                                        if(illustration.getId().equals(illustrationValues[0])){
                                            System.out.println("Ошибка! В каталоге у книги с Isbn=" + illustrationValues[1] + " уже есть иллюстрация с Id=" + illustrationValues[0]);
                                            if(LOGGER.isLoggable(Level.WARNING)){
                                                LOGGER.log(Level.WARNING, "illustration found (with id= "+illustrationValues[0]+") already exists in the catalog and added to the book with isbn: ", illustrationValues[1]);}
                                            illustrationDuplicate = true;
                                            break;
                                        }
                                    }
                                    if(!illustrationDuplicate){Model.getInstance().addIllustration(illustrationValues[1], illustrationValues[0], illustrationValues[2], illustrationValues[3]);}
                                }
                                else {Model.getInstance().addIllustration(illustrationValues[1], illustrationValues[0], illustrationValues[2], illustrationValues[3]);}
                            }
                            else{
                                System.out.println("Ошибка! Не получилось добавить в каталог иллюстрацию с id=" + illustrationValues[0]+"\nВ каталоге отсутствует книга с ISBN=" + illustrationValues[1]+"\n");
                                if(LOGGER.isLoggable(Level.WARNING)){
                                    LOGGER.log(Level.WARNING, "illustration found (with ID= "+illustrationValues[0]+") can not be added to the catalog, there is no book with isbn: ", illustrationValues[1]);}
                            }
                            offset += Integer.parseInt(illustrationValues[4]);
                        }
                    }
                }
            }
            catch (Exception e){
                View.getInstance().printErrorText(17);
                LOGGER.log(Level.SEVERE, "Application or format file error: " + e.toString() + "");
            }
            returnMainMenu = backToMainMenu();
        }
        else{returnMainMenu = backToMainMenu();}
        return returnMainMenu;
    }

    /**
     * Служебный метод для возврата в главное меню программы.
     * @return Boolean
     */
    private boolean backToMainMenu(){
        if(LOGGER.isLoggable(Level.FINE)){
            LOGGER.fine("Back to main menu.");}
        View.getInstance().printSubMenuText(0);
        Model.getInstance().keyboardInput();
        return true;
    }

    /**
     * Служебный метод для проверки номера пункта меню, введённого пользователем и вывода текста меню.
     * @param numberOfItems Integer - количество пунктов в меню.
     * @param numberOfSubMenu Integer - номер пункта (раздела) главноно меню.
     * @return Boolean: true, если введённое значение соответствует пунктам меню, иначе - false.
     */
    private int checkSubMenu(Integer numberOfItems, Integer numberOfSubMenu){
        int result = 0;
        boolean exit = false;
        while (!exit){
            View.getInstance().printMessage(15);
            String inputSubMenu = Model.getInstance().keyboardInput();
            if (inputSubMenu.isEmpty()) {result = 0; exit = true;}
            else{
                if(checkMenuItem('1', numberOfItems.toString().charAt(0), inputSubMenu)){
                    result = Integer.parseInt(inputSubMenu);
                    exit = true;
                }
                else{
                    View.getInstance().printErrorText(10);
                    View.getInstance().printMenu(numberOfSubMenu);
                }
            }
        }
        if(LOGGER.isLoggable(Level.FINE)){
            LOGGER.log(Level.FINE, "The result of check sub menu selection is: ", result);}
        return result;
    }

    /**
     * Служебный метод для проверки допустимых значений атрибутов книги.
     * @param isbn String - Isbn книги.
     * @param title String - навзвание книги.
     * @param author String - автор книги.
     * @return Boolean: true, если переданные атрибуты соответствуют допустимым значениям, иначе - false.
     */
    public boolean checkBookValues(String isbn, String title, String author){
        boolean resultCheck = false;
        if(isbn.length() <= Model.getInstance().getBookIsbnSize() &&
                title.length() <= Model.getInstance().getBookTitleSize() &&
                author.length() <= Model.getInstance().getBookAuthorSize() && !(isbn.isEmpty()) ){
            resultCheck = true;
        }
        else if(!(isbn.length() <= Model.getInstance().getBookIsbnSize()) || isbn.isEmpty()){
            View.getInstance().printMessage(8);
        }
        else if(!(title.length() <= Model.getInstance().getBookTitleSize())){
            View.getInstance().printErrorText(11);
        }
        else {View.getInstance().printErrorText(12);}
        if(LOGGER.isLoggable(Level.FINE)){
            LOGGER.log(Level.FINE, "The result of check book values is: ", resultCheck);}
        return resultCheck;
    }

    /**
     * Служебный метод для проверки допустимых значений атрибутов иллюстрации.
     * @param imageId String - Id иллюстрации.
     * @param name String - название иллюстрации.
     * @param author String - автор иллюстрации.
     * @return Boolean: true, если переданные атрибуты соответствуют допустимым значениям, иначе - false.
     */
    public boolean checkIllustrationValues(String imageId, String name, String author){
        boolean resultCheck = false;
        if(imageId.length() <= Model.getInstance().getIllustrationIdSize() && name.length() <= Model.getInstance().getIllustrationNameSize() && author.length() <= Model.getInstance().getIllustrationAuthorSize() && !(imageId.isEmpty())){
            resultCheck = true;
        }
        else if(!(imageId.length() <= Model.getInstance().getIllustrationIdSize()) || imageId.isEmpty()){
            View.getInstance().printErrorText(8);
        }
        else if(!(name.length() <= Model.getInstance().getIllustrationNameSize())){
            View.getInstance().printErrorText(11);
        }
        else {View.getInstance().printErrorText(12);}
        if(LOGGER.isLoggable(Level.FINE)){
            LOGGER.log(Level.FINE, "The result of check illustration values is: ", resultCheck);}
        return resultCheck;
    }

    /**
     * Служебный метод для проверки isbn на наличие запрещённых символов.
     * @param isbn String - Isbn книги, который нужно проверить.
     * @return Boolean: true, если переданный isbn не содержит запрещённых символов, иначе - false.
     */
    public boolean checkIsbnForbidden(String isbn){
        boolean result = false;
        char forbidden[] = { '/', ':', '*', '|', '<', '>', '\"', '\''};
        for(char symbol : forbidden){
            Pattern pattern = Pattern.compile(".*["+symbol+"]+.*");
            if(pattern.matcher(isbn).lookingAt()){
                result = false;
                break;
            }
            else{result = true;}
        }
        Pattern patternBackSlash = Pattern.compile(".*[\\\\]+.*");
        if(patternBackSlash.matcher(isbn).lookingAt()){result = false;}
        if(!result){View.getInstance().printErrorText(13);}
        if(LOGGER.isLoggable(Level.FINE)){
            LOGGER.log(Level.FINE, "The result of book isbn is: ", result);}
        return result;
    }

    /**
     * Служебный метод для проверки номера пункта главного меню, введённого пользователем и вывода текста меню.
     * @param startPattern char - номер первог пункта меню
     * @param endPattern char - номер последнего пункта меню
     * @param choice String - что ввёл пользователь
     * @return Boolean: true, если введённое пользователем значение соответствует какому-либо пункту меню.
     */
    public boolean checkMenuItem(char startPattern, char endPattern, String choice){
        boolean right = false;
        try {
            Pattern pattern = Pattern.compile("["+startPattern+"-"+endPattern+"]");
            right = pattern.matcher(choice).matches();
        }
        catch (Exception e){View.getInstance().printErrorText(0);}
        if(LOGGER.isLoggable(Level.FINE)){
            LOGGER.log(Level.FINE, "The result of check menu selection is: ", right);}
        return right;
    }

    /**
     * Служебный метод для поиска атрибутов книги в тексте файла импорта.
     * @param fileText String - текст для поиска.
     * @return String[4], где [0] - isbn книги, [1] - название книги, [2] - автор книги, [3] - позиция, на котором поиск остановился.
     */
    private String[] findValuesBook(String fileText){
        String[] values = new String[4];
        if (fileText.contains("[Книга]")) { //проверка на наличие тега
            int beginStr = fileText.indexOf("[Книга]"); //символ начала тега книга
            int endStr = fileText.indexOf('\n', beginStr); //номер символа - конец тега книга
            values[0] = fileText.substring(endStr + 1, fileText.indexOf('\n', endStr + 1)).trim();
            beginStr = fileText.indexOf('\n', endStr + 1) + 1; // номер символа - начало названия книги
            endStr = fileText.indexOf('\n', beginStr); // номер символа - конец названия книги
            values[1] = fileText.substring(beginStr, endStr).trim();
            beginStr = endStr + 1; // номер символа - начало автора книги
            endStr = fileText.indexOf('\n', beginStr); // номер символа - конец автора книги
            values[2] = fileText.substring(beginStr, endStr).trim();
            values[3] = String.valueOf(endStr);
        }
        else{
            if(LOGGER.isLoggable(Level.WARNING)){
                LOGGER.warning("Tag [Book] is not found text file ");}
        }
        return values;
    }

    /**
     * Служебный метод для поиска атрибутов иллюстрации в тексте файла импорта.
     * @param fileText String - текст для поиска.
     * @return String[5], где [0] - id иллюстрации, [1] - isbn книги, [2] - название илюстрации, [3] - автор иллюстрации, [4] - позиция, на котором поиск остановился.
     */
    private String[] findValuesIllustration(String fileText){
        String[] values = new String[5];
        if (fileText.contains("[Иллюстрация]")) { //проверка на наличие тега
            int beginStr = fileText.indexOf("[Иллюстрация]"); //символ начала тега книга
            int endStr = fileText.indexOf('\n', beginStr); //номер символа - конец тега книга
            values[0] = fileText.substring(endStr + 1, fileText.indexOf('\n', endStr + 1)).trim();
            beginStr = fileText.indexOf('\n', endStr + 1) + 1; // номер символа - начало названия книги
            endStr = fileText.indexOf('\n', beginStr); // номер символа - конец названия книги
            values[1] = fileText.substring(beginStr, endStr).trim();
            beginStr = endStr + 1; // номер символа - начало автора книги
            endStr = fileText.indexOf('\n', beginStr); // номер символа - конец автора книги
            values[2] = fileText.substring(beginStr, endStr).trim();
            beginStr = endStr + 1;
            endStr = fileText.indexOf('\n', beginStr);
            values[3] = fileText.substring(beginStr, endStr).trim();
            values[4] = String.valueOf(endStr);
        }
        else{
            if(LOGGER.isLoggable(Level.WARNING)){
                LOGGER.warning("Tag [Illustration] is not found text file ");}
        }
        return values;
    }
}