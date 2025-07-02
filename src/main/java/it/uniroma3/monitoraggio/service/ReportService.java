package it.uniroma3.monitoraggio.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.monitoraggio.model.Report;
import it.uniroma3.monitoraggio.repository.ReportRepository;

@Service
public class ReportService {

	@Autowired
	private ReportRepository reportRepository;
	
	public void delete(Report report) {
		reportRepository.delete(report);
	}
}
