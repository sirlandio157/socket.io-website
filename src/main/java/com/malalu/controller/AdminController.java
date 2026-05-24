package com.malalu.controller;

import com.malalu.service.CredentialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private CredentialService credentialService;

    @GetMapping("/credentials")
    @ResponseBody
    public String showForm(@RequestParam(required = false) String msg) {
        String username = credentialService.getUsername();
        StringBuilder sb = new StringBuilder();
        sb.append("<html><head><meta charset='utf-8'><title>Alterar credenciais</title></head><body>");
        if (msg != null) {
            sb.append("<p style='color:green;'>").append(msg).append("</p>");
        }
        sb.append("<form method='post' action='/admin/credentials'>");
        sb.append("Usuário: <input name='username' value='").append(username).append("' /> <br/>");
        sb.append("Senha: <input name='password' type='password' /> <br/>");
        sb.append("<button type='submit'>Atualizar</button>");
        sb.append("</form>");
        sb.append("</body></html>");
        return sb.toString();
    }

    @PostMapping("/credentials")
    public String update(@RequestParam String username, @RequestParam String password, RedirectAttributes redirect) {
        try {
            credentialService.updateCredentials(username, password);
            redirect.addAttribute("msg", "Credenciais atualizadas com sucesso");
        } catch (Exception e) {
            redirect.addAttribute("msg", "Erro ao atualizar: " + e.getMessage());
        }
        return "redirect:/admin/credentials";
    }
}
