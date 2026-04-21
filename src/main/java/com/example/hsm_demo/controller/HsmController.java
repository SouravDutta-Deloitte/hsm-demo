package com.example.hsm_demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.hsm_demo.service.HsmCryptoService;

@RestController
@RequestMapping("/hsm")
public class HsmController {

	private final HsmCryptoService service;

	public HsmController(HsmCryptoService service) {
		this.service = service;
	}

	@GetMapping("/keys")
	public String keys() throws Exception {
		service.listKeys();
		return "Keys printed in logs";
	}

	@PostMapping("/encrypt")
	public String encrypt(@RequestParam String alias, @RequestParam String data) throws Exception {
		return service.encrypt(alias, data) + "\n";
	}

	@PostMapping("/decrypt")
	public String decrypt(@RequestParam String alias, @RequestParam String data) throws Exception {
		return service.decrypt(alias, data) + "\n";
	}
}