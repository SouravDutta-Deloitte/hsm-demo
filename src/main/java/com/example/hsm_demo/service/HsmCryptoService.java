package com.example.hsm_demo.service;

import java.security.KeyStore;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Base64;
import java.util.Enumeration;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
public class HsmCryptoService {

	private KeyStore keyStore;
	private Provider provider;

	private final String PIN = "Yotta123";

	@PostConstruct
	public void init() throws Exception {

		provider = Security.getProvider("SunPKCS11").configure("src/main/resources/hsm/pkcs11.cfg");

		Security.addProvider(provider);

		keyStore = KeyStore.getInstance("PKCS11", provider);
		keyStore.load(null, PIN.toCharArray());
	}

	public String encrypt(String plainText) throws Exception {

		SecretKey key = getKey("java_aes_key");

		byte[] iv = new byte[16];
		new SecureRandom().nextBytes(iv);

		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

		cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));

		byte[] encrypted = cipher.doFinal(plainText.getBytes());

		return Base64.getEncoder().encodeToString(iv) + ":" + Base64.getEncoder().encodeToString(encrypted);
	}

	public String decrypt(String input) throws Exception {

		String[] parts = input.split(":");

		byte[] iv = Base64.getDecoder().decode(parts[0]);
		byte[] data = Base64.getDecoder().decode(parts[1]);

		SecretKey key = getKey("java_aes_key");

		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

		cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));

		return new String(cipher.doFinal(data));
	}

	private SecretKey getKey(String alias) throws Exception {

		Enumeration<String> aliases = keyStore.aliases();

		while (aliases.hasMoreElements()) {
			String a = aliases.nextElement();
			if (a.equals(alias)) {
				return (SecretKey) keyStore.getKey(a, PIN.toCharArray());
			}
		}

		throw new RuntimeException("HSM key not found: " + alias);
	}
}