package com.biblioteca.sistema.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // Mapea varias rutas comunes hacia el index estático
    // NOTE: do NOT map "/index.html" here — forwarding to /index.html would re-invoke this
    // controller and cause an infinite forward loop. Keep only friendly root/index routes.
    @GetMapping({"/", "", "/.", "/index"})
    public String index() {
        // Forward para servir el recurso estático en /static/index.html
        return "forward:/index.html";
    }
}
