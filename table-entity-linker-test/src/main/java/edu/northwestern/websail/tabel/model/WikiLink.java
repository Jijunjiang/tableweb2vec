package edu.northwestern.websail.tabel.model;

public class WikiLink extends WikiElement {
    public WikiTitle target;
    public String surface;
    public LinkType linkType;

    public WikiLink() {
        super();
    }

    public WikiLink(int start, int end, WikiPageLocationType locType, String surface, WikiTitle target,
                    LinkType type) {
        super(start, end, locType);
        this.target = target;
        this.surface = surface;
        this.linkType = type;
    }

    public WikiLink(int start, int end, WikiPageLocationType locType, String surface, WikiTitle target) {
        super(start, end, locType);
        this.target = target;
        this.surface = surface;
        this.linkType = LinkType.UNSET;
    }

    public WikiLink(int start, int end, WikiPageLocationType locType, String surface, WikiTitle
            target, boolean isInTemplate) {
        super(start, end, locType, isInTemplate);
        this.target = target;
        this.surface = surface;
    }

    public String toString() {
        return this.surface + "->" + target;
    }
}
