package BookDirectory;

import java.util.ArrayList;

interface CatalogItem{
    String getIsbn();
    String getAuthor();
    String getName();
}

/**
 * Класс используется для представления книги как объекта программы, а иллюстраций книги - внутреннего класса этого объекта.
 * У объекта Book 4 атрибута:
 * String isbn - isbn книги
 * String name - название книги
 * String author - автор книги
 * ArrayList<Illustration> - коллекция иллюстраций к книге
 * @version 1.1
 */
public class Book implements CatalogItem {
    private String isbn = "";
    private String name = "";
    private String author = "";
    private ArrayList<Illustration> illustrations = new ArrayList<Illustration>();

    private Book(String isbn, String title, String author){
        this.isbn = isbn;
        this.name = title;
        this.author = author;
    }

    /**
     * Метод для создания нового объекта - книга.
     * @param isbn Isbn создаеваемой книги
     * @param title Название создаваемой книги
     * @param author Автор создаваемой книги
     * @return Объект Book - новая книга
     */
    public static Book getBook(String isbn, String title, String author){
        return new Book(isbn, title, author);
    }

    /**
     * "Геттер" для получения isbn книги
     * @return String isbn
     */
    public String getIsbn() {
        return isbn;
    }

    /**
     * "Геттер" для получения названия книги
     * @return String название книги
     */
    public String getName() {
        return name;
    }

    /**
     * "Геттер" для получения имени автора(ов) книги
     * @return String имя автора книги
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Метод для добавления новой иллюстрации в коллекцию illustrations книги.
     * @param id Id добавляемой иллюстрации
     * @param name Название добавляемой иллюстрации
     * @param author Автор создаваемой иллюстрации
     */
    public void addIllustration(String id, String name, String author){
        this.illustrations.add(new Illustration(id, name, author));
    }

    /**
     * Метод для получения иллюстраций книги
     * @return коллекцию иллюстраций книги
     */
    public ArrayList<Illustration> getIllustrations() {
        return illustrations;
    }

    @Override
    public String toString() {
        return " ISBN= "+ isbn +
                ", Название: \"" + name + "\"" +
                ", Автор: " + author + ", Иллюстраций в книге: " +illustrations.size();
    }

    /**
     * Внутренний класс Book - иллюстрации.
     * У объекта 3 поля:
     * String id
     * String name
     * String author
     * И одно поле класса-обёртки(Book): String isbn
     */
    protected class Illustration  {
        private String id = "";
        private String name = "";
        private String author = "";

        private Illustration(String id, String name, String author) {
            this.id = id;
            this.name = name;
            this.author = author;
        }

        @Override
        public String toString() {
            return  "Id='" + id + '\'' +
                    ", Название='" + name + '\'' +
                    ", Автор='" + author + '\'' + ", isbn= " + getIsbn();
        }

        /**
         * "Геттер" для получения id иллюстраци
         * @return String id
         */
        public String getId() {
            return id;
        }

        /**
         * "Геттер" для получения названия иллюстрации
         * @return String name
         */
        public String getName() {
            return name;
        }

        /**
         * "Геттер" для получения имени автора иллюстрации
         * @return String author
         */
        public String getAuthor() {
            return author;
        }

        /**
         * "Геттер" для получения isbn книги, которой принадлежит иллюстрация
         * @return String isbn
         */
        public String getIsbn() {
            return Book.this.getIsbn();
        }
    }
}