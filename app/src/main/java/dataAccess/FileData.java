package dataAccess;

public class FileData {
    private byte[] data;
    private String name;
    private String type;
    public FileData(byte[] data, String name, String type) {
        this.data = data;
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    /**
     * Returns the file type
     * @return the file type
     */
    public String getType() {
        return type;
    }

    public byte[] getData() {
        return data;
    }
}
