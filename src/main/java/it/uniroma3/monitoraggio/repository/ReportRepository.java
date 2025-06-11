package it.uniroma3.monitoraggio.repository;

import org.springframework.data.repository.CrudRepository;

import it.uniroma3.monitoraggio.model.Report;

public interface ReportRepository extends CrudRepository<Report, Long> {

}
