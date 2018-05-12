package edu.northwestern.websail.tabel.model;

public class WikiTitle {
    public int id;
    public String language;
    public String title;
    public boolean redirecting;
    public WikiTitle redirectedTitle;
    public int namespace;

    public WikiTitle() {
    }

    public WikiTitle(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public WikiTitle(String language, int id, String title) {
        this(language, id, title, false, null);
    }

    public WikiTitle(String language, int id, String title, boolean redirecting, WikiTitle redirectedTitle) {
        this.id = id;
        this.language = language;
        this.title = title;
        this.redirecting = redirecting;
        this.redirectedTitle = redirectedTitle;
        this.namespace = 0;
    }

    public WikiTitle(String language, int id, String title, boolean redirecting, WikiTitle redirectedTitle,
                     int namespace) {
        this.id = id;
        this.language = language;
        this.title = title;
        this.redirecting = redirecting;
        this.redirectedTitle = redirectedTitle;
        this.namespace = namespace;
    }
}
