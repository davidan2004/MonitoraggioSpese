package it.uniroma3.monitoraggio.controller;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

import it.uniroma3.monitoraggio.model.Transaction;
import it.uniroma3.monitoraggio.model.TransactionCategory;
import it.uniroma3.monitoraggio.model.User;
import it.uniroma3.monitoraggio.service.TransactionService;
import it.uniroma3.monitoraggio.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class TransactionController {

	@Autowired
	private TransactionService transactionService;
	
	@Autowired
	private UserService userService;
	
	/* Per Modal */
	@GetMapping("/transactions/add/{userId}")
	public String getAddTransactionForm(@PathVariable Long userId, @RequestParam String fromPage, 
			Model model, HttpSession session) {
		model.addAttribute("userId", userId);	
		model.addAttribute("expenseCategories", TransactionCategory.getExpenseCategories());
		model.addAttribute("profitCategories", TransactionCategory.getProfitCategories());
		model.addAttribute("fromPage", fromPage);
		
		Transaction transaction = (Transaction) session.getAttribute("transactionWithErrors");
		String errors = (String) session.getAttribute("errors");
		
		if(transaction == null) {
			if(session.getAttribute("editTransaction") != null) {
				model.addAttribute("transaction", (Transaction) session.getAttribute("editTransaction"));
				model.addAttribute("edit", true);
				session.removeAttribute("editTransaction");
			}
			else
				model.addAttribute("transaction", new Transaction());
		}
		else {
			model.addAttribute("transaction", transaction);
			session.removeAttribute("transactionWithErrors");
		}
		
		User currentUser = userService.getById(userId);
		model.addAttribute("paymentMethods", currentUser.getPaymentMethods());
		
		if(errors !=null) {
			session.removeAttribute("errors");
			List<String> errorList = Arrays.asList(errors.split(","));
			if(errorList.contains("wrongAmount"))
				model.addAttribute("wrongAmount", "Inserire un importo valido");
			if(errorList.contains("emptyDesc"))
				model.addAttribute("emptyDesc", "Inserire una descrizione");
			if(errorList.contains("wrongDate"))
				model.addAttribute("wrongDate", "Inserire una data passata");
		}
		
		
		return "fragments/addTransactionForm.html";
	}
	

	@PostMapping("/transactions/add/{userId}")
	public String addTransaction(@PathVariable Long userId,@Valid @ModelAttribute Transaction transaction, BindingResult bindingResult,
			@RequestParam String fromPage, RedirectAttributes redirectAttributes , @RequestParam(required=false) Boolean edit,
			Model model, HttpSession session) {
		if(bindingResult.hasErrors()) {
			String errors = "";
			
			if(transaction.getAmount() <= 0)
				errors += "wrongAmount,";
			if(transaction.getDescription().isBlank())
				errors += "emptyDesc,";
			if(transaction.getDate() == null ||  transaction.getDate().isAfter(LocalDate.now()))
				errors += "wrongDate,";

			redirectAttributes.addFlashAttribute("modalToShow", "addTransaction");
			session.setAttribute("errors", errors);
			if(edit != null && edit)
				session.setAttribute("editTransaction", transaction);
			else
				session.setAttribute("transactionWithErrors", transaction);
			return "redirect:" + fromPage;
		}
		
		User currentUser = userService.getById(userId);
		
		if(!(currentUser.getId() == userService.getCurrentUser().getId())) {
			redirectAttributes.addFlashAttribute("change", "Operazione non permessa");
			return "redirect:" + fromPage;
		}
		
		if(edit != null && edit) {
		    Transaction existing = transactionService.getById(transaction.getId());
		    existing.setAmount(transaction.getAmount());
		    existing.setCategory(transaction.getCategory());
		    existing.setDate(transaction.getDate());
		    existing.setDescription(transaction.getDescription());
		    existing.setPaymentMethod(transaction.getPaymentMethod());
		    existing.setType(transaction.getType());
		    transactionService.save(existing);
		    redirectAttributes.addFlashAttribute("change", "Transazione modificata correttamente");
		}
		else {
		currentUser.getTransactions().add(transaction);
			transaction.setUser(currentUser);
			transactionService.save(transaction);
			userService.save(currentUser);
			
			redirectAttributes.addFlashAttribute("change", "Transazione aggiunta correttamente");
		}
		return "redirect:" + fromPage;
	}
	
	@GetMapping("/transactions/manage/{userId}")
	public String viewTransactions(@PathVariable Long userId, RedirectAttributes redirectAttributes, Model model) {
		User currentUser = userService.getById(userId);
		
		if( currentUser == null || !(currentUser.getId().equals(userService.getCurrentUser().getId()))) {
			redirectAttributes.addFlashAttribute("change", "Operazione non permessa");
			return "redirect:/";
		}
		
		model.addAttribute("transactions", currentUser.getTransactions());
		model.addAttribute("user", currentUser);
		
		/* Per grafico ultime 10 transazioni */
		List<Transaction> last10Transactions = currentUser.getLast10Transactions();
		
        List<String> recentTransactionsDescriptions = last10Transactions.stream()
                .map(Transaction::getDescription)
                .collect(Collectors.toList());
        List<Integer> recentTransactionsAmounts = last10Transactions.stream()
                .map(Transaction::getAmount)
                .collect(Collectors.toList());
        List<String> recentTransactionsTypes = last10Transactions.stream()
                .map(transaction -> transaction.getType().name())
                .collect(Collectors.toList());
        List<String> recentTransactionsDates = last10Transactions.stream()
                .map(transaction -> transaction.getDate().toString())
                .collect(Collectors.toList());
        
        model.addAttribute("transactionsDescriptions", recentTransactionsDescriptions);
        model.addAttribute("transactionsAmounts", recentTransactionsAmounts);
        model.addAttribute("transactionsTypes", recentTransactionsTypes);
        model.addAttribute("transactionsDates", recentTransactionsDates);
		
		return "manageTransactions.html";
	}
	
	@PostMapping("/transactions/delete/{userId}/{transactionId}")
	public String deleteTransaction(@PathVariable Long userId, @PathVariable Long transactionId,
			RedirectAttributes redirectAttributes, Model model) {
		User currentUser = userService.getById(userId);
		
		if( currentUser == null || !(currentUser.getId().equals(userService.getCurrentUser().getId()))) {
			redirectAttributes.addFlashAttribute("change", "Operazione non permessa");
			return "redirect:/";
		}		
		
		Transaction toDelete = transactionService.getById(transactionId);
		currentUser.getTransactions().remove(toDelete);
		userService.save(currentUser);
		
		redirectAttributes.addFlashAttribute("change", "Transazione rimossa correttamente");
		return "redirect:/transactions/manage/" + currentUser.getId();
	}
	
	@GetMapping("/transactions/edit/{userId}/{transactionId}")
	public String editTransaction(@PathVariable Long userId, @PathVariable Long transactionId,
			@RequestParam String fromPage, Model model, RedirectAttributes redirectAttributes, HttpSession session) {
		Transaction transaction = transactionService.getById(transactionId);
		User currentUser = userService.getById(userId);
		
		if( currentUser == null || !(currentUser.getId().equals(userService.getCurrentUser().getId()))) {
			redirectAttributes.addFlashAttribute("change", "Operazione non permessa");
			return "redirect:" + fromPage;
		}
		List<Transaction> userTransactions = currentUser.getTransactions();
		if(transaction == null || !userTransactions.contains(transaction)) {
			redirectAttributes.addFlashAttribute("change", "Operazione non permessa");
			return "redirect:" + fromPage;
		}
		
		session.setAttribute("editTransaction", transaction);
		
		return "redirect:/transactions/add/" + userId + "?fromPage=" + fromPage;
	}
	
}
