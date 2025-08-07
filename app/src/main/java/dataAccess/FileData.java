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

    public void setName(String name) {
        this.name = name;
    }
    /**
     * Gets the file extension
     * @return the file extension or the file name if it has no extension
     */
    public String getExtension() {
        return name.substring(name.lastIndexOf('.') + 1);
    }

    /**
     * Returns the file type
     * @return the file type
     */
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public byte[] getData() {
        return data;
    }
}
