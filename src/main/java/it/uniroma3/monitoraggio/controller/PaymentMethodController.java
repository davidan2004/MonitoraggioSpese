package it.uniroma3.monitoraggio.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.uniroma3.monitoraggio.model.PaymentMethod;
import it.uniroma3.monitoraggio.model.PaymentType;
import it.uniroma3.monitoraggio.model.User;
import it.uniroma3.monitoraggio.service.PaymentMethodService;
import it.uniroma3.monitoraggio.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class PaymentMethodController {

	@Autowired
	private PaymentMethodService paymentMethodService;
	
	@Autowired
	private UserService userService;
	
	/* Per Modal */
	@GetMapping("/methods/add/{userId}")
	public String getAddMethodForm(@PathVariable Long userId, @RequestParam String fromPage,
			RedirectAttributes redirectAttributes, Model model, HttpSession session) {
		
		User currentUser = userService.getById(userId);
		if( currentUser == null || !(currentUser.getId().equals(userService.getCurrentUser().getId()))) {
			redirectAttributes.addFlashAttribute("change", "Operazione non permessa");
			return "redirect:/";
		}	
		
		model.addAttribute("userId", userId);	
		model.addAttribute("method", new PaymentMethod());
		model.addAttribute("methodTypes", PaymentType.values());
		model.addAttribute("fromPage", fromPage);
		
		String errors = (String) session.getAttribute("errors");
		if(errors != null) {
			session.removeAttribute("errors");
			List<String> errorList = Arrays.asList(errors.split(","));
			if(errorList.contains("emptyName")) {
				model.addAttribute("emptyName", "Inserire un nome per il metodo di pagamento");
			}
		}

		return "fragments/addMethodForm.html";
	}
	
	
	@PostMapping("/methods/add/{userId}")
	public String addMethod(@PathVariable Long userId,@Valid @ModelAttribute PaymentMethod paymentMethod, BindingResult bindingResult,
			@RequestParam String fromPage, RedirectAttributes redirectAttributes , Model model, HttpSession session) {
		
		User currentUser = userService.getById(userId);
		if( currentUser == null || !(currentUser.getId().equals(userService.getCurrentUser().getId()))) {
			redirectAttributes.addFlashAttribute("change", "Operazione non permessa");
			return "redirect:/";
		}	
		
		if(bindingResult.hasErrors()) {
			String errors = "";
			
			if(paymentMethod.getName().isBlank())
				errors += "emptyName,";
			
	        session.setAttribute("errors", errors);
	        redirectAttributes.addFlashAttribute("modalToShow", "addMethod");
			return "redirect:" + fromPage;
		}
		
//		paymentMethodService.save(paymentMethod);
		currentUser.getPaymentMethods().add(paymentMethod);
		userService.save(currentUser);
		
		redirectAttributes.addFlashAttribute("change", "Metodo di pagamento aggiunto correttamente");
		return "redirect:" + fromPage;
	}
	
	@GetMapping("/methods/manage/{userId}")
	public String viewMethods(@PathVariable Long userId, Model model) {
		User currentUser = userService.getById(userId);
		model.addAttribute("paymentMethods", currentUser.getPaymentMethods());
		model.addAttribute("user", currentUser);
		
		return "manageMethods.html";
	}
	
	@PostMapping("/methods/delete/{userId}/{methodId}")
	public String deleteMethod(@PathVariable Long userId, @PathVariable Long methodId,
			RedirectAttributes redirectAttributes, Model model) {
		User currentUser = userService.getById(userId);
		if( currentUser == null || !(currentUser.getId().equals(userService.getCurrentUser().getId()))) {
			redirectAttributes.addFlashAttribute("change", "Operazione non permessa");
			return "redirect:/";
		}			
		
		
		PaymentMethod toDelete = paymentMethodService.getById(methodId);
		currentUser.getPaymentMethods().remove(toDelete);
		userService.save(currentUser);
//		paymentMethodService.removeById(methodId);
		
		redirectAttributes.addFlashAttribute("change", "Metodo di pagamento rimosso correttamente");
		return "redirect:/methods/manage/" + currentUser.getId();
	}
}
