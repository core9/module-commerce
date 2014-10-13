package io.core9.commerce.cart.old;

import java.util.Map;

import io.core9.core.plugin.Core9Plugin;
import io.core9.plugin.admin.AdminPlugin;

public interface CommerceEncryptionPlugin extends Core9Plugin, AdminPlugin {

	String encrypt(String content);
	
	String encrypt(Map<String,Object> map);
	
	String encrypt(String key, String content);
	
	String encrypt(String key, Map<String,Object> map);

}
