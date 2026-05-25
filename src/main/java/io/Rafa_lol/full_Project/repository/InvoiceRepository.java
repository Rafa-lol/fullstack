package io.Rafa_lol.full_Project.repository;



import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import io.Rafa_lol.full_Project.domain.Invoice;

import java.util.Map;

public interface InvoiceRepository extends PagingAndSortingRepository<Invoice, Long>, ListCrudRepository<Invoice, Long> {}
