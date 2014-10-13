package io.core9.commerce.cart.old;

import io.core9.commerce.cart.old.CommerceEncryptionPlugin;
import io.core9.plugin.importer.ImporterPlugin;
import io.core9.plugin.server.request.Request;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.annotations.events.PluginLoaded;

@PluginImplementation
public class CommerceEncryptionPluginImpl implements CommerceEncryptionPlugin {
	
	@PluginLoaded
	public void onImportPluginAvailable(ImporterPlugin importer) {
		importer.registerProcessor(new EncryptionProcessor(this));
	}
	
	@Override
	public String getControllerName() {
		return "commerce_encrypt";
	}

	@Override
	public void handle(Request request) {
		request.getResponse().end(encrypt(request.getBodyAsMap().toBlocking().last()));
	}

	@Override
	public String encrypt(String content) {
		return encrypt(System.getenv("CORE9_ENCRYPTION_KEY"), content);
	}

	@Override
	public String encrypt(Map<String, Object> map) {
		return encrypt(System.getenv("CORE9_ENCRYPTION_KEY"), map);
	}

	@Override
	public String encrypt(String key, String content) {
		return encrypt((key + content).getBytes());
	}

	@Override
	public String encrypt(String key, Map<String, Object> map) {
		SortedSet<String> mapKeys = new TreeSet<String>(map.keySet());
		String input = "";
		for(String mapKey : mapKeys) {
			input += mapKey + "=" + map.get(mapKey) + key;
		}
		return encrypt(input.getBytes());
	}
	
	private String encrypt(byte[] bytes) {	
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return bytesToHex(md.digest(bytes)).toUpperCase();
	}

	private String bytesToHex(byte[] bytes) {
		char[] hexArray = "0123456789ABCDEF".toCharArray();
	    char[] hexChars = new char[bytes.length * 2];
	    for ( int j = 0; j < bytes.length; j++ ) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}
}
