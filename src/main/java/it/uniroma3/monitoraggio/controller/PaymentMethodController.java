package it.uniroma3.monitoraggio.controller;

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
import jakarta.validation.Valid;

@Controller
public class PaymentMethodController {

	@Autowired
	private PaymentMethodService paymentMethodService;
	
	@Autowired
	private UserService userService;
	
	/* Per Modal */
	@GetMapping("/methods/add/{userId}")
	public String getAddMethodForm(@PathVariable Long userId, @RequestParam String fromPage, Model model) {
		model.addAttribute("userId", userId);	
		model.addAttribute("method", new PaymentMethod());
		model.addAttribute("methodTypes", PaymentType.values());
		model.addAttribute("fromPage", fromPage);

		return "fragments/addMethodForm.html";
	}
	
	
	@PostMapping("/methods/add/{userId}")
	public String addMethod(@PathVariable Long userId,@Valid @ModelAttribute PaymentMethod paymentMethod, BindingResult bindingResult,
			@RequestParam String fromPage, RedirectAttributes redirectAttributes , Model model) {
		if(bindingResult.hasErrors()) {
			redirectAttributes.addFlashAttribute("paymentMethod", paymentMethod);
	        redirectAttributes.addFlashAttribute("emptyName", "Inserire un nome ed una descrizione per la transazione.");
	        redirectAttributes.addFlashAttribute("showAddTransactionModal", true);
			return "redirect:" + fromPage;
		}
		
//		paymentMethodService.save(paymentMethod);
		User currentUser = userService.getById(userId);
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
		PaymentMethod toDelete = paymentMethodService.getById(methodId);
		currentUser.getPaymentMethods().remove(toDelete);
		userService.save(currentUser);
//		paymentMethodService.removeById(methodId);
		
		redirectAttributes.addFlashAttribute("change", "Metodo di pagamento rimosso correttamente");
		return "redirect:/methods/manage/" + currentUser.getId();
	}
}
