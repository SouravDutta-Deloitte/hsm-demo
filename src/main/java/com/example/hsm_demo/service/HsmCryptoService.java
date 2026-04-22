package com.example.hsm_demo.service;

import java.security.KeyStore;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Base64;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import org.springframework.stereotype.Service;

import com.example.hsm_demo.configs.Pkcs11ConfigLoader;
import com.example.hsm_demo.models.DecryptRequest;
import com.example.hsm_demo.models.DecryptResponse;
import com.example.hsm_demo.models.EncryptRequest;
import com.example.hsm_demo.models.EncryptResponse;

import jakarta.annotation.PostConstruct;

@Service
public class HsmCryptoService {

	private volatile String activeKeyAlias = "adv_aes_v1";

	private Provider provider;
	private KeyStore keyStore;

	private static final String PIN = "Yotta123";

	@PostConstruct
	public void init() {

		try {
			String cfg = Pkcs11ConfigLoader.loadConfig();

			Provider pkcs11Provider = Security.getProvider("SunPKCS11");
			provider = pkcs11Provider.configure(cfg);

			Security.addProvider(provider);

			keyStore = KeyStore.getInstance("PKCS11", provider);
			keyStore.load(null, PIN.toCharArray());

			System.out.println("HSM Provider Loaded: " + provider.getName());

		} catch (Exception e) {
			throw new RuntimeException("HSM init failed", e);
		}
	}

	public Map<String, String> rotateKey(String newAlias) {

		this.activeKeyAlias = newAlias;

		Map<String, String> response = new HashMap<>();
		response.put("activeKey", activeKeyAlias);
		response.put("message", "Key rotated successfully");

		return response;
	}

	public Set<String> listKeys() throws Exception {

		Enumeration<String> keyAliases = keyStore.aliases();

		Set<String> keys = new HashSet<>();

		while (keyAliases.hasMoreElements()) {
			String alias = keyAliases.nextElement();
			keys.add(alias);
		}

		return keys;
	}

	public EncryptResponse encrypt(EncryptRequest encryptRequest) throws Exception {

		String data = encryptRequest.getData();

		SecretKey key = (SecretKey) keyStore.getKey(this.activeKeyAlias, PIN.toCharArray());

		byte[] iv = new byte[16];
		new SecureRandom().nextBytes(iv);

		IvParameterSpec spec = new IvParameterSpec(iv);

		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", provider);

		cipher.init(Cipher.ENCRYPT_MODE, key, spec);

		byte[] enc = cipher.doFinal(data.getBytes());

		EncryptResponse response = new EncryptResponse();
		response.setEncryptedValue(Base64.getEncoder().encodeToString(enc));
		response.setIvBase64Value(Base64.getEncoder().encodeToString(iv));
		response.setKeyAlias(this.activeKeyAlias);

		return response;
	}

	public DecryptResponse decrypt(DecryptRequest decryptRequest) throws Exception {

		String alias = decryptRequest.getKeyAlias();
		SecretKey key = (SecretKey) keyStore.getKey(alias, PIN.toCharArray());

		byte[] iv = Base64.getDecoder().decode(decryptRequest.getIvBase64Value());
		byte[] cipherText = Base64.getDecoder().decode(decryptRequest.getEncryptedValue());

		IvParameterSpec spec = new IvParameterSpec(iv);

		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", provider);

		cipher.init(Cipher.DECRYPT_MODE, key, spec);

		DecryptResponse response = new DecryptResponse();
		response.setData(new String(cipher.doFinal(cipherText)));

		return response;
	}

}