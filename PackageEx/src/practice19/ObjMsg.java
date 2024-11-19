// 1911249 나준선
package practice19;

import java.io.File;
import java.io.Serializable;
import java.util.Vector;

import javax.swing.ImageIcon;


public class ObjMsg implements Serializable{
	public final static int MODE_LOGIN			=  0x1;
	public final static int MODE_LOGOUT			=  0x2;
	public final static int MODE_TX_STRING		= 0x10;
	public final static int MODE_TX_FILE		= 0x20;
	public final static int MODE_TX_IMAGE		= 0x40;
	
	String userID;
	int mode;
	String message;
	ImageIcon image;
	long size;
	String filename;
	Vector<byte[]> file = new Vector<>();
	
	public ObjMsg(String userID, int code, String message, ImageIcon image, long size, Vector<byte[]> file) {
		this.userID = userID; 
		this.mode = code;
		this.message = message;
		this.image = image;
		this.size = size;
		this.file = file;
	}
	
	public ObjMsg(String userID, int code, String message, ImageIcon image) {
		this(userID, code, message, image, 0, null);
	}
	
	public ObjMsg(String userID, int code) {
		this(userID, code, null, null, 0, null);
	}
	
	public ObjMsg(String userID, int code, String message) {
		this(userID, code, message, null, 0, null);
	}
	
	public ObjMsg(String userID, int code, ImageIcon image) {
		this(userID, code, null, image);
	}
	
	public ObjMsg(String userID, int code, String filename, long size) {
		this(userID, code, filename, null, size, null);
	}
	
	public ObjMsg(String userID, int code, String filename, Vector<byte[]> file) {
		this.userID = userID; 
		this.mode = code;
		this.filename = filename;
		this.file = file;
	}
}
