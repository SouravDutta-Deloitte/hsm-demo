package com.example.hsm_demo.controller;

import java.util.Set;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.hsm_demo.models.DecryptRequest;
import com.example.hsm_demo.models.DecryptResponse;
import com.example.hsm_demo.models.EncryptRequest;
import com.example.hsm_demo.models.EncryptResponse;
import com.example.hsm_demo.service.HsmCryptoService;

@RestController
@RequestMapping("/hsm")
public class HsmController {

	private final HsmCryptoService service;

	public HsmController(HsmCryptoService service) {
		this.service = service;
	}

	@GetMapping("/keys")
	public Set<String> keys() throws Exception {
		return service.listKeys();
	}

	@PostMapping("/encrypt")
	public EncryptResponse encrypt(@RequestBody EncryptRequest encryptRequest) throws Exception {
		return service.encrypt(encryptRequest);
	}

	@PostMapping("/decrypt")
	public DecryptResponse decrypt(@RequestBody DecryptRequest decryptRequest) throws Exception {
		return service.decrypt(decryptRequest);
	}
}