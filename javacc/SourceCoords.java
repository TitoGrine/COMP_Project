public class SourceCoords {
    Token token;

    SourceCoords(Token t){
        token = t;
    }

    public int getTokenLine(){
        return this.token.beginLine;
    }

    public int getTokenColumn() { return this.token.beginColumn; }
}
