package com.example.hsm_demo.service;

import java.security.KeyStore;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Base64;
import java.util.Enumeration;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

import org.springframework.stereotype.Service;

import com.example.hsm_demo.configs.Pkcs11ConfigLoader;

import jakarta.annotation.PostConstruct;

@Service
public class HsmCryptoService {

	private Provider provider;
	private KeyStore keyStore;

	private static final String PIN = "Yotta123";

	@PostConstruct
	public void init() {

		try {
			String cfg = Pkcs11ConfigLoader.loadConfig();

			// ✅ CORRECT WAY (NO sun.security.pkcs11)
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

	public void listKeys() throws Exception {

		System.out.println("---- HSM KEYS ----");

		Enumeration<String> e = keyStore.aliases();

		while (e.hasMoreElements()) {
			System.out.println("Key: " + e.nextElement());
		}
	}

	public String encrypt(String alias, String data) throws Exception {

		SecretKey key = (SecretKey) keyStore.getKey(alias, PIN.toCharArray());

		byte[] iv = new byte[12];
		new SecureRandom().nextBytes(iv);

		Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding", provider);

		GCMParameterSpec spec = new GCMParameterSpec(128, iv);

		cipher.init(Cipher.ENCRYPT_MODE, key, spec);

		byte[] enc = cipher.doFinal(data.getBytes());

		return Base64.getEncoder().encodeToString(enc);
	}

	public String decrypt(String alias, String enc) throws Exception {

		SecretKey key = (SecretKey) keyStore.getKey(alias, PIN.toCharArray());

		byte[] iv = new byte[12];

		Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding", provider);

		cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(128, iv));

		return new String(cipher.doFinal(Base64.getDecoder().decode(enc)));
	}
}