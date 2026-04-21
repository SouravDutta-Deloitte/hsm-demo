package com.example.hsm_demo.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.hsm_demo.service.HsmCryptoService;

@RestController
@RequestMapping("/crypto")
public class CryptoController {

	private final HsmCryptoService service;

	public CryptoController(HsmCryptoService service) {
		this.service = service;
	}

	@PostMapping("/encrypt")
	public String encrypt(@RequestBody String data) throws Exception {
		return service.encrypt(data);
	}

	@PostMapping("/decrypt")
	public String decrypt(@RequestBody String data) throws Exception {
		return service.decrypt(data);
	}
}