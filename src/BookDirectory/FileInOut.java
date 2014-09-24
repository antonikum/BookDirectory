package BookDirectory;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Класс используется для работы с файлами (чтение, запись).
 * Паттерн Singleton.
 * @author dyakonov
 */
public final class FileInOut {

    /**
     * Статическая переменная класса для хранения единственного экземпляра класса
     */
    private static FileInOut instance;

    /**
     * Конструктор класса с закрытым доступом (private).
     */
    private FileInOut(){}

    /**
     * "Геттер" для получения экземляра класса
     * @return Единственный экземпляр класса FileInOut.
     */
    public static FileInOut getInstance(){
        if(instance == null){
            instance = new FileInOut();
        }
        return instance;
    }

    /**
     * "Логгер" класса.
     */
    private final Logger LOGGER = Logger.getLogger(Model.class.getName());

    /**
     * "Сепаратор" - символ-разделитель в файловой системе ОС.
     */
    private final char SEPARATOR = File.separatorChar;

    /**
     * Метод для записи информации в файл.
     * @param fileName String - Имя файла для записи.
     * @param directory - Имя папки (обычно это export) для записи.
     * @param text String - информация (текст) для записи.
     * @return boolean: возвращает true, если запись прошла успешно; иначе - false.
     */
    public boolean writeFile(String fileName, String directory, String text){
        boolean successWrite = false;
        File file = new File(directory+ SEPARATOR +fileName);
        File folder = new File(directory);
        try {
            if(!folder.exists()){createNewDir(directory);}
            if(!file.exists()) //проверяем, что если файл не существует то создаем его
            {
                file.createNewFile();
                System.out.println("Файл " + file.getAbsoluteFile() + " не найден. Создание...");
                if(LOGGER.isLoggable(Level.WARNING)){
                    LOGGER.log(Level.WARNING, "Creation of new file: ", file.getCanonicalFile());}
            }
            OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(file.getAbsoluteFile()), "UTF-8");
            try {
                out.write(text);
                successWrite = true;
            } finally {
                out.close();
            }
        }
        catch (FileNotFoundException e){
            System.out.println("Файл " + file.getAbsoluteFile() + " не найден");
            LOGGER.log(Level.SEVERE, "FileNotFoundException: " + e.toString() + "", file.getAbsoluteFile());
        }
        catch(IOException e) {
            View.getInstance().printErrorText(22);
            LOGGER.log(Level.SEVERE, "IOException: " + e.toString() + "", fileName);
        }
        if(LOGGER.isLoggable(Level.FINE)){
            LOGGER.log(Level.FINE, "Write of the file was successful?", successWrite);}
        return successWrite;
    }

    /**
     * Служебный метод для создания новой директории (папки).
     * @param directoryName String - имя папки, которую нужно создать.
     * @return Boolean: результат создания. Если папка успешно создана - true, иначе - false;
     */
    protected boolean createNewDir(String directoryName){
        boolean result = false;
        try {
            File file = new File(directoryName);
            if(LOGGER.isLoggable(Level.WARNING)){
                LOGGER.log(Level.WARNING, "The new directory was created!", directoryName);
            }
            result = file.mkdir();
        }
        catch (Exception e){
            View.getInstance().printErrorText(0);
            LOGGER.log(Level.SEVERE, "Application Error: " + e.toString() + "");
        }
        return result;
    }

    /**
     * Метод для чтения информации из файла.
     * @param fileName String - Имя файла для чтения.
     * @param directory String - Имя директории. где располагается файл.
     * @return String - информация из файла.
     */
    public String readFile(String fileName, String directory){
        StringBuilder sb = new StringBuilder();
        try {
            File folder = new File(directory);
            if(!folder.exists()){createNewDir(directory);}
            File file = exists(directory+ SEPARATOR +fileName);
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file.getAbsoluteFile()), "UTF-8"));
            try {
                String s;
                while ((s = in.readLine()) != null) {
                    sb.append(s);
                    sb.append("\n");
                }
            }
            finally {in.close();}
        }
        catch (FileNotFoundException e){
            View.getInstance().printErrorText(14);
            if(LOGGER.isLoggable(Level.WARNING)){
                LOGGER.log(Level.WARNING, "The file for read is not found!", fileName);}
        }
        catch(Exception e)
        {
            View.getInstance().printErrorText(0);
            LOGGER.log(Level.SEVERE, "Application error: " + e.toString() + "");
        }
        if(LOGGER.isLoggable(Level.FINE)){
            LOGGER.log(Level.FINE, "The length of string is: "+sb.toString().length()+"");}
        return sb.toString();
    }

    /**
     * Служебный метод для проверки существования указаного файла
     * @param fileName String - Имя файла (директория+название) для проверки.
     * @return Объект File.
     * @throws FileNotFoundException Вызывает исключение, если файл не был найден.
     */
    private static File exists(String fileName) throws FileNotFoundException
    {
        File file = new File(fileName);
        if (!file.exists()){
            System.out.println("Файл " + file.getAbsoluteFile() + " не найден");
            throw new FileNotFoundException(file.getName());
        }
        return file;
    }
}