package io.core9.commerce.cart;

import io.core9.plugin.database.mongodb.MongoDatabase;
import io.core9.plugin.importer.processor.AbstractProcessor;
import io.core9.plugin.importer.processor.ImporterConfig;
import io.core9.plugin.server.VirtualHost;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EncryptionProcessor extends AbstractProcessor<EncryptionProcessorConfig> {
	
	private CommerceEncryptionPlugin encryption;

	public EncryptionProcessor(CommerceEncryptionPluginImpl plugin) {
		this.encryption = plugin;
	}

	@Override
	public Class<? extends ImporterConfig> getConfigClass() {
		return EncryptionProcessorConfig.class;
	}

	@Override
	public String getProcessorIdentifier() {
		return "commerce_encryption";
	}

	@Override
	public String getProcessorName() {
		return "Commerce Encryption Processor";
	}

	//TODO Cleanup
	@SuppressWarnings("unchecked")
	@Override
	public String process(String type, VirtualHost vhost, EncryptionProcessorConfig configuration) {
		MongoDatabase db = this.pgetImporterPlugin().getRepository();
		Map<String,Object> contentSpec = getContentSpecification(db, vhost, configuration.getContentType());
		Map<String,List<String>> hashFields = getHashFields(contentSpec);
		if(hashFields != null) {
			List<Map<String,Object>> items = db.getMultipleResults((String) vhost.getContext("database"), vhost.getContext("prefix") + configuration.getContentType(), new HashMap<String, Object>());
			for(Map<String,Object> item : items) {
				for(Map.Entry<String, List<String>> hashField : hashFields.entrySet()) {
					Map<String,Object> hashData = new HashMap<String, Object>();
					for(String field : hashField.getValue()) {
						hashData.put(field, item.get(field));
					}
					String hash = encryption.encrypt(hashData);
					if(item.get(hashField.getKey()) == null || !hash.equals(((Map<String,Object>) item.get(hashField.getKey())).get("hash"))) {
						Map<String,Object> hashSettings = new HashMap<String, Object>();
						hashSettings.put("fields", hashField.getValue());
						hashSettings.put("hash", hash);
						item.put(hashField.getKey(), hashSettings);
						Map<String,Object> query = new HashMap<String, Object>();
						query.put("_id", item.get("_id"));
						db.upsert((String) vhost.getContext("database"), vhost.getContext("prefix") + configuration.getContentType(), item, query);
					}
				}
			}
		}
		return "ok";
	}
	
	private Map<String,Object> getContentSpecification(MongoDatabase db, VirtualHost vhost, String contentType) {
		Map<String,Object> query = new HashMap<String, Object>();
		query.put("configtype", "content");
		query.put("name", contentType);
		return db.getSingleResult((String) vhost.getContext("database"), vhost.getContext("prefix") + "configuration", query);
	}
	
	@SuppressWarnings("unchecked")
	private Map<String,List<String>> getHashFields(Map<String,Object> contentSpec) {
		Map<String,Object> schema = (Map<String,Object>) contentSpec.get("schemaOptions");
		Map<String, List<String>> result = null;
		for(Map.Entry<String, Object> field : schema.entrySet()) {
			Map<String,Object> fieldSpec = (Map<String,Object>) field.getValue();
			if(fieldSpec.get("widget") != null && fieldSpec.get("widget").equals("hash")) {
				if(result == null) {
					result = new HashMap<String, List<String>>(); 
				}
				result.put(field.getKey(), (List<String>) fieldSpec.get("fields"));
			}
		}
		return result;
	}

	
}
