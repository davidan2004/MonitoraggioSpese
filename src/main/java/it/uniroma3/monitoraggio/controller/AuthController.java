package it.uniroma3.monitoraggio.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.monitoraggio.model.Credentials;
import it.uniroma3.monitoraggio.model.Transaction;
import it.uniroma3.monitoraggio.model.TransactionType;
import it.uniroma3.monitoraggio.model.User;
import it.uniroma3.monitoraggio.service.CredentialsService;
import it.uniroma3.monitoraggio.service.TransactionService;
import it.uniroma3.monitoraggio.service.UserService;
import jakarta.validation.Valid;

@Controller
public class AuthController {

	@Autowired
	private CredentialsService credentialsService;
	
	@Autowired
	private TransactionService transactionService;
	
	@Autowired
	private UserService userService;
	
	@GetMapping("/register")
	public String showRegisterPage(Model model) {
		model.addAttribute("user", new User());
		model.addAttribute("credentials", new Credentials());
		
		return "registerForm.html";
	}
	
	@GetMapping("/login")
	public String showLoginPage(Model model) {
		return "loginForm.html";
	}
	
	@GetMapping("/")
	public String root(Model model, @ModelAttribute("modalToShow") String modalName) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if(authentication instanceof AnonymousAuthenticationToken)
			return "root.html";
		
		UserDetails userDetails = (UserDetails)authentication.getPrincipal();
		Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
		if(credentials.getRole().equals(Credentials.ADMIN_ROLE))
			return "admin/rootAdmin.html";
		
		User currentUser = credentials.getUser();
		model.addAttribute("user", currentUser);
		model.addAttribute("paymentMethods", currentUser.getPaymentMethods());
		model.addAttribute("recentTransactions", currentUser.getLast10Transactions());
		model.addAttribute("report", currentUser.getReport());
		model.addAttribute("modalToShow", modalName);	
		
		/* Per chart */
		List<Transaction> transactions = transactionService.findBetweenDates(currentUser, LocalDate.of(2000, 1, 1), LocalDate.now());
				
		
		
		model.addAttribute("expenseAmount", Transaction.getTotalAmount(transactions, TransactionType.EXPENSE));
		model.addAttribute("profitAmount", Transaction.getTotalAmount(transactions, TransactionType.PROFIT));

		
		return "dashboard.html";
	}
	
	
	@GetMapping("/success")
	public String loginSuccessful(Model model) {
		UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
		if(credentials.getRole().equals(Credentials.ADMIN_ROLE))
			return "admin/rootAdmin.html";
		return "redirect:/";
	}
	
	@PostMapping("/register") 
	public String registerUser(@Valid @ModelAttribute User user, BindingResult userBindingResult,
			@Valid @ModelAttribute Credentials credentials, BindingResult credentialsBindingResult, Model model) {
		
		if(credentialsService.isUsernameTaken(credentials.getUsername()))
			credentialsBindingResult.rejectValue("username", "error.user", "Username già in uso");
			
	
		if(!userBindingResult.hasErrors() && !credentialsBindingResult.hasErrors()) {
			userService.save(user);
			credentials.setUser(user);
			credentialsService.saveCredentials(credentials);
			model.addAttribute("user", user);
			return "registrationSuccessful.html";
		}

		return "registerForm.html";
	}
}
