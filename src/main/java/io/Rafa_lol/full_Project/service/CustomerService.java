package io.Rafa_lol.full_Project.service;

import io.Rafa_lol.full_Project.domain.Customer;
import io.Rafa_lol.full_Project.domain.Invoice;
import io.Rafa_lol.full_Project.domain.Stats;
import org.springframework.data.domain.Page;

public interface CustomerService {

    //Customer functions
    Customer createCustomer(Customer customer);
    Customer updateCustomer(Customer customer);
    Page<Customer> getCustomers(int page, int size);
    Iterable<Customer> getCustomers();
    Customer getCustomerById(Long id);
    Page<Customer> searchCustomers(String name, int page, int size);


    //Invoice functions
    Invoice createInvoice(Invoice invoice);
    Page<Invoice> getInvoices(int page, int size);
    void addInvoiceToCustomer(Long id, Invoice  invoice);

    Invoice getInvoice(Long id);

    Stats getStats();
}
