package edu.northwestern.websail.tabel.model;

public abstract class WikiElement {
    public int offset;
    public int endOffset;
    public boolean isInTemplate;
    public WikiPageLocationType locType;

    public WikiElement() {
    }

    public WikiElement(int start, int end, WikiPageLocationType locType, boolean isInTemplate) {
        this.offset = start;
        this.endOffset = end;
        this.isInTemplate = isInTemplate;
        this.locType = locType;
    }

    public WikiElement(int start, int end, WikiPageLocationType locType) {
        this.offset = start;
        this.endOffset = end;
        this.locType = locType;
        this.isInTemplate = false;
    }

}