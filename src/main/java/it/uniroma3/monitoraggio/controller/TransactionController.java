package it.uniroma3.monitoraggio.controller;

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
import jakarta.validation.Valid;

@Controller
public class TransactionController {

	@Autowired
	private TransactionService transactionService;
	
	@Autowired
	private UserService userService;
	
	/* Per Modal */
	@GetMapping("/transactions/add/{userId}")
	public String getAddTransactionForm(@PathVariable Long userId, @RequestParam String fromPage, Model model) {
		model.addAttribute("userId", userId);	
		model.addAttribute("expenseCategories", TransactionCategory.getExpenseCategories());
		model.addAttribute("profitCategories", TransactionCategory.getProfitCategories());
		model.addAttribute("fromPage", fromPage);
		
		User currentUser = userService.getById(userId);
		model.addAttribute("paymentMethods", currentUser.getPaymentMethods());
		
		model.addAttribute("transaction", new Transaction());
		
		return "fragments/addTransactionForm.html";
	}
	

	@PostMapping("/transactions/add/{userId}")
	public String addTransaction(@PathVariable Long userId,@Valid @ModelAttribute Transaction transaction, BindingResult bindingResult,
			@RequestParam String fromPage, RedirectAttributes redirectAttributes , Model model) {
		if(bindingResult.hasErrors()) {
			model.addAttribute("emptyName", "Inserire un nome ed una descrizione per la transazione: " + bindingResult.getAllErrors());
			return "fragments/addTransactionForm.html";
		}
		
		transactionService.save(transaction);
		User currentUser = userService.getById(userId);
		currentUser.getTransactions().add(transaction);
		userService.save(currentUser);
		
		redirectAttributes.addFlashAttribute("change", "Transazione aggiunta correttamente");
		return "redirect:" + fromPage;
	}
	
	@GetMapping("/transactions/manage/{userId}")
	public String viewTransactions(@PathVariable Long userId, Model model) {
		User currentUser = userService.getById(userId);
		model.addAttribute("transactions", currentUser.getTransactions());
		model.addAttribute("user", currentUser);
		
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
		Transaction toDelete = transactionService.getById(transactionId);
		currentUser.getTransactions().remove(toDelete);
		userService.save(currentUser);
		
		redirectAttributes.addFlashAttribute("change", "Transazione rimossa correttamente");
		return "redirect:/transactions/manage/" + currentUser.getId();
	}
	
}
