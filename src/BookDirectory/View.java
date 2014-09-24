package BookDirectory;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Класс "Представление"(Вид) - отображение данных (модель MVC).
 * Паттерн Singleton.
 * @author dyakonov
 * @version 1.2
 */
public final class View
{
    /**
     * Статическая переменная класса для хранения единственного экземпляра класса
     */
    private static View instance;

    /**
     * Конструктор класса с закрытым доступом (private).
     */
    private View(){}

    /**
     * "Геттер" для получения экземляра класса
     * @return Единственный экземпляр класса View.
     */
    public static View getInstance(){
        if(instance == null){
            instance = new View();
        }
        return instance;
    }

    private final String TEXT_HELLO = "Добро пожаловать в приложение \"Книжный каталог\".\n";
    private final String TEXT_INSTRUCTION = "Для начала работы, пожалуйста, введите с клавиатуры номер пункта меню (цифры от 1 до 6).\n" +
            "(или нажмите клавишу Enter для выхода из приложения)\n\n";
    private final String TEXT_INVITE_ENTER_NUMBER = "\nВведите номер пункта меню: ";
    private final String MENU_TEXT_1 = "Главное меню:\n\n1 - Вывести список книг\n";
    private final String MENU_TEXT_11 = "Список книг в каталоге:\n";
    private final String MENU_TEXT_2 = "2 - Добавить книгу\n";
    private final String MENU_TEXT_21 = "Добавить книгу в каталог.\nПожалуйста, укажите данные о книге.\n1)Введите ISBN: ";
    private final String MENU_TEXT_211 = "2)Введите название книги: ";
    private final String MENU_TEXT_212 = "3)Введите автора книги: ";
    private final String MENU_TEXT_3 = "3 - Удалить книгу\n";
    private final String MENU_TEXT_31 = "Удалить книгу из каталога.\nВы можете удалить книгу из каталога по isbn.\nПожалуйста, введите ISBN: ";
    private final String MENU_TEXT_4 = "4 - Иллюстрации\n";
    private final String MENU_TEXT_4_TITLE = "\nРаздел: ИЛЛЮСТРАЦИИ\n\n";
    private final String MENU_TEXT_41 = "1 - Добавить иллюстрацию\n";
    private final String MENU_TEXT_411 = "Добавить иллюстрацию в каталог.\n";
    private final String MENU_TEXT_411_COUNT = "Укажите количество книг в каталоге, где используется данная иллсютрация: ";
    private final String MENU_TEXT_411_ISBN = "Введите ISBN: ";
    private final String MENU_TEXT_412 = "\nУкажите id иллюстрации: ";
    private final String MENU_TEXT_413 = "\nУкажите название иллюстрации: ";
    private final String MENU_TEXT_414 = "\nУкажите автора иллюстрации: ";
    private final String MENU_TEXT_42 = "2 - Удалить иллюстрацию\n";
    private final String MENU_TEXT_421 = "Удалить книгу из каталога.\nВы можете удалить иллюстрацию из каталога по id.\nПожалуйста, введите id: ";
    private final String MENU_TEXT_43 = "3 - Поиск книг по иллюстрации\n";
    private final String MENU_TEXT_431 = "Поиск книг по иллюстрации\nПожалуйста, введите id иллюстрации: ";
    private final String MENU_TEXT_44 = "4 - Список иллюстраций книги\n";
    private final String MENU_TEXT_5 = "5 - Экспорт\n";
    private final String MENU_TEXT_5_TITLE = "\nРаздел: ЭКСПОРТ\n\n";
    private final String MENU_TEXT_51 = "1 - Экспорт одной книги\n";
    private final String MENU_TEXT_52 = "2 - Экспорт каталога\n";
    private final  String MENU_TEXT_6 = "6 - Импорт\n";
    private final String MENU_TEXT_6_TITLE = "\nРаздел: ИМПОРТ\n\n";
    private final String MENU_TEXT_61 = "1 - Импорт одной книги\n";
    private final String MENU_TEXT_62 = "2 - Импорт каталога\n";
    private final String MENU_TEXT_FILENAME = "Введите имя файла(без расширения .txt): ";
    private final String MENU_TEXT_EXIT = "\nEnter - Закрыть приложение\n";
    private final String MENU_TEXT_BACK = "\nEnter - Назад в главное меню\n";
    private final String MENU_TEXT_BACK_ANY = "\nЛюбая клавиша - назад в главное меню\n";
    private final String MENU_TEXT_SEARCH_ILLUSTRATION = "\nПоиск иллюстраций...\n\n";
    private final String MENU_ERROR_TEXT = "Ошибка в выборе пункта меню.\nПожалуйста, для продолжения работы введите" +
            " номер пункта меню или или нажмите клавишу Enter для выхода из приложения.\n\n";
    private final String SUB_MENU_ERROR_TEXT = "Ошибка в выборе пункта меню.\nПожалуйста, для продолжения работы введите" +
            " номер пункта меню или нажмите клавишу Enter для перехода в главное меню.\n";
    private final String TEXT_ERROR_BOOK_ADD_DUPLICATE = "Ошибка: введённый Вами ISBN уже присутствует в каталоге.\n";
    private final String TEXT_ERROR_BD = "Критическая ошибка: нет доступа к бд.\n";
    private final String TEXT_ERROR_TABLE_BLOCK = "Ошибка: таблица заблокирована.\n";
    private final String TEXT_ERROR_KEYBOARD = "Критическая ошибка: ввод с клавиатуры.\n";
    private final String TEXT_ERROR_IN_OUT = "\nОшибка ввода\\вывода.\n";
    private final String TEXT_ERROR_LOGGER_SECURITY = "\nОшибка: не удалось создать файл лога из-за политики безопасности.\n";
    private final String TEXT_ERROR = "Общая ошибка.\n";
    private final String TEXT_ERROR_FILE_WRITE = "Ошибка записи.\n";
    private final String TEXT_ERROR_BOOK_ISBN = "Ошибка ввода ISBN: максимум 17 символов и не пустое значение.\n";
    private final String TEXT_ERROR_BOOK_ISBN_FORBIDDEN = "Ошибка ввода ISBN: Вы ввели запрещённые символы (/, \\, <, >, |, :, *, ')\n";
    private final String TEXT_ERROR_ISBN_FIND = "\nОшибка: в каталоге нет книги с таким ISBN ";
    private final String TEXT_ERROR_ID_FIND = "\nОшибка: в каталоге нет иллюстрации с таким id.";
    private final String TEXT_ERROR_ILLUSTRATION_ID = "Ошибка ввода id: максимум 20 символов и не пустое значение.\n";
    private final String TEXT_ERROR_TITLE = "Ошибка ввода названия: Вы ввели слишком длинное название (максимум 128 символов).\n";
    private final String TEXT_ERROR_AUTHOR = "Ошибка ввода автора: Вы ввели слишком много символов (максимум 128 символов).\n";
    private final String TEXT_ERROR_BOOK_COUNT = "Ошибка ввода количества книг.\n";
    private final String TEXT_ERROR_IMPORT_TAG_BOOK = "Ошибка форматирования: отстутствует тег [Книга]";
    private final String TEXT_ERROR_ILLUSTRATION_DUPLICATE = "\nОшибка, у книги уже есть иллюстрация с таким Id.\n";
    private final String TEXT_ERROR_FILE_NOT_FOUND = "\nОшибка, файл не найден.\n";
    private final String TEXT_ERROR_IMPORT_FORMAT = "\nОшибка форматирования файла импорта.\n См. справочный файл README.txt в директории import\n";
    private final String TEXT_ERROR_JAVA_NO_METHOD = "\nКритическая ошибка Java. Нет такого метода.\n";
    private final String TEXT_ERROR_UNSUPPORTED_ENCODING = "Error: the system is used a coding format unsupported by application.\n";
    private final String TEXT_BOOK_ADD_ILLUSTRATION = "\nИллюстрация будет добавлена к следующим книгам:\n";
    private final String TEXT_SUCCESS_ADD_BOOK = "\nУспешно. Книга добавлена в каталог.";
    private final String TEXT_SUCCESS_ADD_BOOK_EXPORT = "\nУспешно. Книга (и иллюстрация) добавлены в файл экспорта(export_isbn.txt).\nВы можете найти его в каталоге export\n";
    private final String TEXT_SUCCESS_CATALOG_EXPORT = "\nУспешно. Каталог (книги и иллюстрации) добавлен в файл экспорта(catalog_export.txt).\nВы можете найти его в каталоге export\n";
    private final String TEXT_SUCCESS_ADD_ILLUSTRATION = "\nУспешно. Иллюстрация добавлена в каталог.";
    private final String TEXT_SUCCESS_DEL_BOOK = "\nУспешно. Книга удалена из каталога.";
    private final String TEXT_SUCCESS_DEL_BOOK_ILL = "\nУспешно. Иллюстрация к книге была удалена. Удаление книги...\n";
    private final String TEXT_NOT_FIND_ILLUSTRATION_DELETE = "\nНе найдено илююстрации к книге. Удаление книги...\n";
    private final String TEXT_SUCCESS_DELETED_ILLUSTRATION = "\nУспешно. Иллюстрация удалена из каталога.";
    private final String TEXT_SUCCESS_NEW_DB = "\nУспешно. Новая БД создана.";
    private final String TEXT_WARN_NO_BOOK = "\nВ каталоге ещё нет книг.";
    private final String TEXT_WARN_DUPLICATE_BOOK_CATALOG_IMPORT = "\nВнимание! Найденная книга уже присутствует в каталоге. ISBN: ";
    private final String TEXT_WARN_NEW_DATABASE = "\nВнимание! БД не сушествует!\nСоздание новой БД...";
    private final HashMap<Integer, String> menuItems = new HashMap<Integer, String>();

    /**
     * "Логгер" класса.
     */
    private final Logger LOGGER = Logger.getLogger(Model.class.getName());

    /**
     * Служебный метод для начальной инициализации ассоциативного массива menuItems, где хранятся пункты меню в формате String.
     */
    private void setMenuItems(){
        menuItems.put(0, MENU_TEXT_1 + MENU_TEXT_2 + MENU_TEXT_3 + MENU_TEXT_4 + MENU_TEXT_5 + MENU_TEXT_6 + MENU_TEXT_EXIT);
        menuItems.put(1, MENU_TEXT_11);
        menuItems.put(2, MENU_TEXT_21);
        menuItems.put(3, MENU_TEXT_31);
        menuItems.put(4, MENU_TEXT_4_TITLE + MENU_TEXT_41 + MENU_TEXT_42 + MENU_TEXT_43 + MENU_TEXT_44 + MENU_TEXT_BACK);
        menuItems.put(5, MENU_TEXT_5_TITLE + MENU_TEXT_51 + MENU_TEXT_52 + MENU_TEXT_BACK);
        menuItems.put(6, MENU_TEXT_6_TITLE + MENU_TEXT_61 + MENU_TEXT_62 + MENU_TEXT_BACK);
    }

    /**
     * Метод для начальной инициализации программы - вывод приветствия и главного меню.
     */
    protected void initializeView(){
     setMenuItems();
     System.out.print(TEXT_HELLO + TEXT_INSTRUCTION);
        if (LOGGER.isLoggable(Level.FINE)){
            LOGGER.fine("Print welcome message");}
    }


    /**
     * Метод для вывода пунктов меню.
     * @param currentSection int - какой пункт главного меню нужно вывести (от 1 до 6)
     */
    protected void printMenu(int currentSection){
        if (currentSection >=0 && currentSection <= 6){
          System.out.print(menuItems.get(currentSection));
            if (LOGGER.isLoggable(Level.FINE)){
                LOGGER.fine("Print menu");}
        }
    }

    /**
     * Служебный метод для вывода сообщений и запросов при добавлении новой книги в каталог.
     * @return Массив String[3] - введённые пользователем 3 параметра книги (isbn, название, автор).
     */
    protected String[] printAddBookMenu(){
        String[] inputBookValues = new String[3];
        inputBookValues[0] = Model.getInstance().keyboardInput(); //isbn
        System.out.print(MENU_TEXT_211);
        inputBookValues[1] = Model.getInstance().keyboardInput(); //название
        System.out.print(MENU_TEXT_212);
        inputBookValues[2] = Model.getInstance().keyboardInput(); //автор
        return inputBookValues;
    }

    /**
     * Служебный метод для вывода сообщений и запросов при добавлении новой иллюстрации в каталог.
     * @return Массив String[3] - введённые пользователем 3 параметра иллюстрации (id, название, автор).
     */
    public String[] printAddIllustrationMenu(){
        String[] inputIllustrValues = new String[3];
        System.out.print(MENU_TEXT_412);
        inputIllustrValues[0] = Model.getInstance().keyboardInput(); //id
        System.out.print(MENU_TEXT_413);
        inputIllustrValues[1] = Model.getInstance().keyboardInput(); //назавание
        System.out.print(MENU_TEXT_414);
        inputIllustrValues[2] = Model.getInstance().keyboardInput(); //автор
        return inputIllustrValues;
    }

    /**
     * Метод для вывода сообщений об ошибках (программы, пользователя, некорретных данных и т.п.)
     * @param number int - номер сообщения для вывода.
     */
    protected void printErrorText(Integer number){
        switch (number){
            case 0: {System.out.print(TEXT_ERROR);break;}
            case 1: {System.out.print(MENU_ERROR_TEXT);break;}
            case 2: {System.out.print(TEXT_ERROR_BD);break;}
            case 3: {System.out.print(TEXT_ERROR_KEYBOARD);break;}
            case 4: {System.out.print(TEXT_ERROR_BOOK_ADD_DUPLICATE);break;}
            case 5: {System.out.print(TEXT_ERROR_ISBN_FIND);break;}
            case 6: {System.out.print(TEXT_ERROR_ILLUSTRATION_DUPLICATE);break;}
            case 7: {System.out.print(TEXT_ERROR_BOOK_COUNT);break;}
            case 8: {System.out.print(TEXT_ERROR_ILLUSTRATION_ID);break;}
            case 9: {System.out.print(TEXT_ERROR_ID_FIND);break;}
            case 10: {System.out.print(SUB_MENU_ERROR_TEXT);break;}
            case 11: {System.out.print(TEXT_ERROR_TITLE);break;}
            case 12: {System.out.print(TEXT_ERROR_AUTHOR);break;}
            case 13: {System.out.print(TEXT_ERROR_BOOK_ISBN_FORBIDDEN);break;}
            case 14: {System.out.print(TEXT_ERROR_FILE_NOT_FOUND);break;}
            case 15: {System.out.print(TEXT_WARN_DUPLICATE_BOOK_CATALOG_IMPORT);break;}
            case 16: {System.out.print(TEXT_ERROR_IMPORT_TAG_BOOK);break;}
            case 17: {System.out.print(TEXT_ERROR_IMPORT_FORMAT);break;}
            case 18: {System.out.print(TEXT_ERROR_FILE_WRITE);break;}
            case 19: {System.out.print(TEXT_ERROR_TABLE_BLOCK);break;}
            case 20: {System.out.print(TEXT_ERROR_JAVA_NO_METHOD);break;}
            case 21: {System.out.print(TEXT_ERROR_UNSUPPORTED_ENCODING);break;}
            case 22: {System.out.print(TEXT_ERROR_IN_OUT);break;}
            case 23: {System.out.print(TEXT_ERROR_LOGGER_SECURITY);break;}
        }
        if(LOGGER.isLoggable(Level.WARNING)){
            LOGGER.log(Level.WARNING, "Print Error!", number);}
    }

    /**
     * Метод для вывода пунктов меню, служебных сообщений.
     * @param number int - номер сообщения для вывода.
     */
    protected void printSubMenuText(Integer number){
        switch (number){
            case 0: {System.out.print(MENU_TEXT_BACK_ANY);break;}
            case 1: {System.out.print(MENU_TEXT_411_ISBN);break;}
            case 2: {System.out.print(MENU_TEXT_411);break;}
            case 3: {System.out.print(MENU_TEXT_411_COUNT);break;}
            case 4: {System.out.print(MENU_TEXT_421);break;}
            case 5: {System.out.print(MENU_TEXT_431);break;}
            case 6: {System.out.print(MENU_TEXT_FILENAME);break;}
        }
    }

    /**
     * Метод для вывода сообщений, предупреждений и т.п.
     * @param number int - номер сообщения для вывода.
     */
    protected void printMessage(Integer number){
        switch (number){
            case 0: {System.out.print(TEXT_SUCCESS_ADD_BOOK);break;}
            case 1: {System.out.print(MENU_TEXT_BACK_ANY);break;}
            case 2: {System.out.print(TEXT_SUCCESS_ADD_ILLUSTRATION);break;}
            case 3: {System.out.print(TEXT_SUCCESS_DEL_BOOK_ILL);break;}
            case 4: {System.out.print(TEXT_NOT_FIND_ILLUSTRATION_DELETE);break;}
            case 5: {System.out.print(TEXT_WARN_NO_BOOK);break;}
            case 6: {System.out.print(TEXT_SUCCESS_CATALOG_EXPORT);break;}
            case 7: {System.out.print(TEXT_SUCCESS_ADD_BOOK_EXPORT);break;}
            case 8: {System.out.print(TEXT_ERROR_BOOK_ISBN);break;}
            case 9: {System.out.print(TEXT_SUCCESS_DEL_BOOK);break;}
            case 10: {System.out.print(TEXT_BOOK_ADD_ILLUSTRATION);break;}
            case 11: {System.out.print(TEXT_SUCCESS_NEW_DB);break;}
            case 12: {System.out.print(TEXT_SUCCESS_DELETED_ILLUSTRATION);break;}
            case 13: {System.out.print(MENU_TEXT_SEARCH_ILLUSTRATION);break;}
            case 14: {System.out.print(TEXT_WARN_NEW_DATABASE);break;}
            case 15: {System.out.print(TEXT_INVITE_ENTER_NUMBER);break;}
        }
    }
}