package aux;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Vector;


public class CCPrinter {
	Vector<String> msgs;
	
	public CCPrinter() {
		msgs = new Vector<String>();
	}
	
	public void print(String s) {
		msgs.addElement(s);
	}
	
	public void println(String s) {
		msgs.addElement(s+"\n");
	}
	
	public void printAll() {
		for (int i=0; i<msgs.size(); i++) {
			System.out.print(msgs.elementAt(i));
		}
	}
	
	public InputStream toInputStream() {
		String tmp = "";
		for (int i=0; i<msgs.size(); i++) {
			tmp += msgs.elementAt(i);
		}
		return new ByteArrayInputStream(tmp.getBytes());
	}
}
