package edu.northwestern.websail.tabel.text;

import java.io.Serializable;

/**
 * Created by riflezhang on 11/10/16.
 */
public class Token implements Serializable {
    private static final long serialVersionUID = -8977067965522627662L;
    public static Token START = new Token("<s>", -1, -1, -1);
    public static Token END = new Token("</s>", -1, -1, -1);
    public String text;
    public int startOffset;
    public int endOffset;
    public int index;
    public int localIndex = 0;
    public Token() {
    }

    public Token(String text, int start, int end, int index) {
        this.text = text;
        this.startOffset = start;
        this.endOffset = end;
        this.index = index;
    }
}
