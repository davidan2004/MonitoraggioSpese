package it.uniroma3.monitoraggio.authentication;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.uniroma3.monitoraggio.model.Report;
import it.uniroma3.monitoraggio.model.Transaction;
import it.uniroma3.monitoraggio.model.TransactionCategory;
import it.uniroma3.monitoraggio.model.User;
import jakarta.validation.Valid;

@Controller
public class ReportController {

		@Autowired
		private ReportService reportService;
	
	
	/* Per Modal */
	@GetMapping("/reports/add/{userId}")
	public String getGenerateReportForm(@PathVariable Long userId, @RequestParam String fromPage, Model model) {
		model.addAttribute("userId", userId);
		model.addAttribute("fromPage", fromPage);
		
		model.addAttribute("expenseCategories", TransactionCategory.getExpenseCategories());
		model.addAttribute("profitCategories", TransactionCategory.getProfitCategories());
		
		model.addAttribute("report", new Report());
		
		return "fragments/generateReportForm.html";
	}
	
	
	@PostMapping("/reports/add/{userId}")
	public String addTransaction(@PathVariable Long userId,@Valid @ModelAttribute Report report, BindingResult bindingResult,
			@RequestParam String fromPage, RedirectAttributes redirectAttributes , Model model) {
		if(bindingResult.hasErrors()) {
			model.addAttribute("emptyName", "Inserire un nome ed una descrizione per la transazione: " + bindingResult.getAllErrors());
			return "fragments/addTransactionForm.html";
		}
		
		report.setEarnedAmount(0);
		report.setExpenseAmount(0);
		
		transactionService.save(transaction);
		User currentUser = userService.getById(userId);
		currentUser.getTransactions().add(transaction);
		userService.save(currentUser);
		
		redirectAttributes.addFlashAttribute("change", "Report aggiunto correttamente");
		return "redirect:" + fromPage;
	}
}
