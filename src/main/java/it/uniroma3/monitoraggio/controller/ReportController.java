package it.uniroma3.monitoraggio.controller;

import java.time.LocalDate;
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

import it.uniroma3.monitoraggio.model.Report;
import it.uniroma3.monitoraggio.model.Transaction;
import it.uniroma3.monitoraggio.model.TransactionType;
import it.uniroma3.monitoraggio.model.User;
import it.uniroma3.monitoraggio.service.ReportService;
import it.uniroma3.monitoraggio.service.TransactionService;
import it.uniroma3.monitoraggio.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class ReportController {
	
	@Autowired
	private ReportService reportService;
	
	 @Autowired
	 private TransactionService transactionService;
	 
	 @Autowired
	 private UserService userService;
	 
	 
	/* Per Modal */
	@GetMapping("/reports/add/{userId}")
	public String getGenerateReportForm(@PathVariable Long userId, @RequestParam String fromPage,
			Model model, HttpSession session) {
			model.addAttribute("userId", userId);	
			model.addAttribute("report", new Report());
			model.addAttribute("fromPage", fromPage);
			model.addAttribute("transactionTypes", TransactionType.values());
			model.addAttribute("reportExists", userService.getById(userId).getReport() != null);
			
			String errors = (String) session.getAttribute("errors");
			if(errors !=null) {
				session.removeAttribute("errors");
				List<String> errorList = Arrays.asList(errors.split(","));
				if(errorList.contains("wrongStart"))
					model.addAttribute("wrongStart", "Inserire una data di inizio valida");
				if(errorList.contains("wrongEnd"))
					model.addAttribute("wrongEnd", "Inserire una data di fine valida");
			}
			

			return "fragments/generateReportForm.html";
		}
	
	
	@PostMapping("/reports/add/{userId}")
	public String generateReport(@PathVariable Long userId, @RequestParam String fromPage,
			@Valid @ModelAttribute Report report, BindingResult bindingResult,
			RedirectAttributes redirectAttributes, Model model, HttpSession session) {
		
		User currentUser = userService.getById(userId);
		if( currentUser == null || !(currentUser.getId().equals(userService.getCurrentUser().getId()))) {
			redirectAttributes.addFlashAttribute("change", "Operazione non permessa");
			return "redirect:/";
		}	
		if(bindingResult.hasErrors() || report.getStart().isAfter(report.getEnd())) {
			String errors = "";
			
			if(report.getStart().isAfter(LocalDate.now()) || report.getStart().isAfter(report.getEnd()))
				errors+="wrongStart,";
			if(report.getEnd().isAfter(LocalDate.now()))
				errors+="wrongEnd,";
			
	        session.setAttribute("errors", errors);
	        redirectAttributes.addFlashAttribute("modalToShow", "generateReport");
	        return "redirect:" + fromPage;
		}
		
		Report oldReport = currentUser.getReport();
	
		
		List<Transaction> reportTransactions;
		
		if(report.getType() == null)
			reportTransactions = transactionService.findBetweenDates(currentUser, report.getStart(), report.getEnd());
		else
			reportTransactions = transactionService.findBetweenDatesByType(currentUser, report.getStart(), report.getEnd(), report.getType());
		
		int transactionsAmount = reportTransactions.size();
		
		int expenseAmount = Transaction.getTotalAmount(reportTransactions, TransactionType.EXPENSE);
		int profitAmount = Transaction.getTotalAmount(reportTransactions, TransactionType.PROFIT);
		
		
		report.setTransactionsAmount(transactionsAmount);
		report.setExpenseAmount(expenseAmount);
		report.setEarnedAmount(profitAmount);
		
		currentUser.setReport(report);
		userService.save(currentUser);
		reportService.delete(oldReport);
		
		redirectAttributes.addFlashAttribute("change", "Report generato correttamente");
		return "redirect:" + fromPage;
	}
}
