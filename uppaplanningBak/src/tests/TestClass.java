package tests;

import net.irokwa.uppa.hyperplanning.HttpConnection;

public class TestClass {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		HttpConnection http = new HttpConnection();
		
		System.out.println(http.getContent("http://sciences.univ-pau.fr/edt/"));
		System.out.println(http.getContentLenght("http://sciences.univ-pau.fr/edt/"));
		System.out.println(http.getLastModified("http://sciences.univ-pau.fr/edt/"));
		
	}

}
