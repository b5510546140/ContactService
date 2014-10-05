package contact.entity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
/**
 * This class use to encode
 * @author wat wattanagaroon
 * @version 2014/10/5
 */
public abstract class Md5 {

	/**
	 * Get MD5 of this object.
	 * @return MD5 of this object.
	 */
	public abstract String getMd5();

	/**
	 * Digest the UTF-8 String to MD5.
	 * @param data data to be digested.
	 * @return String of MD5.
	 * @throws NoSuchAlgorithmException 
	 */
	protected String digest( String data )  {
		MessageDigest md;
		StringBuffer sb = null;
		try {
			md = MessageDigest.getInstance("MD5");

			md.update(data.getBytes());

			byte byteData[] = md.digest();

			//convert the byte to hex format method 1
			sb = new StringBuffer();
			for (int i = 0; i < byteData.length; i++) {
				sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
//		System.out.println(sb.toString());
		return sb.toString();
	}

}