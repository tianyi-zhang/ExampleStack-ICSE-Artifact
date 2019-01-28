import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;


/* 
 * Een commit object is een object dat behoort tot een bepaalde commit.
 * Deze bevat: 
 * 		- Een set van files die tot de commit behoren
 * 		- Referentie naar een parent commit object 
 * 		- SHA1 ID, een 40-char string dat het object identificeert
 */
public class CommitObject implements Serializable {

	private static final long serialVersionUID = 1L;
	public ClientRepository repo;
	public List<String> files;
	public String author;
	public String message;
	public String ID;
	public CommitObject parent;
	//public String CurrentObjectDirectory;
	
	public CommitObject(ClientRepository repo, CommitObject parent, String message, String author) throws IOException {
		this.repo = repo;
		this.parent = parent;
		this.message = message;
		this.author = author;
		files = repo.index;
	
		//unique ID creeren
		SHA1 hash = new SHA1(FilesToByteArray(files));
		ID = hash.getSHA1();
		
	   // MetaFile();
	}
	
	public boolean equals(CommitObject co) {
		return ID.equals(co.ID);
	}
	
	//Makes a bytearray from all the files that are in the staging area
	public byte[] FilesToByteArray(List<String> files) throws IOException {
		byte[] buffer;
		int length = 0;
		for(int i=0; i<=files.size()-1; i++) {
			String path = repo.ProjectDirectory + File.separator + files.get(i);
			File file = new File(path);
			length += file.length();
		}
		buffer = new byte[length];
		int bufptr = 0;
		for(int i=0; i<=files.size()-1; i++) {
			String path = repo.ProjectDirectory + File.separator + files.get(i);
			File file = new File(path);
			FileInputStream fis = new FileInputStream(file);
			fis.read(buffer, bufptr, (int)file.length());
			bufptr += (int)file.length();
			fis.close();
		}
		return buffer;
	}
	
	
	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
	public static String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for ( int j = 0; j < bytes.length; j++ ) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}
}
