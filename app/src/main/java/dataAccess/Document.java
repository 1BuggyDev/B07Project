package dataAccess;

public class Document extends UserData {

    public Document() {};
    public Document(String title, String description) {
        super(title, description);
    }

    public Document(String title, String description, String filePath) {
        super(title, description, filePath);
    }

}
